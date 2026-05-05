package ru.jadegg2568.Postify.exception.post;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class PostNotFoundException extends ApiException {
    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "Post not found");
    }
}

