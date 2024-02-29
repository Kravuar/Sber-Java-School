package net.kravuar.cache.db;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDBPersistenceDelegate implements PersistenceDelegate {
    protected final Connection connection;
    protected final String tableName;
    protected final String keyColumnName;
    protected final String valueColumnName;
    private final String selectStateSQL;

    /**
     * Constructs {@code AbstractDBPersistenceDelegate} with the given setting.
     *
     * @param connection      JDBC connection to use.
     * @param tableName       name of the table to use.
     * @param createTableSQL  SQL query to create table.
     * @param selectStateSQL  SQL query to load persistedState.
     * @param keyColumnName   name of the key column.
     * @param valueColumnName name of the value column.
     */
    public AbstractDBPersistenceDelegate(Connection connection, String createTableSQL, String selectStateSQL, String tableName, String keyColumnName, String valueColumnName) {
        this.connection = connection;
        this.tableName = tableName;
        this.keyColumnName = keyColumnName;
        this.valueColumnName = valueColumnName;
        this.selectStateSQL = selectStateSQL;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create table", e);
        }
    }

    @Override
    public Map<Object, Object> loadPersistedState() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectStateSQL);
            Map<Object, Object> entries = new HashMap<>();
            while (resultSet.next()) {
                byte[] keyBytes = resultSet.getBytes(keyColumnName);
                byte[] valueBytes = resultSet.getBytes(valueColumnName);
                Object key = objectFromBytes(keyBytes);
                Object value = objectFromBytes(valueBytes);
                entries.put(key, value);
            }
            return entries;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to load cache state", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException("Deserialization error", e);
        }
    }

    protected static byte[] bytesFromObject(Object object) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(object);
        return byteStream.toByteArray();
    }

    protected static Object objectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectStream = new ObjectInputStream(byteStream);
        return objectStream.readObject();
    }
}
