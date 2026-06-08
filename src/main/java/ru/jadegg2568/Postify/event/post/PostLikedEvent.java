package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class PostLikedEvent extends PostEvent {
    public PostLikedEvent(User liker, Post post) {
        super(liker, post);
    }
}