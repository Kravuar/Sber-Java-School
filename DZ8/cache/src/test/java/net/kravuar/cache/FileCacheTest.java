package net.kravuar.cache;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileCacheTest {
    final Path dir;
    FileCache fileCache;

    FileCacheTest() throws IOException {
//        dir = Files.createTempDirectory("fileCacheTest");
        dir = Paths.get("src","test", "resources", "cache");
    }



    @AfterAll
    void totalCleanup() throws IOException {
//        Files.delete(dir);
    }

    @AfterEach
    void cleanup() {
        fileCache.clear();
    }

    @Test
    void givenSerializableValue_WhenPuttingValue_ThenFileCreated() {
        // given
        fileCache = new FileCache(true, dir, false);
        String value = "bebe";
        String key = "key";

        // when
        fileCache.put(key, value);

        // then
        assertTrue(Files.exists(dir.resolve(key)));
    }

    @Test
    void givenExistingCacheValue_WhenRetrieving_ThenOk() {
        // given
        fileCache = new FileCache(true, dir, false);
        String value = "bebe";
        String key = "key";
        fileCache.put(key, value);

        // when & then
        assertNotNull(fileCache.get(key));
    }

    @Test
    void givenExistingCacheValue_WhenEvicting_ThenValueDeleted() {
        // given
        fileCache = new FileCache(true, dir, false);
        String value = "bebe";
        String key = "key";
        fileCache.put(key, value);

        // when
        fileCache.evict(key);

        // then
        assertNull(fileCache.get(key));
    }

    @Test
    void givenCacheWithValues_WhenClear_ThenDirEmpty() throws IOException {
        // Given
        fileCache = new FileCache(true, dir, false);
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
    void givenNonSerializableValue_WhenPuttingValue_ThenThrowsIllegalArgumentException() {
        // given
        fileCache = new FileCache(true, dir, false);
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
        assertThrows(IllegalArgumentException.class, () -> new FileCache(true, nonExistingDir, false));
    }

    @Test
    void givenCacheWithZipEnabled_WhenPutValue_ThenFileIsCached() throws IOException {
        // given
        fileCache = new FileCache(true, dir, true);
        String key = "key";

        // when
        fileCache.put(key, "value");

        // then
        assertEquals("application/zip", Files.probeContentType(fileCache.keyToFile(key)));
    }
}