package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.config.ViewProperties;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.PostViewedEvent;
import ru.jadegg2568.Postify.repository.PostViewRepository;
import ru.jadegg2568.Postify.service.ViewService;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewServiceTest {

    private static final Duration VIEW_AGE = Duration.ofHours(24);

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private ViewProperties viewProperties;

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
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.postId()).isEqualTo(2L);
        assertThat(event.userUuid()).isEqualTo(userUuid);
        assertThat(event.postUuid()).isEqualTo(postUuid);
    }

    @Test
    @DisplayName("clearOldViews - deletes records older than configured age")
    void clearOldViews_ShouldDeleteRecordsOlderThanConfiguredAge() {
        // given
        Duration duration = Duration.ofHours(24);
        when(viewProperties.getExpiration()).thenReturn(duration);
        when(postViewRepository.deleteByCreatedAtBefore(any())).thenReturn(3);

        // when
        int deleted = viewService.clearOldViews();

        // then
        assertThat(deleted).isEqualTo(3);

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(postViewRepository).deleteByCreatedAtBefore(captor.capture());

        Instant cutoff = captor.getValue();
        Instant expectedCutoff = Instant.now().minus(duration);

        assertThat(cutoff).isBetween(
                expectedCutoff.minusSeconds(2),
                expectedCutoff.plusSeconds(2)
        );
    }

}
