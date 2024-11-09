package com.library.model.document;

public class Book extends Document {

    /**
     * Create new document from info and id.
     *
     * @param documentInfo documentInfo
     * @param documentId   documentId
     */
    public Book(DocumentInfo documentInfo, int documentId) {
        super(documentInfo, documentId);
    }
}
