package ru.jadegg2568.Postify.auth;

import ru.jadegg2568.Postify.user.UserResponse;

import java.util.UUID;

public record AuthResponse(String refreshToken,
                           String token,
                           UUID uuid,
                           SessionResponse session,
                           UserResponse data) {
}
