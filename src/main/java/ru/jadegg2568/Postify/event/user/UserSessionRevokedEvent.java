package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class UserSessionRevokedEvent extends UserEvent {
    private final Session session;

    public UserSessionRevokedEvent(User user, Session session) {
        super(user);
        this.session = session;
    }
}