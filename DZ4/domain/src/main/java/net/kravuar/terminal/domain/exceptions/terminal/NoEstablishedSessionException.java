package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted with an invalid session.
 * This exception typically represents a programming error where an action is being
 * performed in a context that requires a valid session, but session was in invalid state.
 */
@Getter
public class NoEstablishedSessionException extends RuntimeException {
    /**
     * Constructs a {@code NoEstablishedSessionException} with the specified attempted action name.
     */
    public NoEstablishedSessionException() {
        super("Session invalid.");
    }
}