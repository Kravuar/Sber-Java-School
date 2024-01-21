package net.kravuar.cache;

import net.kravuar.cache.addapting.ValueWrapper;

public interface Cache {
    /**
     * Return the value to which this cache maps the specified key.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key,
     * contained within a {@link ValueWrapper} which may also hold
     * a cached {@code null} or any other special value. A straight {@code null} being
     * returned means that the cache contains no mapping for this key.
     */
    ValueWrapper get(Object key);

    /**
     * Associate the specified value with the specified key in this cache.
     * If the cache previously contained a mapping for this key, the old
     * value is replaced by the specified value.
     *
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     */
    void put(Object key, Object value);

    /**
     * Evict the mapping for this key from this cache if it is present.
     *
     * @param key the key whose mapping is to be removed from the cache.
     */
    void evict(Object key);

    /**
     * Clear the cache through removing all mappings.
     */
    void clear();
}