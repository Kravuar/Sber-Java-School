package net.kravuar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
public class ShrimpleThreadPoolExecutor extends SimpleAbstractExecutorService {
    @Getter // providing a getter for debugging/testing purposes only, just like the jdk
    private final Set<Worker> workers = new HashSet<>();
    @Getter // providing a getter for debugging/testing purposes only, just like the jdk
    private final BlockingQueue<Runnable> tasks;
    private final ThreadFactory threadFactory;
    @Getter
    @Setter
    private volatile int corePoolSize;
    @Getter
    @Setter
    private volatile int maximumPoolSize;
    @Getter
    @Setter
    private volatile long keepAliveTimeInMs; // simplified time unit as well
    private final CountDownLatch start = new CountDownLatch(1);
    private final ReentrantLock mainLock = new ReentrantLock();

    @Override
    public void start() {
        start.countDown();
    }

    @Override
    public void execute(Runnable task) {
        // Not reached corePoolSize yet, so the queue is still empty, mitigating it
        if (workers.size() < corePoolSize)
            if (addWorker(task))
                return;

        // synchronizing on mainLock so that we won't run out of workers while we're enqueuing task
        // (and because I'm not doing all this atomic, bitwise synchronization jdk does)
        mainLock.lock();
        try {
            // corePoolSize is reached, try to enqueue task or start a new thread;
            // it's worth noting that if queue is full, newly submitted task will bypass the queue
            // (it's how the jdk does it)
            if (!tasks.offer(task) && !addWorker(task))
                // failed, throwing exception since I've simplified away the rejectHandler stuff.
                throw new RejectedExecutionException();
        } finally {
            mainLock.unlock();
        }
    }

    private boolean addWorker(Runnable task) {
        if (workers.size() == maximumPoolSize)
            return false;

        Worker worker = new Worker(task);
        Thread workerThread = worker.thread;
        if (workerThread != null) {
            mainLock.lock();
            try {
                if (!workers.add(worker)) // doesn't look likely
                    return false;
                workerThread.start(); // start the worker loop
                return true;
            } finally {
                mainLock.unlock();
            }
        }
        return false;
    }

    private void processWorkerExit(Worker worker, boolean completedAbruptly) {
        mainLock.lock();
        try {
            workers.remove(worker);
        } finally {
            mainLock.unlock();
        }

        if (!completedAbruptly && workers.size() >= corePoolSize)
            return;
        addWorker(null); // replace with a new one, if worker terminated due to task exceptions
    }

    private void runWorker(Worker worker) {
        Runnable task = worker.initialTask;
        worker.initialTask = null;

        worker.unlock(); // from now worker can be interrupted
        boolean completedAbruptly = true;
        try {
            start.await();

            // here worker is unlocked, jdk implementation uses this fact to distinguish
            // between idle workers (waiting for task) and active workers.
            while (task != null || (task = acquireTask()) != null) {
                worker.lock(); // locked - means executing task

                try {
                    task.run();
                } finally {
                    task = null;
                    worker.unlock();
                }
            }
            completedAbruptly = false;
        } catch (InterruptedException e) {
            processWorkerExit(worker, false);
        } finally {
            processWorkerExit(worker, completedAbruptly);
        }
    }

    private Runnable acquireTask() {
        boolean lastPollTimedOut = false;

        for (;;) {
            int workersCount = workers.size();
            boolean timed = workersCount > corePoolSize;

            // if we have more than max workers due to setter call or if timed out
            // also recheck the queue once again if we're the last one worker
            if (workersCount > maximumPoolSize || timed && lastPollTimedOut && (workersCount > 1 || tasks.isEmpty()))
                return null;

            try {
                Runnable task = timed
                        ? tasks.poll(keepAliveTimeInMs, TimeUnit.NANOSECONDS)
                        : tasks.take();
                if (task != null)
                    return task;
                lastPollTimedOut = true;
            } catch (InterruptedException retryPoll) {
                lastPollTimedOut = false;
            }
        }
    }

    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private Runnable initialTask;
        private final Thread thread;

        private Worker(Runnable initialTask) {
            setState(-1); // inhibiting interruption until worker actually starts to work
            this.initialTask = initialTask;
            this.thread = threadFactory.newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() { acquire(1); }
        public void unlock() { release(1); }
    }
}