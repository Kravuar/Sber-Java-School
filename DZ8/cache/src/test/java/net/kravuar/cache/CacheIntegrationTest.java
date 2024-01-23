package net.kravuar.cache;

import net.kravuar.cache.annotations.Cached;
import net.kravuar.cache.key.ConcurrentMapKeyGeneratorResolver;
import net.kravuar.cache.key.KeyGenerator;
import net.kravuar.cache.key.KeyGeneratorResolver;
import net.kravuar.cache.key.SimpleKeyGenerator;
import net.kravuar.cache.proxy.CacheProxyFactory;
import net.kravuar.cache.registry.ConcurrentMapCacheRegistry;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheIntegrationTest {
    private CacheResolver cacheResolver;
    private KeyGeneratorResolver keyGeneratorResolver;
    private CacheProxyFactory proxyFactory;
    private ITarget target;
    private ITarget cachedTarget;

    private final Path dir;

    CacheIntegrationTest() throws IOException {
        dir = Files.createTempDirectory("integrationTest");
    }

    @AfterAll
    void cleanUp() throws IOException {
        try(var files = Files.walk(dir).skip(1)) {
            files.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Files.delete(dir);
    }

    private ZipFileCache zipFileCacheSupplier() {
        try {
            return new ZipFileCache(true, dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConcurrentMapCache concurrentMapCacheSupplier() {
        return new ConcurrentMapCache(true);
    }

    @BeforeAll
    void init() {
        var zipRegistry = new ConcurrentMapCacheRegistry<>(this::zipFileCacheSupplier);
        var concurrentMapRegistry = new ConcurrentMapCacheRegistry<>(this::concurrentMapCacheSupplier);
        var cacheRegistries = Map.of(
                "", concurrentMapRegistry,
                "concurrentMapCaches", concurrentMapRegistry,
                "zipCaches", zipRegistry
        );

        cacheResolver = new ConcurrentMapRegistryCacheResolver(cacheRegistries);

        KeyGenerator badKeyGenerator = (target, method, params) -> "BAD" + method.getName();
        KeyGenerator simpleKeyGenerator = new SimpleKeyGenerator();
        var keyGeneratorRegistries = Map.of(
                "", simpleKeyGenerator,
                "bad", badKeyGenerator
        );

        keyGeneratorResolver = new ConcurrentMapKeyGeneratorResolver(keyGeneratorRegistries);

        proxyFactory = new CacheProxyFactory(cacheResolver, keyGeneratorResolver);
    }

    @BeforeEach
    void setUp() {
        target = mock(ITarget.class);
        when(target.method1()).thenReturn(new String("bebe")); // ignore string pool
        when(target.method2()).thenReturn(new String("bebe")); // ignore string pool
        when(target.method3()).thenReturn(new Object());
        when(target.method4()).thenReturn(new Object());
        cachedTarget = proxyFactory.cache(target, this.getClass().getClassLoader());
    }

    @Test
    void method1IsCached() throws NoSuchMethodException {
        // when
        cachedTarget.method1();
        cachedTarget.method1();

        // then
        // assertSame(result1, result2); won't work as result2 is deserialized (thus new identity)
        verify(target, times(1)).method1();

        Object key = keyGeneratorResolver.resolve("bad").generate(target, ITarget.class.getDeclaredMethod("method1"));
        assertNotNull(cacheResolver.getCache("zipCaches", "someCache").get(key)); // all we can do
    }

    @Test
    void method2IsCached_UsingDefaultKeyGenerator() throws NoSuchMethodException {
        // when
        cachedTarget.method2();
        cachedTarget.method2();

        // then
        // assertSame(result1, result2); won't work as result2 is deserialized (thus new identity)
        verify(target, times(1)).method2();

        Object key = keyGeneratorResolver.resolve("").generate(target, ITarget.class.getDeclaredMethod("method2"));
        assertNotNull(cacheResolver.getCache("zipCaches", "someCache").get(key)); // all we can do
    }

    @Test
    void method3IsCached_UsingDefaultKeyGenerator_AndCacheRegistry() throws NoSuchMethodException {
        // when
        var result1 = cachedTarget.method3();
        var result2 = cachedTarget.method3();

        // then
        assertSame(result1, result2);
        verify(target, times(1)).method3();

        Object key = keyGeneratorResolver.resolve("").generate(target, ITarget.class.getDeclaredMethod("method3"));
        assertNotNull(cacheResolver.getCache("", "someOtherCache").get(key));
    }

    @Test
    void method4IsCached_UsingDefaultKeyGenerator_AndCacheRegistry_AndNameAsMethodGenericString() throws NoSuchMethodException {
        // when
        var result1 = cachedTarget.method4();
        var result2 = cachedTarget.method4();

        // then
        assertSame(result1, result2);
        verify(target, times(1)).method4();

        Object key = new SimpleKeyGenerator().generate(target, ITarget.class.getDeclaredMethod("method4"));
        assertNotNull(cacheResolver.getCache("", ITarget.class.getDeclaredMethod("method4").toGenericString()).get(key));
    }

    private interface ITarget {
        // Serializable
        @Cached(cache = "someCache", keyGenerator = "bad", cacheRegistry = "zipCaches")
        String method1();

        // Serializable
        // default keyGen
        @Cached(cache = "someCache", cacheRegistry = "zipCaches")
        String method2();

        // default keyGen, default cacheRegistry
        @Cached(cache = "someOtherCache")
        Object method3();

        // default cacheName, default keyGen, default cacheRegistry
        @Cached
        Object method4();
    }
}
