package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

import java.time.Duration;

/**
 * Thrown to indicate that an action was attempted on a locked terminal.
 */
@Getter
public class TerminalIsLockedException extends RuntimeException {
    private final Duration timeLeft;
    private final String actionName;

    /**
     * Constructs a {@code TerminalIsLockedException} with the specified time left until the terminal is available.
     *
     * @param timeLeft the duration representing the time left until the terminal becomes available.
     * @param actionName the name of an attempted action.
     */
    public TerminalIsLockedException(Duration timeLeft, String actionName) {
        super(String.format("Cannot perform action: %s. Terminal is locked. Time left: %s", actionName, timeLeft));
        this.timeLeft = timeLeft;
        this.actionName = actionName;
    }
}
