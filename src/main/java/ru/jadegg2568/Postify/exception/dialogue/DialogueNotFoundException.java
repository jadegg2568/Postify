package ru.jadegg2568.Postify.exception.dialogue;

import org.springframework.http.HttpStatus;
import ru.jadegg2568.Postify.exception.ApiException;

public class DialogueNotFoundException extends ApiException {
    public DialogueNotFoundException() {
        super(HttpStatus.NOT_FOUND, "DIALOGUE_NOT_FOUND", "Dialogue not found");
    }
}
