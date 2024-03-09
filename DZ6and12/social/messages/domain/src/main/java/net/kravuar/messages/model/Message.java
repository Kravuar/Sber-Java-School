package net.kravuar.messages.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Message {
    private final Long id;
    private final long senderAccountId;
    private final long receiverAccountId; // Not considering groups, chats
    private String message;
    private final LocalDateTime creationTime;
    private LocalDateTime lastEditTime;

    public static Message withoutId(
            long senderAccountId,
            long receiverAccountId,
            String message
    ) {
        return new Message(null, senderAccountId, receiverAccountId, message, LocalDateTime.now(), null);
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
        this.lastEditTime = LocalDateTime.now();
    }
}
