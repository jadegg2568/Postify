package ru.jadegg2568.Postify.exception.handler;

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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.jadegg2568.Postify.exception.ApiException;
import ru.jadegg2568.Postify.exception.param.ParamError;
import ru.jadegg2568.Postify.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        log.debug("API error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(ex);
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        log.debug("Not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "ENDPOINT_NOT_FOUND",
                "Resource not found"
        );

        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex) {
        log.debug("Method not allowed: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "HTTP method not supported"
        );

        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        log.debug("DB conflict: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();

        String message = ex.getMostSpecificCause().getMessage();

        if (message != null) {
            if (message.contains("mail")) {
                details.put("mail", ParamError.BUSY);
            }
            if (message.contains("name")) {
                details.put("name", ParamError.BUSY);
            }
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT,
                "CONFLICT",
                "Data conflict",
                details
        );

        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.debug("Validation error: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();

        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            if (err instanceof FieldError fieldError) {
                String field = fieldError.getField();
                String message = err.getDefaultMessage();

                try {
                    details.put(field, ParamError.valueOf(message));
                } catch (Exception ignored) {
                    details.put(field, message);
                }
            }
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Invalid request",
                details
        );

        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.debug("Type mismatch: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "TYPE_MISMATCH",
                "Invalid parameter type"
        );

        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled error", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Internal server error"
        );

        return ResponseEntity.status(error.status()).body(error);
    }
}