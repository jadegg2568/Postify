package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.jadegg2568.Postify.entity.Like;
import ru.jadegg2568.Postify.entity.LikeId;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.repository.LikeRepository;
import ru.jadegg2568.Postify.service.FileService;
import ru.jadegg2568.Postify.service.LikeService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private LikeService likeService;

    @Mock private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("getLikesCount - returns count from repository")
    void getLikesCount_ShouldReturnCount() {
        Post post = Post.builder().uuid(UUID.randomUUID()).title("t").content("c").build();

        when(likeRepository.countByPost(post)).thenReturn(3L);

        long count = likeService.getLikesCount(post);

        assertThat(count).isEqualTo(3L);
        verify(likeRepository).countByPost(post);
    }

    @Test
    @DisplayName("getLikedUsers - returns users from likes ordered by created_at")
    void getLikedUsers_ShouldReturnUsers() {
        UUID userUuid = UUID.randomUUID();
        Post post = Post.builder().uuid(UUID.randomUUID()).title("t").content("c").build();
        User user = User.builder().id(1L).uuid(userUuid).name("liker").build();
        Like like = new Like();
        like.setUser(user);

        when(likeRepository.findByPostOrderByCreatedAtAsc(post)).thenReturn(List.of(like));

        List<User> users = likeService.getLikedUsers(post);

        assertThat(users).containsExactly(user);
        verify(likeRepository).findByPostOrderByCreatedAtAsc(post);
    }

    @Test
    @DisplayName("like - saves like for post and user")
    void like_ShouldSaveLike_WhenNotExists() {
        User user = User.builder().id(10L).uuid(UUID.randomUUID()).name("liker").build();
        Post post = Post.builder().id(20L).uuid(UUID.randomUUID()).title("t").content("c").build();

        when(likeRepository.save(any(Like.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = likeService.like(user, post);

        assertThat(result).isEqualTo(post);

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        assertThat(captor.getValue().getPost()).isEqualTo(post);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("like - ignores duplicate like and returns post")
    void like_ShouldIgnoreDuplicate_WhenAlreadyLiked() {
        User user = User.builder().id(10L).uuid(UUID.randomUUID()).name("liker").build();
        Post post = Post.builder().id(20L).uuid(UUID.randomUUID()).title("t").content("c").build();

        when(likeRepository.save(any(Like.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        Post result = likeService.like(user, post);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("unlike - deletes like by composite id")
    void unlike_ShouldDeleteLike_WhenExists() {
        User user = User.builder().id(10L).uuid(UUID.randomUUID()).name("liker").build();
        Post post = Post.builder().id(20L).uuid(UUID.randomUUID()).title("t").content("c").build();

        Post result = likeService.unlike(user, post);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).deleteById(new LikeId(20L, 10L));
    }

    @Test
    @DisplayName("unlike - ignores missing like and returns post")
    void unlike_ShouldIgnoreMissingLike_WhenNotLiked() {
        User user = User.builder().id(10L).uuid(UUID.randomUUID()).name("liker").build();
        Post post = Post.builder().id(20L).uuid(UUID.randomUUID()).title("t").content("c").build();

        doThrow(new EmptyResultDataAccessException(1))
                .when(likeRepository).deleteById(new LikeId(20L, 10L));

        Post result = likeService.unlike(user, post);

        assertThat(result).isEqualTo(post);
        verify(likeRepository).deleteById(new LikeId(20L, 10L));
    }
}
