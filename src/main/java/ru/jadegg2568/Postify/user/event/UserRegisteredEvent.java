package ru.jadegg2568.Postify.user.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class UserRegisteredEvent extends UserEvent {
    private final String email;
    private final String name;

    public UserRegisteredEvent(User user) {
        super(EventType.USER_REGISTERED, user);
        this.email = user.getMail();
        this.name = user.getName();
    }
}