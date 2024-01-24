package net.kravuar.messages.adapters;

import net.kravuar.messages.ports.out.AccountExistenceCheckPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoopAccountExistenceCheckAdapter implements AccountExistenceCheckPort {
    private final Logger log = LogManager.getLogger(NoopAccountExistenceCheckAdapter.class);

    @Override
    public boolean exists(long accountId) {
        log.info(
                "Account existence check by id={}",
                accountId
        );
        return false;
    }
}