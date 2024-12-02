package com.library.model;

import java.time.LocalDate;

import com.library.services.DocumentDAO;
import com.library.services.UserDAO;

import javafx.beans.property.SimpleBooleanProperty;


public class Transaction {
    private int transactionId;
    private int userId;
    private int documentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private boolean isReturned;

     private final SimpleBooleanProperty selected; // For checkbox

    public Transaction(int userId, int documentId, LocalDate borrowDate, LocalDate dueDate) {
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
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
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public String getStatus() {
        if (returnDate != null) {
            if (returnDate.isAfter(LocalDate.now())) {
                return "Overdue";
            } else if (returnDate.isAfter(dueDate)) {
                return "Returned (late)";
            }
            return "Returned";
        } else if (LocalDate.now().isAfter(dueDate)) {
            return "Overdue";
        } else {
            return "Borrowing";
        }
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
