package ru.jadegg2568.Postify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.jadegg2568.Postify.request.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NotAuthorizedException;
import ru.jadegg2568.Postify.exception.user.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.security.JwtManager;
import ru.jadegg2568.Postify.entity.Permissions;
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

    @Mock
    private JwtManager jwtManager;

    @InjectMocks
    private UserService userService;

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

        User result = userService.updateProfile(uuid, updateProfileRequest);

        assertThat(result.getName()).isEqualTo(updateProfileRequest.name());
        assertThat(result.getDisplayName()).isEqualTo(updateProfileRequest.displayName());
        assertThat(result.getDescription()).isEqualTo(updateProfileRequest.description());
    }

    @Test
    @DisplayName("updateProfile - должен выбросить ошибку если пользователь не найден")
    void updateMyProfile_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(uuid, updateProfileRequest))
                .isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    @DisplayName("updateRights - должен успешно обновить права пользователя")
    void updateRights_ShouldUpdateUserPermissions_WhenUserExists() {
        // given
        Permissions newPermissions = Permissions.ADMIN; // Предполагаем, что ADMIN есть в твоем enum
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when
        userService.updatePermissions(uuid, newPermissions);

        // then
        assertThat(user.getPermissions()).isEqualTo(newPermissions);
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    @DisplayName("updateRights - должен выбросить ошибку если пользователь не найден")
    void updatePermissions_ShouldThrowException_WhenUserNotFound() {
        // given
        Permissions newPermissions = Permissions.ADMIN;
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updatePermissions(uuid, newPermissions))
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
        userService.delete(uuid);

        // then
        verify(userRepository).deleteByUuid(uuid);
    }

    @Test
    @DisplayName("delete - должен выбросить ошибку если пользователь не найден")
    void delete_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.existsByUuid(uuid)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.delete(uuid))
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
    @DisplayName("getByName - должен вернуть пользователя когда он найден")
    void getByName_ShouldReturnUser_WhenUserExists() {
        // given
        when(userRepository.findByName(user.getName())).thenReturn(Optional.of(user));

        // when
        User result = userService.getByName(user.getName());

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("getByName - должен выбросить ошибку если пользователь не найден по названию")
    void getByName_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByName(user.getName())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getByName(user.getName()))
                .isInstanceOf(UserNotFoundException.class);
    }
}