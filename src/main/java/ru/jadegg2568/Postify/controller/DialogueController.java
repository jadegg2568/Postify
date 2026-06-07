package ru.jadegg2568.Postify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.Message;
import ru.jadegg2568.Postify.mapper.DialogueMapper;
import ru.jadegg2568.Postify.mapper.MessageMapper;
import ru.jadegg2568.Postify.request.MessageCreateRequest;
import ru.jadegg2568.Postify.response.DialogueResponse;
import ru.jadegg2568.Postify.response.MessageResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.DialogueService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/dialogues")
@RequiredArgsConstructor
public class DialogueController {

    private final DialogueService dialogueService;
    private final DialogueMapper dialogueMapper;
    private final MessageMapper messageMapper;

    @PostMapping
    public ResponseEntity<DialogueResponse> startDialogue(
            @AuthenticationPrincipal UuidUserDetails auth,
            @RequestParam("with") UUID otherUserUuid
    ) {
        Dialogue dialogue = dialogueService.createOrGetDialogue(auth.uuid(), otherUserUuid);
        return ResponseEntity.ok(dialogueMapper.toResponse(dialogue));
    }

    @GetMapping
    public ResponseEntity<Page<DialogueResponse>> listDialogues(
            @AuthenticationPrincipal UuidUserDetails auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<Dialogue> dialogues = dialogueService.listDialoguesForUser(auth.uuid(), PageRequest.of(page, size));
        return ResponseEntity.ok(dialogues.map(dialogueMapper::toResponse));
    }

    @PostMapping("/{dialogueUuid}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UuidUserDetails auth,
            @PathVariable UUID dialogueUuid,
            @RequestBody MessageCreateRequest request,
            @RequestParam(required = false) UUID replyToUuid
    ) {
        Message msg = dialogueService.sendMessage(dialogueUuid, auth.uuid(), request, replyToUuid);
        return ResponseEntity.ok(messageMapper.toResponse(msg));
    }

    @GetMapping("/{dialogueUuid}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @AuthenticationPrincipal UuidUserDetails auth,
            @PathVariable UUID dialogueUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Page<Message> messages = dialogueService.getMessages(dialogueUuid, page, size);
        return ResponseEntity.ok(messages.map(messageMapper::toResponse));
    }
}