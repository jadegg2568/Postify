package ru.jadegg2568.Postify.auth.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.common.exception.ApiException;

public class SessionMismatchException extends ApiException {
    public SessionMismatchException() {
        super(HttpStatus.BAD_REQUEST, "SESSION_MISMATCH", "Session mismatch with given refresh token");
    }
}
