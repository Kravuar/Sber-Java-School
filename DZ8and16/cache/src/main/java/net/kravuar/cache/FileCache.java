package net.kravuar.cache;

import lombok.Getter;
import net.kravuar.cache.addapting.AbstractNullAdaptingCache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Filesystem based implementation of cache.
 * Uses {@link Object#toString()} to map key to file entry.
 */
public class FileCache extends AbstractNullAdaptingCache {
    @Getter
    protected final Path directory;

    /**
     * Constructs {@code FileCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values.
     * @param directory       directory in which cache files will be stored.
     * @throws IllegalArgumentException if {@code directory} does not exist;
     *                                  is not a directory; cannot be determined if the file is a directory or not;
     *                                  or is not empty.
     * @throws IOException              if IO errors occurred upon checkin if the directory is empty.
     */
    public FileCache(boolean allowNullValues, Path directory) throws IOException {
        super(allowNullValues);
        if (!Files.isDirectory(directory))
            throw new IllegalArgumentException("Incorrect directory path.");
        try (var files = Files.list(directory)) {
            if (files.findAny().isPresent())
                throw new IllegalArgumentException("Directory is not empty.");
        }
        this.directory = directory;
    }

    /**
     * Performs lookup in the configured directory.
     *
     * @param key the key whose associated value is to be returned.
     * @throws FileCacheException if value is found but couldn't be deserialized.
     */
    @Override
    protected Object lookup(Object key) {
        try (ObjectInputStream ois = new ObjectInputStream(getInputStream(key))) {
            return ois.readObject();
        } catch (FileNotFoundException | NoSuchFileException ignored) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new FileCacheException("Could not read cache value: " + e.getMessage(), e);
        }
    }

    /**
     * Stores cache value in the configured directory.
     *
     * @param key   the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @throws IllegalArgumentException if a value is not serializable.
     * @throws FileCacheException       if IO errors occurred upon saving.
     */
    @Override
    public void put(Object key, Object value) {
        try (ObjectOutputStream ous = new ObjectOutputStream(getOutputStream(key))) {
            ous.writeObject(value);
        } catch (NotSerializableException e) {
            throw new IllegalArgumentException(String.format("%s class is not serializable.", value.getClass()));
        } catch (IOException e) {
            throw new FileCacheException("Could not write cache value to file: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes file associated with provided cache key.
     *
     * @param key the key whose mapping is to be removed from the cache.
     * @throws FileCacheException if IO errors occurred upon deletion.
     */
    @Override
    public void evict(Object key) {
        Path file = keyToPath(key);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new FileCacheException("Could not evict cache value for key " + key + ": " + e.getMessage(), e);
        }
    }

    /**
     * Deletes all cache files within configured directory.
     *
     * @throws FileCacheException if IO errors occurred upon deletion.
     */
    @Override
    public synchronized void clear() {
        try (var directoryFiles = Files.walk(directory).skip(1)) {
            directoryFiles.forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    throw new FileCacheException("Could not evict cache value for key: " + file.getFileName().toString() + ": " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new FileCacheException("Could not open cache directory in order to clear it: " + e.getMessage(), e);
        }
    }

    protected String keyToFileName(Object key) {
        return key.toString();
    }

    protected Path keyToPath(Object key) {
        return directory.resolve(keyToFileName(key));
    }

    protected OutputStream getOutputStream(Object key) throws IOException {
        return Files.newOutputStream(keyToPath(key));
    }

    protected InputStream getInputStream(Object key) throws IOException {
        return Files.newInputStream(keyToPath(key));
    }
}
