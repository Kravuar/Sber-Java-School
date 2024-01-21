package net.kravuar.cache;

import net.kravuar.cache.addapting.ValueWrapper;
import net.kravuar.cache.annotations.Cached;
import net.kravuar.cache.annotations.CachedParameter;
import net.kravuar.cache.key.KeyGenerator;
import net.kravuar.cache.key.KeyGeneratorResolver;
import net.kravuar.cache.proxy.CachedInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedInvocationHandlerTest {
    @Mock
    CacheResolver cacheResolver;
    Cache cache;

    @Mock
    KeyGeneratorResolver keyGeneratorResolver;
    KeyGenerator keyGenerator;

    CachedInvocationHandler cachedInvocationHandler;

    ITarget target;
    ITarget cachedTarget;

    @BeforeEach
    void setUp() {
        cache = mock(Cache.class);
        keyGenerator = mock(KeyGenerator.class);

        lenient().when(cacheResolver.getCache(anyString(), anyString())).thenReturn(cache);
        lenient().when(keyGeneratorResolver.resolve(anyString())).thenReturn(keyGenerator);

        target = mock(ITarget.class);
        cachedInvocationHandler = new CachedInvocationHandler(
                cacheResolver,
                keyGeneratorResolver,
                target
        );
        cachedTarget = (ITarget) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ITarget.class},
                cachedInvocationHandler
        );
    }

    @Test
    void givenNonCachedMethod_whenInvokeTwice_ResultIsNotCached() {
        // given
        Object a = new Object();
        Object b = new Object();

        // when &
        cachedTarget.nonCached(a, b);
        cachedTarget.nonCached(a, b);

        // then
        verifyNoInteractions(cacheResolver, keyGeneratorResolver);
        verifyNoInteractions(cache, keyGenerator);
    }

    @Test
    void givenCachedMethodWithArgs_whenInvokeTwice_ResultIsCached() throws NoSuchMethodException {
        // given
        Method method = ITarget.class.getMethod("cached", Object.class, Object.class);

        Object a = new Object();
        Object b = new Object();
        Object key = "beb";
        //                                           only explicit varargs work...
        when(keyGenerator.generate(eq(target), eq(method), any(), any()))
                .thenReturn(key);
        doNothing().when(cache).put(any(), any());

        // when
        when(cache.get(key)).thenReturn(null);
        cachedTarget.cached(a, b);
        when(cache.get(key)).thenReturn(mock(ValueWrapper.class));
        cachedTarget.cached(a, b);

        // then
        verify(cacheResolver, times(2)).getCache(anyString(), anyString());
        verify(keyGeneratorResolver, times(2)).resolve(anyString());

        verify(keyGenerator, times(2)).generate(any(), eq(method), any(), any());
        verify(cache, times(2)).get(any());
        verify(cache, times(1)).put(anyString(), any());

        verify(target, times(1)).cached(eq(a), eq(b));
    }

    @Test
    void givenPartiallyCachedMethod_whenInvokeTwiceWithRequiredParamRemainsTheSame_ResultIsCached() throws NoSuchMethodException {
        // given
        Method method = ITarget.class.getMethod("partiallyCached", Object.class, Object.class);

        Object a1 = new Object();
        Object a2 = new Object();
        Object b = new Object();
        Object key = "beb";
        //                                                   Omitted
        when(keyGenerator.generate(eq(target), eq(method), eq(null), any()))
                .thenReturn(key);
        doNothing().when(cache).put(any(), any());

        // when
        when(cache.get(key)).thenReturn(null);
        cachedTarget.partiallyCached(a1, b);
        when(cache.get(key)).thenReturn(mock(ValueWrapper.class));
        cachedTarget.partiallyCached(a2, b);

        // then
        verify(cacheResolver, times(2)).getCache(anyString(), anyString());
        verify(keyGeneratorResolver, times(2)).resolve(anyString());

        verify(keyGenerator, times(2)).generate(any(), eq(method), eq(null), any());
        verify(cache, times(2)).get(any());
        verify(cache, times(1)).put(anyString(), any());

        verify(target, times(1)).partiallyCached(any(), eq(b));
    }

    @Test
    void givenPartiallyCachedMethod_whenInvokeTwiceWithDifferentRequiredParam_ResultIsNotCached() throws NoSuchMethodException {
        // given
        Method method = ITarget.class.getMethod("partiallyCached", Object.class, Object.class);

        Object a = new Object();
        Object b1 = new Object();
        Object b2 = new Object();
        Object key = "beb";
        //                                                   Omitted
        when(keyGenerator.generate(eq(target), eq(method), eq(null), any()))
                .thenReturn(key);
        doNothing().when(cache).put(any(), any());

        when(cache.get(key)).thenReturn(null);

        // when
        cachedTarget.partiallyCached(a, b1);
        cachedTarget.partiallyCached(a, b2);

        // then
        verify(cacheResolver, times(2)).getCache(anyString(), anyString());
        verify(keyGeneratorResolver, times(2)).resolve(anyString());

        verify(keyGenerator, times(2)).generate(any(), eq(method), eq(null), any());
        verify(cache, times(2)).get(any());
        verify(cache, times(2)).put(anyString(), any());

        verify(target, times(2)).partiallyCached(eq(a), any());
    }

    @Test
    void givenCachedMethodWithoutArgs_whenInvokeTwice_ResultIsCached() throws NoSuchMethodException {
        // given
        Method method = ITarget.class.getMethod("cachedNoArg");
        Object key = "beb";
        //                                                 No args
        when(keyGenerator.generate(eq(target), eq(method)))
                .thenReturn(key);
        doNothing().when(cache).put(any(), any());

        // when
        when(cache.get(key)).thenReturn(null);
        cachedTarget.cachedNoArg();
        when(cache.get(key)).thenReturn(mock(ValueWrapper.class));
        cachedTarget.cachedNoArg();

        // then
        verify(cacheResolver, times(2)).getCache(anyString(), anyString());
        verify(keyGeneratorResolver, times(2)).resolve(anyString());

        verify(keyGenerator, times(2)).generate(any(), eq(method));
        verify(cache, times(2)).get(any());
        verify(cache, times(1)).put(anyString(), any());

        verify(target, times(1)).cachedNoArg();
    }

    private interface ITarget {
        @Cached
        Object cached(Object a, Object b);

        @Cached
        Object partiallyCached(Object a, @CachedParameter Object b);

        @Cached
        Object cachedNoArg();

        Object nonCached(Object a, Object b);
    }
}