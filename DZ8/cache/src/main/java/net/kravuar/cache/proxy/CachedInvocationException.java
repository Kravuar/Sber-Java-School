package net.kravuar.cache.proxy;

public class CachedInvocationException extends RuntimeException {
    public CachedInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CachedInvocationException(String message) {
        super(message);
    }
}
