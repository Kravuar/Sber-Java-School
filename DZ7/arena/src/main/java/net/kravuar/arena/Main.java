package net.kravuar.arena;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarFile;

public class Main {
    // To run this - run mvn package for DZ7,
    // plugins (participants) directory will appear in plugins module.
    // Works only from CLI. So java -jar...
    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, URISyntaxException {
        // Isolate core from common (plugins)
        var urls = loadFromJar();

        URLClassLoader commonsLoader = new URLClassLoader("common", urls.common, null);
        URLClassLoader appLoader = new URLClassLoader("core", urls.core, commonsLoader);

        // Load and invoke runner class from the core app
        Class<?> runnerClass = appLoader.loadClass("net.kravuar.arena.ArenaRunner");
        Constructor<?> runnerConstructor = runnerClass.getConstructor(Path.class, int.class);

        // Infinite loop
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Options:");
            System.out.println("1. Start Arena");
            System.out.println("2. Exit program");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Incorrect Input. Try again.");
                continue;
            } finally {
                scanner.nextLine();
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter path to plugins directory: ");
                    Path pluginsDir;
                    try {
                        pluginsDir = Paths.get(scanner.nextLine());
                    } catch (InvalidPathException ignored) {
                        System.out.println("Path is incorrect.");
                        continue;
                    }

                    System.out.print("Enter the number of rounds to win the battle: ");
                    int roundsToWinBattle = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        Runnable runnerInstance = (Runnable) runnerConstructor.newInstance(pluginsDir, roundsToWinBattle);
                        runnerInstance.run();
                    } catch (Throwable e) {
                        System.out.println("Couldn't start Arena: " + e.getMessage());
                        continue;
                    }
                    break;
                case 2:
                    exit = true;
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    private record URLs(URL[] common, URL[] core) {}

    private static URLs loadFromJar() throws URISyntaxException {
        Path jarFilePath = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        try (var ignored = new JarFile(jarFilePath.toFile())) {
            List<URL> commonUrls = new ArrayList<>();
            List<URL> coreUrls = new ArrayList<>();

            commonUrls.add(URI.create("jar:" + jarFilePath.toUri().toURL() + "!/lib/common/").toURL());
            coreUrls.add(URI.create("jar:" + jarFilePath.toUri().toURL() + "!/lib/core/").toURL());
            coreUrls.add(URI.create("jar:" + jarFilePath.toUri().toURL() + "!/").toURL());

            return new URLs(
                    commonUrls.toArray(URL[]::new),
                    coreUrls.toArray(URL[]::new)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
