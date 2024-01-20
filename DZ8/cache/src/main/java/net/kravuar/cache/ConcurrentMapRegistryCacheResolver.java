package net.kravuar.cache;

import lombok.NonNull;
import net.kravuar.cache.registry.CacheRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConcurrentMapRegistryCacheResolver implements CacheResolver {
    private final ConcurrentMap<String, CacheRegistry<?>> registryManager;

    public ConcurrentMapRegistryCacheResolver(
            Map<String, Supplier<? extends Cache>> entryMap,
            Function<Supplier<? extends Cache>, CacheRegistry<?>> cacheRegistryFactory) {
        this.registryManager = entryMap.entrySet().stream()
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        entry -> cacheRegistryFactory.apply(entry.getValue())
                ));
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
