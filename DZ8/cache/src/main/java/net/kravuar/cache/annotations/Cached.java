package net.kravuar.cache.annotations;

import net.kravuar.cache.Cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method's invocations should be cached based on its parameters.
 * It uses {@link CachedParameter} to select parameters on which to cache invocations,
 * or it will use all parameters if no parameters are annotated with {@link CachedParameter}.
 *
 * @see CachedParameter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    /**
     * The name of the {@link Cache} instance to use for this method.
     * Default is "", which results in a name inferred as
     * {@code Method.toGenericString()}.
     * Will create new instance of cache, if there wasn't one for that name
     * in the specified cache registry.
     */
    String cache() default "";

    /**
     * The name of the cache registry to use for this method.
     */
    String cacheRegistry() default "";

    /**
     * The name of the keyGenerator to use for this method.
     */
    String keyGenerator() default "";
}
