package ru.jadegg2568.Postify.dialogue.exception;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class SelfDialogueException extends ApiException {
    public SelfDialogueException() {
        super(HttpStatus.BAD_REQUEST, "SELF_DIALOGUE_NOT_ALLOWED", "Self dialogue is not allowed");
    }
}
