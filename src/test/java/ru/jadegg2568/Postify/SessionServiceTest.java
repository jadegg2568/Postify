package ru.jadegg2568.Postify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jadegg2568.Postify.config.JwtProperties;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;
import ru.jadegg2568.Postify.service.SessionService;
import ru.jadegg2568.Postify.service.UserService;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private JwtProperties jwtProperties;

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
        when(jwtProperties.getRefreshExpirationTime())
                .thenReturn(Duration.ofDays(30));

        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Session result = sessionService.generateSession(user);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);

        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("generateToken (Refresh) - должен создать access токен при валидном refresh")
    void generateToken_Refresh_ShouldReturnAccessToken() {
        // given
        when(tokenManager.getClaim(refreshToken, "sid", UUID.class)).thenReturn(sessionUuid);
        when(tokenManager.getSubject(refreshToken)).thenReturn(userUuid.toString());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));
        when(tokenManager.generateAccessToken(any(), any())).thenReturn("new-access-token");

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
        when(tokenManager.generateAccessToken(any(), any())).thenReturn("direct-access-token");

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
    @DisplayName("cancelUserSessions - должен отменить все сессии пользователя")
    void cancelUserSessions_ShouldCancelAllSessions() {
        // given
        Session session2 = Session.builder().user(user).cancelled(false).build();
        when(userService.getByUuid(userUuid)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(List.of(session, session2));

        // when
        sessionService.cancelUserSessions(userUuid);

        // then
        assertThat(session.isCancelled()).isTrue();
        assertThat(session2.isCancelled()).isTrue();
    }
}