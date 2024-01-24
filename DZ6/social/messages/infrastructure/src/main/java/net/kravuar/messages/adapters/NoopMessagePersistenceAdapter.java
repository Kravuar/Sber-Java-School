package net.kravuar.messages.adapters;

import net.kravuar.messages.model.Message;
import net.kravuar.messages.ports.out.MessagePersistencePort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoopMessagePersistenceAdapter implements MessagePersistencePort {
    private final Logger log = LogManager.getLogger(NoopMessagePersistenceAdapter.class);

    @Override
    public Message save(Message message) {
        log.info(
                "Message save operation: senderId={}, receiverId={}, content={}",
                message.getSenderAccountId(),
                message.getReceiverAccountId(),
                message.getMessage()
        );
        return null;
    }

    @Override
    public void delete(Message message) {
        log.info(
                "Message delete operation: senderId={}, receiverId={}, content={}",
                message.getSenderAccountId(),
                message.getReceiverAccountId(),
                message.getMessage()
        );
    }
}