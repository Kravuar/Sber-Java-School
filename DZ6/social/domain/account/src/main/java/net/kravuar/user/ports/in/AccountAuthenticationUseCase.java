package net.kravuar.user.ports.in;

import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;

public interface AccountAuthenticationUseCase {
    Account authenticate(AccountAuthenticationRequest request);
}
