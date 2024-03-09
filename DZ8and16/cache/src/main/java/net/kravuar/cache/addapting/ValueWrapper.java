package net.kravuar.cache.addapting;

/**
 * Allows distinguishing between the absence of a cached value ({@code ValueWrapper == null})
 * and cached {@code null} value ({@code ValueWrapper.value() == null}).
 *
 * @param value value to wrap.
 */
public record ValueWrapper(Object value) {}