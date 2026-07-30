package ru.jadegg2568.Postify.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.PostView;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.post.PostViewedEvent;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.repository.PostViewRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewListener {
    private final PostViewRepository postViewRepository;
    private final PostRepository postRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostViewed(PostViewedEvent event) {
        Post post = event.getPost();
        User user = event.getUser();
        try {
            if (event.getActorId() != null) {
                PostView view = PostView.builder()
                        .post(post)
                        .user(user)
                        .build();
                postViewRepository.save(view);
                postRepository.incrementViews(post.getId());
                log.info("Post {} unique view registered for user {}", post.getUuid(), user.getUuid());
            } else {
                log.info("Post {} viewed by anonymous guest", post.getUuid());
            }
        } catch (DataIntegrityViolationException ex) {
            log.debug("Post {} already viewed by user {} within retention window",
                    post.getUuid(), user.getUuid());
        }
    }
}
