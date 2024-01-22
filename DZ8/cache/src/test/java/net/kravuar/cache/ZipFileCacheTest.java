package net.kravuar.cache;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZipFileCacheTest {
    private final Path dir;
    private final String suffix = "cache";
    private final String zipName = "zippedCache";
    private ZipFileCache zipCache;

    ZipFileCacheTest() throws IOException {
        dir = Files.createTempDirectory("fileCacheTest");
    }

    @AfterAll
    void totalCleanup() throws IOException {
        Files.delete(zipCache.getZipPath());
        Files.delete(dir);
    }

    @AfterEach
    void cleanup() {
        zipCache.clear();
    }

    @Test
    void givenExistingCacheValue_WhenPutAndRetrieve_ThenOk() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir, suffix, zipName);
        String value = "bebe";
        String key = "key";
        zipCache.put(key, value);

        // when & then
        assertNotNull(zipCache.get(key));
    }

    @Test
    void givenNonExistingCacheValue_WhenRetrieve_ThenNull() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir, suffix, zipName);
        String key = "key";

        // when & then
        assertNull(zipCache.get(key));
    }

    @Test
    void givenExistingCacheValue_WhenEvicting_ThenValueDeleted() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir, suffix, zipName);
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
        zipCache = new ZipFileCache(true, dir, suffix, zipName);
        zipCache.put("key1", "value1");
        zipCache.put("key2", "value2");

        // When
        zipCache.clear();

        // Then
        try (var zip = new ZipFile(zipCache.getZipPath().toFile())) {
            assertFalse(zip.entries().hasMoreElements());
        }
    }

    @Test
    void givenNonSerializableValue_WhenPuttingValue_ThenThrowsIllegalArgumentException() throws IOException {
        // given
        zipCache = new ZipFileCache(true, dir, suffix, zipName);
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
        assertThrows(IllegalArgumentException.class, () -> new ZipFileCache(true, nonExistingDir, suffix, zipName));
    }

    @Test
    void givenExistingDir_WhenCreateCache_ThenZipFileCreated() throws IOException {
        // when
        zipCache = new ZipFileCache(true, dir, suffix, zipName);

        // then
        assertTrue(Files.exists(zipCache.getZipPath()));
    }

//    @Test
//    void test() throws IOException {
//        // when
//        fileCache = new ZipFileCache(true, dir, suffix, zipName);
//
//        for (var i = 0; i < 500; ++i)
//        {
//            var x = UUID.randomUUID();
//            fileCache.put(x, x);
//        }
//    }
}