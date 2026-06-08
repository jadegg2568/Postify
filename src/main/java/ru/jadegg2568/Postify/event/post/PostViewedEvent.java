package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class PostViewedEvent extends PostEvent {
    public PostViewedEvent(User user, Post post) {
        super(user, post);
    }
}