package ru.jadegg2568.Postify.user.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.BaseEvent;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class AdminChangePermissionsEvent extends BaseEvent {
    private final User whom;

    public AdminChangePermissionsEvent(User user, User whom) {
        super(EventType.ADMIN_CHANGED_PERMISSIONS, user.getUuid());
        this.whom = whom;
    }
}