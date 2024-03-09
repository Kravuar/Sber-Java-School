package net.kravuar.cache.proxy;

import lombok.RequiredArgsConstructor;
import net.kravuar.cache.CacheResolver;
import net.kravuar.cache.key.KeyGeneratorResolver;

import java.lang.reflect.Proxy;

@RequiredArgsConstructor
public class CacheProxyFactory {
    private final CacheResolver cacheResolver;
    private final KeyGeneratorResolver keyGeneratorResolver;

    @SuppressWarnings("unchecked")
    public <T> T cache(T target, ClassLoader classLoader) {
        var invocationHandler = new CachedInvocationHandler(
                cacheResolver,
                keyGeneratorResolver,
                target
        );
        return (T) Proxy.newProxyInstance(
                classLoader,
                target.getClass().getInterfaces(),
                invocationHandler
        );
    }
}
