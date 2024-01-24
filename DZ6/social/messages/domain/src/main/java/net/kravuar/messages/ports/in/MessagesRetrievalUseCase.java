package net.kravuar.messages.ports.in;

import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;

import java.util.List;

public interface MessagesRetrievalUseCase {
    /**
     * Retrieves all messages sent by the specified sender and receiver.
     *
     * @param senderId The ID of the sender.
     * @param receiverId The ID of the receiver.
     * @return A list of messages sent by the specified sender to specified receiver.
     * @throws AccountNotFoundException If the sender or receiver account does not exist.
     */
    List<Message> findAllBySenderIdAndReceiverId(long senderId, long receiverId);
}