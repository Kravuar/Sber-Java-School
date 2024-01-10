package net.kravuar.components.reflection;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionUtilsTests {

    @Test
    void validateStringEnumPatternWorks() throws NoSuchFieldException {
        var clazz = new Object() {
            private static final String BEBE = "BEBE";
            public static final String BABA = "BABA";
            
            private static final String notBOBO = "BOBO";
            public static final String definitelyNotBIBI = "BIBI";
        }.getClass();

        var invalidExpected = Set.of(
                clazz.getDeclaredField("notBOBO"),
                clazz.getDeclaredField("definitelyNotBIBI")
        );
        var invalidActual = ReflectionUtils.validateStringEnumPattern(clazz);

        assertEquals(invalidExpected, invalidActual);
    }
}