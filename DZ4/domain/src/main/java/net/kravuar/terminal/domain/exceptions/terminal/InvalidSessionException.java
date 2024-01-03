package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted with an invalid session.
 */
@Getter
public class InvalidSessionException extends Exception {
    public InvalidSessionException() {
        super("Session invalid.");
    }
}
