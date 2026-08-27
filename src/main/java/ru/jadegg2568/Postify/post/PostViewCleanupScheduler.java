package ru.jadegg2568.Postify.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewCleanupScheduler {
    private final ViewService viewService;

    @Scheduled(fixedDelayString = "${app.views.cleanup.delay}")
    public void cleanupOldPostViews() {
        viewService.clearOldViews();
    }
}
