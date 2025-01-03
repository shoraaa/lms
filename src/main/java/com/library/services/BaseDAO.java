package com.library.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.library.model.Document;
import com.library.util.ErrorHandler;
import com.library.util.LibraryDatabaseUtil;

public abstract class BaseDAO<T> {

    protected List<T> entriesCache; // Cache 
    protected boolean cacheValid; // Cache validity flag
    private static final String SELECT_ALL_ENTRIES = "SELECT * FROM %s";

    public BaseDAO() {
        cacheValid = false;
    }

    // Abstract method to map a ResultSet to a specific entity
    protected abstract T mapToEntity(ResultSet rs) throws SQLException;

    // Helper method to get a connection (can be overridden if needed)
    protected Connection getConnection() throws SQLException {
        return LibraryDatabaseUtil.getConnection();
    }

    // Generic method to execute an update (insert, update, delete) query
    public Integer executeUpdate(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(stmt, params);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated ID
                    }
                }
            }
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
        return null;
    }

    // Generic method to execute a query and get a single entity by ID
    public T executeQueryForSingleEntity(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToEntity(rs);
            }
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
        return null;
    }

    // Generic method to execute a query and return a list of entities
    public List<T> executeQueryForList(String query, Object... params) {
        List<T> entities = null;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            entities = processResultSet(rs);
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
        return entities;
    }

    // Executes a query that returns a single result (e.g., SELECT COUNT(*) or SELECT by ID)
    protected int executeQueryForSingleInt(String query, Object... params) {
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            setParameters(stmt, params);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);  // Returning the first column, which should be the count
            }
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
        return 0;  // Default to 0 if no result found
    }

    // Helper method to set parameters for a PreparedStatement
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    // Abstract method to process the result set and map it to a list of entities
    protected List<T> processResultSet(ResultSet rs) throws SQLException {
        // Delegates to the child class's mapToEntity method to convert ResultSet to entities
        List<T> entities = new java.util.ArrayList<>();
        while (rs.next()) {
            entities.add(mapToEntity(rs));
        }
        return entities;
    }

    public List<T> getAllEntries() {
        if (!cacheValid) {
            refreshCache();
        }
        return Collections.unmodifiableList(entriesCache);
    }

    // Refresh the cache
    protected synchronized void refreshCache() {
        entriesCache = executeQueryForList(String.format(SELECT_ALL_ENTRIES, getTableName()));
        cacheValid = true;
    }

    // Invalidate the cache
    protected synchronized void invalidateCache() {
        cacheValid = false;
    }

    // Functional interface for extracting a field value from an entity of type T
    protected interface FieldRetriever<T> {
        String getFieldValue(T entity);
    }
    // Generalized method to filter entities by a field value using a FieldRetriever
    protected List<T> getEntitiesByField(String field, String value, FieldRetriever<T> fieldRetriever) {
        if (!cacheValid) {
            refreshCache();
        }

        List<T> filteredEntities = new ArrayList<>();
        for (T entity : entriesCache) {
            String fieldValue = fieldRetriever.getFieldValue(entity);
            if (fieldValue != null && fieldValue.toLowerCase().contains(value.toLowerCase())) {
                filteredEntities.add(entity);
            }
        }
        return filteredEntities;
    }

    protected List<T> getEntitiesByField(String field, String value) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + field + " LIKE ?";
        return executeQueryForList(sql, "%" + value + "%");
    }

    public List<String> getFieldOfAll(String field) {
        String sql = "SELECT " + field + " FROM " + getTableName();
        List<String> result = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(field));
            }
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
        return result;
    }


    public abstract Integer add(T entity);
    protected abstract String getTableName();
}
