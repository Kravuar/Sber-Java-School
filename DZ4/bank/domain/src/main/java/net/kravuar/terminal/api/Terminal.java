package net.kravuar.terminal.api;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.domain.exceptions.terminal.AccountIsLockedException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidPinFormatException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The {@code Terminal} interface represents a terminal device with session management.
 * Users of this interface are obligated to check the session validity before invoking session-dependent methods.
 * Otherwise {@code InvalidSessionException} or {@code NoEstablishedSessionException} could be thrown.
 */
public interface Terminal {
    /**
     * Retrieves the current balance of the account associated with current session's account.
     *
     * @return the current balance of the user's account.
     * @throws NoEstablishedSessionException if action attempted without established session.
     * @throws InvalidSessionException if action attempted with invalid session.
     */
    double getBalance() throws InvalidSessionException;

    /**
     * Deposits the specified amount to the account associated with current session's account.
     *
     * @param amount the amount to be deposited (must be divisible by 100 and greater than 0).
     * @return the new balance after the deposit.
     * @throws IllegalArgumentException if the amount is not greater than 0 or not divisible by 100.
     * @throws NoEstablishedSessionException if action attempted without established session.
     * @throws InvalidSessionException if action attempted with invalid session.
     */
    double deposit(double amount) throws InvalidSessionException;

    /**
     * Withdraws the specified amount from the account associated with the provided access token.
     *
     * @param amount the amount to be withdrawn (must be divisible by 100 and greater than 0).
     * @return the new balance after the withdrawal.
     * @throws InsufficientFundsException if the account does not have sufficient funds.
     * @throws IllegalArgumentException if the amount is not greater than 0 or not divisible by 100.
     * @throws NoEstablishedSessionException if action attempted without established session.
     * @throws InvalidSessionException if action attempted with invalid session.
     */
    double withdraw(double amount) throws InsufficientFundsException, InvalidSessionException;

    /**
     * Starts session for a given card.
     * Will block the account after several unsuccessful attempts
     * (account will be unblocked automatically after some time).
     *
     * @param cardDetails card info.
     * @param pin The PIN entered by the user.
     * @return {@code boolean} indicating whether PIN was correct (session started).
     * @throws InvalidPinFormatException if the PIN is not in valid format.
     * @throws InvalidCardDetailsException if the provided CardDetails are invalid.
     * @throws AccountIsLockedException if account is locked.
     */
    boolean startSession(CardDetails cardDetails, char[] pin) throws InvalidCardDetailsException, InvalidPinFormatException;

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
    Duration getActiveSessionDuration();

    /**
     * Changes session duration (in seconds). Will be applied starting from the next session.
     *
     * @param duration duration of the session.
     * @throws IllegalArgumentException if the {@code duration} is not positive.
     */
    void setSessionDuration(Duration duration);

    /**
     * Retrieves currently set session duration parameter.
     *
     * @return time duration (in seconds).
     */
    Duration getSessionDuration();

    /**
     * Check whether account associated with provided {@code CardDetails} is locked.
     */
    boolean isLocked(CardDetails cardDetails);

    /**
     * Retrieve unlock time of account associated with provided {@code CardDetails}.
     *
     * @return {@code LocalDateTime} representing time at which account will be unlocked.
     * @throws IllegalStateException if account isn't locked.
     */
    LocalDateTime getUnlockTime(CardDetails cardDetails);
}
