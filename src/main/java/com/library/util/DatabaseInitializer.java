package com.library.util;

import java.sql.*;

public class DatabaseInitializer {

    private static final String CREATE_AUTHORS_TABLE = 
        "CREATE TABLE IF NOT EXISTS authors (" +
        "author_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "email TEXT NOT NULL" +
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
        "total_quantity INTEGER NOT NULL" +
        ");";

    private static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "email TEXT NOT NULL UNIQUE, " +
        "phone_number TEXT NOT NULL, " +
        "time_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ");";
    
    private static String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS transactions (" +
        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "user_id INTEGER NOT NULL, " +
        "document_id INTEGER NOT NULL, " +
        "borrow_date DATE NOT NULL, " +
        "return_date DATE, " +
        "is_returned BOOLEAN DEFAULT 0, " +
        "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
        "FOREIGN KEY(document_id) REFERENCES document(document_id)" +
        ");";
    

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/library/database/library.db")) {
            Statement stmt = connection.createStatement();

            // Execute the table creation queries
            stmt.execute(CREATE_AUTHORS_TABLE);
            stmt.execute(CREATE_CATEGORIES_TABLE);
            stmt.execute(CREATE_PUBLISHERS_TABLE);
            stmt.execute(CREATE_DOCUMENTS_TABLE);

            stmt.execute(CREATE_USER_TABLE);
            stmt.execute(CREATE_TRANSACTION_TABLE);

            System.out.println(" Database tables created or already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
