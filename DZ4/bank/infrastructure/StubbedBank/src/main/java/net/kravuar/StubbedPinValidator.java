package net.kravuar;

import lombok.RequiredArgsConstructor;
import net.kravuar.exceptions.AccountNotFoundException;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.spi.PinValidator;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Validates pin only if the last digit is {@code SPECIAL_DIGIT}.
 */
@RequiredArgsConstructor
public class StubbedPinValidator implements PinValidator {
    private static final char SPECIAL_DIGIT = '0';
    private final AccountService accountService;

    @Override
    public Optional<String> authenticate(CardDetails cardDetails, char[] pin) throws InvalidCardDetailsException {
        if (pin.length != 4 || !IntStream.range(0, 4).mapToObj(i -> pin[i]).allMatch(Character::isDigit))
            throw new IllegalArgumentException("Incorrect pin format.");
        if (pin[3] != SPECIAL_DIGIT)
            return Optional.empty();
        try {
            return Optional.of(accountService.toToken(cardDetails));
        } catch (AccountNotFoundException e) {
            throw new InvalidCardDetailsException(cardDetails, e);
        }
    }
}
