package net.kravuar.messages;

import lombok.RequiredArgsConstructor;
import net.kravuar.messages.model.EditMessageRequest;
import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.SendMessageRequest;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;
import net.kravuar.messages.model.exceptions.MessageNotFoundException;
import net.kravuar.messages.ports.in.MessageManagementUseCase;
import net.kravuar.messages.ports.out.AccountExistenceCheckPort;
import net.kravuar.messages.ports.out.MessagePersistencePort;
import net.kravuar.messages.ports.out.MessageRetrievalPort;

@RequiredArgsConstructor
public class MessageManagementFacade implements MessageManagementUseCase {
    private final MessagePersistencePort persistencePort;
    private final MessageRetrievalPort retrievalPort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public Message sendMessage(SendMessageRequest request) {
        Message newMessage = Message.withoutId(
                request.senderId(),
                request.receiverId(),
                request.message()
        );

        if (!accountExistenceCheckPort.exists(request.senderId()))
            throw new AccountNotFoundException(request.senderId());
        if (!accountExistenceCheckPort.exists(request.receiverId()))
            throw new AccountNotFoundException(request.receiverId());

        validateMessageOrElseThrow(request.message());

        return persistencePort.save(newMessage);
    }

    @Override
    public void editMessage(EditMessageRequest request) {
        Message message = retrievalPort.findById(request.messageId())
                .orElseThrow(() -> new MessageNotFoundException(request.messageId()));

        validateMessageOrElseThrow(request.newMessage());

        message.setMessage(request.newMessage());
        persistencePort.save(message);
    }

    private void validateMessageOrElseThrow(String message) {
        if (message == null)
            throw new IllegalArgumentException("Message cannot be null.");
        if (message.isBlank())
            throw new IllegalArgumentException("Message cannot be blank.");
    }
}
