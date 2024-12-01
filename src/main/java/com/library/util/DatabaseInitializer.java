package com.library.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

public class DatabaseInitializer {

    private static final String CREATE_AUTHORS_TABLE = 
        "CREATE TABLE IF NOT EXISTS authors (" +
        "author_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "email TEXT" +
        ");";

    private static final String CREATE_CATEGORIES_TABLE = 
        "CREATE TABLE IF NOT EXISTS categories (" +
        "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL" +
        ");";

    private static final String CREATE_PUBLISHERS_TABLE = 
        "CREATE TABLE IF NOT EXISTS publishers (" +
        "publisher_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL" +
        ");";

    private static final String CREATE_DOCUMENTS_TABLE = 
        "CREATE TABLE IF NOT EXISTS documents (" +
        "document_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "author_ids TEXT, " + // Store a comma-separated list of author IDs
        "category_ids TEXT, " + // Store a comma-separated list of category IDs
        "publisher_id INTEGER, " + // Store a comma-separated list of publisher IDs
        "isbn TEXT, " +
        "publication_date DATE, " +
        "date_added DATE, " +
        "current_quantity INTEGER NOT NULL, " +
        "total_quantity INTEGER NOT NULL, " +
        "language_id INTEGER, " + // Add language_id column
        "image_url TEXT," + // Add image_url column
        "description TEXT " + // Add description column
        ");";

    private static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "password TEXT NOT NULL, " +
        "name TEXT NOT NULL, " +
        "email TEXT NOT NULL UNIQUE, " +
        "phone_number TEXT NOT NULL UNIQUE, " +
        "registration_date DATE," +
        "role TEXT DEFAULT 'USER'" +
        ");";
    
    private static String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS transactions (" +
        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "user_id INTEGER NOT NULL, " +
        "document_id INTEGER NOT NULL, " +
        "borrow_date DATE NOT NULL, " +
        "due_date DATE, " +
        "return_date DATE, " +
        "is_returned BOOLEAN DEFAULT 0, " +
        "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
        "FOREIGN KEY(document_id) REFERENCES documents(document_id)" +
        ");";

    private static String CREATE_LANGUAGES_TABLE = "CREATE TABLE IF NOT EXISTS languages (" +
        "language_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL" +
        ");";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/library/database/library.db")) {
            Statement stmt = connection.createStatement();

            // stmt.execute("DROP TABLE IF EXISTS users;");
            // stmt.execute("DROP TABLE IF EXISTS transactions;");

            // Execute the table creation queries
            stmt.execute(CREATE_AUTHORS_TABLE);
            stmt.execute(CREATE_CATEGORIES_TABLE);
            stmt.execute(CREATE_PUBLISHERS_TABLE);
            stmt.execute(CREATE_DOCUMENTS_TABLE);
            stmt.execute(CREATE_USER_TABLE);
            stmt.execute(CREATE_TRANSACTION_TABLE);
            stmt.execute(CREATE_LANGUAGES_TABLE); // Execute the languages table creation query

            // insertSampleTransactions(connection);

            System.out.println(" Database tables created or already exist.");
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
    }

     private static void insertSampleTransactions(Connection connection) throws SQLException {
        String insertTransactionSQL = "INSERT OR IGNORE INTO transactions (user_id, document_id, borrow_date, due_date, return_date, is_returned) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertTransactionSQL)) {
            Random random = new Random();
            for (int i = 1; i <= 30; i++) {
                pstmt.setInt(1, random.nextInt(10) + 1); // Random user_id between 1 and 10
                pstmt.setInt(2, random.nextInt(10) + 1); // Random document_id between 1 and 10
                pstmt.setDate(3, Date.valueOf(LocalDate.now().minusDays(random.nextInt(30)))); // Random borrow date within the last 30 days
                pstmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(14))); // 2-week due date
                pstmt.setDate(5, null); // No return date initially
                pstmt.setBoolean(6, false); // Initially not returned
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}
