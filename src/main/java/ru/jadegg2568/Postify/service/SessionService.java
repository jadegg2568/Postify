package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.config.SessionProperties;
import ru.jadegg2568.Postify.data.DeviceData;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.exception.auth.SessionNotFoundException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionProperties sessionProperties;
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final TokenManager tokenManager;

    @Transactional
    public Session generateSession(User user, DeviceData deviceData) {
        UUID uuid = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(sessionProperties.getRefreshExpiration());

        String browser = deviceData.browser();
        String os = deviceData.os();

        Session session = Session.builder()
                .uuid(uuid)
                .user(user)
                .browser(browser)
                .os(os)
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
                user.getUuid()
        );
    }

    public String generateRefreshToken(Session session) {
        return tokenManager.generateRefreshToken(session.getUser().getUuid(), session.getUuid());
    }

    @Transactional
    public void revokeSession(UUID userUuid, UUID uuid) {
        Session session = sessionRepository.findByDetailedInfo(userUuid, uuid)
                .orElseThrow(SessionNotFoundException::new);
        session.setCancelled(true);
    }

    @Transactional
    public void revokeSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        List<Session> sessions = sessionRepository.findByUserId(user.getId());
        sessions.forEach(s -> s.setCancelled(true));
    }

    @Transactional
    public void clearExpiredSessions() {
        int limit = sessionProperties.getCleanup().getSize();

        List<Long> ids = sessionRepository.findExpiredIds(Instant.now(), PageRequest.of(0, limit));
        sessionRepository.deleteByIds(ids);
        log.info("Deleted {} expired sessions", ids.size());
    }

    @Transactional(readOnly = true)
    public Session getSession(UUID userUuid, UUID sessionUuid) {
        return sessionRepository.findByDetailedInfo(userUuid, sessionUuid)
                .orElseThrow(SessionNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Session> getSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        return sessionRepository.findByUserId(user.getId());
    }
}