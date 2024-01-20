package net.kravuar.cache;

import lombok.NonNull;

@FunctionalInterface
public interface CacheResolver {
    /**
     * Get the cache associated with the given registry name and cache name.
     *
     * @param registryName cache registry identifier (must not be {@code null}).
     * @param cacheName cache identifier (must not be {@code null}).
     * @return associated cache, or {@code null} if it wasn't found.
     */
    Cache getCache(@NonNull String registryName, @NonNull String cacheName);
}