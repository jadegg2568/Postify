package ru.jadegg2568.Postify.response;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ErrorResponse(
            HttpStatus status,
            String code,
            String message,
            String traceId,
            Map<String, Object> details,
            Instant timestamp
    ) {
        public ErrorResponse(HttpStatus status, String code, String message) {
            this(
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    null,
                    Instant.now()
            );
        }
        public ErrorResponse(HttpStatus status, String code, String message, Map<String, Object> details) {
            this(
                    status,
                    code,
                    message,
                    UUID.randomUUID().toString(),
                    details,
                    Instant.now()
            );
        }
        public ErrorResponse(ApiException apiException) {
            this(
                    apiException.getHttpStatus(),
                    apiException.getCode(),
                    apiException.getMessage(),
                    UUID.randomUUID().toString(),
                    null,
                    Instant.now()
            );
        }
    }