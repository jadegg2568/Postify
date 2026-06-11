package ru.jadegg2568.Postify.event.dialogue;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class DialogueCreatedEvent extends DialogueEvent {
    private final User initiator;
    private final User invited;

    public DialogueCreatedEvent(User initiator, Dialogue dialogue, User invited) {
        super(initiator, dialogue, invited);
        this.initiator = initiator;
        this.invited = invited;
    }
}