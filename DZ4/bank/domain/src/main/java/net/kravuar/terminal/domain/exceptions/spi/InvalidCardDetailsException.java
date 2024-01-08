package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;
import net.kravuar.terminal.domain.card.CardDetails;

/**
 * Thrown to indicate that an action was attempted with an invalid instance of {@code CardDetails}.
 */
@Getter
public class InvalidCardDetailsException extends Exception {
    private final CardDetails cardDetails; // Seems okay to include this?

    /**
     * Constructs an InvalidCardDetailsException with the specified card details.
     *
     * @param cardDetails The {@code CardDetails} which caused the exception.
     */
    public InvalidCardDetailsException(CardDetails cardDetails) {
        super("No account with provided Card Details.");
        this.cardDetails = cardDetails;
    }

    /**
     * Constructs an InvalidCardDetailsException with the specified card details and causing exception.
     *
     * @param cardDetails The {@code CardDetails} which caused the exception.
     * @param cause Causing exception.
     */
    public InvalidCardDetailsException(CardDetails cardDetails, Throwable cause) {
        this(cardDetails);
        this.initCause(cause);
    }
}
