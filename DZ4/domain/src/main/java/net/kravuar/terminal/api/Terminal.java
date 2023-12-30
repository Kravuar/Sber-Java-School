package net.kravuar.terminal.api;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.IncorrectPinException;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The {@code Terminal} interface represents a terminal device with session management.
 * Users of this interface are obligated to check the session validity before invoking session-dependent methods.
 * Otherwise {@code NoEstablishedSessionException} could be thrown.
 */
public interface Terminal {
    /**
     * Retrieves the current balance of the account associated with current session's account.
     *
     * @return the current balance of the user's account.
     * @throws NoEstablishedSessionException if action attempted without established session.
     */
    double getBalance() throws NoEstablishedSessionException;

    /**
     * Deposits the specified amount to the account associated with current session's account.
     *
     * @param amount the amount to be deposited (must be divisible by 100 and greater than 0).
     * @return the new balance after the deposit.
     * @throws IllegalArgumentException if the amount is not greater than 0 or not divisible by 100.
     * @throws NoEstablishedSessionException if action attempted without established session.
     */
    double deposit(double amount) throws IllegalArgumentException, NoEstablishedSessionException;

    /**
     * Withdraws the specified amount from the account associated with the provided access token.
     *
     * @param amount the amount to be withdrawn (must be divisible by 100 and greater than 0).
     * @return the new balance after the withdrawal.
     * @throws InsufficientFundsException if the account does not have sufficient funds.
     * @throws IllegalArgumentException if the amount is not greater than 0 or not divisible by 100.
     * @throws NoEstablishedSessionException if action attempted without established session.
     */
    double withdraw(double amount) throws NoEstablishedSessionException, InsufficientFundsException;

    /**
     * Starts session for a given card.
     *
     * @param cardDetails card info.
     * @param pin         The PIN entered by the user as a character array.
     * @return {@code LocalDateTime} representing time at which session will be invalidated.
     * @throws IllegalArgumentException If the PIN is not in valid format.
     * @throws IncorrectPinException If the PIN is in valid format, but doesn't match card's real PIN.
     */
    LocalDateTime startSession(CardDetails cardDetails, char[] pin) throws IncorrectPinException;

    /**
     * Ends the session ahead of schedule.
     *
     * @return false, if there wasn't one, true otherwise.
     */
    boolean endSession();

    /**
     * Check whether there's an active session.
     */
    boolean hasActiveSession();

    /**
     * Retrieve current session expiration time.
     *
     * @return {@code Duration} representing time at which session will be valid.
     * @throws NoEstablishedSessionException if there wasn't active session.
     */
    Duration getActiveSessionExpirationTime();

    /**
     * Changes session duration (in seconds). Will be applied starting from the next session.
     *
     * @param time duration in seconds.
     * @throws IllegalArgumentException if the {@code time} is less than 1.
     */
    void setSessionDuration(int time) throws IllegalArgumentException;

    /**
     * Retrieves currently set session duration.
     *
     * @return time duration (in seconds).
     */
    int getSessionDuration();

    /**
     * Check whether terminal is locked.
     */
    boolean isLocked();

    /**
     * Retrieve unlock time.
     *
     * @return {@code Duration} representing time after which terminal will be unlocked.
     * @throws NoEstablishedSessionException if isn't locked.
     */
    Duration getLockedDuration();
}
