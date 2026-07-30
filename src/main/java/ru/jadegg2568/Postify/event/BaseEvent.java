package ru.jadegg2568.Postify.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class BaseEvent {
    private final EventType type;
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final UUID actorId;

    protected BaseEvent(EventType type, UUID actorId) {
        this.type = type;
        this.actorId = actorId;
    }

    // read fields automatically by reflection
    public Map<String, Object> toDetails() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
    }
}