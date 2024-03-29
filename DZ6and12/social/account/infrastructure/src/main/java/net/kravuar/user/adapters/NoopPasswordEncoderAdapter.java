package net.kravuar.user.adapters;

import net.kravuar.user.ports.out.PasswordEncoderPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoopPasswordEncoderAdapter implements PasswordEncoderPort {
    private final Logger log = LogManager.getLogger(NoopPasswordEncoderAdapter.class);

    @Override
    public String encode(String password) {
        log.info(
                "Encoding password={}",
                password
        );
        return null;
    }
}
