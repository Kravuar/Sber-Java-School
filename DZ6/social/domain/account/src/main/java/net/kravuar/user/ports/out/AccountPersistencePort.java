package net.kravuar.user.ports.out;

import net.kravuar.user.model.Account;

public interface AccountPersistencePort {
    Account save(Account account);
    void delete(long userId);
}
