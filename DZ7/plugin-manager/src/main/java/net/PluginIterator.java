package net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Iterator for loading plugins from JAR files in a specified directory.
 * Plugins should have {@link PluginIterator#PROPERTIES_NAME} file
 * with specified {@link PluginIterator#MAIN_CLASS_PROPERTY_NAME}
 * to be detected by iterator.
 *
 * @param <E> Type parameter representing the plugin interface.
 */
public class PluginIterator<E> implements Iterator<E> {
    private static final String PROPERTIES_NAME = "plugin.properties";
    private static final String MAIN_CLASS_PROPERTY_NAME = "main.class";

    private final Class<E> pluginInterface;
    private final Deque<File> jars;
    private final Function<URL[], URLClassLoader> classloaderProvider;
    private E nextPlugin;

    /**
     * Constructs a PluginIterator.
     *
     * @param pluginBaseDirectoryPath The base directory containing the plugins.
     * @param pluginInterface The interface that plugins must implement.
     * @param parent The parent ClassLoader.
     * @param reversedClassloading Whether to user reversed classloading approach.
     * @throws IOException If an I/O error occurs while scanning for JAR files.
     */
    public PluginIterator(Path pluginBaseDirectoryPath, Class<E> pluginInterface, ClassLoader parent, boolean reversedClassloading) throws IOException {
        try (var files = Files.walk(pluginBaseDirectoryPath)) {
            this.jars = files
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".jar"))
                    .map(Path::toFile)
                    .collect(Collectors.toCollection(LinkedList::new));
        }
        this.pluginInterface = pluginInterface;
        this.classloaderProvider = reversedClassloading
                ? (urls) -> new ReversedURLClassloader(urls, parent)
                : (urls) -> new URLClassLoader(urls, parent);
    }

    /**
     * Checks if there is another plugin available.
     *
     * @return True if there is another plugin, false otherwise.
     */
    @Override
    public boolean hasNext() {
        if (nextPlugin == null) {
            advance();
            return nextPlugin != null;
        } else
            return true;
    }

    /**
     * Retrieves the next plugin in the iteration.
     *
     * @return The next plugin.
     * @throws NoSuchElementException If there are no more plugins to iterate over.
     */
    @Override
    public E next() {
        if (nextPlugin == null)
            throw new NoSuchElementException();

        var plugin = nextPlugin;
        advance();
        return plugin;
    }

    private void advance() {
        this.nextPlugin = null;
        while (!jars.isEmpty()) {
            File jarFile = jars.pollFirst();
            try (var jar = new JarFile(jarFile)) {
                var props = getPluginProps(jar);
                String pluginClassName = props.getProperty(MAIN_CLASS_PROPERTY_NAME);

                try {
                    var classLoader = classloaderProvider.apply(new URL[]{Utils.convertToJarUrl(jarFile)});
                    Class<?> pluginClass = classLoader.loadClass(pluginClassName);
                    this.nextPlugin = pluginClass.asSubclass(pluginInterface).getDeclaredConstructor().newInstance();
                    return;
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                         InstantiationException | InvocationTargetException | ClassCastException ignore) {
                    // Couldn't instantiate plugin (Plugin creator mistake). Skipping.
                }
            } catch (IOException e) {
                // Couldn't open JAR. Skipping.
            }
        }
    }

    private Properties getPluginProps(JarFile jar) throws IOException {
        Properties result = null;
        var entries = jar.entries();

        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (entry.getName().equals(PROPERTIES_NAME)) {
                try (InputStream is = jar.getInputStream(entry)) {
                    result = new Properties();
                    result.load(is);
                }
            }
        }
        return result;
    }
}