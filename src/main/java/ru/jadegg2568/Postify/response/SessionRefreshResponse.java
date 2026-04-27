package ru.jadegg2568.Postify.response;

public record SessionRefreshResponse(String newToken, UserResponse data) {
}
