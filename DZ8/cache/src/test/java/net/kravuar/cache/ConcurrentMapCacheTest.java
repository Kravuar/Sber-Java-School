package net.kravuar.cache;

import net.kravuar.cache.addapting.ValueWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConcurrentMapCacheTest {
    private ConcurrentMapCache cache;

    @BeforeEach
    void setUp() {
        cache = new ConcurrentMapCache(true);
    }

    @Test
    void putEntry_ThenGet_ReturnsWrapped() {
        // Given
        Object key = "key";
        Object value = "value";
        cache.put(key, value);

        // When
        ValueWrapper result = cache.get(key);

        // Then
        assertEquals(value, result.value());
    }

    @Test
    void entryDoesNotExist_ThenGet_ReturnsNull() {
        // Given
        Object key = "nonExistingKey";

        // When
        ValueWrapper result = cache.get(key);

        // Then
        assertNull(result);
    }

    @Test
    void putEntry_ThenReplace_ReturnsReplaced() {
        // Given
        Object key = "key";
        Object originalValue = "value";
        Object updatedValue = "updatedValue";
        cache.put(key, originalValue);

        // When
        cache.put(key, updatedValue);

        // Then
        ValueWrapper result = cache.get(key);
        assertEquals(updatedValue, result.value());
    }

    @Test
    void putNull_ThenGet_ReturnsWrappedNull() {
        // Given
        Object key = "key";
        cache.put(key, null);

        // When
        ValueWrapper result = cache.get(key);

        // Then
        assertNull(result.value());
    }

    @Test
    void putEntry_ThenEvict_ReturnsNull() {
        // Given
        Object key = "key";
        Object value = "value";
        cache.put(key, value);

        // When
        cache.evict(key);

        // Then
        ValueWrapper result = cache.get(key);
        assertNull(result);
    }

    @Test
    void putEntries_ThenClear_ReturnsNullForEach() {
        // Given
        Object key1 = "key1";
        Object value1 = "value1";
        Object key2 = "key2";
        Object value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);

        // When
        cache.clear();

        // Then
        ValueWrapper result1 = cache.get(key1);
        ValueWrapper result2 = cache.get(key2);

        assertNull(result1);
        assertNull(result2);
    }
}