package net.kravuar.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Applies zip compression to cache files.
 */
public class ZipFileCache extends FileCache {
    /**
     * Constructs {@code FileCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values.
     * @param directory directory in which cache files will be stored.
     * @throws IllegalArgumentException if {@code directory} does not exist;
     * is not a directory; cannot be determined if the file is a directory or not;
     * or is not empty.
     * @throws IOException if IO errors occurred upon checkin if the directory is empty.
     */
    public ZipFileCache(boolean allowNullValues, Path directory) throws IOException {
        super(allowNullValues, directory);
    }

    @Override
    protected String keyToFileName(Object key) {
        return super.keyToFileName(key) + ".zip";
    }

    @Override
    protected OutputStream getOutputStream(Object key) throws IOException {
        var fos = Files.newOutputStream(keyToPath(key));
        var zos = new ZipOutputStream(fos);
        zos.putNextEntry(new ZipEntry(super.keyToFileName(key)));
        return zos;
    }

    @Override
    protected InputStream getInputStream(Object key) throws IOException {
        try(var zip = new ZipFile(keyToPath(key).toFile())) {
            // Cant return inputstream itself, it closed by ZipFile.close()
            var copy = zip.getInputStream(zip.getEntry(super.keyToFileName(key))).readAllBytes();
            return new ByteArrayInputStream(copy);
        }
    }

}
