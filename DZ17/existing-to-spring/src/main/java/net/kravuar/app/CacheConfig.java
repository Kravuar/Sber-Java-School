package net.kravuar.app;

import net.kravuar.cache.CacheResolver;
import net.kravuar.cache.ConcurrentMapCache;
import net.kravuar.cache.ConcurrentMapRegistryCacheResolver;
import net.kravuar.cache.key.ConcurrentMapKeyGeneratorResolver;
import net.kravuar.cache.key.KeyGeneratorResolver;
import net.kravuar.cache.key.SimpleKeyGenerator;
import net.kravuar.cache.proxy.CacheProxyFactory;
import net.kravuar.cache.registry.ConcurrentMapCacheRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Supplier;

@Configuration
class CacheConfig {
    @Bean
    KeyGeneratorResolver keyGeneratorResolver() {
        return new ConcurrentMapKeyGeneratorResolver(Map.of(
                "simpleKeyGenerator", new SimpleKeyGenerator(),
                "anotherSimpleKeyGeneratorBecauseWhyNot", new SimpleKeyGenerator()
        ));
    }

    @Bean
    CacheResolver cacheResolver() {
        Supplier<ConcurrentMapCache> concurrentMapCacheSupplier = () -> new ConcurrentMapCache(true);
        return new ConcurrentMapRegistryCacheResolver(Map.of(
                "inMemoryCache", new ConcurrentMapCacheRegistry<>(concurrentMapCacheSupplier),
                "anotherInMemoryCacheForTheSameReason", new ConcurrentMapCacheRegistry<>(concurrentMapCacheSupplier)
        ));
    }

    @Bean
    CacheProxyFactory cacheProxyFactory(CacheResolver cacheResolver, KeyGeneratorResolver keyGeneratorResolver) {
        return new CacheProxyFactory(cacheResolver, keyGeneratorResolver);
    }
}
