package net.kravuar.cache;

public class CachedInvocationException extends RuntimeException {
    public CachedInvocationException(Throwable cause) {
        super("An error occurred during cached method invocation.", cause);
    }

    public CachedInvocationException(String message) {
        super(message);
    }
}
