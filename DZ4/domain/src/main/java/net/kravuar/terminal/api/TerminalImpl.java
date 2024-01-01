package net.kravuar.terminal.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.IncorrectPinException;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.AccountIsLockedException;
import net.kravuar.terminal.domain.lock.LockStorage;
import net.kravuar.terminal.domain.session.Session;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class TerminalImpl implements Terminal {
    private static final int ATTEMPTS_BEFORE_LOCK = 3;
    private static final Duration LOCK_TIME = Duration.of(15, ChronoUnit.SECONDS);
    private static final Duration ATTEMPTS_RESET_AFTER = Duration.of(30, ChronoUnit.SECONDS);

    private final BalanceService balanceService;
    private final PinValidator pinValidator;

    @Getter
    private int sessionDuration;
    private Session activeSession = null;

    private final LockStorage lockStorage = new LockStorage(ATTEMPTS_RESET_AFTER);


    /**
     * Constructs a {@code TerminalImpl} with the specified BalanceService, PinValidator and sessionDuration.
     *
     * @throws IllegalArgumentException if provided session duration is less than 1.
     */
    public TerminalImpl(BalanceService balanceService, PinValidator pinValidator, int sessionDuration) {
        this(balanceService, pinValidator);
        if (sessionDuration < 1)
            throw new IllegalArgumentException("Session duration cannot be less than 1.");
        this.sessionDuration = sessionDuration;
    }

    @Override
    public double getBalance()  {
        if (!hasActiveSession())
            throw new IllegalStateException("No session established.");
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
    public LocalDateTime startSession(CardDetails cardDetails, char[] pin) throws IncorrectPinException {
        if (isLocked(cardDetails))
            throw new AccountIsLockedException(getLockedDuration());

        try {
            var accessToken = pinValidator.authenticate(cardDetails, pin);
            var expiresAt = LocalDateTime.now().plusSeconds(sessionDuration);
            this.activeSession = new Session(
                    cardDetails,
                    accessToken,
                    expiresAt
            );
            return expiresAt;
        } catch (IncorrectPinException e) {
            lockStorage.putFailAttempt(cardDetails);
            if (lockStorage.getFailAttempts(cardDetails) == ATTEMPTS_BEFORE_LOCK)
                lockStorage.lock(cardDetails, LOCK_TIME);
            throw e;
        }
    }

    @Override
    public boolean endSession() {
        var result = activeSession != null;
        activeSession = null;
        return result;
    }

    @Override
    public boolean hasActiveSession() {
        return activeSession != null
                && activeSession.expiresAt().isAfter(LocalDateTime.now());
    }

    @Override
    public Duration getActiveSessionExpirationTime() {
        if (hasActiveSession())
            return Duration.between(LocalDateTime.now(), activeSession.expiresAt());
        throw new IllegalStateException("No session established.");
    }

    @Override
    public void setSessionDuration(int duration) throws IllegalArgumentException {
        if (duration < 1)
            throw new IllegalArgumentException("Session duration cannot be less than 1.");
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
