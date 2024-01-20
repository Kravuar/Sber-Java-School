package net.kravuar.cache.key;

import net.kravuar.cache.addapting.ValueWrapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleKeyGeneratorTest {
    final SimpleKeyGenerator keyGenerator = new SimpleKeyGenerator();
    final Method method = SimpleKeyGeneratorTest.class.getMethod("stub");

    public void stub() {}

    SimpleKeyGeneratorTest() throws NoSuchMethodException {
    }

    @Test
    void generateKeyWithNonNullParams() {
        // given
        Object target = new Object();
        ValueWrapper[] params = new ValueWrapper[] { new ValueWrapper("param1"), new ValueWrapper("param2") };

        // when
        Object result = keyGenerator.generate(target, method, params);

        // then
        assertEquals("java.lang.Object-stub-param1;param2;", result);
    }

    @Test
    void generateKeyWithNullParams() {
        // given
        Object target = new Object();
        ValueWrapper[] params = new ValueWrapper[] { null, null };

        // when
        Object result = keyGenerator.generate(target, method, params);

        // then
        assertEquals("java.lang.Object-stub-EMPTY;", result);
    }

    @Test
    void generateKeyWithMixedParams() {
        // given
        Object target = new Object();
        ValueWrapper[] params = new ValueWrapper[] { new ValueWrapper("param1"), null, new ValueWrapper("param2") };

        // when
        Object result = keyGenerator.generate(target, method, params);

        // then
        assertEquals("java.lang.Object-stub-param1;param2;", result);
    }
}