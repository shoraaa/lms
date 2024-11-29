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
    public Integer add(Document document) {
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
            return false;
        }
    
        String query = "DELETE FROM documents WHERE document_id = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
    
            for (int documentId : documentIds) {
                preparedStatement.setInt(1, documentId);
                preparedStatement.addBatch();
            }
            int[] result = preparedStatement.executeBatch();
            return result.length == documentIds.size();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
    public List<Document> getDocumentsByTitle(String title) {
        String query = "SELECT * FROM documents WHERE name LIKE ?";
        String keywordPattern = "%" + title + "%";
        return executeQueryForList(query, keywordPattern);
    }

    // Retrieve all documents
    public List<Document> getAllDocuments() {
        return executeQueryForList(SELECT_ALL_DOCUMENTS);
    }

    // Lazy-retrieval of documents with pagination
    public List<Document> getDocumentsWithPagination(int offset, int limit) {
        String query = "SELECT * FROM documents LIMIT ? OFFSET ?";
        return executeQueryForList(query, limit, offset);
    }
    

    // Count all documents
    public int countAllDocuments() {
        String query = "SELECT COUNT(*) FROM documents";
        return executeQueryForSingleInt(query);
    }

    // Convert ResultSet to Document object
    @Override
    protected Document mapToEntity(ResultSet rs) throws SQLException {
        return new Document.Builder(rs.getString("name"))
            .documentId(rs.getInt("document_id"))
            .authorIds(stringToList(rs.getString("author_ids")))
            .categoryIds(stringToList(rs.getString("category_ids")))
            .publisherId(rs.getInt("publisher_id"))
            .isbn(rs.getString("isbn"))
            .publicationDate(rs.getDate("publication_date") != null ? rs.getDate("publication_date").toLocalDate() : null)
            .dateAddedToLibrary(rs.getDate("date_added") != null ? rs.getDate("date_added").toLocalDate() : null)
            .currentQuantity(rs.getInt("current_quantity"))
            .totalQuantity(rs.getInt("total_quantity"))
            .languageId(rs.getInt("language_id"))
            .imageUrl(rs.getString("image_url"))
            .description(rs.getString("description"))
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
