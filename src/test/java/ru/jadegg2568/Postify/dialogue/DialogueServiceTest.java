package ru.jadegg2568.Postify.dialogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.auth.exception.NoAccessException;
import ru.jadegg2568.Postify.user.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // ← лучше так, чем openMocks вручную
class DialogueServiceTest {

    @Mock
    private DialogueRepository dialogueRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageMapper messageMapper;  // ← добавили!

    @Mock
    private ApplicationEventPublisher eventPublisher;  // ← если используется в сервисе

    @InjectMocks
    private DialogueService dialogueService;

    private User userA;
    private User userB;
    private UUID userAUuid;
    private UUID userBUuid;

    @BeforeEach
    void setUp() {
        userAUuid = UUID.randomUUID();
        userBUuid = UUID.randomUUID();

        userA = new User();
        userA.setUuid(userAUuid);

        userB = new User();
        userB.setUuid(userBUuid);
    }

    @Test
    void createOrGetDialogue_creates_when_missing() {
        when(userService.getByUuid(userAUuid)).thenReturn(userA);
        when(userService.getByUuid(userBUuid)).thenReturn(userB);
        when(dialogueRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.empty());
        when(dialogueRepository.save(any(Dialogue.class))).thenAnswer(inv -> {
            Dialogue d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        Dialogue result = dialogueService.createOrGetDialogue(userAUuid, userBUuid);

        assertNotNull(result);
        assertNotNull(result.getId());
        verify(dialogueRepository).save(any(Dialogue.class));
    }

    @Test
    void sendMessage_checks_participation_and_saves() {
        UUID dialogueUuid = UUID.randomUUID();
        String messageText = "hello";

        Dialogue dialogue = new Dialogue();
        dialogue.setUser1(userA);
        dialogue.setUser2(userB);

        MessageCreateRequest request = new MessageCreateRequest();
        request.setText(messageText);

        Message expectedMessage = new Message();
        expectedMessage.setText(messageText);
        expectedMessage.setSender(userA);
        expectedMessage.setDialogue(dialogue);

        when(dialogueRepository.findByUuid(dialogueUuid)).thenReturn(Optional.of(dialogue));
        when(userService.getByUuid(userAUuid)).thenReturn(userA);
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = dialogueService.sendMessage(dialogueUuid, userAUuid, request, null);

        assertNotNull(result);
        assertEquals(messageText, result.getText());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_throws_when_user_not_participant() {
        UUID dialogueUuid = UUID.randomUUID();
        UUID strangerUuid = UUID.randomUUID();
        User stranger = new User();
        stranger.setUuid(strangerUuid);

        Dialogue dialogue = new Dialogue();
        dialogue.setUser1(userA);
        dialogue.setUser2(userB);

        MessageCreateRequest request = new MessageCreateRequest();
        request.setText("hello");

        when(dialogueRepository.findByUuid(dialogueUuid)).thenReturn(Optional.of(dialogue));
        when(userService.getByUuid(strangerUuid)).thenReturn(stranger);

        assertThrows(NoAccessException.class, () ->
                dialogueService.sendMessage(dialogueUuid, strangerUuid, request, null)
        );

        verify(messageRepository, never()).save(any());
    }
}