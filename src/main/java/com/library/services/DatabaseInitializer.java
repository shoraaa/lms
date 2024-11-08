package com.library.services;

import java.sql.*;

public class DatabaseInitializer {

    private static final String CREATE_AUTHORS_TABLE = 
        "CREATE TABLE IF NOT EXISTS authors (" +
        "author_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "email TEXT NOT NULL" +
        ");";

    private static final String CREATE_TAGS_TABLE = 
        "CREATE TABLE IF NOT EXISTS tags (" +
        "tag_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
        "tag_ids TEXT, " + // Store a comma-separated list of tag IDs
        "publisher_id INTEGER, " + // Store a comma-separated list of publisher IDs
        "isbn10 TEXT, " +
        "isbn13 TEXT, " +
        "date_published DATE, " +
        "date_added DATE, " +
        "quantity_current INTEGER NOT NULL, " +
        "quantity_total INTEGER NOT NULL" +
        ");";

    // Method to create all the tables
    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            Statement stmt = connection.createStatement();

            // Execute the table creation queries
            stmt.execute(CREATE_AUTHORS_TABLE);
            stmt.execute(CREATE_TAGS_TABLE);
            stmt.execute(CREATE_PUBLISHERS_TABLE);
            stmt.execute(CREATE_DOCUMENTS_TABLE);

            System.out.println("Database tables created or already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
