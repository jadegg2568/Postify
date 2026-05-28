package ru.jadegg2568.Postify.response;

import java.util.UUID;

public record AuthResponse(String refreshToken,
                           String token,
                           UUID uuid,
                           SessionResponse session,
                           UserResponse data) {
}
