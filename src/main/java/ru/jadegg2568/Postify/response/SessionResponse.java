package ru.jadegg2568.Postify.response;

import java.util.UUID;

public record SessionResponse(String refreshToken, String token, UUID uuid, UserResponse data) {
}
