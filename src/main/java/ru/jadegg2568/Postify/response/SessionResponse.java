package ru.jadegg2568.Postify.response;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID uuid,
        String title,
        boolean cancelled,
        Instant expiresAt,
        Instant createdAt
) {
}
