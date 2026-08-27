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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {

    // Централизованный метод для сборки ответа убирает дублирование во всех обработчиках
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, String msg, Map<String, Object> details) {
        return ResponseEntity.status(status).body(new ErrorResponse(status, code, msg, details));
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, String msg) {
        return buildResponse(status, code, msg, null);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        log.debug("API error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        log.debug("Not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND",
                "Endpoint not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex) {
        log.debug("Method not allowed: {}", ex.getMessage());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "HTTP method not supported");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        Map<String, Object> details = new HashMap<>();

        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
            String constraintName = constraintEx.getConstraintName();
            log.debug("DB constraint violation: {}", constraintName);

            if (constraintName != null) {
                if (constraintName.contains("uq_user_mail") || constraintName.contains("mail")) {
                    details.put("mail", ParamError.BUSY);
                } else if (constraintName.contains("uq_user_name") || constraintName.contains("name")) {
                    details.put("name", ParamError.BUSY);
                }
            }
        } else {
            log.debug("DB integrity violation without constraint: {}", ex.getMessage());
        }

        return buildResponse(HttpStatus.CONFLICT, "CONFLICT",
                "Data conflict occurred", details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.debug("Validation error: {}", ex.getMessage());
        Map<String, Object> details = new HashMap<>();

        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            if (err instanceof FieldError fieldError) {
                details.put(fieldError.getField(), ParamError.valueOf(err.getDefaultMessage()));
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Invalid request", details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.debug("Type mismatch: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH",
                "Invalid parameter type");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Internal server error");
    }
}