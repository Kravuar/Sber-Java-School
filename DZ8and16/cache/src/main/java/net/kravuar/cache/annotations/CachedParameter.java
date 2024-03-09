package net.kravuar.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on method parameters to indicate to the
 * {@link Cached} annotation that the method invocation
 * should be cached based on the annotated parameter(s).
 *
 * @see Cached
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CachedParameter {
}