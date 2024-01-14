package net.kravuar.user.ports.in;

import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;
import net.kravuar.user.model.AccountRegistrationRequest;
import net.kravuar.user.model.exceptions.AccountExistsException;
import net.kravuar.user.model.exceptions.AccountNotFoundException;
import net.kravuar.user.model.exceptions.IncorrectPasswordException;

public interface AccountAuthenticationUseCase {
    /**
     * Authenticates a user account based on the provided authentication request.
     *
     * @param request The authentication request containing username and password.
     * @return The authenticated account.
     * @throws AccountNotFoundException If the specified account does not exist.
     * @throws IncorrectPasswordException If the provided password does not match the account's encoded password.
     */
    Account authenticate(AccountAuthenticationRequest request);

    /**
     * Registers a new user account based on the provided registration request.
     *
     * @param request The registration request containing user details.
     * @return The newly registered account.
     * @throws AccountExistsException If an account with the specified username already exists.
     */
    Account register(AccountRegistrationRequest request);
}
