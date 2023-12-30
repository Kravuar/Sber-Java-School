package net.kravuar;

import net.kravuar.terminal.api.Terminal;
import net.kravuar.terminal.api.TerminalImpl;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
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

        var db = new HashMap<Long, Double>();
        db.put(1L, 99.9);
        db.put(2L, 43509d);
        db.put(3L, 1d);
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
                    handleSessionInfo();
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
        System.out.println("6. Session Info");
        System.out.println("7. Set Session Duration");
        System.out.println("8. Get Session Duration");
        System.out.println("9. Check if Terminal is Locked");
        System.out.println("10. Get Locked Duration");
        System.out.println("11. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void handleViewDB() {
        var stubbed = (StubbedBalanceService) balanceService;
        System.out.println("Bank DB:");
        System.out.println(stubbed);
    }

    private static void handleGetBalance() {
        try {
            double balance = terminal.getBalance();
            System.out.println("Current balance: " + balance);
        } catch (NoEstablishedSessionException e) {
            System.out.println("No established session. " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();

        try {
            double newBalance = terminal.deposit(amount);
            System.out.println("New balance after deposit: " + newBalance);
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit Error: " + e.getMessage());
        } catch (NoEstablishedSessionException e) {
            System.out.println("No established session. " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();

        try {
            double newBalance = terminal.withdraw(amount);
            System.out.println("New balance after withdrawal: " + newBalance);
        } catch (NoEstablishedSessionException e) {
            System.out.println("No established session. " + e.getMessage());
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            System.out.println("Withdraw Error: " + e.getMessage());
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

    private static void handleSessionInfo() {
        try {
            var expirationTime = terminal.getActiveSessionExpirationTime();
            System.out.println("Session will be active for: " + expirationTime);
        } catch (NoEstablishedSessionException e) {
            System.out.println("No established session. " + e.getMessage());
        }
    }

    private static void handleSetSessionDuration() {
        System.out.print("Enter new session duration (in seconds): ");
        int duration = scanner.nextInt();

        try {
            terminal.setSessionDuration(duration);
            System.out.println("Session duration set successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid duration: " + e.getMessage());
        }
    }

    private static void handleGetSessionDuration() {
        int duration = terminal.getSessionDuration();
        System.out.println("Current session duration: " + duration + " seconds");
    }

    private static void handleIsLocked() {
        boolean isLocked = terminal.isLocked();
        System.out.println("Terminal is locked: " + isLocked);
    }

    private static void handleGetLockedDuration() {
        try {
            var lockedDuration = terminal.getLockedDuration();
            System.out.println("Terminal will be locked for: " + lockedDuration);
        } catch (NoEstablishedSessionException e) {
            System.out.println("No established session. " + e.getMessage());
        }
    }
}