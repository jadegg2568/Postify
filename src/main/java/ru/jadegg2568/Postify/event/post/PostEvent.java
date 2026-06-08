package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

@Getter
public abstract class PostEvent extends BaseEvent {
    private final User user;
    private final Post post;

    protected PostEvent(User user, Post post) {
        super(user.getUuid());
        this.user = user;
        this.post = post;
    }
}