package net.kravuar;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Task<T> {
    private volatile int state = NEW;
    private static final int NEW = 0;
    private static final int COMPLETING = 1;
    private static final int NORMAL = 2;
    private static final int EXCEPTIONAL = 3;


    private final CountDownLatch done = new CountDownLatch(1);
    private Object result; // Writes to result are always before write to volatile state, so no need for it to be volatile
    private final Callable<T> callable;
    private final AtomicReference<Thread> runner = new AtomicReference<>(null);

    public Task(Callable<T> callable) {
        this.callable = callable;
    }

    public T get() throws InterruptedException, ExecutionException {
        if (state > COMPLETING) // if completed - return immediately
            return evaluate();
        run(); // only first thread will actually run, as the runner is CAS'ed
        done.await();
        return evaluate();
    }

    @SuppressWarnings("unchecked")
    private T evaluate() throws ExecutionException {
        if (state == NORMAL) // write to result happens before state update, so
            return (T) result; // result is always fresh because we're reading volatile state above
        throw new ExecutionException((Throwable) result); // here as well
    }

    private void run() {
        if (!runner.compareAndSet(null, Thread.currentThread())) // if we're running already
            return;
        try {
            result = callable.call();
            state = NORMAL;
        } catch (Throwable ex) {
            result = ex;
            state = EXCEPTIONAL;
        } finally {
            done.countDown();
        }
    }
}
