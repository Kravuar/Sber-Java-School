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
     * The amount requested in the failed transaction.
     */
    private final double requestedAmount;

    /**
     * Constructs an {@code InsufficientFundsException} with the specified requested amount.
     *
     * @param requestedAmount The amount requested in the failed transaction.
     */
    public InsufficientFundsException(double requestedAmount) {
        super(String.format("Transaction failed due to insufficient funds. Requested amount: %.2f", requestedAmount));
        this.requestedAmount = requestedAmount;
    }
}

