package ru.jadegg2568.Postify.auth.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.common.exception.ApiException;

public class NoAccessException extends ApiException {
    public NoAccessException() {
        super(HttpStatus.FORBIDDEN, "NO_ACCESS", "No access");
    }
}
