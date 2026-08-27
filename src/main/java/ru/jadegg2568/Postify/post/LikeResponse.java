package ru.jadegg2568.Postify.post;

import ru.jadegg2568.Postify.user.UserResponse;

import java.util.List;

public record LikeResponse(
        long count,
        List<UserResponse> users
) {
}
