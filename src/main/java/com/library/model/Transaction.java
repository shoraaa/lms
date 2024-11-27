package com.library.model;

import java.time.LocalDate;

import javafx.beans.property.SimpleBooleanProperty;


public class Transaction {
    private int transactionId;
    private int userId;
    private int documentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;

     private final SimpleBooleanProperty selected; // For checkbox

    public Transaction(int userId, int documentId, LocalDate borrowDate) {
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.returnDate = null;
        this.isReturned = false;

        this.selected = new SimpleBooleanProperty(false);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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

    /**
     * @return The property for the checkbox
     */
    public SimpleBooleanProperty isSelectedProperty() {
        return selected;
    }

    /**
     * @return The selected status of the user
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * @param selected The selected status of the user
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
