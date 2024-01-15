package net.kravuar.messages.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;
import net.kravuar.messages.ports.in.MessagesRetrievalUseCase;
import net.kravuar.messages.ports.out.AccountExistenceCheckPort;
import net.kravuar.messages.ports.out.MessageRetrievalPort;

import java.util.List;

@RequiredArgsConstructor
public class MessagesRetrievalService implements MessagesRetrievalUseCase {
    private final MessageRetrievalPort retrievalPort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public List<Message> findAllBySender(long senderId) {
        if (!accountExistenceCheckPort.exists(senderId))
            throw new AccountNotFoundException(senderId);
        return retrievalPort.findAllBySender(senderId);
    }

    @Override
    public List<Message> findAllByReceiver(long receiverId) {
        if (!accountExistenceCheckPort.exists(receiverId))
            throw new AccountNotFoundException(receiverId);
        return retrievalPort.findAllByReceiver(receiverId);
    }
}
