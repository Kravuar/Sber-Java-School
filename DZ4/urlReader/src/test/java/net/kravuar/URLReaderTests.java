package net.kravuar;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class URLReaderTests {

    @Test
    void readContentSucceededWithCorrectURL() {
        assertDoesNotThrow(() -> Main.readContent("https://docs.oracle.com/en/java/"));
    }

    @Test
    void readContentFailsWithIncorrectURL() {
        var exception = assertThrows(URLReadingException.class, () -> Main.readContent("bebebe"));
        assertInstanceOf(
                IllegalArgumentException.class,
                exception.getCause(),
                "Expected to fail due to IllegalArgumentException"
        );
    }

    @Test
    void readContentFailsWithUnrecognizedProtocol() {
        var exception = assertThrows(URLReadingException.class, () -> Main.readContent("bebe:/bebebe"));
        assertInstanceOf(
                MalformedURLException.class,
                exception.getCause(),
                "Expected to fail due to MalformedURLException."
        );
    }
}