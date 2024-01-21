package net.kravuar.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on method with {@link java.util.List} return type
 * to indicate to the {@link Cached} annotation that the method invocation
 * should cache only specified amount of elements.
 *
 * @see Cached
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SizeLimited {
    /**
     * Limit of cached elements in a list.
     * Default value is 0, which disables the limit.
     */
    int amount() default 0;
}