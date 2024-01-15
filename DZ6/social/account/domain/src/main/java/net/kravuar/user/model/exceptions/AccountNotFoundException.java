package net.kravuar.user.model.exceptions;

public class AccountNotFoundException extends AccountException {
    public AccountNotFoundException(long id) {
        super(String.format("Account with id %d not found", id));
    }

    public AccountNotFoundException(String username) {
        super(String.format("Account with username %s not found", username));
    }
}
