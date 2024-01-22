package net.kravuar.cache;

import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

/**
 * Applies zip compression to cache files.
 */
public class ZipFileCache extends FileCache implements Closeable {
    @Getter
    private final Path zipPath;
    private final FileSystem zipFileSystem;

    /**
     * Constructs {@code FileCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values.
     * @param directory directory in which to create zip file.
     * @param suffix suffix (without trailing '.') that will be appended to cache files (will be used to prevent deletion of non cache files).
     * @param zipName name of the zip file.
     */
    public ZipFileCache(boolean allowNullValues, Path directory, String suffix, String zipName) throws IOException {
        super(allowNullValues, directory, suffix);
        this.zipPath = directory.resolve(zipName + ".zip");
        this.zipFileSystem = FileSystems.newFileSystem(
                this.zipPath,
                Map.of("create", "true")
        );
    }

    @Override
    protected Path keyToPath(Object key) {
        return zipFileSystem.getPath(keyToFileName(key));
    }

    @Override
    public void close() throws IOException {
        this.zipFileSystem.close();
    }
}
