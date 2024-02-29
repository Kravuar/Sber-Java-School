package net.kravuar.cache.db;

import java.util.Map;

public interface PersistenceDelegate {
    /**
     * Load persisted state
     *
     * @return stream of key-value pairs
     * @throws PersistenceException if failed to load state
     */
    Map<Object, Object> loadPersistedState();

    /**
     * Create or update existing key-value pair in persistent storage
     *
     * @param key key
     * @param value value
     * @throws PersistenceException if failed to put entry
     */
    void put(Object key, Object value);

    /**
     * Delete key-value pair by key
     *
     * @param key key to delete
     * @throws PersistenceException if failed to delete entry
     */
    void delete(Object key);

    /**
     * Delete all key-value pairs from in persistent storage
     *
     * @throws PersistenceException if failed to clear persistent storage
     */
    void clear();
}
