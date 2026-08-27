package ru.jadegg2568.Postify.file.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public abstract class FileException extends ApiException {
    protected FileException(String message) {
        super(HttpStatus.BAD_REQUEST, "FILE_ERROR", message);
    }
}