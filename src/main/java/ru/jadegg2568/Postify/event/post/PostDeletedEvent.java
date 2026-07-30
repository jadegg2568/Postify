package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

import java.util.UUID;

@Getter
public class PostDeletedEvent extends PostEvent {
    public PostDeletedEvent(User user, Post post) {
        super(EventType.POST_DELETED, user, post);
    }
}