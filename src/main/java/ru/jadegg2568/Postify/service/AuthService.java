package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.InvalidCredentialsException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.security.JwtManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
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

    public String generateToken(User user) {
        return jwtManager.toToken(user.getUuid(), Permissions.USER.getAuthorities());
    }
}
