package com.library.util;

import java.sql.*;

public class LibraryDatabaseUtil {

    private static final String URL = "jdbc:sqlite:src/main/resources/com/library/database/library.db";
    private static Connection connection;

    // This method initializes the connection (if not already initialized)
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    // This method closes the connection
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
