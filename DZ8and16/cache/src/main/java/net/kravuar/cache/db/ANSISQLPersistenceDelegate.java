package net.kravuar.cache.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ANSISQLPersistenceDelegate extends AbstractDBPersistenceDelegate {


    /**
     * Constructs {@code ANSISQLPersistenceDelegate} with the given setting.
     *
     * @param connection      JDBC connection to use.
     * @param createTableSQL  SQL query to create table.
     * @param selectStateSQL  SQL query to load persistedState.
     * @param tableName       name of the table to use.
     * @param keyColumnName   name of the key column.
     * @param valueColumnName name of the value column.
     */
    public ANSISQLPersistenceDelegate(Connection connection, String createTableSQL, String selectStateSQL, String tableName, String keyColumnName, String valueColumnName) {
        super(connection, createTableSQL, selectStateSQL, tableName, keyColumnName, valueColumnName);
    }

    @Override
    public void delete(Object key) {
        String deleteSQL = "DELETE FROM " + tableName + " WHERE " + keyColumnName + "=?;";
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            // Writing key bytes into SQL key params
            statement.setBytes(1, bytesFromObject(key));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("DB evict failed", e);
        } catch (IOException e) {
            throw new PersistenceException("Serialization failed", e);
        }
    }

    @Override
    public void clear() {
        String truncateTableSQL = "TRUNCATE TABLE " + tableName + ";";
        try (Statement statement = connection.createStatement()) {
            statement.execute(truncateTableSQL);
        } catch (SQLException e) {
            throw new PersistenceException("DB clear failed", e);
        }
    }
}
