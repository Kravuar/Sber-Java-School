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
class ZipFileCacheTest {
    private final Path dir;
    private ZipFileCache zipCache;

    ZipFileCacheTest() throws IOException {
        dir = Files.createTempDirectory("fileCacheTest");
    }

    @AfterAll
    void totalCleanup() throws IOException {
        Files.delete(dir);
    }

    @AfterEach
    void cleanup() {
        zipCache.clear();
    }

    @Test
    void givenCacheValue_WhenPutAndRetrieve_ThenOk() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir);
        String value = "bebe";
        String key = "key";

        // when
        zipCache.put(key, value);

        // then
        assertNotNull(zipCache.get(key));
    }

    @Test
    void givenTwoCacheValuesForSameKey_WhenPutBothAndRetrieve_ThenLastOneReturned() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir);
        String value1 = "bebe1";
        String value2 = "bebe2";
        String key = "key";

        //when
        zipCache.put(key, value1);
        zipCache.put(key, value2);

        // then
        assertEquals(value2, zipCache.get(key).value());
    }

    @Test
    void givenNonExistingCacheValue_WhenRetrieve_ThenNull() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir);
        String key = "key";

        // when & then
        assertNull(zipCache.get(key));
    }

    @Test
    void givenExistingCacheValue_WhenEvicting_ThenValueDeleted() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir);
        String value = "bebe";
        String key = "key";
        zipCache.put(key, value);

        // when
        zipCache.evict(key);

        // then
        assertNull(zipCache.get(key));
    }

    @Test
    void givenCacheWithValues_WhenClear_ThenDirEmpty() throws IOException {
        // Given
        zipCache = new ZipFileCache(true, dir);
        zipCache.put("key1", "value1");
        zipCache.put("key2", "value2");

        // When
        zipCache.clear();

        // Then
        try (var files = Files.walk(zipCache.getDirectory()).skip(1)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void givenNonSerializableValue_WhenPuttingValue_ThenThrowsIllegalArgumentException() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir);
        Object value = new Object();
        String key = "key";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> zipCache.put(key, value));
    }

    @Test
    void givenNonExistingDir_WhenCreateCache_ThrowsIllegalArgumentException() {
        // given
        Path nonExistingDir = Path.of("hopefullythisdoesnotexist");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new ZipFileCache(true, nonExistingDir));
    }
}