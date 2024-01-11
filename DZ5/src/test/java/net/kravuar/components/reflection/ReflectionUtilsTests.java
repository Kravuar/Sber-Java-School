package net.kravuar.components.reflection;

import net.kravuar.components.subjects.reflection.StringEnum;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionUtilsTests {

    @Test
    void validateStringEnumPatternWorks() throws NoSuchFieldException {
        var clazz = StringEnum.class;

        var invalidExpected = Set.of(
                clazz.getDeclaredField("notBOBO"),
                clazz.getDeclaredField("definitelyNotBIBI")
        );
        var invalidActual = ReflectionUtils.validateStringEnumPattern(clazz);

        assertEquals(invalidExpected, invalidActual);
    }
}