package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

import java.util.UUID;

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