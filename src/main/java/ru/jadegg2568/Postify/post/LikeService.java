package ru.jadegg2568.Postify.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.post.event.PostLikedEvent;
import ru.jadegg2568.Postify.post.event.PostUnlikedEvent;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public long getLikesCount(Post post) {
        return likeRepository.countByPost(post);
    }

    @Transactional(readOnly = true)
    public List<User> getLikedUsers(Post post) {
        return likeRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(Like::getUser)
                .toList();
    }

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public Post like(User user, Post post) {
        try {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            log.info("Post {} liked by user {}", post.getUuid(), user.getUuid());

            eventPublisher.publishEvent(new PostLikedEvent(user, post));

        } catch (DataIntegrityViolationException ex) {
            log.debug("Post {} already liked by user {}", post.getUuid(), user.getUuid());
        }

        return post;
    }

    @Transactional(noRollbackFor = EmptyResultDataAccessException.class)
    public Post unlike(User user, Post post) {
        try {
            likeRepository.deleteById(new LikeId(post.getId(), user.getId()));
            log.info("Like removed from post {} by user {}", post.getUuid(), user.getUuid());

            eventPublisher.publishEvent(new PostUnlikedEvent(user, post));

        } catch (EmptyResultDataAccessException ex) {
            log.debug("Like not found for post {} by user {}", post.getUuid(), user.getUuid());
        }

        return post;
    }
}
