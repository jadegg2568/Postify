package ru.jadegg2568.Postify.exception.handler;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.jadegg2568.Postify.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException e) {
        log.debug("JWT error: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Invalid or expired token"
        );

        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.debug("Access denied: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "You don't have permission"
        );

        return new ResponseEntity<>(error, error.status());
    }
}