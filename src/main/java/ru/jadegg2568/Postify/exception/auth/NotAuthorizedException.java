package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class NotAuthorizedException extends ApiException {
    public NotAuthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "NOT_AUTHORIZED", "Not authorized");
    }
}
