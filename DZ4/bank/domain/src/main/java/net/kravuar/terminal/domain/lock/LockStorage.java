package net.kravuar.terminal.domain.lock;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

/**
 * A storage class for managing locks associated with card details.
 */
@RequiredArgsConstructor
public class LockStorage {
    private final Map<CardDetails, LockInfo> entries = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<CardDetails, ScheduledFuture<?>> expirationTasks = new ConcurrentHashMap<>();
    /**
     * If no fail attempts were performed for {@code resetAfterIdleDuration}, entry will be invalidated.
     */
    private final Duration resetAfterIdleDuration;

    /**
     * Increments lock fail attempts for specified {@link CardDetails}.
     * Creates a new lock entry if one does not exist for the specified card details.
     *
     * @param cardDetails The card details to associate with the fail attempts.
     */
    public void putFailAttempt(CardDetails cardDetails) {
        entries.compute(cardDetails, (key, existingEntry) -> {
            if (existingEntry == null)
                return LockInfo.builder()
                        .failAttempts(1)
                        .build();
            else {
                existingEntry.incrementFailAttempts();
                return existingEntry;
            }
        });

        resetTimer(cardDetails, resetAfterIdleDuration);
    }

    /**
     * Gets the number of fail attempts for the specified card details.
     *
     * @param cardDetails The card details for which to retrieve the fail attempts.
     * @return The number of fail attempts for the specified card details.
     */
    public int getFailAttempts(CardDetails cardDetails) {
        LockInfo entry = entries.get(cardDetails);
        return (entry != null) ? entry.getFailAttempts() : 0;
    }

    /**
     * Checks if the specified card details are currently locked.
     *
     * @param cardDetails The card details to check for lock status.
     * @return True if the card details are locked; false otherwise.
     */
    public boolean isLocked(CardDetails cardDetails) {
        LockInfo entry = entries.get(cardDetails);
        return (entry != null) && entry.isLocked();
    }

    /**
     * Retrieves lock duration for specified {@link CardDetails}.
     *
     * @param cardDetails The card details of the account to check.
     * @return {@code Duration} Remaining duration of the lock.
     * @throws IllegalStateException if card details aren't locked.
     */
    public LocalDateTime getLockDuration(CardDetails cardDetails) {
        LockInfo entry = entries.get(cardDetails);
        if (entry == null || !entry.isLocked())
            throw new IllegalStateException("Account isn't locked.");
        return entry.unlockTime;
    }

    /**
     * Locks the specified card details for the given duration.
     *
     * @param cardDetails The card details to lock.
     * @param duration The duration for which to lock the card details.
     * @throws IllegalStateException If account is already locked.
     */
    public void lock(CardDetails cardDetails, Duration duration) {
        var lock = entries.get(cardDetails);
        if (lock == null) {
            lock = LockInfo.builder()
                    .failAttempts(-1)
                    .unlockTime(LocalDateTime.now().plus(duration))
                    .build();
            entries.put(cardDetails, lock);
        } else {
            if (lock.isLocked())
                throw new IllegalStateException("Entry already locked.");
            lock.unlockTime = LocalDateTime.now().plus(duration);
        }

        resetTimer(cardDetails, duration);
    }

    private void resetTimer(CardDetails cardDetails, Duration duration) {
        var existingTask = expirationTasks.get(cardDetails);
        if (existingTask != null)
            existingTask.cancel(false);

        var newTask = scheduler.schedule(
                () -> remove(cardDetails),
                duration.toMillis(),
                TimeUnit.MILLISECONDS
        );

        expirationTasks.put(cardDetails, newTask);
    }

    private void remove(CardDetails cardDetails) {
        entries.remove(cardDetails);
        expirationTasks.remove(cardDetails);
    }

    @Builder
    private static class LockInfo {
        @Getter
        private int failAttempts;
        private LocalDateTime unlockTime;

        public void incrementFailAttempts() {
            failAttempts++;
        }

        public boolean isLocked() {
            return unlockTime != null && unlockTime.isAfter(LocalDateTime.now());
        }
    }
}
