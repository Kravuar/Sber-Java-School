package net.kravuar.terminal.spi;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.IncorrectPinException;

/**
 * Service for authenticating terminal sessions by verifying the entered PIN for a given card.
 */
public interface PinValidator {

    /**
     * Authenticates a user's PIN for a given card.
     *
     * @param cardDetails card info.
     * @param pin         The PIN entered by the user as a character array.
     * @return An accessToken containing {some stuff (like some of the card details, terminal info...)}
     *         to be used in subsequent requests to other bank services.
     * @throws IllegalArgumentException If the PIN is not in valid format.
     * @throws IncorrectPinException If the PIN is in valid format, but doesn't match card's real PIN.
     */
    String authenticate(CardDetails cardDetails, char[] pin) throws IncorrectPinException;
}
