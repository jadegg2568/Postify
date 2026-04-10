package ru.jadegg2568.Postify.exception.auth;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

import java.util.Map;

public class ConflictingCredentialsException extends ApiException {
    public ConflictingCredentialsException() {
        super(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "Conflicting credentials");
    }
}
