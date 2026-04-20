package ru.jadegg2568.Postify.exception.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.jadegg2568.Postify.response.ErrorResponse;

@ControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthError(AuthenticationException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "No valid token");
        log.debug("Auth Error: {}", e.getMessage());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Expired session, reauthorize");
        log.debug("Expired Jwt: {}", e.getMessage());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectJwt(JwtException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid session, reauthorize");
        log.debug("Bad Jwt: {}", e.getMessage());
        return new ResponseEntity<>(error, error.status());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", "You don't have permission");
        log.debug("Access Denied: {}", e.getMessage());
        return new ResponseEntity<>(error, error.status());
    }
}
