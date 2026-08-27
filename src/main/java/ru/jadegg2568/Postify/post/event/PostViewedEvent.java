package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.event.EventType;

import java.util.UUID;

@Getter
public class PostViewedEvent extends PostEvent {
    private final UUID viewerUuid;
    private final String source;

    public PostViewedEvent(User user, Post post, String source) {
        super(EventType.POST_VIEWED, user, post);
        this.viewerUuid = user.getUuid();
        this.source = source;
    }
}