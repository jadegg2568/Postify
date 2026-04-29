package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NotAuthorizedException;
import ru.jadegg2568.Postify.exception.user.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.entity.Permissions;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User updateProfile(UUID uuid, UpdateProfileRequest request) {
        log.debug("Updating profile for user UUID: {}", uuid);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(NotAuthorizedException::new);

        userMapper.updateEntity(request, user);
        log.info("Profile updated for user UUID: {}", uuid);

        return user;
    }

    @Transactional
    public void updatePermissions(UUID uuid, Permissions newPermissions) {
        log.debug("Updating rights for user UUID: {} to: {}", uuid, newPermissions);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);

        user.setPermissions(newPermissions);
        log.info("Rights updated for user UUID: {} to: {}", uuid, newPermissions);
    }

    @Transactional
    public void delete(UUID uuid) {
        log.debug("Delete request for user UUID: {}", uuid);
        if (!userRepository.existsByUuid(uuid)) {
            log.warn("Delete failed - user not found: {}", uuid);
            throw new NotAuthorizedException();
        }

        userRepository.deleteByUuid(uuid);
        log.info("User deleted: {}", uuid);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        log.debug("Searching users with query: {}", query);
        List<User> users = userRepository.searchByQuery(query);
        log.debug("Found {} users for query: {}", users.size(), query);
        return users;
    }

    public User getByUuid(UUID uuid) {
        log.debug("Getting user by UUID: {}", uuid);
        return userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getByName(String name) {
        log.debug("Getting user by name: {}", name);
        return userRepository.findByName(name)
                .orElseThrow(UserNotFoundException::new);
    }
}
