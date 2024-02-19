package net.kravuar;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionManagerTest {
    @Test
    void givenTasksExecuted_WhenAllTasksComplete_ThenCallbackIsInvoked() throws InterruptedException {
        // given
        ExecutionManager executionManager = new ExecutionManager();
        Runnable noop = () -> {};
        CountDownLatch finished = new CountDownLatch(1);

        // when
        ExecutionManager.Context context = executionManager.execute(
                finished::countDown,
                noop,
                noop,
                noop
        );

        // then
        finished.await();
        assertTrue(context.isFinished());
        assertEquals(3, context.getCompletedTaskCount());
        assertEquals(0, context.getFailedTaskCount());
        assertEquals(0, context.getInterruptedTaskCount());
    }

    @Test
    void givenTasksExecuted_WhenSomeTasksFail_ThenFailedTaskCountIsIncremented() throws InterruptedException {
        // given
        ExecutionManager executionManager = new ExecutionManager();
        Runnable fail = () -> {throw new RuntimeException();};
        CountDownLatch finished = new CountDownLatch(1);

        // when
        ExecutionManager.Context context = executionManager.execute(
                finished::countDown,
                fail,
                fail,
                fail
        );

        // then
        finished.await();
        assertTrue(context.isFinished());
        assertEquals(0, context.getCompletedTaskCount());
        assertEquals(3, context.getFailedTaskCount());
        assertEquals(0, context.getInterruptedTaskCount());
    }

    @Test
    void givenTasksExecuted_WhenSomeTasksInterrupted_ThenInterruptedTaskCountIsIncremented() throws InterruptedException {
        // given
        ExecutionManager executionManager = new ExecutionManager();
        Runnable interrupt = () -> Thread.currentThread().interrupt();
        CountDownLatch finished = new CountDownLatch(1);

        // when
        ExecutionManager.Context context = executionManager.execute(
                finished::countDown,
                interrupt,
                interrupt,
                interrupt
        );

        // then
        finished.await();
        assertTrue(context.isFinished());
        assertEquals(0, context.getCompletedTaskCount());
        assertEquals(0, context.getFailedTaskCount());
        assertEquals(3, context.getInterruptedTaskCount());
    }

    @Test
    void givenTasksExecuted_WhenContextInterrupt_ThenTasksInterrupted() throws InterruptedException {
        // given
        ExecutionManager executionManager = new ExecutionManager();
        Runnable interruptWaiter = () -> {
            while (!Thread.currentThread().isInterrupted()) {}
        };
        CountDownLatch finished = new CountDownLatch(1);

        // when
        ExecutionManager.Context context = executionManager.execute(
                finished::countDown,
                interruptWaiter,
                interruptWaiter,
                interruptWaiter
        );
        Thread.sleep(50); // wait for task to be submitted
        context.interrupt();

        // then
        finished.await();
        assertTrue(context.isFinished());
        assertEquals(0, context.getCompletedTaskCount());
        assertEquals(0, context.getFailedTaskCount());
        assertEquals(3, context.getInterruptedTaskCount());
    }
}