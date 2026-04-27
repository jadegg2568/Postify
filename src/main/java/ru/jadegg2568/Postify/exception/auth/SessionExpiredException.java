package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class SessionExpiredException extends ApiException {
    public SessionExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "Session expired, please authorize and get new");
    }
}
