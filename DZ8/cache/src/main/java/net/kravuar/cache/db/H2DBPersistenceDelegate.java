package net.kravuar.cache.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2DBPersistenceDelegate extends ANSISQLPersistenceDelegate {

    /**
     * Constructs {@code H2DBPersistenceDelegate} with the given setting.
     *
     * @param connection      JDBC connection to use.
     * @param tableName       name of the table to use.
     * @param keyColumnName   name of the key column.
     * @param valueColumnName name of the value column.
     */
    public H2DBPersistenceDelegate(Connection connection, String tableName, String keyColumnName, String valueColumnName) {
        super(
                connection,
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" + keyColumnName + " BLOB, " + valueColumnName + " BLOB);",
                "SELECT " + keyColumnName + ", " + valueColumnName + " FROM " + tableName + ";",
                tableName,
                keyColumnName,
                valueColumnName
        );
    }

    @Override
    public void put(Object key, Object value) {
        String mergeSQL = String.format(
                "MERGE INTO %s KEY(%s) VALUES(?, ?)",
                tableName, keyColumnName
        );
        try (PreparedStatement statement = connection.prepareStatement(mergeSQL)) {
            // Writing key and value bytes into SQL key params
            statement.setBytes(1, bytesFromObject(key));
            statement.setBytes(2, bytesFromObject(value));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("DB put failed", e);
        } catch (IOException e) {
            throw new PersistenceException("Serialization failed", e);
        }
    }
}
