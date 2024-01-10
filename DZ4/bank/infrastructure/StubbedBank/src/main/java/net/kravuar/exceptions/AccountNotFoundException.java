package net.kravuar.exceptions;

import lombok.Getter;
import net.kravuar.terminal.domain.card.CardDetails;

/**
 * Thrown to indicate that there was no account associated with provided {@link CardDetails}.
 */
@Getter
public class AccountNotFoundException extends RuntimeException {
    private final CardDetails cardDetails; // Seems okay to include this?

    /**
     * Constructs an InvalidAccessTokenException with the specified access token.
     *
     * @param cardDetails The {@link CardDetails} which caused the exception.
     */
    public AccountNotFoundException(CardDetails cardDetails) {
        super("No account with provided Card Details.");
        this.cardDetails = cardDetails;
    }
}
