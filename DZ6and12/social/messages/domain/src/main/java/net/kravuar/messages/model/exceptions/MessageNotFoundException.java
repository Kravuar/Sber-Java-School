package net.kravuar.messages.model.exceptions;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(long messageId) {
        super(String.format("Message with id %d not found.", messageId));
    }
}
