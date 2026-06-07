package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Like;
import ru.jadegg2568.Postify.entity.LikeId;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.post.PostNotFoundException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.LikeRepository;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.response.LikeResponse;
import ru.jadegg2568.Postify.response.UserResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;
    private final FileService fileService;

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

            // TODO: EDA event
            // notificationEventPublisher.publishLikeEvent(user, post);
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

            // TODO: EDA event
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Like not found for post {} by user {}", post.getUuid(), user.getUuid());
        }

        return post;
    }
}
