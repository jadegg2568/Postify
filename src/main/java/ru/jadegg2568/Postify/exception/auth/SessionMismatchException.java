package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class SessionMismatchException extends ApiException {
    public SessionMismatchException() {
        super(HttpStatus.BAD_REQUEST, "SESSION_MISMATCH", "Session mismatch with given refresh token");
    }
}
