package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class SessionNotFoundException extends ApiException {
    public SessionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", "Session not found");
    }
}