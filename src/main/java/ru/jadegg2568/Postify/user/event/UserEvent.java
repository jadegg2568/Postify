package ru.jadegg2568.Postify.user.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.BaseEvent;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public abstract class UserEvent extends BaseEvent {
    private final User user;

    protected UserEvent(EventType type, User user) {
        super(type, user.getUuid());
        this.user = user;
    }
}