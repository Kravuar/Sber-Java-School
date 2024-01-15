package net.kravuar.user.ports.out;

import net.kravuar.user.model.Account;

import java.util.Optional;

public interface AccountRetrievalPort {
    Optional<Account> findById(long id);
    Optional<Account> findByUsername(String username);

    // Maybe criteria stuff
}
