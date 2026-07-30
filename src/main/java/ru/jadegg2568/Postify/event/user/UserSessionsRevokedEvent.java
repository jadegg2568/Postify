package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class UserSessionsRevokedEvent extends UserEvent {
    public UserSessionsRevokedEvent(User user) {
        super(EventType.USER_SESSIONS_REVOKED, user);
    }
}