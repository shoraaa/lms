package com.library.services;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.library.model.Document;
import com.library.util.AdapterUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DocumentDAO extends BaseDAO<Document> {

    private static final String INSERT_DOCUMENT_QUERY = 
        "INSERT INTO documents (name, author_ids, category_ids, publisher_id, isbn, publication_date, date_added, current_quantity, total_quantity, language_id, image_url, description) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_DOCUMENT_BY_ID = "SELECT * FROM documents WHERE document_id = ?";
    private static final String SELECT_ALL_DOCUMENTS = "SELECT * FROM documents";
    
    private static DocumentDAO instance;

    private DocumentDAO() {
        // Private constructor to prevent instantiation
    }

    public static DocumentDAO getInstance() {
        if (instance == null) {
            synchronized (DocumentDAO.class) {
                if (instance == null) {
                    instance = new DocumentDAO();
                }
            }
        }
        return instance;
    }
    // Add a new document
    public int add(Document document) {
        return executeUpdate(INSERT_DOCUMENT_QUERY, 
            document.getTitle(), 
            listToString(document.getAuthorIds()), 
            listToString(document.getCategoryIds()), 
            document.getPublisherId() != 0 ? document.getPublisherId() : null, 
            document.getIsbn(), 
            document.getPublicationDate() != null ? Date.valueOf(document.getPublicationDate()) : null, 
            document.getDateAddedToLibrary() != null ? Date.valueOf(document.getDateAddedToLibrary()) : null, 
            document.getCurrentQuantity(), 
            document.getTotalQuantity(),
            document.getLanguageId(), // Include language ID
            document.getImageUrl(),
            document.getDescription() // Include description
        );
    }

    // Delete a document by ID
    public boolean deleteDocument(int documentId) {
        String query = "DELETE FROM documents WHERE document_id = ?";
        return executeUpdate(query, documentId) > 0;
    }

    public boolean deleteDocuments(List<Integer> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return false; // No documents to delete
        }

        // Build the SQL query dynamically with placeholders for each document ID
        StringBuilder sql = new StringBuilder("DELETE FROM documents WHERE document_id IN (");
        for (int i = 0; i < documentIds.size(); i++) {
            sql.append("?");
            if (i < documentIds.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        // Use the executeUpdate method from BaseDAO to execute the query
        Integer rowsAffected = executeUpdate(sql.toString(), documentIds.toArray());

        return rowsAffected != null && rowsAffected > 0;
    }

    // Update document quantity
    public void updateDocumentQuantity(int documentId, int quantity) {
        String query = "UPDATE documents SET current_quantity = ? WHERE document_id = ?";
        executeUpdate(query, quantity, documentId);
    }

    // Retrieve a document by ID
    public Document getDocumentById(int documentId) {
        return executeQueryForSingleEntity(SELECT_DOCUMENT_BY_ID, documentId);
    }

    // Retrieve list of document by keyword
    public List<Document> getDocumentsByKeyword(String keyword) {
        String query = "SELECT * FROM documents WHERE name LIKE ? OR isbn LIKE ?";
        String keywordPattern = "%" + keyword + "%";
        return executeQueryForList(query, keywordPattern, keywordPattern);
    }

    // Retrieve all documents
    public List<Document> getAllDocuments() {
        return executeQueryForList(SELECT_ALL_DOCUMENTS);
    }

    // Count all documents
    public int countAllDocuments() {
        String query = "SELECT COUNT(*) FROM documents";
        return executeQueryForSingleInt(query);
    }

    // Convert ResultSet to Document object
    @Override
    protected Document mapToEntity(ResultSet rs) throws SQLException {
        int documentId = rs.getInt("document_id");
        String name = rs.getString("name");
        if (rs.wasNull()) {
            name = "Unknown Name";
        }

        List<Integer> authorIds = rs.getString("author_ids") != null ? stringToList(rs.getString("author_ids")) : Collections.emptyList();
        List<Integer> categoryIds = rs.getString("category_ids") != null ? stringToList(rs.getString("category_ids")) : Collections.emptyList();
        
        int publisherId = rs.getInt("publisher_id");
        if (rs.wasNull()) {
            publisherId = -1; // Assuming -1 indicates an unknown publisher
        }

        String isbn = rs.getString("isbn");
        if (rs.wasNull()) {
            isbn = "Unknown ISBN";
        }

        LocalDate datePublished = rs.getDate("publication_date") != null ? rs.getDate("publication_date").toLocalDate() : LocalDate.now();
        LocalDate dateAdded = rs.getDate("date_added") != null ? rs.getDate("date_added").toLocalDate() : LocalDate.now();

        int quantityCurrent = rs.getInt("current_quantity");
        if (rs.wasNull()) {
            quantityCurrent = 0;
        }

        int quantityTotal = rs.getInt("total_quantity");
        if (rs.wasNull()) {
            quantityTotal = 0;
        }

        int languageId = rs.getInt("language_id");
        if (rs.wasNull()) {
            languageId = -1; // Assuming -1 indicates an unknown language
        }

        String imageUrl = rs.getString("image_url");
        if (rs.wasNull()) {
            imageUrl = "No Image Available";
        }

        String description = rs.getString("description");
        if (rs.wasNull()) {
            description = "No Description Available";
        }

        return new Document.Builder(name)
            .documentId(documentId)
            .authorIds(authorIds)
            .categoryIds(categoryIds)
            .publisherId(publisherId)
            .isbn(isbn)
            .publicationDate(datePublished)
            .dateAddedToLibrary(dateAdded)
            .currentQuantity(quantityCurrent)
            .totalQuantity(quantityTotal)
            .languageId(languageId) // Set language ID
            .imageUrl(imageUrl) // Set image URL
            .description(description)
            .build();
    }

    // Helper method to convert List to CSV string
    private String listToString(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    // Helper method to convert CSV string back to List
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
