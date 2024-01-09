package net.kravuar.terminal.domain.exceptions.terminal;

import lombok.Getter;

/**
 * Thrown to indicate that pin wasn't in correct format.
 */
@Getter
public class InvalidPinFormatException extends Exception {
    public InvalidPinFormatException() {
        super("Pin in invalid format.");
    }
}
