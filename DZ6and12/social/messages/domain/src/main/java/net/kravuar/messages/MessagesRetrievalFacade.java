package net.kravuar.messages;

import lombok.RequiredArgsConstructor;
import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;
import net.kravuar.messages.ports.in.MessagesRetrievalUseCase;
import net.kravuar.messages.ports.out.AccountExistenceCheckPort;
import net.kravuar.messages.ports.out.MessageRetrievalPort;

import java.util.List;

@RequiredArgsConstructor
public class MessagesRetrievalFacade implements MessagesRetrievalUseCase {
    private final MessageRetrievalPort retrievalPort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public List<Message> findAllBySenderIdAndReceiverId(long senderId, long receiverId) {
        if (!accountExistenceCheckPort.exists(senderId))
            throw new AccountNotFoundException(senderId);
        if (!accountExistenceCheckPort.exists(receiverId))
            throw new AccountNotFoundException(receiverId);
        return retrievalPort.findAllBySenderIdAndReceiverId(senderId, receiverId);
    }
}
