package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.BaseEvent;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public abstract class PostEvent extends BaseEvent {
    private final User user;
    private final Post post;

    protected PostEvent(EventType type, User user, Post post) {
        super(type, user.getUuid());
        this.user = user;
        this.post = post;
    }
}