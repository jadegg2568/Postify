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
        ErrorResponse error = new ErrorResponse(ex);
        log.debug("Api Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundPath(NoResourceFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Not found for that path");
        log.debug("NotFoundPath Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRequestMethod(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
        log.debug("IncorrectRequestMethod Error: {}", ex.toString());
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
        log.debug("DatabaseCredentialsConflict Error: {}", ex.toString());
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
        log.debug("Validation Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_OCCURRED", "Something went wrong");
        log.error("Unhandled Error: {}", ex.toString());
        return new ResponseEntity<>(error, error.status());
    }
}
