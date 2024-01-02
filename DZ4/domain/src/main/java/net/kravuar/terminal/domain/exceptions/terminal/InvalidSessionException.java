package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted with an invalid session.
 * This exception typically represents a programming error where an action is being
 * performed in a context that requires a valid session, but session was in invalid state.
 */
@Getter
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException() {
        super("Session invalid.");
    }
}
