package net.kravuar.terminal.api;

import lombok.Getter;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.AccountIsLockedException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;
import net.kravuar.terminal.domain.lock.LockStorage;
import net.kravuar.terminal.domain.session.Session;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;

import java.time.Duration;
import java.time.LocalDateTime;

public class TerminalImpl implements Terminal {
    private final int attemptsBeforeLock;
    private final Duration lockDuration;

    private final BalanceService balanceService;
    private final PinValidator pinValidator;

    @Getter
    private Duration sessionDuration;
    private Session activeSession = null;

    private final LockStorage lockStorage;


    /**
     * Constructs a {@code TerminalImpl} with the specified {@code BalanceService}, {@code PinValidator}, {@code sessionDuration}, {@code attemptsBeforeLock}, {@code lockDuration}, {@code attemptsResetDuration}.
     *
     * @throws IllegalArgumentException if provided session duration is not positive.
     */
    public TerminalImpl(BalanceService balanceService, PinValidator pinValidator, int attemptsBeforeLock, Duration lockDuration, Duration attemptsResetDuration, Duration sessionDuration) {
        if (!sessionDuration.isPositive())
            throw new IllegalArgumentException("Session should be positive.");
        this.balanceService = balanceService;
        this.pinValidator = pinValidator;
        this.attemptsBeforeLock = attemptsBeforeLock;
        this.lockDuration = lockDuration;
        this.sessionDuration = sessionDuration;
        this.lockStorage = new LockStorage(attemptsResetDuration);
    }

    @Override
    public double getBalance()  {
        if (!hasActiveSession())
            throw new NoEstablishedSessionException();
        try {
            return balanceService.getBalance(activeSession.accessToken());
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException();
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public double deposit(double amount) {
        if (amount % 100 != 0)
            throw new IllegalArgumentException("Amount should be divisible by 100.");
        if (!hasActiveSession())
            throw new IllegalStateException("No session established.");
        try {
            return balanceService.deposit(activeSession.accessToken(), amount);
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException();
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public double withdraw(double amount) throws InsufficientFundsException {
        if (!hasActiveSession())
            throw new IllegalStateException("No session established.");
        if (amount % 100 != 0)
            throw new IllegalArgumentException("Amount should be divisible by 100.");
        try {
            return balanceService.withdraw(activeSession.accessToken(), amount);
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException();
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public boolean startSession(CardDetails cardDetails, int pin) {
        if (isLocked(cardDetails))
            throw new AccountIsLockedException(getLockedDuration());

        var accessToken = pinValidator.authenticate(cardDetails, pin);
        if (accessToken.isEmpty()) {
            lockStorage.putFailAttempt(cardDetails);
            if (lockStorage.getFailAttempts(cardDetails) == attemptsBeforeLock)
                lockStorage.lock(cardDetails, lockDuration);
            return false;
        }
        var expiresAt = LocalDateTime.now().plus(sessionDuration);
        this.activeSession = new Session(
                cardDetails,
                accessToken.get(),
                expiresAt
        );
        return true;
    }

    @Override
    public boolean endSession() {
        var result = activeSession != null;
        activeSession = null;
        return result;
    }

    @Override
    public boolean hasActiveSession() {
        return activeSession != null && activeSession.expiresAt().isAfter(LocalDateTime.now());
    }

    @Override
    public Duration getActiveSessionExpirationTime() {
        if (hasActiveSession())
            return Duration.between(LocalDateTime.now(), activeSession.expiresAt());
        throw new IllegalStateException("No session established.");
    }

    @Override
    public void setSessionDuration(Duration duration) throws IllegalArgumentException {
        if (!duration.isPositive())
            throw new IllegalArgumentException("Session duration should be positive.");
        sessionDuration = duration;
    }

    @Override
    public boolean isLocked(CardDetails cardDetails) {
        return lockStorage.isLocked(cardDetails);
    }

    @Override
    public Duration getLockedDuration() {
        if (hasActiveSession()) {
            var cardDetails = activeSession.cardDetails();
            return getLockedDuration(cardDetails);
        }
        throw new IllegalStateException("No session established.");
    }

    @Override
    public Duration getLockedDuration(CardDetails cardDetails) {
        return lockStorage.getLockDuration(cardDetails);
    }
}