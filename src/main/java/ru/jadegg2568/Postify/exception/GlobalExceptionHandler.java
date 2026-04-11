package ru.jadegg2568.Postify.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.jadegg2568.Postify.exception.param.ParamError;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
            Instant timestamp,
            HttpStatus status,
            String code,
            String message,
            String traceId,
            Map<String, Object> details
    ) {
        public ApiError(HttpStatus status, String code, String message) {
            this(
                    Instant.now(),
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    null
            );
        }
        public ApiError(HttpStatus status, String code, String message, Map<String, Object> details) {
            this(
                    Instant.now(),
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    details
            );
        }
        public ApiError(ApiException apiException) {
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
    public ResponseEntity<ApiError> handleApi(ApiException ex) {
        ApiError error = new ApiError(ex);
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handle404(NoResourceFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, "NOT_FOUND", "Not found for that path");
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalError(Exception ex) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_OCCURRED", "Something went wrong");
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDatabaseConflictCredentials(DataIntegrityViolationException ex) {
        Map<String, Object> details = new HashMap<>();
        if (ex.getMessage().contains("mail"))
            details.put("mail", ParamError.BUSY);
        if (ex.getMessage().contains("name"))
            details.put("name", ParamError.BUSY);

        ApiError error = new ApiError(HttpStatus.CONFLICT, "ERROR_OCCURRED", "Conflict credentials", details);
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

        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "ERROR_OCCURRED", "Invalid credentials", details);
        return new ResponseEntity<>(error, error.status());
    }
}
