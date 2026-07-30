package ru.jadegg2568.Postify.event.dialogue;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class DialogueCreatedEvent extends DialogueEvent {
    private final User initiator;
    private final User invited;

    public DialogueCreatedEvent(User initiator, Dialogue dialogue, User invited) {
        super(EventType.DIALOGUE_CREATED, initiator, dialogue);
        this.initiator = initiator;
        this.invited = invited;
    }
}