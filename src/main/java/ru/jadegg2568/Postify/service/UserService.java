package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    @Transactional
    public User updateMyProfile(UUID authUuid, UpdateProfileRequest request) {
        User user = userRepository.findByUuid(authUuid)
                .orElseThrow(NotAuthorizedException::new);

        userMapper.updateEntity(request, user);
        return user;
    }

    @Transactional
    public void deleteMe(UUID authUuid) {
        if (!userRepository.existsByUuid(authUuid)) {
            throw new NotAuthorizedException();
        }
        userRepository.deleteByUuid(authUuid);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.searchByQuery(query);
    }

    public User getByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);
    }
}
