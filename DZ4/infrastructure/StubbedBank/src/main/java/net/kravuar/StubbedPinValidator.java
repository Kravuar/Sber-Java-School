package net.kravuar;

import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.IncorrectPinException;
import net.kravuar.terminal.spi.PinValidator;

import java.util.stream.IntStream;

/**
 * Validates pin only if the last digit is {@code SPECIAL_DIGIT}.
 */
@RequiredArgsConstructor
public class StubbedPinValidator implements PinValidator {
    private static final int SPECIAL_DIGIT = 0;
    private final CardDetailsToAccessTokenMapper mapper;

    @Override
    public String authenticate(CardDetails cardDetails, char[] pin) throws IncorrectPinException {
        if (pin.length != 4 || !IntStream.range(0, 4).mapToObj(i -> pin[i]).allMatch(Character::isDigit))
            throw new IllegalArgumentException("Incorrect pin format.");
        if (pin[3] != SPECIAL_DIGIT)
            throw new IncorrectPinException();
        return mapper.toToken(cardDetails);
    }
}
