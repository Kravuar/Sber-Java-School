package net.kravuar.cache.key;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Contains {@link SimpleKeyGenerator} associated with {@code ""}.
 */
public class ConcurrentMapKeyGeneratorResolver implements KeyGeneratorResolver {
    private final ConcurrentMap<String, KeyGenerator> resolvers;

    public ConcurrentMapKeyGeneratorResolver(Map<String, KeyGenerator> resolvers) {
        this.resolvers = new ConcurrentHashMap<>(resolvers);
    }

    @Override
    public KeyGenerator resolve(@NonNull String name) {
        return resolvers.get(name);
    }
}
