package net.kravuar.user.model.exceptions;

import net.kravuar.user.model.AccountRegistrationRequest;

public class AccountExistsException extends AccountException {
    public AccountExistsException(AccountRegistrationRequest request) {
        super(String.format("Account with username %s already exists.", request.username()));
    }
}
