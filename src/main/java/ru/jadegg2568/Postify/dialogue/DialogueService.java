package ru.jadegg2568.Postify.dialogue;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.dialogue.event.DialogueMessageSentEvent;
import ru.jadegg2568.Postify.auth.exception.NoAccessException;
import ru.jadegg2568.Postify.dialogue.exception.DialogueNotFoundException;
import ru.jadegg2568.Postify.dialogue.exception.MessageNotFoundException;
import ru.jadegg2568.Postify.dialogue.exception.SelfDialogueException;
import ru.jadegg2568.Postify.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DialogueService {

    private final DialogueRepository dialogueRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Ensure canonical ordering: the user with smaller UUID (string compare) becomes user1
     */
    private List<User> canonicalizeUsers(User user1, User user2) {
        if (user1.getUuid().toString().compareTo(user2.getUuid().toString()) <= 0) {
            return Arrays.asList(user1, user2);
        } else {
            return Arrays.asList(user2, user1);
        }
    }

    /**
     * Create or return existing dialogue between two User entities.
     * Controller is responsible to fetch/validate User objects.
     */
    @Transactional
    public Dialogue createOrGetDialogue(@MonotonicNonNull UUID a, @MonotonicNonNull UUID b) {
        if (a.equals(b)) {
            throw new SelfDialogueException();
        }
        User u1 = userService.getByUuid(a);
        User u2 = userService.getByUuid(b);

        // canonical ordering to match unique constraint (user1,user2)
        List<User> users = canonicalizeUsers(u1, u2);
        User user1 = users.get(0);
        User user2 = users.get(1);

        // find or create new dialogue
        return dialogueRepository.findByUser1AndUser2(user1, user2).orElseGet(() -> {
            // create new dialogue
            Dialogue d = Dialogue.builder()
                    .user1(user1)
                    .user2(user2)
                    .build();
            return dialogueRepository.save(d);
        });
    }

    /**
     * Send message by an already-loaded sender User.
     * Controller should ensure sender is loaded and authorized.
     */
    @Transactional
    public Message sendMessage(UUID dialogueUuid, UUID senderUuid, MessageCreateRequest request, UUID replyToUuid) {
        Dialogue dialogue = getDialogueByUuid(dialogueUuid);
        User sender = userService.getByUuid(senderUuid);

        if (!sender.equals(dialogue.getUser1()) && !sender.equals(dialogue.getUser2())) {
            throw new NoAccessException();
        }

        Message replyTo = null;
        if (replyToUuid != null) {
            replyTo = getMessageByUuid(replyToUuid);
        }

        Message msg = Message.builder()
                .dialogue(dialogue)
                .text(request.getText())
                .sender(sender)
                .replyTo(replyTo)
                .build();

        Message updated = messageRepository.save(msg);
        eventPublisher.publishEvent(new DialogueMessageSentEvent(sender, msg, dialogue));

        return updated;
    }

    public @NonNull Dialogue getDialogueByUuid(UUID dialogueUuid) {
        return dialogueRepository.findByUuid(dialogueUuid)
                .orElseThrow(DialogueNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<Message> getMessages(UUID dialogueUuid, int page, int size) {
        Dialogue dialogue = getDialogueByUuid(dialogueUuid);
        return messageRepository.findByDialogueOrderByCreatedAtAsc(dialogue, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Message getMessageByUuid(UUID uuid) {
        return messageRepository.findByUuid(uuid)
                .orElseThrow(MessageNotFoundException::new);
    }

    /**
     * List dialogues for already-loaded user.
     * For production use add a repository query instead of fetching all rows.
     */
    @Transactional(readOnly = true)
    public Page<Dialogue> listDialoguesForUser(UUID uuid, Pageable pageable) {
        User user = userService.getByUuid(uuid);
        return dialogueRepository.findByUser1OrUser2(user, user, pageable);
    }
}