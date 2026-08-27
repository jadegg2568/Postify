package ru.jadegg2568.Postify.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.post.event.PostViewedEvent;
import ru.jadegg2568.Postify.user.UserRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostViewListenerTest {

    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostViewListener listener;

    @Test
    @DisplayName("onPostViewed - saves view and increments counter")
    void onPostViewed_ShouldSaveViewAndIncrementCounter() {
        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        User user = User.builder().id(10L).uuid(userUuid).name("viewer").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();
        PostViewedEvent event = new PostViewedEvent(user, post, "COMMON");

        // ✅ Ставим стабы только на то, что реально используется
        when(postViewRepository.save(any(PostView.class))).thenAnswer(inv -> inv.getArgument(0));

        listener.onPostViewed(event);

        verify(postViewRepository).save(any(PostView.class));
        verify(postRepository).incrementViews(20L);
    }

    @Test
    @DisplayName("onPostViewed - ignores duplicate view within retention window")
    void onPostViewed_ShouldIgnoreDuplicateView() {
        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        User user = User.builder().id(10L).uuid(userUuid).name("viewer").build();
        Post post = Post.builder().id(20L).uuid(postUuid).title("t").content("c").build();
        PostViewedEvent event = new PostViewedEvent(user, post, "COMMON");

        // ✅ Ставим стаб только на save, который бросит исключение
        when(postViewRepository.save(any(PostView.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        listener.onPostViewed(event);

        verify(postViewRepository).save(any(PostView.class));
        verify(postRepository, never()).incrementViews(any());
    }
}