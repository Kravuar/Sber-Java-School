package net.kravuar.user.ports.in;

import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountCreationRequest;

public interface AccountPersistenceUseCase {
    Account create(AccountCreationRequest user);
    void delete(long userId);
    void update(Account account);
}
