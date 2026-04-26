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
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.jadegg2568.Postify.exception.ApiException;
import ru.jadegg2568.Postify.response.ErrorResponse;
import ru.jadegg2568.Postify.exception.param.ParamError;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        log.debug("API error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(ex);
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        log.debug("Not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Resource not found"
        );

        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex) {
        log.debug("Method not allowed: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        log.debug("DB conflict: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();

        if (ex.getMessage().contains("mail")) {
            details.put("mail", ParamError.BUSY);
        }
        if (ex.getMessage().contains("name")) {
            details.put("name", ParamError.BUSY);
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT,
                "CONFLICT",
                "Data conflict",
                details
        );

        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.debug("Validation error: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();

        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();

            try {
                details.put(field, ParamError.valueOf(message));
            } catch (Exception ignored) {
                details.put(field, message);
            }
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Invalid request",
                details
        );

        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled error: ", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Something went wrong"
        );

        return new ResponseEntity<>(error, error.status());
    }
}