package net.kravuar.user.model.exceptions;

import net.kravuar.user.model.AccountAuthenticationRequest;

public class IncorrectPasswordException extends AccountException {

    public IncorrectPasswordException(AccountAuthenticationRequest request) {
        super(String.format("Authentication failed for user: %s due to password mismatch.", request.username()));
    }
}
