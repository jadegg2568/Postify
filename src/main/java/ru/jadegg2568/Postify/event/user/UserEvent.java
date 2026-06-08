package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;
import java.util.UUID;

@Getter
public abstract class UserEvent extends BaseEvent {
    private final User user;

    protected UserEvent(User user) {
        super(user.getUuid());
        this.user = user;
    }
}