package net.kravuar;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DecodingClassloaderTest {
    private final DecodingClassloader classloader = new DecodingClassloader(
            "bebebe",
            Paths.get("src/test/resources"),
            getClass().getClassLoader()
    );

    @Test
    void givenValidClassClassname_whenFindClass_thenOk() {
        // given
        var encodedClassBinaryName = "net.kravuar.Test";

        // when
        Class<?> loadedClass = assertDoesNotThrow(() -> classloader.findClass(encodedClassBinaryName));

        // then
        assertNotNull(loadedClass);
    }

    @Test
    void givenInvalidClassname_whenFindClass_thenThrowClassNotFoundException() {
        // given
        var invalidBinaryName = "bebebe.s.bababa";

        // when/then
        assertThrows(ClassNotFoundException.class, () -> classloader.findClass(invalidBinaryName));
    }

    @Test
    void givenClassWithIncorrectEncoding_whenFindClass_thenThrowClassNotFoundException() {
        // given
        var invalidClassBinaryName = "net.kravuar.RockPaperScissorsPlugin"; // Non encoded

        // when & then
        assertThrows(ClassNotFoundException.class, () -> classloader.findClass(invalidClassBinaryName));
    }
}