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
import ru.jadegg2568.Postify.request.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.InvalidCredentialsException;
import ru.jadegg2568.Postify.exception.auth.NotAuthorizedException;
import ru.jadegg2568.Postify.exception.user.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID uuid;
    private User user;
    private User updatedUser;
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
        User result = userService.register(registerRequest);

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
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("login - должен вернуть пользователя когда данные валидны")
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);

        // when
        User result = userService.login(loginRequest);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("login - должен выбросить ошибку если пользователь не найден")
    void login_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login - должен выбросить ошибку если пароль не совпадает")
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // given
        when(userRepository.findByLogin(loginRequest.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("updateProfile - должен обновить профиль когда пользователь найден")
    void updateProfile_ShouldUpdateMyUser_WhenUserExists() {
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        doAnswer(inv -> {
            UpdateProfileRequest req = inv.getArgument(0);
            User u = inv.getArgument(1);
            u.setName(req.name());
            u.setDisplayName(req.displayName());
            u.setDescription(req.description());
            return null;
        }).when(userMapper).updateEntity(any(), any());

        User result = userService.updateMyProfile(uuid, updateProfileRequest);

        assertThat(result.getName()).isEqualTo(updateProfileRequest.name());
        assertThat(result.getDisplayName()).isEqualTo(updateProfileRequest.displayName());
        assertThat(result.getDescription()).isEqualTo(updateProfileRequest.description());
    }

    @Test
    @DisplayName("updateProfile - должен выбросить ошибку если пользователь не найден")
    void updateMyProfile_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateMyProfile(uuid, updateProfileRequest))
                .isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    @DisplayName("searchUsers - должен вернуть список пользователей по поисковому запросу")
    void searchUsers_ShouldReturnListOfUsers() {
        // given
        String query = "test";
        List<User> users = List.of(user);
        when(userRepository.searchByQuery(query)).thenReturn(users);

        // when
        List<User> result = userService.searchUsers(query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(user);
    }

    @Test
    @DisplayName("delete - должен удалить пользователя когда он найден")
    void delete_ShouldDeleteMeUser_WhenUserExists() {
        // given
        when(userRepository.existsByUuid(uuid)).thenReturn(true);
        doNothing().when(userRepository).deleteByUuid(uuid);

        // when
        userService.deleteMe(uuid);

        // then
        verify(userRepository).deleteByUuid(uuid);
    }

    @Test
    @DisplayName("delete - должен выбросить ошибку если пользователь не найден")
    void delete_Me_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.existsByUuid(uuid)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.deleteMe(uuid))
                .isInstanceOf(NotAuthorizedException.class);
        
        verify(userRepository, never()).deleteByUuid(any());
    }

    @Test
    @DisplayName("getByUuid - должен вернуть пользователя когда он найден")
    void getByUuid_ShouldReturnUser_WhenUserExists() {
        // given
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when
        User result = userService.getByUuid(uuid);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("getByUuid - должен выбросить ошибку если пользователь не найден по айди")
    void getByUuid_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getByUuid(uuid))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("getByName - должен вернуть пользователя, если имя существует")
    void getByName_ShouldReturnUser_WhenNameExists() {
        // given
        String name = "testuser";
        when(userRepository.findByName(name)).thenReturn(Optional.of(user));

        // when
        User result = userService.getByName(name);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        verify(userRepository).findByName(name);
    }

    @Test
    @DisplayName("getByName - должен выбросить UserNotFoundException, если имени нет")
    void getByName_ShouldThrowException_WhenNameDoesNotExist() {
        // given
        String name = "nonexistent";
        when(userRepository.findByName(name)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getByName(name))
                .isInstanceOf(UserNotFoundException.class);
    }
}