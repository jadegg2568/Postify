package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
import ru.jadegg2568.Postify.service.FileService;
import ru.jadegg2568.Postify.service.LikeService;
import ru.jadegg2568.Postify.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("getLikes - returns count only when showUsers is false")
    void getLikes_ShouldReturnCountOnly_WhenShowUsersFalse() {
        UUID postUuid = UUID.randomUUID();
        Post post = Post.builder().uuid(postUuid).title("t").content("c").build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.countByPost_Uuid(postUuid)).thenReturn(3L);

        LikeResponse response = likeService.getLikes(postUuid, false);

        assertThat(response.count()).isEqualTo(3L);
        assertThat(response.users()).isNull();
        verify(likeRepository, never()).findByPost_UuidOrderByCreatedAtAsc(any());
    }

    @Test
    @DisplayName("getLikes - returns count and users when showUsers is true")
    void getLikes_ShouldReturnUsers_WhenShowUsersTrue() {
        UUID postUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        Post post = Post.builder().uuid(postUuid).title("t").content("c").build();
        User user = User.builder()
                .id(1L)
                .uuid(userUuid)
                .name("liker")
                .avatarKey("avatar-key")
                .build();
        Like like = new Like();
        like.setUser(user);

        UserResponse userResponse = new UserResponse(userUuid, "liker", null, null, "avatar-url");

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.countByPost_Uuid(postUuid)).thenReturn(1L);
        when(likeRepository.findByPost_UuidOrderByCreatedAtAsc(postUuid)).thenReturn(List.of(like));
        when(fileService.getPresignedUrl("avatar-key")).thenReturn("avatar-url");
        when(userMapper.toResponse(user, "avatar-url")).thenReturn(userResponse);

        LikeResponse response = likeService.getLikes(postUuid, true);

        assertThat(response.count()).isEqualTo(1L);
        assertThat(response.users()).containsExactly(userResponse);
    }

    @Test
    @DisplayName("getLikes - throws when post not found")
    void getLikes_ShouldThrow_WhenPostNotFound() {
        UUID postUuid = UUID.randomUUID();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.getLikes(postUuid, false))
                .isInstanceOf(PostNotFoundException.class);

        verify(likeRepository, never()).countByPost_Uuid(any());
    }

    @Test
    @DisplayName("like - saves like for post and user")
    void like_ShouldSaveLike_WhenNotExists() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User user = User.builder().id(10L).uuid(authUuid).name("liker").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(userService.getByUuid(authUuid)).thenReturn(user);
        when(likeRepository.save(any(Like.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = likeService.like(authUuid, postUuid);

        assertThat(result).isEqualTo(post);

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        assertThat(captor.getValue().getPost()).isEqualTo(post);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("like - ignores duplicate like and returns post")
    void like_ShouldIgnoreDuplicate_WhenAlreadyLiked() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User user = User.builder().id(10L).uuid(authUuid).name("liker").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(userService.getByUuid(authUuid)).thenReturn(user);
        when(likeRepository.save(any(Like.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        Post result = likeService.like(authUuid, postUuid);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("like - throws when post not found")
    void like_ShouldThrow_WhenPostNotFound() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.like(authUuid, postUuid))
                .isInstanceOf(PostNotFoundException.class);

        verify(userService, never()).getByUuid(any());
        verify(likeRepository, never()).save(any());
    }

    @Test
    @DisplayName("unlike - deletes like by composite id")
    void unlike_ShouldDeleteLike_WhenExists() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User user = User.builder().id(10L).uuid(authUuid).name("liker").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(userService.getByUuid(authUuid)).thenReturn(user);

        Post result = likeService.unlike(authUuid, postUuid);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).deleteById(new LikeId(20L, 10L));
    }

    @Test
    @DisplayName("unlike - ignores missing like and returns post")
    void unlike_ShouldIgnoreMissingLike_WhenNotLiked() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        User user = User.builder().id(10L).uuid(authUuid).name("liker").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(userService.getByUuid(authUuid)).thenReturn(user);
        doThrow(new EmptyResultDataAccessException(1))
                .when(likeRepository).deleteById(new LikeId(20L, 10L));

        Post result = likeService.unlike(authUuid, postUuid);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).deleteById(new LikeId(20L, 10L));
    }

    @Test
    @DisplayName("unlike - throws when post not found")
    void unlike_ShouldThrow_WhenPostNotFound() {
        UUID authUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.unlike(authUuid, postUuid))
                .isInstanceOf(PostNotFoundException.class);

        verify(userService, never()).getByUuid(any());
        verify(likeRepository, never()).deleteById(any());
    }
}
