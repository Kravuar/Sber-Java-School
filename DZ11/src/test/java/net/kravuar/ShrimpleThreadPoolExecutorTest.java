package net.kravuar;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ShrimpleThreadPoolExecutorTest {
    private ShrimpleThreadPoolExecutor executorService;

    @Test
    void givenExecutorService_whenExecutingTasksLessThanCore_ThenTasksInWorkers() {
        // given
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 2;
        int maximumPoolSize = 2;
        long keepAliveTime = 0;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        executorService.execute(createSleepingRunnable(wakeup));
        executorService.execute(createSleepingRunnable(wakeup));

        // then
        assertEquals(2, executorService.getWorkers().size());
        assertTrue(executorService.getTasks().isEmpty());

        wakeup.countDown();
    }

    @Test
    void givenExecutorService_whenExecutingTasksMoreThanCore_ThenTasksInWorkers_AndOverheadIsInQueue() {
        // given
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(3);
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 2;
        int maximumPoolSize = 2;
        long keepAliveTime = 0;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        executorService.execute(createSleepingRunnable(wakeup));
        executorService.execute(createSleepingRunnable(wakeup));
        executorService.execute(createSleepingRunnable(wakeup)); // overhead
        executorService.execute(createSleepingRunnable(wakeup)); // overhead
        executorService.execute(createSleepingRunnable(wakeup)); // overhead

        // then
        assertEquals(2, executorService.getWorkers().size());
        assertEquals(3, executorService.getTasks().size());

        wakeup.countDown();
    }

    @Test
    void givenExecutorService_whenHasMaxWorkers_ThenThrowsRejectedException() {
        // given
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        long keepAliveTime = 0;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        executorService.execute(createSleepingRunnable(wakeup)); // goes into worker
        executorService.execute(createSleepingRunnable(wakeup)); // goes into queue

        // then
        assertThrows(RejectedExecutionException.class, () -> executorService.execute(createSleepingRunnable(wakeup)));

        wakeup.countDown();
    }

    @Test
    void givenExecutorService_AndKeepAliveTime_WhenHasMoreThanCoreBusy_AndExtraWorkerFinishes_ThenItGetsRemovedAfterKeepAlive() {
        // given
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
        taskQueue.add(() -> {}); // stub so that non-core get created, instead of putting task into the queue
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 1;
        int maximumPoolSize = 2;
        long keepAliveTime = 200;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        executorService.execute(createSleepingRunnable(wakeup));
        executorService.execute(() -> {}); // non-core, finishes immediately

        // then
        assertEquals(2, executorService.getWorkers().size());
        Awaitility.await().atLeast(keepAliveTime, TimeUnit.MILLISECONDS);
        assertEquals(1, executorService.getWorkers().size());

        wakeup.countDown();
    }

    @Test
    void givenExecutorService_AndKeepAliveTime_WhenHasMoreThanCoreBusy_AndExtraWorkerFinishes_ThenItConsumesNextFromTheQueue() {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
        taskQueue.add(() -> {}); // task that will be consumed by extra worker, after he finished its initial task
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 1;
        int maximumPoolSize = 2;
        long keepAliveTime = 200;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        executorService.execute(createSleepingRunnable(wakeup));

        // then
        assertEquals(1, executorService.getTasks().size());
        executorService.execute(() -> {}); // non-core, finishes immediately
        Awaitility.await().until(() -> executorService.getTasks().isEmpty()); // almost immediately
        assertEquals(0, executorService.getTasks().size());

        wakeup.countDown();
    }

    @Test
    void givenExecutorService_WhenSubmitWithFuture_ThenFutureWorks() throws ExecutionException, InterruptedException {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
        ThreadFactory threadFactory = Thread::new;
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        long keepAliveTime = 0;

        executorService = new ShrimpleThreadPoolExecutor(taskQueue, threadFactory, corePoolSize, maximumPoolSize, keepAliveTime);
        executorService.start();

        CountDownLatch wakeup = new CountDownLatch(1);

        // when
        Future<Integer> future = executorService.submit(createSleepingRunnable(wakeup), 42);

        // then
        assertFalse(future.isDone());
        wakeup.countDown();// makes future finish
        Awaitility.await().until(future::isDone);
        assertTrue(future.isDone());
        assertEquals(42, future.get());
    }

    private Runnable createSleepingRunnable(CountDownLatch countDownLatch) {
        return () -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
