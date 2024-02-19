package net.kravuar;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ExecutionManager {
    // For 'execute' executions only
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Context execute(Runnable callback, Runnable... tasks) {
        // Will execute tasks
        var taskExecutor = Executors.newThreadPerTaskExecutor(Thread::new);
        // Context for this execution
        var context = new Context(tasks.length, taskExecutor);
        executor.submit(() -> {
            try {
                taskExecutor.invokeAll(Stream.of(tasks)
                        .map(task -> wrapWithCallbacks(task, context))
                        .toList()
                ).forEach(voidFuture -> {
                    try {
                        voidFuture.get();
                    } catch (InterruptedException | ExecutionException ignored) {}
                });
                // waiting for all tasks to finish in forEach, then callback
                callback.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return context;
    }

    private Callable<Void> wrapWithCallbacks(Runnable task, Context context) {
        return () -> {
            try {
                task.run();
                if (Thread.currentThread().isInterrupted())
                    context.interruptedTaskCount.incrementAndGet();
                else
                    context.completedTaskCount.incrementAndGet();
            } catch (Throwable e) {
                context.failedTaskCount.incrementAndGet();
            }
            return null;
        };
    }

    public static class Context {
        private final int taskCount;
        private final ExecutorService executor;
        private final AtomicInteger completedTaskCount = new AtomicInteger();
        private final AtomicInteger failedTaskCount = new AtomicInteger();
        private final AtomicInteger interruptedTaskCount = new AtomicInteger();

        private Context(int taskCount, ExecutorService executor) {
            this.taskCount = taskCount;
            this.executor = executor;
        }

        public int getCompletedTaskCount() {
            return completedTaskCount.get();
        }

        public int getFailedTaskCount() {
            return failedTaskCount.get();
        }

        public int getInterruptedTaskCount() {
            return interruptedTaskCount.get();
        }

        public void interrupt() {
            executor.shutdownNow();
        }

        public boolean isFinished() {
            return taskCount == (completedTaskCount.get() + failedTaskCount.get() + interruptedTaskCount.get());
        }
    }
}
