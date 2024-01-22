package net.kravuar.cache;

import net.kravuar.cache.addapting.ValueWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentMapCacheTest {
    private ConcurrentMapCache cache;

    @BeforeEach
    void setUp() {
        cache = new ConcurrentMapCache(true);
    }

    @Test
    void putEntry_ThenGet_ReturnsWrapped() {
        // given
        Object key = "key";
        Object value = "value";
        cache.put(key, value);

        // when
        ValueWrapper result = cache.get(key);

        // then
        assertEquals(value, result.value());
    }

    @Test
    void entryDoesNotExist_ThenGet_ReturnsNull() {
        // given
        Object key = "nonExistingKey";

        // when
        ValueWrapper result = cache.get(key);

        // then
        assertNull(result);
    }

    @Test
    void putEntry_ThenReplace_ReturnsReplaced() {
        // given
        Object key = "key";
        Object originalValue = "value";
        Object updatedValue = "updatedValue";
        cache.put(key, originalValue);

        // when
        cache.put(key, updatedValue);

        // then
        ValueWrapper result = cache.get(key);
        assertEquals(updatedValue, result.value());
    }

    @Test
    void putNull_ThenGet_ReturnsWrappedNull() {
        // given
        Object key = "key";
        cache.put(key, null);

        // when
        ValueWrapper result = cache.get(key);

        // then
        assertNull(result.value());
    }

    @Test
    void nullIsNotAllowed_putNull_ThrowsIllegalArgumentException() {
        // given
        cache = new ConcurrentMapCache(false);
        Object key = "key";

        // when then
        assertThrows(IllegalArgumentException.class, () -> cache.put(key, null));
    }

    @Test
    void putEntry_ThenEvict_ReturnsNull() {
        // given
        Object key = "key";
        Object value = "value";
        cache.put(key, value);

        // when
        cache.evict(key);

        // then
        ValueWrapper result = cache.get(key);
        assertNull(result);
    }

    @Test
    void putEntries_ThenClear_ReturnsNullForEach() {
        // given
        Object key1 = "key1";
        Object value1 = "value1";
        Object key2 = "key2";
        Object value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);

        // when
        cache.clear();

        // then
        ValueWrapper result1 = cache.get(key1);
        ValueWrapper result2 = cache.get(key2);

        assertNull(result1);
        assertNull(result2);
    }
}