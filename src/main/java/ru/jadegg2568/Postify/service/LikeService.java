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
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public LikeResponse getLikes(UUID postUuid, boolean showUsers) {
        postRepository.findByUuid(postUuid)
                .orElseThrow(PostNotFoundException::new);

        long count = likeRepository.countByPost_Uuid(postUuid);
        if (!showUsers) {
            return new LikeResponse(count, null);
        }

        List<UserResponse> users = likeRepository.findByPost_UuidOrderByCreatedAtAsc(postUuid).stream()
                .map(Like::getUser)
                .map(this::toUserResponse)
                .toList();

        return new LikeResponse(count, users);
    }

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public Post like(UUID authUuid, UUID postUuid) {
        Post post = postRepository.findByUuid(postUuid)
                .orElseThrow(PostNotFoundException::new);

        User user = userService.getByUuid(authUuid);

        try {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            log.info("Post {} liked by user {}", postUuid, authUuid);

            // TODO: EDA event
            // notificationEventPublisher.publishLikeEvent(user, post);
        } catch (DataIntegrityViolationException ex) {
            log.debug("Post {} already liked by user {}", postUuid, authUuid);
        }

        return post;
    }

    @Transactional(noRollbackFor = EmptyResultDataAccessException.class)
    public Post unlike(UUID authUuid, UUID postUuid) {
        Post post = postRepository.findByUuid(postUuid)
                .orElseThrow(PostNotFoundException::new);

        User user = userService.getByUuid(authUuid);

        try {
            likeRepository.deleteById(new LikeId(post.getId(), user.getId()));
            log.info("Like removed from post {} by user {}", postUuid, authUuid);

            // TODO: EDA event
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Like not found for post {} by user {}", postUuid, authUuid);
        }

        return post;
    }

    private UserResponse toUserResponse(User user) {
        String avatarKey = user.getAvatarKey();
        String avatarUrl = (avatarKey != null) ? fileService.getPresignedUrl(avatarKey) : null;
        return userMapper.toResponse(user, avatarUrl);
    }
}
