package ru.jadegg2568.Postify.dialogue.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.common.exception.ApiException;

public class MessageNotFoundException extends ApiException {
    public MessageNotFoundException() {
        super(HttpStatus.NOT_FOUND, "MESSAGE_NOT_FOUND", "Message not found");
    }
}
