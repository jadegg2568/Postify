package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class UserDeletedEvent extends UserEvent {
    public UserDeletedEvent(User user) {
        super(user);
    }
}