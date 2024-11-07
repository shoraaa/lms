package com.library.dao;

import com.library.model.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    private static final String INSERT_DOCUMENT_QUERY =
            "INSERT INTO documents (name, author_ids, tag_ids, publisher_id, date_published, date_added, quantity_current, quantity_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_DOCUMENT_BY_ID =
            "SELECT * FROM documents WHERE document_id = ?";
    private static final String SELECT_ALL_DOCUMENTS =
            "SELECT * FROM documents";

    // Method to add a new document
    public int addDocument(Document document) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db");
             PreparedStatement stmt = connection.prepareStatement(INSERT_DOCUMENT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, document.getName());
            stmt.setString(2, listToString(document.getAuthorIds()));    // Convert List<Integer> to comma-separated String
            stmt.setString(3, listToString(document.getTagIds()));       // Convert List<Integer> to comma-separated String
            stmt.setInt(4, document.getPublisherId()); 
            stmt.setDate(5, Date.valueOf(document.getDatePublished()));
            stmt.setDate(6, Date.valueOf(document.getDateAdded()));
            stmt.setInt(7, document.getQuantityCurrent());
            stmt.setInt(8, document.getQuantityTotal());

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

    // Method to retrieve a document by ID
    public Document getDocumentById(int documentId) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db");
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
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db");
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

    private Document buildDocumentFromResultSet(ResultSet resultSet) throws SQLException {
        return new Document(
                resultSet.getInt("document_id"),
                resultSet.getString("name"),
                stringToList(resultSet.getString("author_ids")),
                stringToList(resultSet.getString("tag_ids")),
                resultSet.getInt("publisher_id"),
                resultSet.getString("isbn10"),
                resultSet.getString("isbn13"),
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
