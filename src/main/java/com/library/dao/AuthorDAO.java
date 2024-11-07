package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.library.model.DatabaseConnection;
import com.library.model.document.Author;
import com.library.util.LibraryDatabaseUtil;

public class AuthorDAO {

    private Connection connection;

    // Constructor initializes the database connection
    public AuthorDAO() {
        try {
            this.connection = LibraryDatabaseUtil.getConnection();
        } catch (SQLException e) {
            // Handle the exception, e.g., log the error or rethrow a custom exception
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    // Add a new author to the database
    // Returns the generated ID, or null if failed
    public Integer addAuthor(Author author) {
        String query = "INSERT INTO authors (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getEmail());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Remove an author from the database by ID
    public boolean removeAuthor(int authorId) {
        String query = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing author's details (name, email)
    public boolean updateAuthor(Author author) {
        String query = "UPDATE authors SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getEmail());
            stmt.setInt(3, author.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get an author by ID
    public Author getAuthorById(int authorId) {
        String query = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToAuthor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all authors from the database
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                authors.add(mapToAuthor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    // Search authors by name (useful for autocomplete)
    public List<Author> searchAuthorsByName(String name) {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors WHERE name LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                authors.add(mapToAuthor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    // Helper method to map a ResultSet row to an Author object
    private Author mapToAuthor(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Author(id, name, email);
    }
}
