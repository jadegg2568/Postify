package ru.jadegg2568.Postify.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import ru.jadegg2568.Postify.config.SessionConfig;
import ru.jadegg2568.Postify.config.Cleanup;
import ru.jadegg2568.Postify.util.DeviceData;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.exception.auth.SessionNotFoundException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionConfig sessionConfig;

    @Mock
    private UserService userService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SessionService sessionService;

    private User user;
    private Session session;
    private UUID userUuid;
    private UUID sessionUuid;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        sessionUuid = UUID.randomUUID();
        refreshToken = "valid-refresh-token";

        user = User.builder()
                .id(1L)
                .uuid(userUuid)
                .permissions(Permissions.USER)
                .build();

        session = Session.builder()
                .id(10L)
                .uuid(sessionUuid)
                .user(user)
                .cancelled(false)
                .build();
    }

    @Test
    void generateSession_ShouldSaveAndReturnSession() {
        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DeviceData deviceData = new DeviceData("Chrome 148", "Windows 10 22H2");
        Session result = sessionService.generateSession(user, deviceData);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getBrowser()).isEqualTo("Chrome 148");
        assertThat(result.getOs()).isEqualTo("Windows 10 22H2");

        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен создать access токен при валидном refresh")
    void generateToken_Refresh_ShouldReturnAccessToken() {
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));
        when(tokenManager.generateAccessToken(eq(userUuid))).thenReturn("new-access-token");

        String result = sessionService.generateToken(refreshToken);

        assertThat(result).isEqualTo("new-access-token");
        verify(sessionRepository).findByUuid(sessionUuid);
    }

    @Test
    @DisplayName("generateToken (Login) - должен создать access токен без обращения к БД")
    void generateToken_Login_ShouldReturnAccessToken() {
        when(tokenManager.generateAccessToken(eq(userUuid))).thenReturn("direct-access-token");

        String result = sessionService.generateToken(user, session);

        assertThat(result).isEqualTo("direct-access-token");
        verifyNoInteractions(sessionRepository);
    }

    @Test
    @DisplayName("generateToken - должен кинуть Mismatch если юзер в токене не совпадает с юзером сессии")
    void generateToken_ShouldThrowMismatch_WhenUserNotMatch() {
        UUID strangerUuid = UUID.randomUUID();
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(strangerUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionMismatchException.class);
    }

    @Test
    @DisplayName("generateToken - должен кинуть Expired если сессия отменена")
    void generateToken_ShouldThrowExpired_WhenSessionIsCancelled() {
        session.setCancelled(true);

        assertThatThrownBy(() -> sessionService.generateToken(user, session))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("revokeSessions - должен отменить все сессии пользователя")
    void revokeSessions_ShouldCancelAllSessions() {
        Session session2 = Session.builder().user(user).cancelled(false).build();
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        sessionService.revokeSessions(userUuid);

        assertThat(session.isCancelled()).isTrue();
        assertThat(session2.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("generateRefreshToken - должен создать refresh токен для сессии")
    void generateRefreshToken_ShouldReturnRefreshToken() {
        when(tokenManager.generateRefreshToken(userUuid, sessionUuid)).thenReturn("refresh-token");

        String result = sessionService.generateRefreshToken(session);

        assertThat(result).isEqualTo("refresh-token");
        verify(tokenManager).generateRefreshToken(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("revokeSession - должен отменить сессию пользователя")
    void revokeSession_ShouldCancelSession() {
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.of(session));

        sessionService.revokeSession(userUuid, sessionUuid);

        assertThat(session.isCancelled()).isTrue();
        verify(sessionRepository).findByDetailedInfo(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("revokeSession - должен кинуть NotFound если сессия не найдена")
    void revokeSession_ShouldThrowNotFound_WhenSessionMissing() {
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.revokeSession(userUuid, sessionUuid))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("getSession - должен вернуть сессию пользователя")
    void getSession_ShouldReturnSession() {
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.of(session));

        Session result = sessionService.getSession(userUuid, sessionUuid);

        assertThat(result).isSameAs(session);
        verify(sessionRepository).findByDetailedInfo(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("getSession - должен кинуть NotFound если сессия не найдена")
    void getSession_ShouldThrowNotFound_WhenSessionMissing() {
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSession(userUuid, sessionUuid))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("getSessions - должен вернуть все сессии пользователя")
    void getSessions_ShouldReturnUserSessions() {
        Session session2 = Session.builder().uuid(UUID.randomUUID()).user(user).build();
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        List<Session> result = sessionService.getSessions(userUuid);

        assertThat(result).containsExactly(session, session2);
        verify(userService).getByUuid(userUuid);
        verify(sessionRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("clearExpiredSessions - должен удалить просроченные сессии")
    void clearExpiredSessions_ShouldDeleteExpiredSessions() {
        // given
        Cleanup cleanup = new Cleanup();
        cleanup.setExpiration(Duration.ofHours(24));
        cleanup.setSize(50);
        cleanup.setDelay(Duration.ofMinutes(5));

        when(sessionConfig.getCleanup()).thenReturn(cleanup);
        when(sessionRepository.deleteExpired(any(Instant.class), any(PageRequest.class)))
                .thenReturn(5L);

        // when
        sessionService.clearExpiredSessions();

        // then
        verify(sessionRepository).deleteExpired(any(Instant.class), eq(PageRequest.of(0, 50)));
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен кинуть NotFound если сессия не найдена")
    void generateToken_Refresh_ShouldThrowNotFound_WhenSessionMissing() {
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен кинуть Expired если сессия отменена")
    void generateToken_Refresh_ShouldThrowExpired_WhenSessionIsCancelled() {
        session.setCancelled(true);
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionExpiredException.class);
    }
}