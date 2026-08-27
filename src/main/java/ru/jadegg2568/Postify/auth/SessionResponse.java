package ru.jadegg2568.Postify.auth;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID uuid,
        String browser,
        String os,
        boolean cancelled,
        Instant expiresAt,
        Instant createdAt
) {
}
