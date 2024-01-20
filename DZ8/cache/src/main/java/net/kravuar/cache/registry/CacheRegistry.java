package net.kravuar.cache.registry;

import lombok.NonNull;
import net.kravuar.cache.Cache;

public interface CacheRegistry<T extends Cache> {
    /**
     * Retrieve {@link Cache} by name.
     *
     * @param name name of the requested cache
     * @return cache instance or null there was no cache associated with that name.
     */
    T getCache(@NonNull String name);

    /**
     * Create new {@link Cache} in this registry.
     *
     * @param name name to associate with newly created cache.
     * @return newly created cache instance.
     * @throws IllegalStateException if name already taken.
     */
    T createCache(@NonNull String name);
}
