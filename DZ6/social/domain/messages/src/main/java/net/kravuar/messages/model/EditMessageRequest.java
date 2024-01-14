package net.kravuar.messages.model;

public record EditMessageRequest(
        long messageId,
        String newMessage
) {}
