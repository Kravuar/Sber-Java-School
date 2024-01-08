package net.kravuar.terminal.spi;

import net.kravuar.terminal.domain.exceptions.spi.AuthenticationFailedException;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;


/**
 * Service for retrieving, depositing, and withdrawing funds from user accounts.
 * Access to operations requires a valid access token for user authentication and authorization.
 */
public interface BalanceService {

    /**
     * Retrieves the current balance of the account associated with the provided access token.
     *
     * @param accessToken the access token containing user authentication and authorization information.
     * @return the current balance of the user's account.
     * @throws AuthenticationFailedException if the service fails to authenticate the operation.
     */
    double getBalance(String accessToken) throws AuthenticationFailedException;

    /**
     * Deposits the specified amount to the account associated with the provided access token.
     *
     * @param accessToken the access token containing user authentication and authorization information.
     * @param amount the amount to be deposited (must be greater than 0).
     * @return the new balance after the deposit.
     * @throws IllegalArgumentException if the amount is not greater than 0.
     * @throws AuthenticationFailedException if the service fails to authenticate the operation.
     */
    double deposit(String accessToken, double amount) throws AuthenticationFailedException;

    /**
     * Withdraws the specified amount from the account associated with the provided access token.
     *
     * @param accessToken the access token containing user authentication and authorization information.
     * @param amount the amount to be deposited (must be greater than 0).
     * @return the new balance after the withdrawal.
     * @throws IllegalArgumentException if the amount is not greater than 0.
     * @throws InsufficientFundsException if the account does not have sufficient funds.
     * @throws AuthenticationFailedException if the service fails to authenticate the operation.
     */
    double withdraw(String accessToken, double amount) throws InsufficientFundsException, AuthenticationFailedException;
}