package ru.jadegg2568.Postify.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final UUID userUuid;

    protected BaseEvent(UUID userUuid) {
        this.userUuid = userUuid;
    }
}