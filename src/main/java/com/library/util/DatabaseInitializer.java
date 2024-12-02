package com.library.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import com.library.model.Document;
import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.UserDAO;

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
        "borrow_date DATE, " +
        "due_date DATE, " +
        "return_date DATE, " +
        "FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
        "FOREIGN KEY(document_id) REFERENCES documents(document_id) ON DELETE CASCADE" +
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

            System.out.println(" Database tables created or already exist.");
        } catch (SQLException e) {
            ErrorHandler.showErrorDialog(e);
        }
    }

}
