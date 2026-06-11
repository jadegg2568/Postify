package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.file.FileUploadedEvent;
import ru.jadegg2568.Postify.event.post.PostCreatedEvent;
import ru.jadegg2568.Postify.event.post.PostDeletedEvent;
import ru.jadegg2568.Postify.event.post.PostUpdatedEvent;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.exception.post.PostNotFoundException;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.security.UuidUserDetails;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public boolean isAuthor(UUID postUuid, UUID actorUuid) {
        return postRepository.findByUuid(postUuid)
                .map(post -> post.getAuthor().getUuid().equals(actorUuid))
                .orElse(false);
    }

    @Transactional
    public Post create(UUID authUuid, PostCreateRequest request, UUID replyToUuid) {
        User author = userService.getByUuid(authUuid);

        Post replyTo = (replyToUuid != null ? postRepository.findByUuid(replyToUuid)
                .orElseThrow(PostNotFoundException::new) : null);

        Post post = postMapper.toEntity(request);
        post.setAuthor(author);
        post.setReplyTo(replyTo);

        Post created = postRepository.save(post);
        eventPublisher.publishEvent(new PostCreatedEvent(author, created));
        log.info("Post created: {} by {}", post.getUuid(), author.getUuid());
        return created;
    }

    @Transactional
    public Post update(UUID authUuid, UUID uuid, PostUpdateRequest request) {
        User user = userService.getByUuid(authUuid);
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);

        requireOwningOrAdmin(user, post);
        postMapper.updateEntity(request, post);
        eventPublisher.publishEvent(new PostUpdatedEvent(user, post));

        log.info("Post updated: {} by {}", uuid, authUuid);
        return post;
    }

    @Transactional
    public void delete(UUID authUuid, UUID uuid) {
        User user = userService.getByUuid(authUuid);
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);

        requireOwningOrAdmin(user, post);
        postRepository.delete(post);
        eventPublisher.publishEvent(new PostDeletedEvent(user, post));

        log.info("Post deleted: {} by {}", uuid, authUuid);
    }

    @Transactional(readOnly = true)
    public Post getByUuid(UUID uuid) {
        return postRepository.findByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<Post> searchByTitle(String title, int length) {
        return postRepository.findByTitleContainingIgnoreCase(title, PageRequest.of(0, length));
    }

    public Page<Post> find(int length) {
        return postRepository.findAll(PageRequest.of(0, length));
    }

    private static void requireOwningOrAdmin(User user, Post post) {
        UUID actorUuid = user.getUuid();
        UUID ownerUuid = post.getAuthor().getUuid();

        boolean isAdmin = user.getPermissions().isAdmin();

        if (!actorUuid.equals(ownerUuid) && !isAdmin) {
            throw new NoAccessException();
        }
    }
}
