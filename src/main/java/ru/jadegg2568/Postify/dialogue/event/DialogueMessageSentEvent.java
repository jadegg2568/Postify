package ru.jadegg2568.Postify.dialogue.event;

import lombok.Getter;
import ru.jadegg2568.Postify.dialogue.Dialogue;
import ru.jadegg2568.Postify.dialogue.Message;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.common.event.EventType;

@Getter
public class DialogueMessageSentEvent extends DialogueEvent {
    private final Message message;
    private final String textPreview;
    private final Long replyToId;

    public DialogueMessageSentEvent(User sender, Message msg, Dialogue dialogue) {
        super(EventType.DIALOGUE_MESSAGE_SENT, sender, dialogue);
        this.message = msg;
        this.textPreview = msg.getText().length() > 50
                ? msg.getText().substring(0, 50) + "..."
                : msg.getText();
        this.replyToId = msg.getReplyTo() != null ? msg.getReplyTo().getId() : null;
    }
}