package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

import java.util.UUID;

@Getter
public class PostUpdatedEvent extends PostEvent {
    public PostUpdatedEvent(User user, Post newPost) {
        super(EventType.POST_UPDATED, user, newPost);
    }
}