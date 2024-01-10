package net.kravuar.components.cache;

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
public @interface Cache {

    /**
     * Annotation to be used on method parameters to indicate to the
     * {@link Cache} annotation that the method invocation
     * should be cached based on the annotated parameter(s).
     *
     * @see Cache
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface CachedParameter {
    }
}
