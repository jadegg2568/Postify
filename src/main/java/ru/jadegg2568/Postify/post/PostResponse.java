package ru.jadegg2568.Postify.post;

import ru.jadegg2568.Postify.user.UserResponse;

import java.time.Instant;
import java.util.UUID;

public record PostResponse(
        UUID uuid,
        UUID replyToUuid,
        String title,
        String content,
        long views,
        Instant createdAt,
        UserResponse author
) {
}

