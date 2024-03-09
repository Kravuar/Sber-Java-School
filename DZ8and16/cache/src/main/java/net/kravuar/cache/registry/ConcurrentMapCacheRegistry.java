package net.kravuar.cache.registry;

import lombok.NonNull;
import net.kravuar.cache.Cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class ConcurrentMapCacheRegistry<T extends Cache> implements CacheRegistry<T> {
    private final ConcurrentMap<String, T> registry = new ConcurrentHashMap<>();
    private final Supplier<? extends T> supplier;

    public ConcurrentMapCacheRegistry(@NonNull Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T getCache(@NonNull String name) {
        return registry.get(name);
    }

    @Override
    public T createCache(@NonNull String name) {
        var cache = supplier.get();
        var previous = registry.putIfAbsent(name, cache);
        if (previous != null)
            throw new IllegalStateException("Cache with name " + name + " already exists.");
        return cache;
    }
}