package net.kravuar.cache;

import net.kravuar.cache.proxy.CachedInvocationException;

public class ANSIDBCacheException extends CachedInvocationException {
    public ANSIDBCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
