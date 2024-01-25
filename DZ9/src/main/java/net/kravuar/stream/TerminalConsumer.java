package net.kravuar.stream;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A Consumer which accumulates state as elements are accepted, and allows
 * a result to be retrieved after the computation is finished.
 *
 * @param <T> the type of elements to be accepted
 * @param <R> the type of the result
 */
public interface TerminalConsumer <T, R> extends Consumer<T>, Supplier<R> {
}
