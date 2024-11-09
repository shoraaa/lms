package com.library.services;

import com.library.model.Transaction;
import com.library.model.Document;

import java.time.LocalDate;

public class TransactionService {
    private final DocumentDAO documentDAO;
    private final TransactionDAO transactionDAO;

    public TransactionService(DocumentDAO documentDAO, TransactionDAO transactionDAO) {
        this.documentDAO = documentDAO;
        this.transactionDAO = transactionDAO;
    }

    public boolean borrowDocument(int userId, int documentId) {
        Document document = documentDAO.getDocumentById(documentId);
        if (document.getQuantityCurrent() > 0) {
            Transaction transaction = new Transaction(userId, documentId, LocalDate.now());
            transactionDAO.createTransaction(transaction);
            documentDAO.updateDocumentQuantity(documentId, document.getQuantityCurrent() - 1);
            return true;
        } else {
            System.out.println("Document not available.");
            return false;
        }
    }

    public void returnDocument(int transactionId, int documentId) {
        transactionDAO.markAsReturned(transactionId);
        documentDAO.updateDocumentQuantity(documentId, documentDAO.getDocumentById(documentId).getQuantityCurrent() + 1);
    }
}
