package com.library.services;

import com.library.model.Transaction;
import com.library.model.Document;

import java.time.LocalDate;

public class TransactionService {
    private final DocumentDAO documentDAO;
    private final TransactionDAO transactionDAO;
    private static TransactionService instance;

    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    public TransactionService() {
        this.documentDAO = DocumentDAO.getInstance();
        this.transactionDAO = TransactionDAO.getInstance();
    }

    public boolean borrowDocument(int userId, int documentId, LocalDate borrowDate, LocalDate dueDate) {
        Document document = documentDAO.getDocumentById(documentId);
        if (document.getCurrentQuantity() > 0) {
            Transaction transaction = new Transaction(userId, documentId, borrowDate, dueDate);
            transactionDAO.add(transaction);
            documentDAO.updateDocumentQuantity(documentId, document.getCurrentQuantity() - 1);
            return true;
        } else {
            System.out.println("Document not available.");
            return false;
        }
    }

    public Integer returnDocument(Transaction transaction) {
        documentDAO.updateDocumentQuantity(transaction.getDocumentId(), documentDAO.getDocumentById(transaction.getDocumentId()).getCurrentQuantity() + 1);
        return transactionDAO.markAsReturned(transaction.getTransactionId());
    }
}
