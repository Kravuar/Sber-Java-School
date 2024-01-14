package net.kravuar.messages.model;

public record SendMessageRequest(
        long senderId,
        long receiverId,
        String message
) {}
