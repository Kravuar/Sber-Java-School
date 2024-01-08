package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;

/**
 * Exception thrown when a transaction fails due to insufficient funds.
 * This exception is typically thrown when an operation requiring a specific amount of funds
 * cannot be authorized due to an insufficient balance.
 */
@Getter
public class InsufficientFundsException extends Exception {
    /**
     * The shortfall amount of the failed transaction.
     */
    private final double shortfall;

    /**
     * Constructs an {@code InsufficientFundsException} with the specified shortfall amount.
     *
     * @param shortfall The shortfall amount of the transaction.
     */
    public InsufficientFundsException(double shortfall) {
        super(String.format("Transaction failed due to insufficient funds. Shortfall amount: %.2f", shortfall));
        this.shortfall = shortfall;
    }
}

