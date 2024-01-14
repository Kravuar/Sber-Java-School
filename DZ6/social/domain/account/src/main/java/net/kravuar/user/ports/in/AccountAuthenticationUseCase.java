package net.kravuar.user.ports.in;

import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;
import net.kravuar.user.model.AccountRegistrationRequest;

public interface AccountAuthenticationUseCase {
    Account authenticate(AccountAuthenticationRequest request);
    Account register(AccountRegistrationRequest request);
}
