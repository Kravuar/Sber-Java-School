package net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Manages the loading and iteration of plugins from a specified directory.
 *
 * @param <E> Type parameter representing the plugin interface.
 */
public class PluginManager<E> implements Iterable<E> {
    private final ClassLoader parent;
    private final Class<E> pluginInterface;
    private final Path pluginRootDirectory;
    private final boolean usesReversedClassloading;
    private final Function<URL[], URLClassLoader> classloaderProvider;

    /**
     * Constructs a PluginManager.
     *
     * @param pluginDirectoryPath The root directory containing the plugins (or directories with plugins).
     * @param pluginInterface The interface that plugins must implement.
     * @param parent The parent ClassLoader to use when loading plugins.
     * @param reversedClassloading Whether to use reversed classloading approach.
     */
    public PluginManager(Path pluginDirectoryPath, Class<E> pluginInterface, ClassLoader parent, boolean reversedClassloading) {
        this.pluginRootDirectory = pluginDirectoryPath;
        this.pluginInterface = pluginInterface;
        this.parent = parent;
        this.usesReversedClassloading = reversedClassloading;
        this.classloaderProvider = reversedClassloading
                ? (urls) -> new ReversedURLClassloader(urls, parent)
                : (urls) -> new URLClassLoader(urls, parent);
    }

    /**
     * Returns an iterator over the plugins, contained in the {@code PluginManager} base directory.
     *
     * @return Iterator for iterating over plugins.
     */
    @Override
    public Iterator<E> iterator() {
        try {
            return new PluginIterator<>(pluginRootDirectory, pluginInterface, parent, usesReversedClassloading);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a plugin from the specified subdirectory.
     *
     * @param pluginDirectory The relative directory path within the base directory.
     * @param className The fully qualified class name of the plugin.
     * @return An instance of the loaded plugin.
     * @throws PluginLoadingException If there is an issue loading the plugin.
     * @throws IllegalArgumentException If {@code pluginDirectory} is absolute.
     */
    public E loadPlugin(Path pluginDirectory, String className) throws PluginLoadingException {
        if (pluginDirectory.isAbsolute())
            throw new IllegalArgumentException("Plugin directory should be relative.");

        var files = pluginRootDirectory.resolve(pluginDirectory).toFile().listFiles();
        if (files == null)
            throw new PluginLoadingException(className, new NoSuchElementException("No plugins are present in the dir."));

        for (var file : files) {
            try {
                var classLoader = this.classloaderProvider.apply(new URL[]{file.toURI().toURL()});
                Class<?> pluginClass = classLoader.loadClass(className);
                return pluginClass.asSubclass(pluginInterface).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | ClassCastException | IOException ignored) {
                // Not this one, keep searching.
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                // Found but couldn't create.
                throw new PluginLoadingException(className, e);
            }
        }
        throw new PluginLoadingException(className, new ClassNotFoundException());
    }
}