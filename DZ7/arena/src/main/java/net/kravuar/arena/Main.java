package net.kravuar.arena;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    // To launch this need to provide path to common lib directory (containing plugin-api.jar)
    // path to plugins dir (containing plugins .jar's)
    // roundsPerBattle int (> 1, odd)
    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, URISyntaxException {
        System.out.println(Arrays.toString(args));
        if (args.length != 3)
            throw new RuntimeException("Na-ah");

        var commonURLs = toArrayOfURLs(args[0].split(";"));
        var pluginsDir = new File(args[1]).toPath();
        var roundsPerBattle = Integer.parseInt(args[2]);

        URLClassLoader commonsLoader = new URLClassLoader(commonURLs, null);

        URL[] classpath = toArrayOfURLs(System.getProperty("java.class.path").split(File.pathSeparator));
        URLClassLoader appLoader = new URLClassLoader(classpath, commonsLoader);

        // Load and invoke runner class from the core app
        Class<?> runnerClass = appLoader.loadClass("net.kravuar.arena.ArenaRunner");
        Constructor<?> runnerConstructor = runnerClass.getConstructor(Path.class, int.class);
        Object runnerInstance = runnerConstructor.newInstance(pluginsDir, roundsPerBattle);
        Method mainMethod = runnerClass.getMethod("run");
        mainMethod.invoke(runnerInstance);
    }

    private static URL[] toArrayOfURLs(String[] paths) {
        return Arrays.stream(paths)
                .map(File::new)
                .map(uri -> {
                    try {
                        return uri.toURI().toURL(); // d_(-_-)
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(URL[]::new);
    }
}
