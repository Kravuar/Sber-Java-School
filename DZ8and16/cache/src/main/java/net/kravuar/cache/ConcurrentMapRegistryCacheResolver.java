package net.kravuar.cache;

import lombok.NonNull;
import net.kravuar.cache.registry.CacheRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapRegistryCacheResolver implements CacheResolver {
    private final ConcurrentMap<String, CacheRegistry<?>> registryManager;

    public ConcurrentMapRegistryCacheResolver(Map<String, ? extends CacheRegistry<?>> registries) {
        this.registryManager = new ConcurrentHashMap<>(registries);
    }

    @Override
    public Cache getCache(@NonNull String registryName, @NonNull String name) {
        var registry = registryManager.get(registryName);
        if (registry == null)
            return null;
        var cache = registry.getCache(name);
        if (cache == null)
            cache = registry.createCache(name);
        return cache;
    }
}
