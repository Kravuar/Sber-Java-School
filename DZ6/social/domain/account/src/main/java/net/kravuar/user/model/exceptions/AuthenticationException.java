package net.kravuar.user.model.exceptions;

import net.kravuar.user.model.AccountAuthenticationRequest;

public class AuthenticationException extends AccountException {

    public AuthenticationException(AccountAuthenticationRequest request) {
        super(String.format("Authentication failed for user: %s", request.username()));
    }
}
