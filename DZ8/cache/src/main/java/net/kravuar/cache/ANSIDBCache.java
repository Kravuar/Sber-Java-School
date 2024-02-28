package net.kravuar.cache;

import lombok.Getter;
import net.kravuar.cache.addapting.ValueWrapper;

import java.io.*;
import java.sql.*;

/**
 * DB based implementation of cache, uses provided connection to store
 * cache state in db, so that it persists between launches.
 * Internally delegates caching to provided inner cache.
 */
public class ANSIDBCache implements Cache {
    @Getter
    private final Connection connection;
    private final Cache innerCache;
    private final String mergeSQL;
    private final String deleteSQL;
    private final String truncateTableSQL;

    /**
     * Constructs {@code DBCache} with the given setting.
     *
     * @param connection      JDBC connection to use.
     * @param innerCache      inner cache to use.
     * @param tableName       name of the table.
     * @param keyColumnName   name of the key column.
     * @param valueColumnName name of the value column.
     */
    public ANSIDBCache(Connection connection, Cache innerCache, String tableName, String keyColumnName, String valueColumnName) {
        this.connection = connection;
        this.innerCache = innerCache;

        this.mergeSQL = "MERGE INTO " + tableName + " AS target " +
                "USING (SELECT CAST(? AS BLOB), CAST(? AS BLOB)) AS source(keyVal, valueVal) " +
                "ON target." + keyColumnName + " = source.keyVal " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET target." + valueColumnName + " = source.valueVal " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT (" + keyColumnName + ", " + valueColumnName + ") " +
                "   VALUES (source.keyVal, source.valueVal)";
        this.deleteSQL = "DELETE FROM " + tableName + " WHERE " + keyColumnName + "=?";
        this.truncateTableSQL = "TRUNCATE TABLE " + tableName;


        try {
            boolean exists = connection.getMetaData()
                    .getTables(null, null, tableName, null)
                    .next();

            if (!exists) {
                String createTableSQL = "CREATE TABLE " + tableName + " (" + keyColumnName + " BLOB, " + valueColumnName + " BLOB)";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                }
            } else {
                String selectSQL = "SELECT " + keyColumnName + ", " + valueColumnName + " FROM " + tableName;
                try (Statement statement = connection.createStatement()) {
                    ResultSet existingCache = statement.executeQuery(selectSQL);

                    // Initialize innerCache with persisted state
                    while (existingCache.next()) {
                        Blob keyBlob = existingCache.getBlob(keyColumnName);
                        Blob valueBlob = existingCache.getBlob(valueColumnName);

                        Object key = new ObjectInputStream(keyBlob.getBinaryStream()).readObject();
                        Object value = new ObjectInputStream(valueBlob.getBinaryStream()).readObject();

                        innerCache.put(key, value);
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException | ClassNotFoundException e) {
            throw new ANSIDBCacheException("Failed to read persisted cache", e);
        }
    }

    @Override
    public ValueWrapper get(Object key) {
        return innerCache.get(key);
    }

    /**
     * Stores cache value in the configured directory.
     *
     * @param key   the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @throws IllegalArgumentException if a value is not serializable.
     * @throws ANSIDBCacheException     if IO errors occurred upon saving.
     */
    @Override
    public void put(Object key, Object value) {
        try (PreparedStatement statement = connection.prepareStatement(mergeSQL)) {
            // Writing key and value bytes into SQL key params
            statement.setBlob(1, inputStreamFromObject(key));
            statement.setBlob(2, inputStreamFromObject(value));
            statement.executeUpdate();

            innerCache.put(key, value);
        } catch (SQLException e) {
            throw new ANSIDBCacheException("DB lookup failed", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Serialization error", e);
        }
    }

    /**
     * Deletes file associated with provided cache key.
     *
     * @param key the key whose mapping is to be removed from the cache.
     * @throws ANSIDBCacheException if IO errors occurred upon deletion.
     */
    @Override
    public void evict(Object key) {
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            // Writing key bytes into SQL key params
            statement.setBlob(1, inputStreamFromObject(key));
            statement.executeUpdate();

            innerCache.evict(key);
        } catch (SQLException e) {
            throw new ANSIDBCacheException("DB lookup failed", e);
        } catch (IOException e) {
            throw new ANSIDBCacheException("Serialization error", e);
        }
    }

    /**
     * Deletes all cache files within configured directory.
     *
     * @throws ANSIDBCacheException if IO errors occurred upon deletion.
     */
    @Override
    public void clear() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(truncateTableSQL);

            innerCache.clear();
        } catch (SQLException e) {
            throw new ANSIDBCacheException("", e);
        }
    }

    private static InputStream inputStreamFromObject(Object object) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(object);
        byte[] keyBytes = byteStream.toByteArray();

        return new ByteArrayInputStream(keyBytes);
    }
}
