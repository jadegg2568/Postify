package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventPublicationInterceptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.user.UserLoggedEvent;
import ru.jadegg2568.Postify.event.user.UserRegisteredEvent;
import ru.jadegg2568.Postify.exception.auth.InvalidCredentialsException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User register(RegisterRequest request) {
        log.debug("Registering user with email: {}", request.mail());
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPermissions(Permissions.USER);

        userRepository.save(user);
        log.info("User registered successfully with UUID: {}", user.getUuid());

        eventPublisher.publishEvent(new UserRegisteredEvent(user));

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

        // event publishing is better in its session

        return user;
    }
}
