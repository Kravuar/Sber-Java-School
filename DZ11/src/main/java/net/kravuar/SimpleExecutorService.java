package net.kravuar;

import java.util.concurrent.Executor;

public interface SimpleExecutorService extends Executor {
    void start();
}
