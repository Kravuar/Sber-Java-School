package net.kravuar.terminal.spi;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;

import java.util.Optional;

/**
 * Service for authenticating terminal sessions by verifying the entered PIN for a given card.
 */
public interface PinValidator {

    /**
     * Authenticates a user's PIN for a given card.
     *
     * @param cardDetails card info.
     * @param pin The PIN entered by the user as a single integer.
     * @return An accessToken containing {some stuff (like some of the card details, terminal info...)}
     *         to be used in subsequent requests to other bank services.
     * @throws IllegalArgumentException if the PIN is not in valid format.
     * @throws InvalidCardDetailsException If the provided {@code CardDetails} are invalid.
     */
    Optional<String> authenticate(CardDetails cardDetails, int pin);
}
