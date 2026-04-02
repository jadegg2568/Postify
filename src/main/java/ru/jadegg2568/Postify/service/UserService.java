package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.InvalidCredentialsException;
import ru.jadegg2568.Postify.exception.UserAlreadyExistsException;
import ru.jadegg2568.Postify.exception.UserNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByMailOrName(request.mail(), request.name())) {
            throw new UserAlreadyExistsException();
        }

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
    public User updateProfile(UUID uuid, UpdateProfileRequest request) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);

        userMapper.updateEntity(request, user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.searchByQuery(query);
    }

    @Transactional
    public void delete(UUID uuid) {
        if (!userRepository.existsByUuid(uuid)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteByUuid(uuid);
    }

    public User getByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);
    }
}
