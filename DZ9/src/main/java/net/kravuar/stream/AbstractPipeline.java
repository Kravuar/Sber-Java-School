package net.kravuar.stream;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * Abstract class for stream pipeline.
 *
 * @param <E_IN> The type of the elements that this stage receives.
 * @param <E_OUT> The type of the elements that this stage produces.
 */
public abstract class AbstractPipeline<E_IN, E_OUT> {
    private Iterator<?> sourceIterator;
    protected final AbstractPipeline<?, E_IN> upstream;
    protected AbstractPipeline<E_OUT, ?> downstream;
    protected boolean linkedOrConsumed = false;

    /**
     * Constructor for the head of a stream pipeline.
     *
     * @param source source iterator.
     */
    AbstractPipeline(Iterator<?> source) {
        this.upstream = null;
        this.sourceIterator = source;
    }

    /**
     * Constructor for appending an intermediate operation stage onto an
     * existing pipeline.
     *
     * @param upstream the upstream pipeline stage.
     */
    AbstractPipeline(AbstractPipeline<?, E_IN> upstream) {
        if (upstream.linkedOrConsumed)
            throw new IllegalArgumentException("Upstream is already linked with downstream or consumed.");
        upstream.linkedOrConsumed = true;
        upstream.downstream = this;
        this.upstream = upstream;
    }

    /**
     * Evaluates against provided {@link TerminalOp}.
     *
     * @param terminalOp terminal operator to evaluate against.
     * @return terminal operator result.
     * @param <R> result type of terminal operator.
     */
    final <R> R evaluate(TerminalOp<E_OUT, R> terminalOp) {
        if (linkedOrConsumed)
            throw new IllegalArgumentException("Upstream is already linked with downstream or consumed.");
        linkedOrConsumed = true;

        return terminalOp.evaluate(this);
    }

    /**
     * Retrieve source iterator (by climbing all the way up the pipeline).
     *
     * @return source iterator.
     */
    private Iterator<?> getSourceIterator() {
        @SuppressWarnings("rawtypes")
        AbstractPipeline p = this;
        while (p.upstream != null)
            p=p.upstream;

        return p.sourceIterator;
    }

    /**
     * Wrap provided consumer with all pipeline stages and consume source into it.
     *
     * @param consumer consumer to consume into.
     * @param <S> type of E_OUT elements consumer.
     */
    final <S extends Consumer<E_OUT>> void wrapConsumerAndConsume(S consumer) {
        Objects.requireNonNull(consumer);
        getSourceIterator().forEachRemaining(wrapConsumer(consumer));
    }

    /**
     * Wrap provided consumer with all pipeline stages.
     *
     * @param consumer downstream consumer to wrap.
     * @return wrapped downstream consumer
     * @param <P_IN> type of the source elements.
     */
    @SuppressWarnings("unchecked")
    final <P_IN> Consumer<P_IN> wrapConsumer(Consumer<E_OUT> consumer) {
        for (@SuppressWarnings("rawtypes") AbstractPipeline p = this; p.upstream != null; p=p.upstream)
            consumer = p.opWrapConsumer(consumer);

        return (Consumer<P_IN>) consumer;
    }

    /**
     * Wraps provided consumer with stage logic.
     *
     * @param consumer downstream consumer to wrap.
     * @return wrapped downstream consumer.
     */
    abstract public Consumer<E_IN> opWrapConsumer(Consumer<E_OUT> consumer);
}
