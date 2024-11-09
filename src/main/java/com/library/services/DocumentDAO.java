package com.library.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Document;
import com.library.util.LibraryDatabaseUtil;

public class DocumentDAO {

    private static final String INSERT_DOCUMENT_QUERY = 
    "INSERT INTO documents (name, author_ids, tag_ids, publisher_id, isbn, date_published, date_added, quantity_current, quantity_total) " +
    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_DOCUMENT_BY_ID =
            "SELECT * FROM documents WHERE document_id = ?";
    private static final String SELECT_ALL_DOCUMENTS =
            "SELECT * FROM documents";

    // Method to add a new document
    public int addDocument(Document document) {
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_DOCUMENT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, document.getName());
                stmt.setString(2, listToString(document.getAuthorIds()));  // Convert List<Integer> to comma-separated String
                stmt.setString(3, listToString(document.getTagIds()));     // Convert List<Integer> to comma-separated String
                stmt.setObject(4, document.getPublisherId() != 0 ? document.getPublisherId() : null, Types.INTEGER); // Handle publisher_id null
                stmt.setString(5, document.getIsbn());
                stmt.setDate(6, document.getDatePublished() != null ? Date.valueOf(document.getDatePublished()) : null);
                stmt.setDate(7, document.getDateAdded() != null ? Date.valueOf(document.getDateAdded()) : null);
                stmt.setInt(8, document.getQuantityCurrent());
                stmt.setInt(9, document.getQuantityTotal());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Return the generated document ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if insertion failed
    }

    public boolean deleteDocument(int documentId) {
        String query = "DELETE FROM documents WHERE document_id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
    
            // Set the document ID in the query
            stmt.setInt(1, documentId);
            int rowsAffected = stmt.executeUpdate();
    
            // If rowsAffected is greater than 0, the document was deleted
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // If something goes wrong, return false
        }
    }

    public boolean deleteDocument(List<Integer> documentIds) {
        boolean success = true;
        for (int documentId : documentIds) {
            if (!deleteDocument(documentId)) {
                success = false;
            }
        }
        return success;
    }   

    public void updateDocumentQuantity(int documentId, int quantity) {
        String query = "UPDATE documents SET quantity_current = ? WHERE document_id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, documentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    // Method to retrieve a document by ID
    public Document getDocumentById(int documentId) {
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_DOCUMENT_BY_ID)) {

            stmt.setInt(1, documentId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return buildDocumentFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to retrieve all documents
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            ResultSet resultSet = stmt.executeQuery(SELECT_ALL_DOCUMENTS);
            while (resultSet.next()) {
                documents.add(buildDocumentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public int countAllDocument() {
        String query = "SELECT COUNT(*) FROM documents";  // Assuming you want the count directly from the DB
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            ResultSet resultSet = stmt.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);  // Getting the count directly from the result set
            }
        } catch (SQLException e) {
            // Log the error instead of just printing the stack trace
            System.err.println("Error counting documents: " + e.getMessage());
            e.printStackTrace(); // Optionally, log this using a logging framework
        }
        return 0;
    }
    

    private Document buildDocumentFromResultSet(ResultSet resultSet) throws SQLException {
        return new Document(
                resultSet.getInt("document_id"),
                resultSet.getString("name"),
                stringToList(resultSet.getString("author_ids")),
                stringToList(resultSet.getString("tag_ids")),
                resultSet.getInt("publisher_id"),
                resultSet.getString("isbn"),
                resultSet.getDate("date_published").toLocalDate(),
                resultSet.getDate("date_added").toLocalDate(),
                resultSet.getInt("quantity_current"),
                resultSet.getInt("quantity_total")
        );
    }

    // Helper methods to convert List to CSV string and vice versa
    private String listToString(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private List<Integer> stringToList(String str) {
        List<Integer> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            String[] items = str.split(",");
            for (String item : items) {
                list.add(Integer.parseInt(item));
            }
        }
        return list;
    }
}
