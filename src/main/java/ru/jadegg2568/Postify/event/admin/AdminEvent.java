package ru.jadegg2568.Postify.event.admin;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

@Getter
public abstract class AdminEvent extends BaseEvent {
    private final User user;

    protected AdminEvent(User user) {
        super(user.getUuid());
        this.user = user;
    }
}