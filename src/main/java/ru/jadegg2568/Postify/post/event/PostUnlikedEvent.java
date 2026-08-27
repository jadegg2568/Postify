package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class PostUnlikedEvent extends PostEvent {
    public PostUnlikedEvent(User user, Post post) {
        super(EventType.POST_UNLIKED, user, post);
    }
}