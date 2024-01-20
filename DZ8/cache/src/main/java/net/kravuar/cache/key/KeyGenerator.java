package net.kravuar.cache.key;

import net.kravuar.cache.addapting.ValueWrapper;

import java.lang.reflect.Method;

@FunctionalInterface
public interface KeyGenerator {

    /**
     * Generate a key for the given method and its parameters.
     *
     * @param target the target instance.
     * @param method the method being called.
     * @param params the method parameters, wrapped in {@link ValueWrapper};
     * {@code null} means that parameter is omitted;
     * {@link ValueWrapper} may contain {@code null} as the value.
     * @return generated key.
     */
    Object generate(Object target, Method method, ValueWrapper... params);
}
