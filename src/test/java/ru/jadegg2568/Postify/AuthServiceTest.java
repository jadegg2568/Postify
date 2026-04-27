package ru.jadegg2568.Postify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.InvalidCredentialsException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.security.TokenManager;
import ru.jadegg2568.Postify.service.AuthService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private AuthService authService;

    private UUID uuid;
    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UpdateProfileRequest updateProfileRequest;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        user = User.builder()
                .uuid(uuid)
                .mail("test@example.com")
                .name("testuser")
                .displayName("")
                .description("")
                .passwordHash("encodedPassword")
                .build();

        registerRequest = new RegisterRequest("test@example.com", "password123", "testuser", "", "");

        loginRequest = new LoginRequest("test@example.com", "password123");

        updateProfileRequest = new UpdateProfileRequest("testuser", "User_newname_1234", "cool man");
    }

    @Test
    @DisplayName("register - должен зарегистрировать пользователя когда данные валидны")
    void register_ShouldRegisterUser_WhenCredentialsAreValid() {
        // given
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        User result = authService.register(registerRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMail()).isEqualTo(registerRequest.mail());
        assertThat(result.getName()).isEqualTo(registerRequest.name());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - должен выбросить ошибку если пользователь уже существует")
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        // given
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("login - должен вернуть пользователя когда данные валидны")
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);

        // when
        User result = authService.login(loginRequest);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("login - должен выбросить ошибку если пользователь не найден")
    void login_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login - должен выбросить ошибку если пароль не совпадает")
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
