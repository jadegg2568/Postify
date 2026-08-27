package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class PostDeletedEvent extends PostEvent {
    public PostDeletedEvent(User user, Post post) {
        super(EventType.POST_DELETED, user, post);
    }
}