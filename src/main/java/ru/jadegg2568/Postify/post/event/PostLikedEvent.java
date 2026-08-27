package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class PostLikedEvent extends PostEvent {
    public PostLikedEvent(User liker, Post post) {
        super(EventType.POST_LIKED, liker, post);
    }
}