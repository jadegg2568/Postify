package ru.jadegg2568.Postify.event.post;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;

import java.util.UUID;

@Getter
public class PostCreatedEvent extends PostEvent {
    private final String title;

    public PostCreatedEvent(User user, Post post) {
        super(user, post);
        this.title = post.getTitle();
    }
}