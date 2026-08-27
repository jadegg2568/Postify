package ru.jadegg2568.Postify.dialogue.event;

import lombok.Getter;
import ru.jadegg2568.Postify.dialogue.Dialogue;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.BaseEvent;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public abstract class DialogueEvent extends BaseEvent {
    private final Dialogue dialogue;

    protected DialogueEvent(EventType type, User actor, Dialogue dialogue) {
        super(type, actor.getUuid());
        this.dialogue = dialogue;
    }
}