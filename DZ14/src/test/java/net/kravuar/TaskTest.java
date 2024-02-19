package net.kravuar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    void givenCallableWithNormalCompletion_WhenGet_ThenReturnExpectedValue() throws InterruptedException, ExecutionException {
        // given
        Callable<Integer> callable = () -> 42;
        Task<Integer> task = new Task<>(callable);

        // when
        Integer result = task.get();

        // then
        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    void givenCallableWithExceptionalCompletion_WhenGet_ThenExecutionException() {
        // given
        Callable<Void> callable = () -> {
            throw new RuntimeException("beb");
        };
        Task<Void> task = new Task<>(callable);

        // when & then
        ExecutionException exception = assertThrows(ExecutionException.class, task::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    @Test
    void givenCallableWithCounter_WhenGet_ThenCountIsOne() throws ExecutionException, InterruptedException {
        // given
        var counterCallable = new Callable<Integer>() {
            private final AtomicInteger count = new AtomicInteger();

            @Override
            public Integer call() {
                return count.getAndIncrement();
            }
        };
        Task<Integer> task = new Task<>(counterCallable);

        // when
        try (var threads = Executors.newThreadPerTaskExecutor(Thread::new)) {
            int nThreads = 10;
            var tasks = new ArrayList<Callable<Integer>>();
            for (int i = 0; i < nThreads; i++) {
                tasks.add(task::get);
            }

            for (var future: threads.invokeAll(tasks))
                future.get(); // Wait for all to finish

            // then
            assertEquals(1, counterCallable.call());
        }
    }
}
