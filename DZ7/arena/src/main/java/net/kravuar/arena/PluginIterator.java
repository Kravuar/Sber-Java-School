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
    }

    @Override
    public boolean hasNext() {
        if (nextPlugin == null) {
            advance();
            return nextPlugin != null;
        }
        else
            return true;
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
        this.nextPlugin = null;
        while (!jars.isEmpty()) {
            var nextJar = jars.pollFirst();

            try {
                var jarURL = nextJar.toURI().toURL();

                var commonLoader = getClass().getClassLoader().getParent();
                try (var classLoader = new URLClassLoader(new URL[]{jarURL}, commonLoader)) {
                    var pluginClass = classLoader.loadClass("net.kravuar.plugin.Plugin");
                    this.nextPlugin = (RockPaperScissorsPlugin) pluginClass.getDeclaredConstructor().newInstance();
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                         InstantiationException | InvocationTargetException ignore) {
                    // Couldn't instantiate plugin (Plugin creator mistake). Skipping.
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
