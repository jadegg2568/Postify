package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.SessionMismatchException;
import ru.jadegg2568.Postify.exception.auth.SessionExpiredException;
import ru.jadegg2568.Postify.exception.auth.SessionNotFoundException;
import ru.jadegg2568.Postify.repository.SessionRepository;
import ru.jadegg2568.Postify.security.TokenManager;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final TokenManager tokenManager;

    @Transactional
    public Session generateSession(User user) {
        UUID uuid = UUID.randomUUID();

        Session session = Session.builder()
                .uuid(uuid)
                .user(user)
                .title(uuid.toString())
                .build();

        log.debug("Generated user session: {} of {}", session.getUuid(), user.getUuid());
        return sessionRepository.save(session);
    }

    public List<Session> findUserSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        List<Session> sessions = sessionRepository.findByUserId(user.getId());
        log.debug("Found user sessions (last 5): {}", joinLastSessions(sessions));
        return sessions;
    }

    @Transactional
    public void cancelSession(UUID userUuid, UUID uuid) {
        Session session = sessionRepository.findByDetailedInfo(userUuid, uuid)
                        .orElseThrow(SessionNotFoundException::new);
        session.setCancelled(true);
        log.debug("Cancelled user session: {} of {}", session.getUuid(), userUuid);
    }

    @Transactional
    public void cancelUserSessions(UUID userUuid) {
        User user = userService.getByUuid(userUuid);
        List<Session> sessions = sessionRepository.findByUserId(user.getId());

        for (Session session1 : sessions) {
            session1.setCancelled(true);
        }
        log.debug("Cancelled user sessions: {} of {}", joinLastSessions(sessions), user.getUuid());
    }

    public String generateRefreshToken(Session session) {
        return tokenManager.generateRefreshToken(session.getUser().getUuid(),
                session.getUuid());
    }

    public String generateToken(User user, String refreshToken) {
        UUID sessionUuid = tokenManager.getClaim(refreshToken, "sid", UUID.class);
        UUID tokenUserUuid = UUID.fromString(tokenManager.getSubject(refreshToken));

        Session session = sessionRepository.findByUuid(sessionUuid)
                .orElseThrow(SessionNotFoundException::new);

        if (!session.getUser().getUuid().equals(tokenUserUuid)) {
            log.warn("Security alert: User {} tried to use session of user {}", tokenUserUuid, session.getUser().getUuid());
            throw new SessionMismatchException();
        }

        if (session.isCancelled()) {
            throw new SessionExpiredException();
        }

        return tokenManager.generateAccessToken(user.getUuid(), user.getPermissions().getAuthorities());
    }

    private static @NonNull String joinLastSessions(List<Session> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getCreatedAt).reversed()) // Сначала новые
                .limit(5)
                .map(s -> s.getUuid().toString())
                .collect(Collectors.joining(", "));
    }
}
