package ru.jadegg2568.Postify.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jadegg2568.Postify.common.event.BaseEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketResponseFactory {

    public WebSocketResponse create(BaseEvent event) {
        Map<String, Object> details = event.toDetails();

        return new WebSocketResponse(
                event.getActorId(),
                event.getEventId(),
                event.getOccurredAt(),
                event.getType().name(),
                details
        );
    }
}