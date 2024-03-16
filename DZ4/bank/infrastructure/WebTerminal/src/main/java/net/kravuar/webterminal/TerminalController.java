package net.kravuar.webterminal;

import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.api.Terminal;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.domain.exceptions.terminal.AccountIsLockedException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidPinFormatException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/terminal")
@RequiredArgsConstructor
class TerminalController {
    private final Terminal terminal;

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() throws InvalidSessionException {
        return ResponseEntity.ok(terminal.getBalance());
    }

    @PostMapping("/deposit/{amount}")
    public ResponseEntity<Double> deposit(@PathVariable("amount") double amount) throws InvalidSessionException {
        return ResponseEntity.ok(terminal.deposit(amount));
    }

    @PostMapping("/withdraw/{amount}")
    public ResponseEntity<Double> withdraw(@PathVariable("amount") double amount) throws InvalidSessionException, InsufficientFundsException {
        return ResponseEntity.ok(terminal.withdraw(amount));
    }

    @PostMapping("/start-session")
    public ResponseEntity<Boolean> startSession(@RequestBody StartSessionRequest request) throws InvalidCardDetailsException, InvalidPinFormatException {
        return ResponseEntity.ok(terminal.startSession(request.cardDetails(), request.pin()));
    }

    @PostMapping("/end-session")
    public ResponseEntity<Boolean> endSession() {
        return ResponseEntity.ok(terminal.endSession());
    }

    @GetMapping("/active-session")
    public ResponseEntity<Boolean> hasActiveSession() {
        return ResponseEntity.ok(terminal.hasActiveSession());
    }

    @GetMapping("/active-session-duration")
    public ResponseEntity<Duration> getActiveSessionDuration() throws NoEstablishedSessionException {
        return ResponseEntity.ok(terminal.getActiveSessionDuration());
    }

    @GetMapping("/account-locked")
    public ResponseEntity<Boolean> isLocked(CardDetails cardDetails) {
        return ResponseEntity.ok(terminal.isLocked(cardDetails));
    }

    @GetMapping("/unlock-time")
    public ResponseEntity<LocalDateTime> getUnlockTime(CardDetails cardDetails) throws IllegalStateException {
        return ResponseEntity.ok(terminal.getUnlockTime(cardDetails));
    }

    @ExceptionHandler({InvalidSessionException.class, NoEstablishedSessionException.class})
    public ResponseEntity<String> handleUnauthorizedExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequestExceptions(Exception e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler({InvalidCardDetailsException.class, InvalidPinFormatException.class})
    public ResponseEntity<String> handleInvalidCardOrPinException(Exception e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(AccountIsLockedException.class)
    public ResponseEntity<String> handleAccountIsLockedException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    record StartSessionRequest(
            CardDetails cardDetails,
            char[] pin
    ) {
    }
}
