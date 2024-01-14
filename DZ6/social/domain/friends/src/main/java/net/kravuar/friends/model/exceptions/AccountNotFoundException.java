package net.kravuar.friends.model.exceptions;

public class AccountNotFoundException extends FriendshipException {
    public AccountNotFoundException(long id) {
        super(String.format("Account with id %d not found.", id));
    }
}
