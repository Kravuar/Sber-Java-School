package net.kravuar.cache.key;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Contains {@link SimpleKeyGenerator} associated with {@code ""}.
 */
public class ConcurrentMapKeyGeneratorResolver implements KeyGeneratorResolver {
    private final ConcurrentMap<String, KeyGenerator> registry;

    public ConcurrentMapKeyGeneratorResolver(Map<String, KeyGenerator> registry) {
        this.registry = new ConcurrentHashMap<>(registry);
    }

    @Override
    public KeyGenerator resolve(@NonNull String name) {
        return registry.get(name);
    }
}
