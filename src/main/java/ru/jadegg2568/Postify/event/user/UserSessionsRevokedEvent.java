package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class UserSessionsRevokedEvent extends UserEvent {
    public UserSessionsRevokedEvent(User user) {
        super(user);
    }
}