package net.kravuar.cache;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileCacheTest {
    private final Path dir;
    private FileCache fileCache;

    FileCacheTest() throws IOException {
        dir = Files.createTempDirectory("fileCacheTest");
    }

    @AfterAll
    void totalCleanup() throws IOException {
        Files.delete(dir);
    }

    @AfterEach
    void cleanup() {
        fileCache.clear();
    }

    @Test
    void givenCacheValue_WhenPutAndRetrieve_ThenOk() throws IOException {
        // given
        fileCache = new FileCache(true, dir);
        String value = "bebe";
        String key = "key";

        // when
        fileCache.put(key, value);

        // then
        assertNotNull(fileCache.get(key));
    }

    @Test
    void givenTwoCacheValuesForSameKey_WhenPutBothAndRetrieve_ThenLastOneReturned() throws IOException {
        // given
        fileCache = new FileCache(true, dir);
        String value1 = "bebe1";
        String key = "key1";
        String value2 = "bebe2";
        fileCache.put(key, value1);
        fileCache.put(key, value2);

        // when & then
        assertEquals(value2, fileCache.get(key).value());
    }

    @Test
    void givenExistingCacheValue_WhenEvicting_ThenValueDeleted() throws IOException {
        // given
        fileCache = new FileCache(true, dir);
        String value = "bebe";
        String key = "key";
        fileCache.put(key, value);

        // when
        fileCache.evict(key);

        // then
        assertNull(fileCache.get(key));
    }

    @Test
    void givenNonExistingCacheValue_WhenRetrieve_ThenNull() throws IOException {
        // given
        fileCache = new FileCache(true, dir);
        String key = "key";

        // when & then
        assertNull(fileCache.get(key));
    }

    @Test
    void givenCacheWithValues_WhenClear_ThenDirEmpty() throws IOException {
        // Given
        fileCache = new FileCache(true, dir);
        fileCache.put("key1", "value1");
        fileCache.put("key2", "value2");

        // When
        fileCache.clear();

        // Then
        try (var files = Files.list(dir)) {
            long count = files.count();
            assertEquals(0, count);
        }
    }

    @Test
    void givenNonSerializableValue_WhenPuttingValue_ThenThrowsIllegalArgumentException() throws IOException {
        // given
        fileCache = new FileCache(true, dir);
        Object value = new Object();
        String key = "key";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> fileCache.put(key, value));
    }

    @Test
    void givenNonExistingDir_WhenCreateCache_ThrowsIllegalArgumentException() {
        // given
        Path nonExistingDir = Path.of("hopefullythisdoesnotexist");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new FileCache(true, nonExistingDir));
    }
}