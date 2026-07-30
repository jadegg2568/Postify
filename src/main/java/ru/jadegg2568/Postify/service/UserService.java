package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.user.UserDeletedEvent;
import ru.jadegg2568.Postify.exception.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User updateProfile(UUID uuid, UpdateProfileRequest request) {
        log.debug("Updating profile for user UUID: {}", uuid);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);

        userMapper.updateEntity(request, user);
        log.info("Profile updated for user UUID: {}", uuid);
//        eventPublisher.publishEvent(new UserUpdatedEvent);

        return user;
    }

    @Transactional
    public String updateAvatarKey(User user, String key) {
        user.setAvatarKey(key);
        return fileService.generatePresignedUrl(key);
    }

    @Transactional
    public void delete(UUID uuid) {
        log.debug("Delete request for user UUID: {}", uuid);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
        log.info("User deleted: {}", uuid);
        eventPublisher.publishEvent(new UserDeletedEvent(user));
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
