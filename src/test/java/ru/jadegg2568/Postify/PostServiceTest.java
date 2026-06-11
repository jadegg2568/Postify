package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.exception.post.PostNotFoundException;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.repository.PostRepository;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.service.PostService;
import ru.jadegg2568.Postify.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @Mock private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("create - creates post without reply")
    void create_ShouldCreatePost_WhenNoReplyProvided() {
        UUID authorUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        PostCreateRequest request = new PostCreateRequest("title", "content");

        when(userService.getByUuid(authorUuid)).thenReturn(author);
        Post mapped = Post.builder().title("title").content("content").build();
        when(postMapper.toEntity(request)).thenReturn(mapped);
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setUuid(postUuid);
            return p;
        });

        Post result = postService.create(authorUuid, request, null);

        assertThat(result.getUuid()).isEqualTo(postUuid);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getReplyTo()).isNull();
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContent()).isEqualTo("content");

        verify(postMapper).toEntity(request);
        verify(postRepository, never()).findByUuid(any());
    }

    @Test
    @DisplayName("create - creates reply post when reply uuid provided")
    void create_ShouldCreateReply_WhenReplyProvided() {
        UUID authorUuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        UUID replyUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        User otherUser = User.builder().id(2L).uuid(otherUuid).name("other").build();

        PostCreateRequest request = new PostCreateRequest("title", "content");

        Post replyTo = Post.builder()
                .id(10L)
                .uuid(replyUuid)
                .author(otherUser)
                .title("parent")
                .content("parent content")
                .build();

        when(userService.getByUuid(authorUuid)).thenReturn(author);
        when(postRepository.findByUuid(replyUuid)).thenReturn(Optional.of(replyTo));
        Post mapped = Post.builder().title("title").content("content").build();
        when(postMapper.toEntity(request)).thenReturn(mapped);
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setUuid(postUuid);
            return p;
        });

        Post result = postService.create(authorUuid, request, replyUuid);

        assertThat(result.getUuid()).isEqualTo(postUuid);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getReplyTo()).isEqualTo(replyTo);
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContent()).isEqualTo("content");

        verify(postMapper).toEntity(request);
        verify(postRepository).findByUuid(replyUuid);
    }

    @Test
    @DisplayName("create - throws when reply post not found")
    void create_ShouldThrow_WhenReplyNotFound() {
        UUID authorUuid = UUID.randomUUID();
        UUID replyUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        PostCreateRequest request = new PostCreateRequest("title", "content");

        when(userService.getByUuid(authorUuid)).thenReturn(author);
        when(postRepository.findByUuid(replyUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.create(authorUuid, request, replyUuid))
                .isInstanceOf(PostNotFoundException.class);

        verify(postMapper, never()).toEntity(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("getByUuid - returns post when exists")
    void getByUuid_ShouldReturnPost_WhenExists() {
        UUID authorUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        Post post = Post.builder().uuid(postUuid).author(author).title("t").content("c").build();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        Post result = postService.getByUuid(postUuid);

        assertThat(result).isEqualTo(post);
    }

    @Test
    @DisplayName("getByUuid - throws when post not found")
    void getByUuid_ShouldThrow_WhenNotFound() {
        UUID postUuid = UUID.randomUUID();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getByUuid(postUuid))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("update - allows author to update")
    void update_ShouldUpdate_WhenAuthor() {
        UUID authorUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder()
                .id(1L)
                .uuid(authorUuid)
                .name("author")
                .permissions(Permissions.USER)
                .build();
        Post post = Post.builder()
                .uuid(postUuid)
                .author(author)
                .title("old")
                .content("oldc")
                .build();

        when(userService.getByUuid(authorUuid)).thenReturn(author);
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest("new", "newc");
        doAnswer(inv -> {
            PostUpdateRequest req = inv.getArgument(0);
            Post entity = inv.getArgument(1);
            entity.setTitle(req.title());
            entity.setContent(req.content());
            return null;
        }).when(postMapper).updateEntity(eq(request), eq(post));

        Post result = postService.update(authorUuid, postUuid, request);

        assertThat(result.getTitle()).isEqualTo("new");
        assertThat(result.getContent()).isEqualTo("newc");
        verify(postMapper).updateEntity(eq(request), eq(post));
    }

    @Test
    @DisplayName("update - forbids non-author non-admin")
    void update_ShouldThrowNoAccess_WhenNotOwner() {
        UUID authorUuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        User other = User.builder()
                .id(2L)
                .uuid(otherUuid)
                .name("other")
                .permissions(Permissions.USER)
                .build();
        Post post = Post.builder().uuid(postUuid).author(author).title("old").content("oldc").build();

        when(userService.getByUuid(otherUuid)).thenReturn(other);
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest("new", "newc");
        assertThatThrownBy(() -> postService.update(otherUuid, postUuid, request))
                .isInstanceOf(NoAccessException.class);

        verify(postMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("update - allows admin to update чужой пост")
    void update_ShouldAllowAdmin_WhenNotOwner() {
        UUID authorUuid = UUID.randomUUID();
        UUID adminUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        User admin = User.builder()
                .id(2L)
                .uuid(adminUuid)
                .name("admin")
                .permissions(Permissions.ADMIN)
                .build();
        Post post = Post.builder().uuid(postUuid).author(author).title("old").content("oldc").build();

        when(userService.getByUuid(adminUuid)).thenReturn(admin);
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest("new", null);
        doAnswer(inv -> {
            PostUpdateRequest req = inv.getArgument(0);
            Post entity = inv.getArgument(1);
            if (req.title() != null) entity.setTitle(req.title());
            if (req.content() != null) entity.setContent(req.content());
            return null;
        }).when(postMapper).updateEntity(eq(request), eq(post));

        Post result = postService.update(adminUuid, postUuid, request);

        assertThat(result.getTitle()).isEqualTo("new");
        assertThat(result.getContent()).isEqualTo("oldc");
        verify(postMapper).updateEntity(eq(request), eq(post));
    }

    @Test
    @DisplayName("delete - allows author to delete")
    void delete_ShouldDelete_WhenAuthor() {
        UUID authorUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder()
                .id(1L)
                .uuid(authorUuid)
                .name("author")
                .permissions(Permissions.USER)
                .build();
        Post post = Post.builder().uuid(postUuid).author(author).title("t").content("c").build();

        when(userService.getByUuid(authorUuid)).thenReturn(author);
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        postService.delete(authorUuid, postUuid);

        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("delete - forbids non-author non-admin")
    void delete_ShouldThrowNoAccess_WhenNotOwner() {
        UUID authorUuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User author = User.builder().id(1L).uuid(authorUuid).name("author").build();
        User other = User.builder()
                .id(2L)
                .uuid(otherUuid)
                .name("other")
                .permissions(Permissions.USER)
                .build();
        Post post = Post.builder().uuid(postUuid).author(author).title("t").content("c").build();

        when(userService.getByUuid(otherUuid)).thenReturn(other);
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.delete(otherUuid, postUuid))
                .isInstanceOf(NoAccessException.class);

        verify(postRepository, never()).delete(any());
    }
}
