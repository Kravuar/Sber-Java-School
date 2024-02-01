package net.kravuar;

import java.util.concurrent.LinkedBlockingQueue;

public class ShrimpleExecutors {
    public static SimpleExecutorService newFixedThreadPoolExecutor(int nThreads) {
        return new ShrimpleThreadPoolExecutor(
                new LinkedBlockingQueue<>(),
                Thread::new,
                nThreads,
                nThreads,
                0
        );
    }

    public static SimpleExecutorService newScalableThreadPoolExecutor(int minThreads, int maxThreads) {
        return new ShrimpleThreadPoolExecutor(
                new LinkedBlockingQueue<>(),
                Thread::new,
                minThreads,
                maxThreads,
                0
        );
    }

    // other variations
}
