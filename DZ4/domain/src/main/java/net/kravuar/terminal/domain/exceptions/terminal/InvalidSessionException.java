package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted with an invalid session.
 * This exception typically represents a programming error where an action is being
 * performed in a context that requires a valid session, but no session has been
 * established.
 */
@Getter
public class InvalidSessionException extends RuntimeException {
    private final String actionName;

    /**
     * Constructs a {@code NoSessionEstablishedException} with the specified attempted action name.
     *
     * @param actionName the name of an attempted action.
     */
    public InvalidSessionException(String actionName) {
        super(String.format("Cannot perform action %s without established session.", actionName));
        this.actionName = actionName;
    }
}
