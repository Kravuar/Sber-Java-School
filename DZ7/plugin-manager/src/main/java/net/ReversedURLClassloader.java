package net;

import java.net.URL;
import java.net.URLClassLoader;

public class ReversedURLClassloader extends URLClassLoader {

    public ReversedURLClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ReversedURLClassloader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException ignored) { /* Fine, we'll try the parent*/ }
                if (c == null) {
                    c = super.loadClass(name, false); // Reverting to ClassLoader's forward loadClass implementation
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
