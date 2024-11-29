package com.library.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class LibraryDatabaseUtil {

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/com/library/database/library.db";
    private static final HikariDataSource dataSource;

    // Static block to initialize HikariCP configuration
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DATABASE_URL);
        config.setMaximumPoolSize(10); // Adjust based on your application's needs
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000); // 30 seconds
        config.setConnectionTimeout(30000); // 30 seconds
        config.setMaxLifetime(600000); // 10 minutes

        dataSource = new HikariDataSource(config);
    }

    // Private constructor to prevent instantiation
    private LibraryDatabaseUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Provides a connection from the connection pool.
     * 
     * @return a database connection
     * @throws SQLException if unable to get a connection
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the data source and releases all connections.
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
