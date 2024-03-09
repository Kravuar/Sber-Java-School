package net.kravuar.user.adapters;

import net.kravuar.user.model.Account;
import net.kravuar.user.ports.out.AccountPersistencePort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NoopAccountPersistenceAdapter implements AccountPersistencePort {
    private final Logger log = LogManager.getLogger(NoopAccountPersistenceAdapter.class);

    @Override
    public Account save(Account account) {
        log.info(
                "Account saved id={}, username={}.",
                account.getId(),
                account.getUsername()
        );
        return null;
    }
}
