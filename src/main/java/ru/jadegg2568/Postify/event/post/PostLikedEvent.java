package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class PostLikedEvent extends PostEvent {
    public PostLikedEvent(User liker, Post post) {
        super(EventType.POST_LIKED, liker, post);
    }
}