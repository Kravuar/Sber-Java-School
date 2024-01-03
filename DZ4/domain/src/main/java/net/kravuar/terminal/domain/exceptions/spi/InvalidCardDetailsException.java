package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted with an invalid instance of {@code CardDetails}.
 */
@Getter
public class InvalidCardDetailsException extends Exception {
    public InvalidCardDetailsException() {
        super("Session invalid.");
    }
}
