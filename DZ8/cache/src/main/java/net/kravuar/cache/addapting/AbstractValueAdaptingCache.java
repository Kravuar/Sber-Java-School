package net.kravuar.cache.addapting;

import net.kravuar.cache.Cache;

public abstract class AbstractValueAdaptingCache implements Cache {
    @Override
    public ValueWrapper get(Object key) {
        return toValueWrapper(lookup(key));
    }

    /**
     * Perform an actual lookup in the underlying store.
     *
     * @param key the key whose associated value is to be returned.
     * @return the raw store value for the key, or {@code null} if none.
     */
    protected abstract Object lookup(Object key);

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method.
     *
     * @param storeValue the store value.
     * @return the value to return to the user.
     */
    protected abstract  Object fromStoreValue(Object storeValue);

    /**
     * Convert the given user value, as passed into the put method,
     * to a value in the internal store.
     *
     * @param userValue the given user value.
     * @return the value to store.
     */
    protected abstract Object toStoreValue(Object userValue);

    /**
     * Wrap the given store value with a {@link ValueWrapper}.
     *
     * @param storeValue the original value.
     * @return the wrapped value.
     */
    protected ValueWrapper toValueWrapper (Object storeValue) {
        return (storeValue != null ? new ValueWrapper(fromStoreValue(storeValue)) : null);
    }
}