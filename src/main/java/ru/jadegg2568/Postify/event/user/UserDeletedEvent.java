package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class UserDeletedEvent extends UserEvent {
    public UserDeletedEvent(User user) {
        super(EventType.USER_DELETED, user);
    }
}