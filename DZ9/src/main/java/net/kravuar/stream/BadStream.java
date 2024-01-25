package net.kravuar.stream;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface BadStream<T> {
    static <T> BadStream<T> stream(Iterable<T> iterable) {
        return new RefPipeline.Head<>(iterable.iterator());
    }

    <K,V> Map<K, V> toMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper,
            Supplier<? extends Map<K, V>> mapFactory
    );

    List<T> toList();

    List<T> toList(Supplier<? extends List<T>> listFactory);

    BadStream<T> filter(Predicate<? super T> predicate);

    <R> BadStream<R> map(Function<? super T, ? extends R> mapper);
}
