package net.kravuar.cache.addapting;

import lombok.Getter;

public abstract class AbstractNullAdaptingCache extends AbstractValueAdaptingCache {
    @Getter
    private final boolean allowNullValues;

    /**
     * Create an {@code AbstractNullAdaptingCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected AbstractNullAdaptingCache(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method (adapting {@code null}).
     *
     * @param storeValue the store value.
     * @return the value to return to the user.
     */
    protected Object fromStoreValue(Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE)
            return null;
        return storeValue;
    }

    /**
     * Convert the given user value, as passed into the put method,
     * to a value in the internal store (adapting {@code null}).
     *
     * @param userValue the given user value.
     * @return the value to store.
     */
    protected Object toStoreValue(Object userValue) {
        if (userValue == null) {
            if (this.allowNullValues)
                return NullValue.INSTANCE;
            throw new IllegalArgumentException("Cache instance does not allow null values.");
        }
        return userValue;
    }
}