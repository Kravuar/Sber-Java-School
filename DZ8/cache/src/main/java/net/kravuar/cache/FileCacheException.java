package net.kravuar.cache;

import net.kravuar.cache.proxy.CachedInvocationException;

public class FileCacheException extends CachedInvocationException {
    public FileCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
