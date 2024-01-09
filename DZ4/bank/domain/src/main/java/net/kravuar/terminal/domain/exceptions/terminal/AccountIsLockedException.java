package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Thrown to indicate that an action was attempted on a locked account.
 */
@Getter
public class AccountIsLockedException extends RuntimeException {
    private final LocalDateTime unlockTime;

    /**
     * Constructs a {@code AccountIsLockedException} with the specified unlock time.
     *
     * @param unlockTime the time representing the time at which the account becomes available.
     */
    public AccountIsLockedException(LocalDateTime unlockTime) {
        super(String.format("Cannot perform action. Account is locked. Unlock Time: %s", unlockTime));
        this.unlockTime = unlockTime;
    }
}
