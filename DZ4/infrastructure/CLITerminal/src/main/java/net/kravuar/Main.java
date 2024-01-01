package net.kravuar;

import net.kravuar.terminal.api.Terminal;
import net.kravuar.terminal.api.TerminalImpl;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidSessionException;
import net.kravuar.terminal.domain.exceptions.terminal.NoEstablishedSessionException;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static final CardDetailsToAccessTokenMapper mapper;
    private static final BalanceService balanceService;
    private static final PinValidator pinValidator;
    private static final Terminal terminal;
    private static final Scanner scanner = new Scanner(System.in);

    static {
        mapper = new IdBasedCardDetailsToAccessTokenMapperImpl();

        var db = new HashMap<CardDetails, Double>();
        db.put(new CardDetails(1L), 99.9);
        db.put(new CardDetails(2L), 43509d);
        db.put(new CardDetails(3L), 1d);
        balanceService = new StubbedBalanceService(db, mapper);

        pinValidator = new StubbedPinValidator(mapper);

        terminal = new TerminalImpl(balanceService, pinValidator);
    }

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    handleViewDB();
                    break;
                case 1:
                    handleGetBalance();
                    break;
                case 2:
                    handleDeposit();
                    break;
                case 3:
                    handleWithdraw();
                    break;
                case 4:
                    handleStartSession();
                    break;
                case 5:
                    handleEndSession();
                    break;
                case 6:
                    handleGetSessionExpirationTime();
                    break;
                case 7:
                    handleSetSessionDuration();
                    break;
                case 8:
                    handleGetSessionDuration();
                    break;
                case 9:
                    handleIsLocked();
                    break;
                case 10:
                    handleGetLockedDuration();
                    break;
                case 11:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("0. View Bank DB");
        System.out.println("1. Get Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Start Session");
        System.out.println("5. End Session");
        System.out.println("6. Get Session Expiration Duration");
        System.out.println("7. Set Session Duration");
        System.out.println("8. Get Session Duration");
        System.out.println("9. Check if account is Locked");
        System.out.println("10. Get Locked Duration");
        System.out.println("11. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void handleViewDB() {
        var stubbed = (StubbedBalanceService) balanceService;
        System.out.println("Bank DB:");
        System.out.println(stubbed.getAccounts());
    }

    private static void handleGetBalance() {
        try {
            double balance = terminal.getBalance();
            System.out.println("Current balance: " + balance);
        } catch (NoEstablishedSessionException | InvalidSessionException e) {
            System.err.println("Session Problems: " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();

        try {
            double newBalance = terminal.deposit(amount);
            System.out.println("New balance after deposit: " + newBalance);
        } catch (IllegalArgumentException e) {
            System.err.println("Deposit Error: " + e.getMessage());
        } catch (NoEstablishedSessionException | InvalidSessionException e) {
            System.err.println("Session Problems: " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();

        try {
            double newBalance = terminal.withdraw(amount);
            System.out.println("New balance after withdrawal: " + newBalance);
        } catch (NoEstablishedSessionException | InvalidSessionException e) {
            System.err.println("Session Problems: " + e.getMessage());
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            System.err.println("Withdraw Error: " + e.getMessage());
        }
    }

    private static void handleStartSession() {
        // TODO: gather card, pin
        // terminal.startSession(cardDetails, pin)
    }

    private static void handleEndSession() {
        boolean ended = terminal.endSession();
        System.out.println("Session ended: " + ended);
    }

    private static void handleGetSessionExpirationTime() {
        try {
            var expirationTime = terminal.getActiveSessionExpirationTime();
            System.out.println("Session will be active for: " + expirationTime);
        } catch (NoEstablishedSessionException e) {
            System.err.println("Session Problems: " + e.getMessage());
        }
    }

    private static void handleSetSessionDuration() {
        System.out.print("Enter new session duration (in seconds): ");
        int duration = scanner.nextInt();

        try {
            terminal.setSessionDuration(duration);
            System.out.println("Session duration set successfully.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid duration: " + e.getMessage());
        }
    }

    private static void handleGetSessionDuration() {
        int duration = terminal.getSessionDuration();
        System.out.println("New session will be valid for: " + duration + " seconds");
    }

    private static void handleIsLocked() {
        System.out.println("Enter card id: ");
        int id = scanner.nextInt();
        var cardDetails = new CardDetails(id);
        boolean isLocked = terminal.isLocked(cardDetails);
        System.out.println("Account is locked: " + isLocked);
    }

    private static void handleGetLockedDuration() {
        System.out.println("Enter card id: ");
        int id = scanner.nextInt();
        var cardDetails = new CardDetails(id);

        try {
            var lockedDuration = terminal.getLockedDuration(cardDetails);
            System.out.println("Account will be locked for: " + lockedDuration);
        } catch (IllegalStateException e) {
            System.err.println("Account isn't locked.");
        }
    }
}