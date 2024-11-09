package com.library.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

import com.library.model.Transaction;

public class TransactionDAO extends BaseDAO<Transaction> {

    // Map ResultSet to Transaction entity
    @Override
    protected Transaction mapToEntity(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction(rs.getInt("user_id"),
                rs.getInt("document_id"), rs.getDate("borrow_date").toLocalDate());
        transaction.setId(rs.getInt("id"));
        transaction.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
        transaction.setReturned(rs.getBoolean("is_returned"));
        return transaction;
    }

    // Create a new transaction
    public void createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, document_id, borrow_date, is_returned) VALUES (?, ?, ?, ?)";
        executeUpdate(sql, transaction.getUserId(), transaction.getDocumentId(), transaction.getBorrowDate(), transaction.isReturned());
    }

    // Mark a transaction as returned
    public void markAsReturned(int transactionId) {
        String sql = "UPDATE transactions SET return_date = ?, is_returned = ? WHERE id = ?";
        executeUpdate(sql, Date.valueOf(LocalDate.now()), true, transactionId);
    }

    // Get all transactions for a specific user
    public List<Transaction> getUserTransactions(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        return executeQueryForList(sql, userId);
    }
}
