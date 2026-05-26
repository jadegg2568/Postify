package ru.jadegg2568.Postify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.exception.post.PostNotFoundException;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.repository.UserRepository;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.security.UuidUserDetails;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public Post create(UuidUserDetails details, PostCreateRequest request, UUID replyToUuid) {
        User author = userService.getByUuid(details.uuid());

        Post replyTo = (replyToUuid != null ? postRepository.findByUuid(replyToUuid)
                .orElseThrow(PostNotFoundException::new) : null);

        Post post = postMapper.toEntity(request);
        post.setAuthor(author);
        post.setReplyTo(replyTo);

        postRepository.save(post);
        log.info("Post created: {} by {}", post.getUuid(), author.getUuid());
        return post;
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

    @Transactional
    public Post update(UuidUserDetails details, UUID uuid, PostUpdateRequest request) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);

        requireOwnerOrAdmin(details, post);
        postMapper.updateEntity(request, post);

        log.info("Post updated: {} by {}", uuid, details.uuid());
        return post;
    }

    @Transactional
    public void delete(UuidUserDetails details, UUID uuid) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);

        requireOwnerOrAdmin(details, post);
        postRepository.delete(post);
        log.info("Post deleted: {} by {}", uuid, details.uuid());
    }

    private static void requireOwnerOrAdmin(UuidUserDetails details, Post post) {
        UUID actorUuid = details.uuid();
        UUID ownerUuid = post.getAuthor().getUuid();

        boolean isAdmin = details.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin && !actorUuid.equals(ownerUuid)) {
            throw new NoAccessException();
        }
    }
}
