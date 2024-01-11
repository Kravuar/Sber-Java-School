package net.kravuar.terminal.api;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.domain.exceptions.terminal.AccountIsLockedException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidPinFormatException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TerminalTests {
    @Mock
    BalanceService balanceService;
    @Mock
    PinValidator pinValidator;

    int attemptsBeforeLock = 2;
    Duration lockDuration = Duration.ofMillis(500);
    Duration attemptsResetDuration = Duration.ofMillis(500);
    Duration sessionDuration = Duration.ofSeconds(30);

    Terminal terminal = new TerminalImpl(
            balanceService,
            pinValidator,
            attemptsBeforeLock,
            lockDuration,
            attemptsResetDuration,
            sessionDuration
    );

    @Test
    void everythingThatRequiresSessionThrowsNoEstablishedSessionException() {
        assertThrows(NoEstablishedSessionException.class, () -> terminal.getBalance());
        assertThrows(NoEstablishedSessionException.class, () -> terminal.deposit(100));
        assertThrows(NoEstablishedSessionException.class, () -> terminal.withdraw(100));
        assertThrows(NoEstablishedSessionException.class, () -> terminal.getActiveSessionDuration());
    }

    @Test
    void sessionDurationParameterGetsUpdated() {
        assertEquals(sessionDuration, terminal.getSessionDuration());

        var newDuration = Duration.ofSeconds(50);
        terminal.setSessionDuration(Duration.ofSeconds(50));

        assertEquals(newDuration, terminal.getSessionDuration());
    }

    @Test
    void locksAfterSeveralFailedAttempts() throws InvalidCardDetailsException, InvalidPinFormatException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenReturn(Optional.empty());

        var cardDetails = mock(CardDetails.class);
        var incorrectPin = new char[0];
        for (var i = 0; i < attemptsBeforeLock; ++i)
            terminal.startSession(cardDetails, incorrectPin);
        assertThrows(
                AccountIsLockedException.class,
                () -> terminal.startSession(cardDetails, incorrectPin)
        );
        assertTrue(terminal.isLocked(cardDetails));
        Awaitility.await()
                .atMost(lockDuration.plusMillis(100))
                .until(() -> !terminal.isLocked(cardDetails));
        assertFalse(terminal.isLocked(cardDetails));
    }

    @Test
    void throwsIllegalArgumentExceptionIfPinInInvalidFormat() throws InvalidCardDetailsException, InvalidPinFormatException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenThrow(IllegalArgumentException.class);

        var cardDetails = mock(CardDetails.class);
        var invalidPin = new char[0];
        assertThrows(
                IllegalArgumentException.class,
                () -> terminal.startSession(cardDetails, invalidPin)
        );
    }

    @Test
    void throwsInvalidCardDetailsExceptionIfCardDetailsAreInvalid() throws InvalidCardDetailsException, InvalidPinFormatException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenThrow(InvalidCardDetailsException.class);

        var invalidCardDetails = mock(CardDetails.class);
        var pin = new char[0];
        assertThrows(
                InvalidCardDetailsException.class,
                () -> terminal.startSession(invalidCardDetails, pin)
        );
    }

    @Test
    void wontLockIfWaitAfterFailedAttempts() throws InvalidCardDetailsException, InvalidPinFormatException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenReturn(Optional.empty());

        var cardDetails = mock(CardDetails.class);
        var incorrectPin = new char[0];
        for (var i = 0; i < attemptsBeforeLock - 1; ++i)
            terminal.startSession(cardDetails, incorrectPin);

        Awaitility.await().atMost(attemptsResetDuration);

        assertDoesNotThrow(() -> terminal.startSession(cardDetails, incorrectPin));
    }

    @Test
    void whenCorrectPinThenSessionEstablished() throws InvalidCardDetailsException, InvalidPinFormatException, InsufficientFundsException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenReturn(Optional.of("access_token"));
        when(balanceService.getBalance(eq("access_token")))
                .thenReturn(1d);
        when(balanceService.deposit(eq("access_token"), anyDouble()))
                .thenReturn(1d);
        when(balanceService.withdraw(eq("access_token"), anyDouble()))
                .thenReturn(1d);

        var cardDetails = mock(CardDetails.class);
        var pin = new char[0];
        assertTrue(terminal.startSession(cardDetails, pin));
        assertTrue(terminal.hasActiveSession());
        assertDoesNotThrow(() -> terminal.getBalance());
        assertDoesNotThrow(() -> terminal.deposit(100d));
        assertDoesNotThrow(() -> terminal.withdraw(100d));
    }

    @Test
    void whenBalanceOperationHasAmountNotDivisibleBy100ThrowsIllegalArgumentException() throws InvalidCardDetailsException, InvalidPinFormatException, InsufficientFundsException {
        when(pinValidator.authenticate(
                any(CardDetails.class),
                any(char[].class))
        ).thenReturn(Optional.of("access_token"));
        when(balanceService.deposit(eq("access_token"), anyDouble()))
                .thenReturn(1d);
        when(balanceService.withdraw(eq("access_token"), anyDouble()))
                .thenReturn(1d);

        var cardDetails = mock(CardDetails.class);
        var pin = new char[0];
        assertTrue(terminal.startSession(cardDetails, pin));
        assertTrue(terminal.hasActiveSession());
        assertThrows(IllegalArgumentException.class, () -> terminal.deposit(1d));
        assertThrows(IllegalArgumentException.class, () -> terminal.withdraw(1d));
    }
}