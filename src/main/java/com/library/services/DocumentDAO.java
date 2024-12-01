package com.library.services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Document;

public class DocumentDAO extends BaseDAO<Document> {

    private static final String INSERT_DOCUMENT_QUERY =
        "INSERT INTO documents (name, author_ids, category_ids, publisher_id, isbn, publication_date, date_added, current_quantity, total_quantity, language_id, image_url, description) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_DOCUMENTS = "SELECT * FROM documents";

    private static DocumentDAO instance;

    private List<Document> documentCache; // Cache for documents
    private boolean cacheValid; // Cache validity flag

    private DocumentDAO() {
        documentCache = new ArrayList<>();
        cacheValid = false; // Cache starts as invalid
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

    @Override
    public Integer add(Document document) {
        Integer result = executeUpdate(INSERT_DOCUMENT_QUERY,
            document.getTitle(),
            listToString(document.getAuthorIds()),
            listToString(document.getCategoryIds()),
            document.getPublisherId() != 0 ? document.getPublisherId() : null,
            document.getIsbn(),
            document.getPublicationDate() != null ? Date.valueOf(document.getPublicationDate()) : null,
            document.getDateAddedToLibrary() != null ? Date.valueOf(document.getDateAddedToLibrary()) : null,
            document.getCurrentQuantity(),
            document.getTotalQuantity(),
            document.getLanguageId(),
            document.getImageUrl(),
            document.getDescription()
        );
        if (result != null) {
            invalidateCache();
        }
        return result;
    }

    public List<Document> getAllDocuments() {
        if (!cacheValid) {
            refreshCache();
        }
        return Collections.unmodifiableList(documentCache);
    }

    // Refresh the cache
    private synchronized void refreshCache() {
        documentCache = executeQueryForList(SELECT_ALL_DOCUMENTS);
        cacheValid = true;
    }

    // Invalidate the cache
    private synchronized void invalidateCache() {
        cacheValid = false;
    }

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

    private String listToString(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private List<Integer> stringToList(String str) {
        if (str == null || str.isEmpty()) return new ArrayList<>();
        String[] items = str.split(",");
        List<Integer> list = new ArrayList<>();
        for (String item : items) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }

    // Retrieve a document by ID
    public Document getDocumentById(int documentId) {
        // Check cache first if valid
        if (cacheValid) {
            for (Document document : documentCache) {
                if (document.getDocumentId() == documentId) {
                    return document; // Return the document from cache
                }
            }
        }

        // Fallback to database query if not found in cache
        String query = "SELECT * FROM documents WHERE document_id = ?";
        Document document = executeQueryForSingleEntity(query, documentId);

        // Optionally add to cache (to avoid stale data, prefer reloading full cache)
        if (document != null && cacheValid) {
            documentCache.add(document);
        }

        return document;
    }

    // Update document quantity
    public void updateDocumentQuantity(int documentId, int newQuantity) {
        String query = "UPDATE documents SET current_quantity = ? WHERE document_id = ?";
        
        // Update the database
        executeUpdate(query, newQuantity, documentId);

        // Update the cache if valid
        if (cacheValid) {
            for (Document document : documentCache) {
                if (document.getDocumentId() == documentId) {
                    document.setCurrentQuantity(newQuantity); // Update the cached document's quantity
                    break;
                }
            }
        }
    }

    // Retrieve list of documents by title
    public List<Document> getDocumentsByTitle(String title) {
        String keywordPattern = "%" + title + "%";

        // Use the cache if it's valid
        if (cacheValid) {
            List<Document> filteredDocuments = new ArrayList<>();
            for (Document document : documentCache) {
                if (document.getTitle() != null && document.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    filteredDocuments.add(document);
                }
            }
            return filteredDocuments;
        }

        // Fallback to database query if cache is invalid
        String query = "SELECT * FROM documents WHERE name LIKE ?";
        return executeQueryForList(query, keywordPattern);
    }

    // Count all documents
    public int countAllDocuments() {
        // Use cache if valid
        if (cacheValid) {
            return documentCache.size();
        }

        // Fallback to database query if cache is invalid
        String query = "SELECT COUNT(*) FROM documents";
        return executeQueryForSingleInt(query);
    }

    // Delete a single document by ID
    public boolean deleteDocument(int documentId) {
        String query = "DELETE FROM documents WHERE document_id = ?";
        boolean deleted = executeUpdate(query, documentId) > 0;

        // Update the cache if valid and deletion was successful
        if (deleted && cacheValid) {
            documentCache.removeIf(document -> document.getDocumentId() == documentId);
        }

        return deleted;
    }





}
