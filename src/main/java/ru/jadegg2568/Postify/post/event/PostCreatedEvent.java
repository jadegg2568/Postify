package ru.jadegg2568.Postify.post.event;

import lombok.Getter;
import ru.jadegg2568.Postify.post.Post;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class PostCreatedEvent extends PostEvent {
    private final String title;

    public PostCreatedEvent(User user, Post post) {
        super(EventType.POST_CREATED, user, post);
        this.title = post.getTitle();
    }
}