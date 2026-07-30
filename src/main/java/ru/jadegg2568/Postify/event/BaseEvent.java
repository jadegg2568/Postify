package ru.jadegg2568.Postify.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEvent {
    private final EventType type;
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final UUID userUuid;

    protected BaseEvent(EventType type, UUID userUuid) {
        this.type = type;
        this.userUuid = userUuid;
    }
}