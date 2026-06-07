package ru.jadegg2568.Postify.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.service.ViewService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OldPostViewCleanupTask {
    private final ViewService viewService;

    @Scheduled(fixedDelayString = "#{@viewProperties.cleanupDelay.toMillis()}")
    public void cleanupOldPostViews() {
        viewService.clearOldViews();
    }
}
