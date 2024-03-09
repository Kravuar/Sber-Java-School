package net.kravuar.cache.db;

import net.kravuar.cache.Cache;
import net.kravuar.cache.addapting.ValueWrapper;

/**
 * Persistent cache wrapper, uses provided inner cache and persistence delegate,
 * so that cache state persists between launches.
 * Internally delegates caching to provided inner cache.
 */
public class PersistentCacheWrapper implements Cache {
    private final Cache innerCache;
    private final PersistenceDelegate delegate;

    /**
     * Constructs {@code PersistentCacheWrapper} with the given setting.
     *
     * @param innerCache inner cache to use.
     * @param delegate   persistent delegate to save cache results
     */
    public PersistentCacheWrapper(Cache innerCache, PersistenceDelegate delegate) {
        this.innerCache = innerCache;
        this.delegate = delegate;

        delegate.loadPersistedState().forEach(innerCache::put);
    }

    @Override
    public ValueWrapper get(Object key) {
        return innerCache.get(key);
    }

    /**
     * Stores cache value in the configured inner cache and persistence storage.
     *
     * @param key   the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     */
    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
        innerCache.put(key, value);
    }

    /**
     * Deletes entry associated with provided cache key.
     *
     * @param key the key whose mapping is to be removed from the cache.
     */
    @Override
    public void evict(Object key) {
        delegate.delete(key);
        innerCache.evict(key);
    }

    /**
     * Deletes all cache entries within configured inner cache and persistent storage.
     */
    @Override
    public void clear() {
        delegate.clear();
        innerCache.clear();
    }
}
