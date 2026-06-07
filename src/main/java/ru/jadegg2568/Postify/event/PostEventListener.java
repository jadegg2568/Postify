package ru.jadegg2568.Postify.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.PostView;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.repository.PostViewRepository;
import ru.jadegg2568.Postify.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final PostViewRepository postViewRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @EventListener
    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public void onPostViewed(PostViewedEvent event) {
        try {
            Post post = postRepository.getReferenceById(event.postId());
            User user = userRepository.getReferenceById(event.userId());

            PostView view = new PostView();
            view.setPost(post);
            view.setUser(user);
            postViewRepository.save(view);

            postRepository.incrementViews(event.postId());
            log.info("Post {} viewed by user {}", event.postUuid(), event.userUuid());
        } catch (DataIntegrityViolationException ex) {
            log.debug("Post {} already viewed by user {} within retention window",
                    event.postUuid(), event.userUuid());
        }
    }
}
