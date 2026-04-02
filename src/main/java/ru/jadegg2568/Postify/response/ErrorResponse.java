package ru.jadegg2568.Postify.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private HttpStatus status;
    private String code;
    private Map<String, Object> details;
    private String traceId;
}