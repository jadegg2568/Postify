package ru.jadegg2568.Postify.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.admin.AdminChangePermissionsEvent;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.exception.UserNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AdminService adminService;

    private final UUID adminUuid = UUID.randomUUID();
    private final UUID targetUserUuid = UUID.randomUUID();

    private User createUser(UUID uuid, Permissions permissions) {
        return User.builder()
                .uuid(uuid)
                .permissions(permissions)
                .build();
    }

    @Test
    @DisplayName("updatePermissions - должен обновить права целевого пользователя, если админ")
    void updatePermissions_ShouldUpdateTargetUserPermissions_WhenAdmin() {
        // given
        User admin = createUser(adminUuid, Permissions.ADMIN);
        User targetUser = createUser(targetUserUuid, Permissions.USER);
        Permissions newPermissions = Permissions.OWNER;

        when(userService.getByUuid(adminUuid)).thenReturn(admin);
        when(userService.getByUuid(targetUserUuid)).thenReturn(targetUser);

        // when
        adminService.updatePermissions(adminUuid, targetUserUuid, newPermissions);

        // then
        assertThat(targetUser.getPermissions()).isEqualTo(newPermissions);
        verify(eventPublisher).publishEvent(any(AdminChangePermissionsEvent.class));
    }

    @Test
    @DisplayName("updatePermissions - должен выбросить NoAccessException, если пользователь не админ")
    void updatePermissions_ShouldThrowNoAccessException_WhenNotAdmin() {
        // given
        User nonAdmin = createUser(adminUuid, Permissions.USER);

        when(userService.getByUuid(adminUuid)).thenReturn(nonAdmin);

        // when & then
        assertThatThrownBy(() -> adminService.updatePermissions(adminUuid, targetUserUuid, Permissions.ADMIN))
                .isInstanceOf(NoAccessException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updatePermissions - должен выбросить UserNotFoundException, если целевой пользователь не найден")
    void updatePermissions_ShouldThrowUserNotFoundException_WhenTargetUserNotFound() {
        // given
        User admin = createUser(adminUuid, Permissions.ADMIN);

        when(userService.getByUuid(adminUuid)).thenReturn(admin);
        when(userService.getByUuid(targetUserUuid)).thenThrow(new UserNotFoundException());

        // when & then
        assertThatThrownBy(() -> adminService.updatePermissions(adminUuid, targetUserUuid, Permissions.ADMIN))
                .isInstanceOf(UserNotFoundException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updatePermissions - должен выбросить UserNotFoundException, если админ не найден")
    void updatePermissions_ShouldThrowUserNotFoundException_WhenAdminNotFound() {
        // given
        when(userService.getByUuid(adminUuid)).thenThrow(new UserNotFoundException());

        // when & then
        assertThatThrownBy(() -> adminService.updatePermissions(adminUuid, targetUserUuid, Permissions.ADMIN))
                .isInstanceOf(UserNotFoundException.class);

        verify(userService, never()).getByUuid(targetUserUuid);
        verify(eventPublisher, never()).publishEvent(any());
    }
}