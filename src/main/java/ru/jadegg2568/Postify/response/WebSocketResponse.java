package ru.jadegg2568.Postify.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record WebSocketResponse(UUID actorId,
                                UUID eventId,
                                Instant occurredAt,
                                String type,
                                Map<String, Object> details) {
}
