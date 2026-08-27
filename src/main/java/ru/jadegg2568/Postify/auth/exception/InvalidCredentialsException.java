package ru.jadegg2568.Postify.auth.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.common.exception.ApiException;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
            super(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "Invalid credentials");
    }
}
