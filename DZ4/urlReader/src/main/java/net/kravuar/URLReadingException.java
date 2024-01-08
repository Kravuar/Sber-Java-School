package net.kravuar;

public class URLReadingException extends Exception {
    public URLReadingException(Throwable cause) {
        super("An error occurred during url reading process.", cause);
    }
}
