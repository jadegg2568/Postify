package ru.jadegg2568.Postify.dialogue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MessageResponse {
    private UUID uuid;
    private UUID senderUuid;
    private String senderName;
    private String text;
    private UUID replyTo;
    private Instant createdAt;
}