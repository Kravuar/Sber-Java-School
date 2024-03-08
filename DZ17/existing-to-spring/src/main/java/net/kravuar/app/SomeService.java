package net.kravuar.app;

import net.kravuar.cache.annotations.Cached;
import net.kravuar.cache.annotations.CachedParameter;

public interface SomeService {

    @Cached(cacheRegistry = "inMemoryCache", keyGenerator = "simpleKeyGenerator")
    String[] calculate(@CachedParameter int x, @CachedParameter int y, int iAmIgnored, Runnable callback);
}
