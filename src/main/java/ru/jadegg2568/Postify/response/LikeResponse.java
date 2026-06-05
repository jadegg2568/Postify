package ru.jadegg2568.Postify.response;

import java.util.List;

public record LikeResponse(
        long count,
        List<UserResponse> users
) {
}
