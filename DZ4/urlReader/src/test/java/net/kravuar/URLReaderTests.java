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
        try {
            Main.readContent("bebebe");
        } catch (URLReadingException e) {
            assertInstanceOf(IllegalArgumentException.class, e.getCause(), "Expected to fail due to IllegalArgumentException");
        }
    }

    @Test
    void readContentFailsWithUnrecognizedProtocol() {
        try {
            Main.readContent("bebe:/bebebe");
        } catch (URLReadingException e) {
            assertInstanceOf(MalformedURLException.class, e.getCause(), "Expected to fail due to MalformedURLException.");
        }
    }
}