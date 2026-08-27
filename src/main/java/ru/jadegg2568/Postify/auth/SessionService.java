package ru.jadegg2568.Postify.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.parse.Device;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.user.event.UserLoggedEvent;
import ru.jadegg2568.Postify.user.event.UserSessionRevokedEvent;
import ru.jadegg2568.Postify.user.event.UserSessionsRevokedEvent;
import ru.jadegg2568.Postify.auth.exception.SessionExpiredException;
import ru.jadegg2568.Postify.auth.exception.SessionMismatchException;
import ru.jadegg2568.Postify.auth.exception.SessionNotFoundException;
import ru.jadegg2568.Postify.security.TokenManager;
import ru.jadegg2568.Postify.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final TokenManager tokenManager;
    private final ApplicationEventPublisher eventPublisher;
    private final SessionConfig sessionConfig;

    @Transactional
    public Session generateSession(User user, Device device) {
        UUID uuid = UUID.randomUUID();

        String browser = device.browser();
        String os = device.os();

        Session session = Session.builder()
                .uuid(uuid)
                .user(user)
                .browser(browser)
                .os(os)
                .build();

        log.debug("Generated user session: {} for user {}", uuid, user.getUuid());

        eventPublisher.publishEvent(new UserLoggedEvent(user, null,
                String.format("%s on %s", device.browser(), device.os())));

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
        eventPublisher.publishEvent(new UserSessionRevokedEvent(session.getUser(), session));
    }

    @Transactional
    public void revokeSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        List<Session> sessions = sessionRepository.findByUserId(user.getId());
        sessions.forEach(s -> s.setCancelled(true));
        eventPublisher.publishEvent(new UserSessionsRevokedEvent(user));
    }

    @Transactional
    public void clearExpiredSessions() {
        Instant cutoff = Instant.now().minus(sessionConfig.getCleanup().getExpiration());
        int limit = sessionConfig.getCleanup().getSize();

        long deleted = sessionRepository.deleteExpired(cutoff, PageRequest.of(0, limit));
        log.info("Deleted {} expired sessions", deleted);
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