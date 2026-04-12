package ru.jadegg2568.Postify.exception.user;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");
    }
}
