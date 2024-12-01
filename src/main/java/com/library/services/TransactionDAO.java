package com.library.services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Document;
import com.library.model.Publisher;
import com.library.model.Transaction;
import com.library.model.User;

public class TransactionDAO extends BaseDAO<Transaction> {

    private static TransactionDAO instance;

    @Override
    protected String getTableName() {
        return "transactions";
    }

    private TransactionDAO() {
        entriesCache = getAllTransactions();
    }

    public static TransactionDAO getInstance() {
        if (instance == null) {
            synchronized (TransactionDAO.class) {
                if (instance == null) {
                    instance = new TransactionDAO();
                }
            }
        }
        return instance;
    }

    // Map ResultSet to Transaction entity
    @Override
    protected Transaction mapToEntity(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction(rs.getInt("user_id"),
                rs.getInt("document_id"), rs.getDate("borrow_date").toLocalDate(), rs.getDate("borrow_date").toLocalDate());
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
        transaction.setReturned(rs.getBoolean("is_returned"));
        return transaction;
    }

    // Create a new transaction
    public Integer add(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, document_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, transaction.getUserId(), transaction.getDocumentId(), Date.valueOf(transaction.getBorrowDate()), Date.valueOf(transaction.getDueDate()));
    }

    // Mark a transaction as returned
    public void markAsReturned(int transactionId) {
        String sql = "UPDATE transactions SET return_date = ?, is_returned = ? WHERE transaction_id = ?";
        executeUpdate(sql, Date.valueOf(LocalDate.now()), true, transactionId);
    }

    // Get all transactions for a specific user
    public List<Transaction> getUserTransactions(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        return executeQueryForList(sql, userId);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        String sql = "SELECT * FROM transactions";
        return executeQueryForList(sql);
    }

    // Count all transactions
    public int countAllTransactions() {
        String sql = "SELECT COUNT(*) FROM transactions";
        return executeQueryForSingleInt(sql);
    }

    // Delete a transaction by ID
    public void deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        executeUpdate(sql, transactionId);
    }

    // Delete multiple transactions by a list of IDs
    public void deleteTransactions(List<Integer> transactionIds) {
        if (transactionIds == null || transactionIds.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM transactions WHERE transaction_id IN (" + transactionIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", ")) + ")";
        executeUpdate(sql, transactionIds.toArray());
    }

    public List<Transaction> getMostBorrowedTransaction(int count) {
        String sql = "SELECT * FROM transactions GROUP BY document_id ORDER BY COUNT(*) DESC LIMIT ?";
        return executeQueryForList(sql, count);
    }

    public List<Transaction> getTransactionsById(String transactionId) {
        return getEntitiesByField("transaction_id", transactionId);
    }

    public List<Transaction> getTransactionsByUser(String userName) {
        return getEntitiesByField("user_id", userName, transaction -> {
            User user = UserDAO.getInstance().getUserById(transaction.getUserId());
            return user != null ? user.getName() : "";
        });
    }

    public List<Transaction> getTransactionsByDocument(String documentName) {
        return getEntitiesByField("document_id", documentName, transaction -> {
            Document document = DocumentDAO.getInstance().getDocumentById(transaction.getDocumentId());
            return document != null ? document.getTitle() : "";
        });
    }

    public List<Transaction> getTransactionsByKeyword(String keyword, String type) {
        switch (type) {
            case "ID":
                return getTransactionsById(keyword);
            case "User":
                return getTransactionsByUser(keyword);
            case "Document":
                return getTransactionsByDocument(keyword);
            default:
                return Collections.emptyList();
        }
    }
}
