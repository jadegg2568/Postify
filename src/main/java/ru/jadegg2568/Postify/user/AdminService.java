package ru.jadegg2568.Postify.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.auth.Permissions;
import ru.jadegg2568.Postify.user.event.AdminChangePermissionsEvent;
import ru.jadegg2568.Postify.auth.exception.NoAccessException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updatePermissions(UUID adminUuid, UUID userUuid, Permissions newPermissions) {
        log.debug("Updating rights for user UUID: {} to: {}", userUuid, newPermissions);
        User admin = userService.getByUuid(adminUuid);
        User user = userService.getByUuid(userUuid);
        requireAdmin(admin);

        user.setPermissions(newPermissions);
        log.info("Rights updated for user UUID: {} to: {}", user.getUuid(), newPermissions);
        eventPublisher.publishEvent(new AdminChangePermissionsEvent(admin, user));
    }

    private void requireAdmin(User admin) {
        if (!admin.getPermissions().isAdmin()) {
            throw new NoAccessException();
        }
    }
}
