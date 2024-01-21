package net.kravuar.cache;

import net.kravuar.cache.addapting.AbstractNullAdaptingCache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Filesystem based implementation of cache.
 * Uses {@link Object#toString()} to map key to file entry.
 */
public class FileCache extends AbstractNullAdaptingCache {
    private final Path directory;
    private final boolean zip;

    /**
     * Constructs {@code FileCache} with the given setting.
     *
     * @param directory root directory in which cache files will be stored.
     * @param zip whether to zip cache files.
     * @throws IllegalArgumentException if {@code directory} if the file does not exist;
     * is not a directory; or it cannot be determined if the file is a directory or not.
     */
    public FileCache(boolean allowNullValues, Path directory, boolean zip) {
        super(allowNullValues);
        if (!Files.isDirectory(directory))
            throw new IllegalArgumentException("Directory is not a directory, he-he.");
        this.directory = directory;
        this.zip = zip;
    }

    /**
     * Performs lookup in the configured directory.
     *
     * @param key the key whose associated value is to be returned.
     * @throws FileCacheException if value is found but couldn't be deserialized.
     */
    @Override
    protected Object lookup(Object key) {
        Path file = keyToFile(key);
        try (ObjectInputStream ois = getInputStream(file)) {
            return ois.readObject();
        } catch (FileNotFoundException ignored) {
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
        Path file = keyToFile(key);
        try (ObjectOutputStream ous = getOutputStream(file)) {
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
        Path file = directory.resolve(key.toString());
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
        try (var cacheFilesStream = Files.list(directory).filter(Files::isRegularFile)) {
            cacheFilesStream.forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    throw new FileCacheException("Could not evict cache value for key: " + fileToKey(file) + ": " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new FileCacheException("Could not open cache directory in order to clear it: " + e.getMessage(), e);
        }
    }

    protected Path keyToFile(Object key) {
        return directory.resolve(key.toString() + ".cache");
    }

    private Object fileToKey(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.substring(0, fileName.length() - ".cache".length());
    }

    // Better create another layer of abstraction.
    protected ObjectOutputStream getOutputStream(Path path) throws IOException {
        OutputStream fos = new FileOutputStream(path.toFile());
        if (zip)
            fos = new ZipOutputStream(fos);
        return new ObjectOutputStream(fos);
    }

    protected ObjectInputStream getInputStream(Path path) throws IOException {
        InputStream fis = new FileInputStream(path.toFile());
        if (zip)
            fis = new ZipInputStream(fis);
        return new ObjectInputStream(fis);
    }
}
