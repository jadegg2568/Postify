package ru.jadegg2568.Postify.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.jadegg2568.Postify.exception.param.ParamError;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            Instant timestamp,
            HttpStatus status,
            String code,
            String message,
            String traceId,
            Map<String, Object> details
    ) {
        public ErrorResponse(HttpStatus status, String code, String message) {
            this(
                    Instant.now(),
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    null
            );
        }
        public ErrorResponse(HttpStatus status, String code, String message, Map<String, Object> details) {
            this(
                    Instant.now(),
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    details
            );
        }
        public ErrorResponse(ApiException apiException) {
            this(
                    Instant.now(),
                    apiException.getHttpStatus(),
                    apiException.getCode(),
                    apiException.getMessage(),
                    UUID.randomUUID().toString(),
                    null
            );
        }
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        ErrorResponse error = new ErrorResponse(ex);
        log.warn("Api Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundPath(NoResourceFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Not found for that path");
        log.warn("NotFoundPath Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRequestMethod(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
        log.warn("IncorrectRequestMethod Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDatabaseConflictCredentials(DataIntegrityViolationException ex) {
        Map<String, Object> details = new HashMap<>();
        if (ex.getMessage().contains("mail"))
            details.put("mail", ParamError.BUSY);
        if (ex.getMessage().contains("name"))
            details.put("name", ParamError.BUSY);

        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT, "CREDENTIALS_CONFLICT", "Conflict credentials", details);
        log.error("DatabaseCredentialsConflict Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> details = new HashMap<>();

        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            try {
                details.put(fieldName, ParamError.valueOf(message));
            } catch (IllegalArgumentException | NullPointerException ex1) {
                details.put(fieldName, message);
            }
        }

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "ERROR_OCCURRED", "Invalid credentials", details);
        log.error("Validation Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_OCCURRED", "Something went wrong");
        log.error("Unhandled Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }
}
