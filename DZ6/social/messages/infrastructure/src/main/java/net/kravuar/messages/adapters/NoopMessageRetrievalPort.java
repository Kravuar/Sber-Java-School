package net.kravuar.messages.adapters;

import net.kravuar.messages.model.Message;
import net.kravuar.messages.ports.out.MessageRetrievalPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NoopMessageRetrievalPort implements MessageRetrievalPort {
    private final Logger log = LogManager.getLogger(NoopMessageRetrievalPort.class);

    @Override
    public Optional<Message> findById(long id) {
        log.info(
                "Finding Message by id={}",
                id
        );
        return Optional.empty();
    }

    @Override
    public List<Message> findAllBySender(long senderId) {
        log.info(
                "Finding all Messages by senderId={}",
                senderId
        );
        return Collections.emptyList();
    }

    @Override
    public List<Message> findAllByReceiver(long receiverId) {
        log.info(
                "Finding all Messages by receiverId={}",
                receiverId
        );
        return Collections.emptyList();
    }
}