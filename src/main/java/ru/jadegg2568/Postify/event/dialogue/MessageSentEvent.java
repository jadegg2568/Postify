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

    public MessageSentEvent(User sender, Message message, Dialogue dialogue, User user2) {
        super(sender, dialogue, user2);
        this.message = message;
        this.textPreview = message.getText().length() > 50 
                ? message.getText().substring(0, 50) + "..." 
                : message.getText();
        this.replyToId = message.getReplyTo() != null ? message.getReplyTo().getId() : null;
    }
}