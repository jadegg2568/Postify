package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
            super(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "Invalid credentials");
    }
}
