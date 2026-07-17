package ru.jadegg2568.Postify.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.jadegg2568.Postify.service.SessionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredSessionCleanupTask {
    private final SessionService sessionService;

    @Scheduled(fixedDelayString = "${app.session.cleanup.delay}")
    public void cleanupExpiredSessions() {
        sessionService.clearExpiredSessions();
    }
}
