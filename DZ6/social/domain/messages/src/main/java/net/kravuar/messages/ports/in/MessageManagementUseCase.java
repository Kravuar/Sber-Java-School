package net.kravuar.messages.ports.in;

import net.kravuar.messages.model.EditMessageRequest;
import net.kravuar.messages.model.Message;
import net.kravuar.messages.model.SendMessageRequest;
import net.kravuar.messages.model.exceptions.AccountNotFoundException;
import net.kravuar.messages.model.exceptions.MessageNotFoundException;

public interface MessageManagementUseCase {
    /**
     * Sends a new message based on the provided request.
     *
     * @param request The request containing sender ID, receiver ID, and message content.
     * @return The newly created message.
     * @throws AccountNotFoundException If either the sender or receiver account does not exist.
     * @throws IllegalArgumentException If the message content is null or blank.
     */
    Message sendMessage(SendMessageRequest request);

    /**
     * Edits an existing message based on the provided request.
     *
     * @param request The request containing the message ID and the new message content.
     * @throws MessageNotFoundException If the specified message ID is not found.
     * @throws IllegalArgumentException If the new message content is null or blank.
     */
    void editMessage(EditMessageRequest request);
}