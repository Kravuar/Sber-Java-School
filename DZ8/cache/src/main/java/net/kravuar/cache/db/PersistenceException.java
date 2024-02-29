package net.kravuar.cache.db;

import net.kravuar.cache.proxy.CachedInvocationException;

public class PersistenceException extends CachedInvocationException {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
