package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import java.util.UUID;

@Getter
public class UserLoggedEvent extends UserEvent {
    private final String ipAddress;
    private final String agent;

    public UserLoggedEvent(User user, String ipAddress, String agent) {
        super(user);
        this.ipAddress = ipAddress;
        this.agent = agent;
    }
}