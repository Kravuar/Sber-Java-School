package net.kravuar.cache;

import net.kravuar.cache.addapting.AbstractNullAdaptingCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapCache extends AbstractNullAdaptingCache {
    private final ConcurrentMap<Object, Object> store = new ConcurrentHashMap<>();

    /**
     * Create an {@code ConcurrentMapCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values.
     */
    protected ConcurrentMapCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    protected Object lookup(Object key) {
        return store.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        store.put(key, toStoreValue(value));
    }

    @Override
    public void evict(Object key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
