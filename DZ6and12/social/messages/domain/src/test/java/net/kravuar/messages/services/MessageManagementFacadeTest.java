package net.kravuar.messages.services;

import net.kravuar.messages.MessageManagementFacade;
import net.kravuar.messages.model.EditMessageRequest;
import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.SendMessageRequest;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;
import net.kravuar.messages.model.exceptions.MessageNotFoundException;
import net.kravuar.messages.ports.out.AccountExistenceCheckPort;
import net.kravuar.messages.ports.out.MessagePersistencePort;
import net.kravuar.messages.ports.out.MessageRetrievalPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageManagementFacadeTest {

    @Mock
    private MessagePersistencePort persistencePort;

    @Mock
    private MessageRetrievalPort retrievalPort;

    @Mock
    private AccountExistenceCheckPort existenceCheckPort;

    @InjectMocks
    private MessageManagementFacade managementService;

    @Test
    void sendMessage_ValidRequest_ReturnsNewMessage() {
        // given
        long senderId = 1L;
        long receiverId = 2L;
        String messageContent = "bebebe";

        var request = new SendMessageRequest(
                senderId,
                receiverId,
                messageContent
        );

        var returnMessage = Message.withoutId(
                senderId,
                receiverId,
                messageContent
        );

        when(existenceCheckPort.exists(senderId)).thenReturn(true);
        when(existenceCheckPort.exists(receiverId)).thenReturn(true);
        when(persistencePort.save(any(Message.class))).thenReturn(returnMessage);

        // when
        var resultMessage = assertDoesNotThrow(() -> managementService.sendMessage(request));

        // then
        assertNotNull(resultMessage);
        assertEquals(senderId, resultMessage.getSenderAccountId());
        assertEquals(receiverId, resultMessage.getReceiverAccountId());
        assertEquals(messageContent, resultMessage.getMessage());

        verify(persistencePort, times(1)).save(any(Message.class));
    }

    @Test
    void sendMessage_SenderNotFound_ThrowsAccountNotFoundException() {
        // given
        long senderId = 1L;
        long receiverId = 2L;

        var request = new SendMessageRequest(
                senderId,
                receiverId,
                "baba"
        );

        when(existenceCheckPort.exists(senderId)).thenReturn(false);

        // when & then
        assertThrows(AccountNotFoundException.class, () -> managementService.sendMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void sendMessage_ReceiverNotFound_ThrowsAccountNotFoundException() {
        // given
        long senderId = 1L;
        long receiverId = 2L;

        var request = new SendMessageRequest(
                senderId,
                receiverId,
                "bobo"
        );

        when(existenceCheckPort.exists(senderId)).thenReturn(true);
        when(existenceCheckPort.exists(receiverId)).thenReturn(false);

        // when & then
        assertThrows(AccountNotFoundException.class, () -> managementService.sendMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void sendMessage_NullMessageContent_ThrowsIllegalArgumentException() {
        // given
        long senderId = 1L;
        long receiverId = 2L;
        var request = new SendMessageRequest(
                senderId,
                receiverId,
                null
        );

        when(existenceCheckPort.exists(senderId)).thenReturn(true);
        when(existenceCheckPort.exists(receiverId)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> managementService.sendMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void sendMessage_BlankMessageContent_ThrowsIllegalArgumentException() {
        // given
        long senderId = 1L;
        long receiverId = 2L;
        var request = new SendMessageRequest(
                senderId,
                receiverId,
                ""
        );

        when(existenceCheckPort.exists(senderId)).thenReturn(true);
        when(existenceCheckPort.exists(receiverId)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> managementService.sendMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void editMessage_ValidRequest_SuccessfullyEditsMessage() {
        // given
        long messageId = 1L;
        String newMessageContent = "newbebe";

        var existingMessage = Message.withoutId(
                0L,
                0L,
                "oldbebe"
        );

        var request = new EditMessageRequest(
                messageId,
                newMessageContent
        );

        when(retrievalPort.findById(messageId)).thenReturn(Optional.of(existingMessage));
        when(persistencePort.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        assertDoesNotThrow(() -> managementService.editMessage(request));

        // then
        assertEquals(newMessageContent, existingMessage.getMessage());
        verify(persistencePort, times(1)).save(existingMessage);
    }

    @Test
    void editMessage_MessageNotFound_ThrowsMessageNotFoundException() {
        // given
        long messageId = 1L;

        var request = new EditMessageRequest(
                messageId,
                "newbebe"
        );

        when(retrievalPort.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class, () -> managementService.editMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void editMessage_NullNewMessageContent_ThrowsIllegalArgumentException() {
        // Arrange
        long messageId = 1L;

        var request = new EditMessageRequest(
                messageId,
                null
        );

        var existingMessage = mock(Message.class);

        when(retrievalPort.findById(messageId)).thenReturn(Optional.of(existingMessage));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> managementService.editMessage(request));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void editMessage_BlankNewMessageContent_ThrowsIllegalArgumentException() {
        // Arrange
        long messageId = 1L;

        var request = new EditMessageRequest(
                messageId,
                ""
        );

        var existingMessage = mock(Message.class);

        when(retrievalPort.findById(messageId)).thenReturn(Optional.of(existingMessage));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> managementService.editMessage(request));
        verifyNoInteractions(persistencePort);
    }
}