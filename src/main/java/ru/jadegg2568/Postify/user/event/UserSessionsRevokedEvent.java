package ru.jadegg2568.Postify.user.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class UserSessionsRevokedEvent extends UserEvent {
    public UserSessionsRevokedEvent(User user) {
        super(EventType.USER_SESSIONS_REVOKED, user);
    }
}