package ru.jadegg2568.Postify.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.post.event.PostViewedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private PostViewConfig postViewConfig;

    @InjectMocks
    private ViewService viewService;

    @Test
    @DisplayName("viewedPost - publishes PostViewedEvent")
    void viewedPost_ShouldPublishEvent() {
        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        User user = User.builder().id(1L).uuid(userUuid).name("viewer").build();
        Post post = Post.builder().id(2L).uuid(postUuid).title("t").content("c").build();

        viewService.viewedPost(user, post);

        ArgumentCaptor<PostViewedEvent> captor = ArgumentCaptor.forClass(PostViewedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        PostViewedEvent event = captor.getValue();

        // ✅ Исправлено: сравниваем UUID с UUID
        assertThat(event.getActorId()).isEqualTo(userUuid);
        assertThat(event.getPost().getUuid()).isEqualTo(postUuid);
        assertThat(event.getUser()).isSameAs(user);
        assertThat(event.getPost()).isSameAs(post);
    }

    @Test
    @DisplayName("clearOldViews - deletes records older than configured age")
    void clearOldViews_ShouldDeleteRecordsOlderThanConfiguredAge() {
        Duration expiration = Duration.ofHours(24);
        when(postViewConfig.getCleanup().getExpiration()).thenReturn(expiration);
        when(postViewRepository.deleteByCreatedAtBefore(any())).thenReturn(3);

        int deleted = viewService.clearOldViews();

        assertThat(deleted).isEqualTo(3);

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(postViewRepository).deleteByCreatedAtBefore(captor.capture());

        Instant cutoff = captor.getValue();
        Instant expectedCutoff = Instant.now().minus(expiration);

        assertThat(cutoff).isBetween(
                expectedCutoff.minusSeconds(2),
                expectedCutoff.plusSeconds(2)
        );
    }
}