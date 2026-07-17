package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.config.PostViewConfig;
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
    private final PostViewConfig postViewConfig;

    public void viewedPost(User user, Post post) {
        // TODO: Post category (source)
        eventPublisher.publishEvent(new PostViewedEvent(user, post, "COMMON"));
    }

    @Transactional
    public int clearOldViews() {
        Instant cutoff = Instant.now().minus(postViewConfig.getCleanup().getExpiration());
        int deleted = postViewRepository.deleteByCreatedAtBefore(cutoff);
        if (deleted > 0) {
            log.info("Deleted {} old post view records older than {} hours",
                    deleted, postViewConfig.getCleanup().getExpiration().toHours());
        }
        return deleted;
    }
}