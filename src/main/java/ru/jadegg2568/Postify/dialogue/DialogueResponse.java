package ru.jadegg2568.Postify.dialogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.jadegg2568.Postify.user.UserResponse;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DialogueResponse {
    private UUID uuid;
    private UserResponse user1;
    private UserResponse user2;
    private Instant createdAt;
//    private String lastMessagePreview; // optional
}