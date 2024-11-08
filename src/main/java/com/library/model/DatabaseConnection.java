package com.library.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides a connection to the library's database. The database is a SQLite
 * database stored in a file named "library.db" in the current directory.
 * <p>
 * The database connection is a singleton, meaning that only one connection
 * to the database is allowed to exist at any time. The first time the
 * connection is requested, the database tables will be created if they do
 * not already exist.
 * <p>
 * The database connection is automatically closed when the program exits.
 *
 * @author Dat
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:library.db";
    private static Connection connection;

    private DatabaseConnection() {}

    /**
     * Returns the database connection. If the connection does not already
     * exist, it will be created. The database tables will also be created
     * if they do not already exist.
     * <p>
     * If the connection is closed, it will be reopened.
     *
     * @return The database connection.
     * @throws SQLException If the connection could not be created or opened.
     */
    public static Connection getInstance() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            initializeDatabase(connection);  // Initialize tables on first connection
        }
        return connection;
    }

    /**
     * Initializes the database tables. This method is called when the
     * connection is first created.
     *
     * @param conn The database connection.
     */
    private static void initializeDatabase(Connection conn) {
        String createAuthorsTable = "CREATE TABLE IF NOT EXISTS Authors (" +
                                    "author_id INTEGER PRIMARY KEY, " +
                                    "name TEXT NOT NULL, " +
                                    "email TEXT" +
                                    ");";

        String createDocumentsTable = "CREATE TABLE IF NOT EXISTS Documents (" +
                                      "document_id INTEGER PRIMARY KEY, " +
                                      "name TEXT NOT NULL, " +
                                      "isbn_13 TEXT, " +
                                      "isbn_10 TEXT, " +
                                      "publisher TEXT, " +
                                      "published_date TEXT" +
                                      ");";

        String createDocumentAuthorsTable = "CREATE TABLE IF NOT EXISTS DocumentAuthors (" +
                                            "document_id INTEGER, " +
                                            "author_id INTEGER, " +
                                            "FOREIGN KEY (document_id) REFERENCES Documents(document_id), " +
                                            "FOREIGN KEY (author_id) REFERENCES Authors(author_id), " +
                                            "PRIMARY KEY (document_id, author_id)" +
                                            ");";

        String createPublishersTable = "CREATE TABLE IF NOT EXISTS Publishers (" +
                                        "publisher_id INTEGER PRIMARY KEY, " +
                                        "name TEXT NOT NULL" +
                                        ");";

        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL commands to create tables
            stmt.execute(createAuthorsTable);
            stmt.execute(createDocumentsTable);
            stmt.execute(createDocumentAuthorsTable);
            stmt.execute(createPublishersTable);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.out.println("Error initializing database tables: " + e.getMessage());
        }
    }

    /**
     * Closes the database connection. This method should be called when the
     * connection is no longer needed.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.out.println("Failed to close the database connection.");
            }
        }
    }
}
