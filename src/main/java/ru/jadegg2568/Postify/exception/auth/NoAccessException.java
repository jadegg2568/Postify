package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class NoAccessException extends ApiException {
    public NoAccessException() {
        super(HttpStatus.FORBIDDEN, "NO_ACCESS", "No access");
    }
}
