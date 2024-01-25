package net.kravuar.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class RefPipeline<E_IN, E_OUT> extends AbstractPipeline<E_IN, E_OUT> implements BadStream<E_OUT> {
    RefPipeline(Iterator<?> source) {
        super(source);
    }

    RefPipeline(AbstractPipeline<?, E_IN> upstream) {
        super(upstream);
    }

    @Override
    public Consumer<E_IN> opWrapConsumer(Consumer<E_OUT> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <K, V> Map<K, V> toMap(
            Function<? super E_OUT, ? extends K> keyMapper,
            Function<? super E_OUT, ? extends V> valueMapper,
            Supplier<? extends Map<K, V>> mapFactory) {
        return evaluate(new TerminalOp<>() {
            final TerminalConsumer<E_OUT, Map<K, V>> toMapConsumer = new TerminalConsumer<>() {
                private final Map<K, V> state = mapFactory.get();

                @Override
                public void accept(E_OUT element) {
                    state.put(keyMapper.apply(element), valueMapper.apply(element));
                }

                @Override
                public Map<K, V> get() {
                    return state;
                }
            };

            @Override
            public Map<K, V> evaluate(AbstractPipeline<?, E_OUT> upstream) {
                upstream.wrapConsumerAndConsume(toMapConsumer);
                return toMapConsumer.get();
            }
        });
    }

    @Override
    public List<E_OUT> toList() {
        return toList(ArrayList::new);
    }

    @Override
    public List<E_OUT> toList(Supplier<? extends List<E_OUT>> listFactory) {
        return evaluate(new TerminalOp<>() {
            final TerminalConsumer<E_OUT, List<E_OUT>> toListConsumer = new TerminalConsumer<>() {
                private final List<E_OUT> state = listFactory.get();

                @Override
                public void accept(E_OUT element) {
                    state.add(element);
                }

                @Override
                public List<E_OUT> get() {
                    return state;
                }
            };

            @Override
            public List<E_OUT> evaluate(AbstractPipeline<?, E_OUT> upstream) {
                upstream.wrapConsumerAndConsume(toListConsumer);
                return toListConsumer.get();
            }
        });
    }

    @Override
    public final BadStream<E_OUT> filter(Predicate<? super E_OUT> predicate) {
        return new RefPipeline<>(this) {
            @Override
            public Consumer<E_OUT> opWrapConsumer(Consumer<E_OUT> consumer) {
                return new ChainedConsumer<>(consumer) {
                    @Override
                    public void accept(E_OUT element) {
                        if (predicate.test(element))
                            downstream.accept(element);
                    }
                };
            }
        };
    }

    @Override
    public final <R> BadStream<R> map(Function<? super E_OUT, ? extends R> mapper) {
        return new RefPipeline<>(this) {
            @Override
            public Consumer<E_OUT> opWrapConsumer(Consumer<R> consumer) {
                return new ChainedConsumer<>(consumer) {
                    @Override
                    public void accept(E_OUT element) {
                        downstream.accept(mapper.apply(element));
                    }
                };
            }
        };
    }

    static class Head<E_IN, E_OUT> extends RefPipeline<E_IN, E_OUT> {
        /**
         * Constructor for the head stage of a pipeline.
         *
         * @param source source iterator.
         */
        Head(Iterator<E_IN> source) {
            super(source);
        }
    }
}
