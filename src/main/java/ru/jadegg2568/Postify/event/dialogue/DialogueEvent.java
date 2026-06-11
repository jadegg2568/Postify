package ru.jadegg2568.Postify.event.dialogue;

import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

public abstract class DialogueEvent extends BaseEvent {
    private final Dialogue dialogue;

    protected DialogueEvent(User actor, Dialogue dialogue) {
        super(actor.getUuid());
        this.dialogue = dialogue;
    }
}