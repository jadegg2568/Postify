package ru.jadegg2568.Postify.exception.user;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "User already exists");
    }
}
