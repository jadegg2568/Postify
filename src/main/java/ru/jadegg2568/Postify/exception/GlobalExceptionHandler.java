package ru.jadegg2568.Postify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
            Instant timestamp,
            HttpStatus status,
            String code,
            String message,
            String traceId
    ) {
        public ApiError(HttpStatus status, String code, String message) {
            this(
                    Instant.now(),
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString()
            );
        }
        public ApiError(ApiException apiException) {
            this(
                    Instant.now(),
                    apiException.getHttpStatus(),
                    apiException.getCode(),
                    apiException.getMessage(),
                    UUID.randomUUID().toString()
            );
        }
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException apiException) {
        ApiError error = new ApiError(apiException);
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handle404(NoResourceFoundException exception) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, "NOT_FOUND", "Not found for that path");
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalError(Exception exception) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_OCCURED", "Something went wrong");
        return new ResponseEntity<>(error, error.status());
    }


}
