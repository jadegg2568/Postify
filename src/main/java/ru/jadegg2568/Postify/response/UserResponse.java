package ru.jadegg2568.Postify.response;

import java.util.UUID;

public record UserResponse(UUID uuid,
                           String name,
                           String displayName,
                           String description,
                           String avatarUrl) {
}
