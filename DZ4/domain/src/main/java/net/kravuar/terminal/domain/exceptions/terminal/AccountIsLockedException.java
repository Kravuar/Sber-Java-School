package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

import java.time.Duration;

/**
 * Thrown to indicate that an action was attempted on a locked account.
 */
@Getter
public class AccountIsLockedException extends RuntimeException {
    private final Duration timeLeft;

    /**
     * Constructs a {@code AccountIsLockedException} with the specified time left until the account will be available.
     *
     * @param timeLeft the duration representing the time left until the account becomes available.
     */
    public AccountIsLockedException(Duration timeLeft) {
        super(String.format("Cannot perform action. Account is locked. Time left: %s", timeLeft));
        this.timeLeft = timeLeft;
    }
}
