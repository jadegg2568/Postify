package ru.jadegg2568.Postify.event.dialogue;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.Message;
import ru.jadegg2568.Postify.entity.User;

@Getter
public class MessageSentEvent extends DialogueEvent {
    private final Message message;
    private final String textPreview;
    private final Long replyToId;

    public MessageSentEvent(User sender, Message msg, Dialogue dialogue) {
        super(sender, dialogue);
        this.message = msg;
        this.textPreview = msg.getText().length() > 50
                ? msg.getText().substring(0, 50) + "..."
                : msg.getText();
        this.replyToId = msg.getReplyTo() != null ? msg.getReplyTo().getId() : null;
    }
}