package ru.jadegg2568.Postify.user.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.event.EventType;

// when user logged through /login
@Getter
public class UserLoggedEvent extends UserEvent {
    private final String ipAddress;
    private final String agent;

    public UserLoggedEvent(User user, String ipAddress, String agent) {
        super(EventType.USER_LOGGED, user);
        this.ipAddress = ipAddress;
        this.agent = agent;
    }
}