package ru.jadegg2568.Postify.event.dialogue;

import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;
import ru.jadegg2568.Postify.event.EventType;

public abstract class DialogueEvent extends BaseEvent {
    private final Dialogue dialogue;

    protected DialogueEvent(EventType type, User actor, Dialogue dialogue) {
        super(type, actor.getUuid());
        this.dialogue = dialogue;
    }
}