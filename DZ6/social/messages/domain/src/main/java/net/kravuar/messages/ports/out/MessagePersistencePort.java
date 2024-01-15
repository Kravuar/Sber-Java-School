package net.kravuar.messages.ports.out;

import net.kravuar.messages.model.Message;

public interface MessagePersistencePort {
    Message save(Message message);
    void delete(Message message);
}
