package ru.jadegg2568.Postify.response;

import java.util.UUID;

public record TokenResponse(String token, UUID uuid, UserResponse data) {
}
