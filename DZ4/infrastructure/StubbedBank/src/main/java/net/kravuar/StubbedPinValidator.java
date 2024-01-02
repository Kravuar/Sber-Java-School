package net.kravuar;

import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.spi.PinValidator;

import java.util.Optional;

/**
 * Validates pin only if the last digit is {@code SPECIAL_DIGIT}.
 */
@RequiredArgsConstructor
public class StubbedPinValidator implements PinValidator {
    private static final int SPECIAL_DIGIT = 0;
    private final CardDetailsToAccessTokenMapper mapper;

    @Override
    public Optional<String> authenticate(CardDetails cardDetails, int pin) {
        if (cardDetails.id() < 0)
            throw new InvalidCardDetailsException();
        if (pin <= 999 || pin > 9999)
            throw new IllegalArgumentException("Incorrect pin format.");
        if (pin % 10 != SPECIAL_DIGIT)
            return Optional.empty();
        return Optional.of(mapper.toToken(cardDetails));
    }
}
