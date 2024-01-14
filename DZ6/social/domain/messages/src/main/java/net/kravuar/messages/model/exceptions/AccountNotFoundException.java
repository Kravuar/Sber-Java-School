package net.kravuar.messages.model.exceptions;

public class AccountNotFoundException extends MessageException {
    public AccountNotFoundException(long id) {
        super(String.format("Account with id %d not found.", id));
    }
}