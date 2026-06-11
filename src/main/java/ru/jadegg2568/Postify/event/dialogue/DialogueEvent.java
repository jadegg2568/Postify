package ru.jadegg2568.Postify.event.dialogue;

import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

public abstract class DialogueEvent extends BaseEvent {
    private final Dialogue dialogue;
    private final User user2;

    protected DialogueEvent(User actor, Dialogue dialogue, User user2) {
        super(actor.getUuid());
        this.dialogue = dialogue;
        this.user2 = user2;
    }
}