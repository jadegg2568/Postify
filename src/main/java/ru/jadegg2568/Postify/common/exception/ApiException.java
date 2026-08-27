package ru.jadegg2568.Postify.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private String message;
}
