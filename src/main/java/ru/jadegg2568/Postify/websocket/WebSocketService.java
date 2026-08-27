package ru.jadegg2568.Postify.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.jadegg2568.Postify.dialogue.event.DialogueEvent;
import ru.jadegg2568.Postify.post.event.PostEvent;
import ru.jadegg2568.Postify.user.event.UserEvent;

@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final WebSocketResponseFactory webSocketResponseFactory;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendUserUpdate(UserEvent event) {
        WebSocketResponse response = webSocketResponseFactory.create(event);
        messagingTemplate.convertAndSend("/topic/user/" + event.getActorId(), response);
    }

    public void sendPostUpdate(PostEvent event) {
        WebSocketResponse response = webSocketResponseFactory.create(event);
        messagingTemplate.convertAndSend("/topic/post/" + event.getPost().getUuid(), response);
    }

    public void sendDialogueUpdate(DialogueEvent event) {
        WebSocketResponse response = webSocketResponseFactory.create(event);
        messagingTemplate.convertAndSend("/topic/dialogue/" + event.getDialogue().getUuid(), response);
    }
}
