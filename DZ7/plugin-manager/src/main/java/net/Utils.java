package net;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Utils {

    public static URL convertToJarUrl(File file) throws MalformedURLException {
        String jarUrl = "jar:" + file.toURI().toURL() + "!/";
        return URI.create(jarUrl).toURL();
    }
}
