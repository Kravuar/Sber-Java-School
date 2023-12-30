package net.kravuar.terminal.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.IncorrectPinException;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;
import net.kravuar.terminal.domain.session.Session;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TerminalImpl implements Terminal {
    private final BalanceService balanceService;
    private final PinValidator pinValidator;

    private Session activeSession = null;
    @Getter
    private int sessionDuration;

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
    public double getBalance() throws NoEstablishedSessionException {
        if (!hasActiveSession())
            throw new NoEstablishedSessionException("getBalance");
        try {
            return balanceService.getBalance(activeSession.accessToken());
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException("getBalance");
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public double deposit(double amount) throws IllegalArgumentException, NoEstablishedSessionException {
        if (amount % 100 != 0)
            throw new IllegalArgumentException("Amount should be divisible by 100.");
        if (!hasActiveSession())
            throw new NoEstablishedSessionException("deposit");
        try {
            return balanceService.deposit(activeSession.accessToken(), amount);
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException("deposit");
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public double withdraw(double amount) throws NoEstablishedSessionException, InsufficientFundsException {
        if (amount % 100 != 0)
            throw new IllegalArgumentException("Amount should be divisible by 100.");
        if (!hasActiveSession())
            throw new NoEstablishedSessionException("withdraw");
        try {
            return balanceService.withdraw(activeSession.accessToken(), amount);
        } catch (InvalidAccessTokenException e) {
            var wrapped = new InvalidSessionException("withdraw");
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override
    public LocalDateTime startSession(CardDetails cardDetails, char[] pin) throws IncorrectPinException {
        var accessToken = pinValidator.authenticate(cardDetails, pin);
        var expiresAt = LocalDateTime.now().plusSeconds(sessionDuration);
        this.activeSession = new Session(
                cardDetails,
                accessToken,
                expiresAt
        );
        return expiresAt;
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
    public LocalDateTime getActiveSessionExpirationTime() {
        if (hasActiveSession())
            return activeSession.expiresAt();
        return LocalDateTime.MIN;
    }

    @Override
    public void setSessionDuration(int duration) throws IllegalArgumentException {
        if (duration < 1)
            throw new IllegalArgumentException("Session duration cannot be less than 1.");
        sessionDuration = duration;
    }
}
