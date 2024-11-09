package com.library.model;

import java.time.LocalDate;


public class Transaction {
    private int id;
    private int userId;
    private int documentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public Transaction(int userId, int documentId, LocalDate borrowDate) {
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.returnDate = null;
        this.isReturned = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
}
