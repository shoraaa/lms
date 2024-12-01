package com.library.services;

import java.sql.*;
import java.util.List;

import com.library.model.Author;

public class AuthorDAO extends BaseDAO<Author> {

    private static AuthorDAO instance;
    
    @Override
    protected String getTableName() {
        return "authors";
    }

    private AuthorDAO() {
        entriesCache = getAllEntries();
    }

    public static AuthorDAO getInstance() {
        if (instance == null) {
            synchronized (AuthorDAO.class) {
                if (instance == null) {
                    instance = new AuthorDAO();
                }
            }
        }
        return instance;
    }

    // Add a new author to the database
    public Integer add(Author author) {
        if (getAuthorByName(author.getName()) != null) {
            return getAuthorByName(author.getName()).getId();
        }

        String query = "INSERT INTO authors (name, email) VALUES (?, ?)";
        return executeUpdate(query, author.getName(), author.getEmail());
    }

    // Remove an author from the database by ID
    public boolean removeAuthor(int authorId) {
        String query = "DELETE FROM authors WHERE author_id = ?";
        return executeUpdate(query, authorId) != null;
    }

    // Update an existing author's details
    public boolean updateAuthor(Author author) {
        String query = "UPDATE authors SET name = ?, email = ? WHERE author_id = ?";
        return executeUpdate(query, author.getName(), author.getEmail(), author.getId()) != null;
    }

    // Get an author by ID
    public Author getAuthorById(int authorId) {
        String query = "SELECT * FROM authors WHERE author_id = ?";
        return executeQueryForSingleEntity(query, authorId);
    }

    // Get authors by their IDs
    public List<Author> getAuthorsByIds(List<Integer> authorIds) {
        String query = "SELECT * FROM authors WHERE author_id IN (" + String.join(",", java.util.Collections.nCopies(authorIds.size(), "?")) + ")";
        return executeQueryForList(query, authorIds.toArray());
    }

    // Get an author by name
    public Author getAuthorByName(String name) {
        String query = "SELECT * FROM authors WHERE name = ?";
        return executeQueryForSingleEntity(query, name);
    }

    // Get all authors from the database
    public List<Author> getAllAuthors() {
        String query = "SELECT * FROM authors";
        return executeQueryForList(query);
    }

    // Search authors by name (useful for autocomplete)
    public List<Author> searchAuthorsByName(String name) {
        String query = "SELECT * FROM authors WHERE name LIKE ?";
        return executeQueryForList(query, "%" + name + "%");
    }

    // Map a ResultSet row to an Author object
    @Override
    protected Author mapToEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("author_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Author(id, name, email);
    }
}
