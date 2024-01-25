package net.kravuar.stream;

import java.util.Objects;
import java.util.function.Consumer;

abstract class ChainedConsumer<T, E_OUT> implements Consumer<T> {
    protected final Consumer<? super E_OUT> downstream;

    public ChainedConsumer(Consumer<? super E_OUT> downstream) {
        this.downstream = Objects.requireNonNull(downstream);
    }
}