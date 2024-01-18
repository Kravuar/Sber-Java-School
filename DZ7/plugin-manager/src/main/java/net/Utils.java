package net;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public class Utils {

    public static URL convertToJarUrl(Path path) throws MalformedURLException {
        String jarUrl = "jar:" + path.toUri().toURL() + "!/";
        return URI.create(jarUrl).toURL();
    }
}
