package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;

/**
 * Exception thrown when the provided PIN doesn't match the card's real PIN.
 * Not including anything related to pin in here due to security considerations
 * (or maybe the provided one can be included?).
 */
@Getter
public class IncorrectPinException extends Exception {
    /**
     * Constructs a {@code IncorrectPinException} with the default detail message.
     */
    public IncorrectPinException() {
        super("Provided PIN doesn't match the card's real");
    }
}
