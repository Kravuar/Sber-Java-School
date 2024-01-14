package net.kravuar.messages.ports.in;

import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;

import java.util.List;

public interface MessagesRetrievalUseCase {
    /**
     * Retrieves all messages sent by the specified sender.
     *
     * @param senderId The ID of the sender.
     * @return A list of messages sent by the specified sender.
     * @throws AccountNotFoundException If the sender account does not exist.
     */
    List<Message> findAllBySender(long senderId);

    /**
     * Retrieves all messages received by the specified receiver.
     *
     * @param receiverId The ID of the receiver.
     * @return A list of messages received by the specified receiver.
     * @throws AccountNotFoundException If the receiver account does not exist.
     */
    List<Message> findAllByReceiver(long receiverId);
}