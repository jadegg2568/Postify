package ru.jadegg2568.Postify.response;

import java.time.Instant;
import java.util.UUID;

public record PostResponse(
        UUID uuid,
        UUID replyToUuid,
        String title,
        String content,
        Instant createdAt,
        UserResponse author
) {
}

