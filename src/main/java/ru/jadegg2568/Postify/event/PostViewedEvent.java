package ru.jadegg2568.Postify.event;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.post.PostEvent;

import java.util.UUID;

@Getter
public class PostViewedEvent extends PostEvent {
    private final UUID viewerUuid;
    private final String source;

    public PostViewedEvent(User user, Post post, String source) {
        super(user, post);
        this.viewerUuid = user.getUuid();
        this.source = source;
    }
}