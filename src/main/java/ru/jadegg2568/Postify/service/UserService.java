package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.InvalidCredentialsException;
import ru.jadegg2568.Postify.exception.auth.NotAuthorizedException;
import ru.jadegg2568.Postify.exception.user.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.security.JwtManager;
import ru.jadegg2568.Postify.security.Role;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtManager jwtManager;

    @Transactional
    public User register(RegisterRequest request) {
        log.debug("Registering user with email: {}", request.mail());
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        log.info("User registered successfully with UUID: {}", user.getUuid());

        return user;
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        log.debug("Login attempt for login: {}", request.login());
        User user = userRepository.findByLogin(request.login())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.info("Failed login attempt for login: {}", request.login());
            throw new InvalidCredentialsException();
        }

        log.info("User logged in successfully: {}", user.getUuid());
        return user;
    }

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
    public void delete(UUID uuid) {
        log.debug("Delete request for user UUID: {}", uuid)
        ;
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

    public String generateToken(User user) {
        return jwtManager.toToken(user.getUuid(), Role.USER);
    }
}
