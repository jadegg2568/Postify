package ru.jadegg2568.Postify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.exception.auth.SessionNotFoundException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.service.SessionService;
import ru.jadegg2568.Postify.service.UserService;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

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
    private Session session2;
    private UUID userUuid;
    private UUID sessionUuid;
    private UUID session2Uuid;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        sessionUuid = UUID.randomUUID();
        session2Uuid = UUID.randomUUID();
        refreshToken = "valid-refresh-token";

        user = User.builder()
                .id(1L)
                .uuid(userUuid)
                .mail("test@example.com")
                .permissions(Permissions.USER)
                .build();

        session = Session.builder()
                .id(10L)
                .uuid(sessionUuid)
                .user(user)
                .title("Firefox - Arch Linux")
                .cancelled(false)
                .createdAt(Instant.now())
                .build();
        session2 = Session.builder()
                .id(20L)
                .uuid(session2Uuid)
                .user(user)
                .title("Google Chrome - Windows 11")
                .cancelled(false)
                .createdAt(Instant.now().plusSeconds(60))
                .build();
    }

    @Test
    @DisplayName("generateSession - должен успешно создать и сохранить сессию")
    void generateSession_ShouldSaveAndReturnSession() {
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        Session result = sessionService.generateSession(user);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTitle()).isEqualTo(result.getUuid().toString());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("findUserSessions - должен вернуть список сессий пользователя")
    void findUserSessions_ShouldReturnListOfSessions() {
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session));

        List<Session> result = sessionService.findUserSessions(userUuid);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(session);
    }

    @Test
    @DisplayName("cancelSession - должен пометить сессию как отмененную")
    void cancelSession_ShouldSetCancelledTrue_WhenSessionExists() {
        when(sessionRepository.findByDetailedInfo(userUuid, sessionUuid)).thenReturn(Optional.of(session));

        sessionService.cancelSession(userUuid, sessionUuid);

        assertThat(session.isCancelled()).isTrue();
        verify(sessionRepository).findByDetailedInfo(userUuid, sessionUuid);
    }

    @Test
    @DisplayName("cancelUserSessions - должен отменить все сессии пользователя")
    void cancelUserSessions_ShouldCancelAllSessions() {
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        sessionService.cancelUserSessions(userUuid);

        assertThat(session.isCancelled()).isTrue();
        assertThat(session2.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("generateToken - должен создать access токен при валидном refresh")
    void generateToken_ShouldReturnAccessToken_WhenRefreshIsValid() {
        // given
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));
        when(tokenManager.generateAccessToken(any(), any())).thenReturn("new-access-token");

        // when
        String result = sessionService.generateToken(user, refreshToken);

        // then
        assertThat(result).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("generateToken - должен кинуть Mismatch если юзер в токене не совпадает с юзером сессии")
    void generateToken_ShouldThrowMismatch_WhenUserNotMatch() {
        UUID strangerUuid = UUID.randomUUID();
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(strangerUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.generateToken(user, refreshToken))
                .isInstanceOf(SessionMismatchException.class);
    }

    @Test
    @DisplayName("generateToken - должен кинуть Expired если сессия отменена")
    void generateToken_ShouldThrowExpired_WhenSessionIsCancelled() {
        session.setCancelled(true);
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.generateToken(user, refreshToken))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("generateToken - должен кинуть NotFound если сессии нет в БД")
    void generateToken_ShouldThrowNotFound_WhenSessionMissing() {
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.generateToken(user, refreshToken))
                .isInstanceOf(SessionNotFoundException.class);
    }
}