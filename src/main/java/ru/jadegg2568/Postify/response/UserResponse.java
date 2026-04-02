package ru.jadegg2568.Postify.response;

import java.util.UUID;

public record UserResponse(UUID uuid,
                           String name,
                           String title,
                           String description) {
}
