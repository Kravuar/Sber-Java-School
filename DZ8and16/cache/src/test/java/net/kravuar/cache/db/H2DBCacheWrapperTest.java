package net.kravuar.cache.db;

import net.kravuar.cache.ConcurrentMapCache;
import net.kravuar.cache.addapting.ValueWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class H2DBCacheWrapperTest {
    private static final String TABLE_NAME = "cache_table";
    private static final String KEY_COLUMN = "very_not_a_keyword";
    private static final String VALUE_COLUMN = "very_not_a_keyword_as_well";
    private PersistentCacheWrapper cache;
    private PersistenceDelegate delegate;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        var innerCache = new ConcurrentMapCache(true);
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
        delegate = new H2DBPersistenceDelegate(
                connection,
                TABLE_NAME,
                KEY_COLUMN,
                VALUE_COLUMN
        );
        cache = new PersistentCacheWrapper(
                innerCache,
                delegate
        );
    }

    @AfterEach
    void cleanUp() throws SQLException {
        delegate.clear();
        if (connection != null)
            connection.close();
    }

    @Test
    void fillCache_ThenRecreate_ContainsPersistedState() {
        // given
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        // when
        // emulate app restart
        PersistentCacheWrapper recreatedCache = new PersistentCacheWrapper(
                new ConcurrentMapCache(true),
                new H2DBPersistenceDelegate(
                        connection,
                        TABLE_NAME,
                        KEY_COLUMN,
                        VALUE_COLUMN
                )
        );

        // then
        ValueWrapper key1 = recreatedCache.get("key1");
        ValueWrapper key2 = recreatedCache.get("key2");

        assertNotNull(key1);
        assertNotNull(key2);
        assertEquals("value1", key1.value());
        assertEquals("value2", key2.value());
    }

    @Test
    void putEntry_ThenReplaceAndRecreate_ReturnsReplaced() {
        // given
        Object key = "key";
        Object originalValue = "value";
        Object updatedValue = "updatedValue";
        cache.put(key, originalValue);

        // when
        cache.put(key, updatedValue);
        // emulate restart
        PersistentCacheWrapper recreatedCache = new PersistentCacheWrapper(
                new ConcurrentMapCache(true),
                new H2DBPersistenceDelegate(
                        connection,
                        TABLE_NAME,
                        KEY_COLUMN,
                        VALUE_COLUMN
                )
        );

        // then
        ValueWrapper valueWrapper = recreatedCache.get(key);

        assertNotNull(valueWrapper);
        assertEquals(updatedValue, valueWrapper.value());

        // then
        ValueWrapper result = cache.get(key);
        assertEquals(updatedValue, result.value());
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