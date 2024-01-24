package net.kravuar.user.adapters;

import net.kravuar.user.model.Account;
import net.kravuar.user.ports.out.AccountRetrievalPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class NoopAccountRetrievalAdapter implements AccountRetrievalPort {
    private final Logger log = LogManager.getLogger(NoopAccountRetrievalAdapter.class);

    @Override
    public Optional<Account> findById(long id) {
        log.info(
                "Searching account by id={}",
                id
        );
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        log.info(
                "Searching account by username={}",
                username
        );
        return Optional.empty();
    }
}
