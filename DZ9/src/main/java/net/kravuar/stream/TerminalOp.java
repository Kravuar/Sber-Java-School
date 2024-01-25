package net.kravuar.stream;

public interface TerminalOp<E_IN, R> {
    R evaluate(AbstractPipeline<?, E_IN> upstream);
}
