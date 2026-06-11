package ru.jadegg2568.Postify.event.admin;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

@Getter
public class AdminChangePermissionsEvent extends BaseEvent {
    private final User whom;

    public AdminChangePermissionsEvent(User user, User whom) {
        super(user.getUuid());
        this.whom = whom;
    }
}