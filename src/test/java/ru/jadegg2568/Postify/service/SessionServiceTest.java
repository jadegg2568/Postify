package ru.jadegg2568.Postify.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.config.SessionConfig;
import ru.jadegg2568.Postify.data.DeviceData;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @InjectMocks
    private SessionService sessionService;

    private User user;
    private Session session;
    private UUID userUuid;
    private UUID sessionUuid;
    private String refreshToken;

    @Mock private ApplicationEventPublisher eventPublisher;

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
        when(sessionConfig.getRefreshExpiration())
                .thenReturn(Duration.ofDays(30));

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
        // given
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));
        when(tokenManager.generateAccessToken(eq(userUuid))).thenReturn("new-access-token");

        // when
        String result = sessionService.generateToken(refreshToken);

        // then
        assertThat(result).isEqualTo("new-access-token");
        verify(sessionRepository).findByUuid(sessionUuid); // check that database was queried
    }

    @Test
    @DisplayName("generateToken (Login) - должен создать access токен без обращения к БД")
    void generateToken_Login_ShouldReturnAccessToken() {
        // given
        when(tokenManager.generateAccessToken(eq(userUuid))).thenReturn("direct-access-token");

        // when
        String result = sessionService.generateToken(user, session);

        // then
        assertThat(result).isEqualTo("direct-access-token");
        verifyNoInteractions(sessionRepository); // check that database wasn't queried
    }

    @Test
    @DisplayName("generateToken - должен кинуть Mismatch если юзер в токене не совпадает с юзером сессии")
    void generateToken_ShouldThrowMismatch_WhenUserNotMatch() {
        // given
        UUID strangerUuid = UUID.randomUUID();
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(strangerUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        // when & then
        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionMismatchException.class);
    }

    @Test
    @DisplayName("generateToken - должен кинуть Expired если сессия отменена")
    void generateToken_ShouldThrowExpired_WhenSessionIsCancelled() {
        // given
        session.setCancelled(true);
        // Для логина (перегрузка с объектами)
        assertThatThrownBy(() -> sessionService.generateToken(user, session))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("revokeSessions - должен отменить все сессии пользователя")
    void revokeSessions_ShouldCancelAllSessions() {
        // given
        Session session2 = Session.builder().user(user).cancelled(false).build();
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        // when
        sessionService.revokeSessions(userUuid);

        // then
        assertThat(session.isCancelled()).isTrue();
        assertThat(session2.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("generateRefreshToken - должен создать refresh токен для сессии")
    void generateRefreshToken_ShouldReturnRefreshToken() {
        // given
        when(tokenManager.generateRefreshToken(userUuid, sessionUuid)).thenReturn("refresh-token");

        // when
        String result = sessionService.generateRefreshToken(session);

        // then
        assertThat(result).isEqualTo("refresh-token");
        verify(tokenManager).generateRefreshToken(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("revokeSession - должен отменить сессию пользователя")
    void revokeSession_ShouldCancelSession() {
        // given
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.of(session));

        // when
        sessionService.revokeSession(userUuid, sessionUuid);

        // then
        assertThat(session.isCancelled()).isTrue();
        verify(sessionRepository).findByDetailedInfo(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("revokeSession - должен кинуть NotFound если сессия не найдена")
    void revokeSession_ShouldThrowNotFound_WhenSessionMissing() {
        // given
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sessionService.revokeSession(userUuid, sessionUuid))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("getSession - должен вернуть сессию пользователя")
    void getSession_ShouldReturnSession() {
        // given
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.of(session));

        // when
        Session result = sessionService.getSession(userUuid, sessionUuid);

        // then
        assertThat(result).isSameAs(session);
        verify(sessionRepository).findByDetailedInfo(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("getSession - должен кинуть NotFound если сессия не найдена")
    void getSession_ShouldThrowNotFound_WhenSessionMissing() {
        // given
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sessionService.getSession(userUuid, sessionUuid))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("getSessions - должен вернуть все сессии пользователя")
    void getSessions_ShouldReturnUserSessions() {
        // given
        Session session2 = Session.builder().uuid(UUID.randomUUID()).user(user).build();
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        // when
        List<Session> result = sessionService.getSessions(userUuid);

        // then
        assertThat(result).containsExactly(session, session2);
        verify(userService).getByUuid(userUuid);
        verify(sessionRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("clearExpiredSessions - должен удалить просроченные сессии")
    void clearExpiredSessions_ShouldDeleteExpiredSessions() {
        // given
        SessionConfig.Cleanup cleanup = new SessionConfig.Cleanup();
        cleanup.setSize(50);
        cleanup.setDelay(Duration.ofHours(1));
        when(sessionConfig.getCleanup()).thenReturn(cleanup);
        when(sessionRepository.findExpiredIds(any(Instant.class), any(Pageable.class)))
                .thenReturn(List.of(1L, 2L, 3L));

        // when
        sessionService.clearExpiredSessions();

        // then
        verify(sessionRepository).findExpiredIds(any(Instant.class), eq(PageRequest.of(0, 50)));
        verify(sessionRepository).deleteByIds(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("clearExpiredSessions - не должен вызывать delete если просроченных сессий нет")
    void clearExpiredSessions_ShouldNotDelete_WhenNoExpiredSessions() {
        // given
        SessionConfig.Cleanup cleanup = new SessionConfig.Cleanup();
        cleanup.setSize(50);
        cleanup.setDelay(Duration.ofHours(1));
        when(sessionConfig.getCleanup()).thenReturn(cleanup);
        when(sessionRepository.findExpiredIds(any(Instant.class), any(Pageable.class)))
                .thenReturn(List.of());

        // when
        sessionService.clearExpiredSessions();

        // then
        verify(sessionRepository).deleteByIds(List.of());
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен кинуть NotFound если сессия не найдена")
    void generateToken_Refresh_ShouldThrowNotFound_WhenSessionMissing() {
        // given
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен кинуть Expired если сессия отменена")
    void generateToken_Refresh_ShouldThrowExpired_WhenSessionIsCancelled() {
        // given
        session.setCancelled(true);
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        // when & then
        assertThatThrownBy(() -> sessionService.generateToken(refreshToken))
                .isInstanceOf(SessionExpiredException.class);
    }
}