package net.kravuar.messages.ports.out;

import net.kravuar.messages.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRetrievalPort {
    Optional<Message> findById(long id);
    List<Message> findAllBySender(long senderId);
    List<Message> findAllByReceiver(long receiverId);
}
