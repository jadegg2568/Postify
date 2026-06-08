package ru.jadegg2568.Postify.event.user;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import java.util.UUID;

@Getter
public class UserRegisteredEvent extends UserEvent {
    private final String email;
    private final String name;

    public UserRegisteredEvent(User user) {
        super(user);
        this.email = user.getMail();
        this.name = user.getName();
    }
}