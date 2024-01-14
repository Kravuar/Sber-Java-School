package net.kravuar.user.ports.in;

import net.kravuar.user.model.Account;

import java.util.Optional;

public interface AccountRetrievalUseCase {
    Optional<Account> findById(long id);
    Optional<Account> findByUsername(String username);
    //    Some other methods for specific retrieval cases
}
