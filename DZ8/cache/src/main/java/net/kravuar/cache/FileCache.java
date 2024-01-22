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
    @Getter
    protected final String suffix;

    /**
     * Constructs {@code FileCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values.
     * @param directory directory in which cache files will be stored.
     * @param suffix suffix (without trailing '.') that will be appended to cache files (will be used to prevent deletion of non cache files).
     * @throws IllegalArgumentException if {@code directory} does not exist;
     * is not a directory; or it cannot be determined if the file is a directory or not.
     */
    public FileCache(boolean allowNullValues, Path directory, String suffix) {
        super(allowNullValues);
        if (!Files.isDirectory(directory))
            throw new IllegalArgumentException("Incorrect directory path.");
        this.directory = directory;
        this.suffix = '.' + suffix;
    }

    /**
     * Performs lookup in the configured directory.
     *
     * @param key the key whose associated value is to be returned.
     * @throws FileCacheException if value is found but couldn't be deserialized.
     */
    @Override
    protected Object lookup(Object key) {
        try (ObjectInputStream ois = getInputStream(key)) {
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
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @throws IllegalArgumentException if a value is not serializable.
     * @throws FileCacheException if IO errors occurred upon saving.
     */
    @Override
    public void put(Object key, Object value) {
        try (ObjectOutputStream ous = getOutputStream(key)) {
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
        try (var directoryFiles = Files.list(directory).filter(Files::isRegularFile)) {
            directoryFiles
                    .filter(file -> file.getFileName().toString().endsWith(suffix))
                    .forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    throw new FileCacheException("Could not evict cache value for key: " + fileNameToKeyString(file.getFileName().toString()) + ": " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new FileCacheException("Could not open cache directory in order to clear it: " + e.getMessage(), e);
        }
    }

    protected Object fileNameToKeyString(String fileName) {
        return fileName.substring(0, fileName.length() - suffix.length());
    }

    protected String keyToFileName(Object key) {
        return key.toString() + suffix;
    }

    protected Path keyToPath(Object key) {
        return this.directory.resolve(keyToFileName(key));
    }

    protected ObjectOutputStream getOutputStream(Object key) throws IOException {
        return new ObjectOutputStream(Files.newOutputStream(keyToPath(key)));
    }

    protected ObjectInputStream getInputStream(Object key) throws IOException {
        return new ObjectInputStream(Files.newInputStream(keyToPath(key)));
    }
}
