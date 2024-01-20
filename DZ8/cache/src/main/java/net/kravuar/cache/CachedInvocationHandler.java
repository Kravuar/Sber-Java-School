package net.kravuar.cache;

import lombok.RequiredArgsConstructor;
import net.kravuar.cache.addapting.ValueWrapper;
import net.kravuar.cache.annotations.Cached;
import net.kravuar.cache.annotations.CachedParameter;
import net.kravuar.cache.key.KeyGenerator;
import net.kravuar.cache.key.KeyGeneratorResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class CachedInvocationHandler implements InvocationHandler {
    private final Logger log = LogManager.getLogger(CachedInvocationHandler.class);
    private final CacheResolver cacheResolver;
    private final KeyGeneratorResolver keyGeneratorResolver;
    private final Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var annotation = method.getAnnotation(Cached.class);
        if (annotation == null)
            return invoke(method, args);

        String cacheRegistryName = annotation.cacheRegistry();
        String cacheName = annotation.cache();
        Cache cache = cacheResolver.getCache(cacheRegistryName, cacheName);
        if (cache == null)
            throw new CachedInvocationException(String.format("No cache could be resolved for registry %s and name %s.", cacheRegistryName, cacheName));

        String keyGeneratorName = annotation.keyGenerator();
        KeyGenerator keyGenerator = keyGeneratorResolver.resolve(keyGeneratorName);
        if (keyGenerator == null)
            throw new CachedInvocationException(String.format("No key generator could be resolved for name %s.", keyGeneratorName));

        var wrappedParameters = getParametersWrapped(method, args);
        Object key = keyGenerator.generate(target, method, wrappedParameters);

        ValueWrapper cached = cache.get(key);
        Object value;
        if (cached == null) {
            log.info("Cache MISS on invocation: {} with params {}.", method.toGenericString(), args);
            value = invoke(method, args);
            cache.put(key, value);
        } else {
            log.info("Cache HIT on invocation: {} with params {}.", method.toGenericString(), args);
            value = cached.value();
        }

        return value;
    }

    private Object invoke(Method method, Object[] args) throws Throwable {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossible", e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private ValueWrapper[] getParametersWrapped(Method method, Object[] args) {
        return IntStream.range(0, args.length)
                .mapToObj(i -> {
                    var isRequired = method.getParameters()[i].isAnnotationPresent(CachedParameter.class);
                    return isRequired
                            ? new ValueWrapper(args[i])
                            : null;
                }).toArray(ValueWrapper[]::new);
    }
}
