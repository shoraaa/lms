package com.library.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.library.model.document.Author;
import com.library.util.LibraryDatabaseUtil;

public class AuthorDAO {

    // Add a new author to the database
    // Returns the generated ID, or null if failed
    public Integer addAuthor(Author author) {
        String query = "INSERT INTO authors (name, email) VALUES (?, ?)";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
        String query = "DELETE FROM authors WHERE author_id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
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
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
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
        String query = "SELECT * FROM authors WHERE author_id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
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

    public List<Author> getAuthorsByIds(List<Integer> authorIds) {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors WHERE author_id IN (" + String.join(",", Collections.nCopies(authorIds.size(), "?")) + ")";

        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < authorIds.size(); i++) {
                stmt.setInt(i + 1, authorIds.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    authors.add(new Author(rs.getInt("author_id"), rs.getString("name"), rs.getString("email")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return authors;
    }

    // Get all authors from the database
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                Statement stmt = connection.createStatement()) {
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
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
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
        int id = rs.getInt("author_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Author(id, name, email);
    }
}
