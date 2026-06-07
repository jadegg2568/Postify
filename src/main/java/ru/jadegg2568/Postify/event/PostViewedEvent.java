package ru.jadegg2568.Postify.event;

import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;

import java.util.UUID;

public record PostViewedEvent(
        Long userId,
        Long postId,
        UUID userUuid,
        UUID postUuid
) {
    public PostViewedEvent(User user, Post post) {
        this(user.getId(), post.getId(), user.getUuid(), post.getUuid());
    }
}
