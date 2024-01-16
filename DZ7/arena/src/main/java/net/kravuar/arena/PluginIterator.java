package net.kravuar.arena;

import net.kravuar.plugin.RockPaperScissorsPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class PluginIterator implements Iterator<RockPaperScissorsPlugin> {
    private final Deque<File> jars;
    private RockPaperScissorsPlugin nextPlugin;

    public PluginIterator(Path pluginDirectoryPath) {
        this.jars = new LinkedList<>(
                List.of(Objects.requireNonNull(
                        pluginDirectoryPath.toFile().listFiles(file -> file.isFile() && file.getName().endsWith(".jar"))
                ))
        );
        advance();
    }

    @Override
    public boolean hasNext() {
        return nextPlugin != null;
    }

    @Override
    public RockPaperScissorsPlugin next() {
        if (nextPlugin == null)
            throw new NoSuchElementException();

        var plugin = nextPlugin;
        advance();
        return plugin;
    }

    private void advance() {
        while (!jars.isEmpty()) {
            var nextJar = jars.pollFirst();

            try {
                var jarURL = nextJar.toURI().toURL();
                var parentLoader = getClass().getClassLoader().getParent();
                try (var classLoader = new URLClassLoader(new URL[]{jarURL}, parentLoader)) {
                    var pluginClass = classLoader.loadClass("net.kravuar.plugin.Plugin");
                    this.nextPlugin = (RockPaperScissorsPlugin) pluginClass.getDeclaredConstructor().newInstance();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                         InstantiationException | InvocationTargetException ignored) {
                    // Couldn't instantiate plugin (Plugin creator mistake). Skipping.
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        this.nextPlugin = null;
    }
}
