package ru.jadegg2568.Postify.exception;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApiException {
    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "Post not found");
    }
}

