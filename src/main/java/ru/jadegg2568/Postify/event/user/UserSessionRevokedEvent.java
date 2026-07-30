package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class UserSessionRevokedEvent extends UserEvent {
    private final Session session;

    public UserSessionRevokedEvent(User user, Session session) {
        super(EventType.USER_SESSION_REVOKED, user);
        this.session = session;
    }
}