package ru.jadegg2568.Postify.event.admin;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public abstract class AdminEvent extends BaseEvent {
    private final User user;

    protected AdminEvent(EventType type, User user) {
        super(type, user.getUuid());
        this.user = user;
    }
}