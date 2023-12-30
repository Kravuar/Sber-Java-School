package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that an action was attempted without an established session.
 * This exception typically represents a programming error where an action is being
 * performed in a context that requires a valid session, but no session has been
 * established.
 */
@Getter
public class NoEstablishedSessionException extends RuntimeException {
    private final String actionName;

    /**
     * Constructs a {@code NoSessionEstablishedException} with the specified attempted action name.
     *
     * @param actionName the name of an attempted action.
     */
    public NoEstablishedSessionException(String actionName) {
        super(String.format("Cannot perform action %s without established session.", actionName));
        this.actionName = actionName;
    }
}
