package ru.jadegg2568.Postify.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.jadegg2568.Postify.event.dialogue.DialogueEvent;
import ru.jadegg2568.Postify.event.post.PostEvent;
import ru.jadegg2568.Postify.event.user.UserEvent;
import ru.jadegg2568.Postify.service.websocket.WebSocketResponseFactory;
import ru.jadegg2568.Postify.service.websocket.WebSocketService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketListener {
    private final WebSocketService webSocketService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUser(UserEvent event) {
        webSocketService.sendUserUpdate(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPost(PostEvent event) {
        webSocketService.sendPostUpdate(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDialogue(DialogueEvent event) {
        webSocketService.sendDialogueUpdate(event);
    }
}
