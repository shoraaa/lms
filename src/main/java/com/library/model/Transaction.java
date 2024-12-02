package com.library.model;

import java.time.LocalDate;

import com.library.services.DocumentDAO;
import com.library.services.UserDAO;
import com.library.util.Localization;

import javafx.beans.property.SimpleBooleanProperty;


public class Transaction {
    private int transactionId;
    private int userId;
    private int documentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;

     private final SimpleBooleanProperty selected; // For checkbox

    public Transaction(int userId, int documentId, LocalDate borrowDate, LocalDate dueDate) {
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;

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

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public String getStatus() {
        Localization localization = Localization.getInstance();
        if (borrowDate == null) {
            return localization.getString("pending");
        }
        if (returnDate != null) {
            if (returnDate.isAfter(LocalDate.now())) {
                return localization.getString("overdue");
            } else if (returnDate.isAfter(dueDate)) {
                return localization.getString("returnLate");
            }
            return localization.getString("returned");
        } else if (LocalDate.now().isAfter(dueDate)) {
            return localization.getString("overdue");
        } else {
            return localization.getString("borrowing");
        }
    }

    public User getUser() {
        return UserDAO.getInstance().getUserById(userId);
    }

    public Document getDocument() {
        return DocumentDAO.getInstance().getDocumentById(documentId);
    }

    public String getUserName() {
        User user = UserDAO.getInstance().getUserById(userId);
        if (user == null) {
            return "Unknown User";
        }
        return user.getName();
    }

    public String getDocumentTitle() {
        if (DocumentDAO.getInstance().getDocumentById(documentId) == null) {
            return "Unknown Document";
        }
        return DocumentDAO.getInstance().getDocumentById(documentId).getTitle();
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
