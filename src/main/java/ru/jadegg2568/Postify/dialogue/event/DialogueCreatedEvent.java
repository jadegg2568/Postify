package ru.jadegg2568.Postify.dialogue.event;

import lombok.Getter;
import ru.jadegg2568.Postify.dialogue.Dialogue;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

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