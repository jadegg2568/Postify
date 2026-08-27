package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class PostUpdatedEvent extends PostEvent {
    public PostUpdatedEvent(User user, Post newPost) {
        super(EventType.POST_UPDATED, user, newPost);
    }
}