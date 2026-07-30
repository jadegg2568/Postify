package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

import java.util.UUID;

@Getter
public class PostUnlikedEvent extends PostEvent {
    public PostUnlikedEvent(User user, Post post) {
        super(EventType.POST_UNLIKED, user, post);
    }
}