package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.config.ViewProperties;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.PostViewedEvent;
import ru.jadegg2568.Postify.repository.PostViewRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewService {
    private final ApplicationEventPublisher eventPublisher;
    private final PostViewRepository postViewRepository;
    private final ViewProperties viewProperties;

    public void viewedPost(User user, Post post) {
        eventPublisher.publishEvent(new PostViewedEvent(user, post));
    }

    @Transactional
    public int clearOldViews() {
        Instant cutoff = Instant.now().minus(viewProperties.getRetentionAge());
        int deleted = postViewRepository.deleteByCreatedAtBefore(cutoff);
        if (deleted > 0) {
            log.info("Deleted {} stale post view records older than {} hours",
                    deleted, viewProperties.getRetentionAge().toHours());
        }
        return deleted;
    }
}