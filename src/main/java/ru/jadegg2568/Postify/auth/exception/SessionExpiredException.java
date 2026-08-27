package ru.jadegg2568.Postify.auth.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.common.exception.ApiException;

public class SessionExpiredException extends ApiException {
    public SessionExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "Session expired, please authorize and get new");
    }
}
