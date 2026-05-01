package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.config.JwtProperties;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.exception.auth.SessionNotFoundException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final TokenManager tokenManager;

    @Transactional
    public Session generateSession(User user) {
        UUID uuid = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(jwtProperties.getRefreshExpirationTime());

        Session session = Session.builder()
                .uuid(uuid)
                .user(user)
                .title(uuid.toString())
                .expiresAt(expiresAt)
                .build();

        log.debug("Generated user session: {} for user {}", uuid, user.getUuid());
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public String generateToken(String refreshToken) {
        UUID sessionUuid = tokenManager.getClaim(refreshToken, "sid", UUID.class);
        UUID tokenUserUuid = UUID.fromString(tokenManager.getSubject(refreshToken));

        Session session = sessionRepository.findByUuid(sessionUuid)
                .orElseThrow(SessionNotFoundException::new);

        User user = session.getUser();

        if (!user.getUuid().equals(tokenUserUuid)) {
            log.warn("Security alert: User {} tried to use session of user {}", tokenUserUuid, user.getUuid());
            throw new SessionMismatchException();
        }

        return generateToken(user, session);
    }

    public String generateToken(User user, Session session) {
        if (session.isCancelled()) {
            log.info("Attempt to use cancelled session: {}", session.getUuid());
            throw new SessionExpiredException();
        }

        return tokenManager.generateAccessToken(
                user.getUuid(),
                user.getPermissions().getAuthorities()
        );
    }

    public String generateRefreshToken(Session session) {
        return tokenManager.generateRefreshToken(session.getUser().getUuid(), session.getUuid());
    }

    @Transactional
    public void cancelSession(UUID userUuid, UUID uuid) {
        Session session = sessionRepository.findByDetailedInfo(userUuid, uuid)
                .orElseThrow(SessionNotFoundException::new);
        session.setCancelled(true);
    }

    @Transactional
    public void cancelUserSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        List<Session> sessions = sessionRepository.findByUserId(user.getId());
        sessions.forEach(s -> s.setCancelled(true));
    }

    public List<Session> findUserSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        return sessionRepository.findByUserId(user.getId());
    }

    private static @NonNull String joinLastSessions(List<Session> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getCreatedAt).reversed())
                .limit(5)
                .map(s -> s.getUuid().toString())
                .collect(Collectors.joining(", "));
    }
}