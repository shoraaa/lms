package com.library.services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
                rs.getInt("document_id"), null, null);
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setBorrowDate(rs.getDate("borrow_date") != null ? rs.getDate("borrow_date").toLocalDate() : null);
        transaction.setDueDate(rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null);
        transaction.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
        return transaction;
    }

    // Create a new transaction
    public Integer add(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, document_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
    
        // Safely convert dates or pass null if they are null
        Date borrowDate = transaction.getBorrowDate() != null ? Date.valueOf(transaction.getBorrowDate()) : null;
        Date dueDate = transaction.getDueDate() != null ? Date.valueOf(transaction.getDueDate()) : null;
    
        return executeUpdate(sql, transaction.getUserId(), transaction.getDocumentId(), borrowDate, dueDate);
    }
    

    // Mark a transaction as returned
    public Integer markAsReturned(int transactionId) {
        String sql = "UPDATE transactions SET return_date = ? WHERE transaction_id = ?";
        return executeUpdate(sql, Date.valueOf(LocalDate.now()), transactionId);
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

    public List<Transaction> getTransactionsByUserId(int userId) {
        return getEntitiesByField("user_id", String.valueOf(userId));
    }

    public List<Transaction> getTransactionsByDocumentId(int documentId) {
        return getEntitiesByField("document_id", String.valueOf(documentId));
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
            case "Status":
                return getAllEntries().stream().filter(transaction -> transaction.getStatus().contains(keyword)).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    public List<Transaction> getOverdueTransactions() {
        String sql = "SELECT * FROM transactions WHERE return_date > due_date";
        return executeQueryForList(sql);
    }

    public List<Transaction> getIssuingTransaction() {
        String sql = "SELECT * FROM transactions WHERE return_date IS NULL";
        return executeQueryForList(sql);
    }

    public Map<Month, Long> getTransactionsPerMonth() {
        // Retrieve all transactions from the database
        List<Transaction> transactions = getAllEntries();

        // Group by month and count transactions
        return transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> transaction.getBorrowDate().getMonth(), // Group by month
                Collectors.counting() // Count transactions
            ));
    }

    public List<Transaction> getBorrowsBetweenDates(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM transactions WHERE (borrow_date BETWEEN ? AND ?)";
        return executeQueryForList(sql, Date.valueOf(startDate), Date.valueOf(endDate));
    }

    public List<Transaction> getReturnsBetweenDates(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM transactions WHERE (return_date BETWEEN ? AND ?)";
        return executeQueryForList(sql, Date.valueOf(startDate), Date.valueOf(endDate));
    }


    public Map<LocalDate, Long> getBorrowPerDayRecent(int days) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Get the date 28 days ago
        LocalDate startDate = today.minusDays(days);

        // Fetch transactions from the database (you might need to modify your DAO method to support this)
        List<Transaction> transactions = TransactionDAO.getInstance().getBorrowsBetweenDates(startDate, today);

        // Group transactions by the date
        Map<LocalDate, Long> transactionCountPerDay = transactions.stream()
                .filter(transaction -> !transaction.getBorrowDate().isBefore(startDate)) // Make sure it's within the last 28 days
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getBorrowDate(), // Use either borrow or return date
                        Collectors.counting()
                ));

        // Fill in the missing dates with 0 if no transactions exist for that date
        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            transactionCountPerDay.putIfAbsent(date, 0L);
        }

        return transactionCountPerDay;
    }

    public Map<LocalDate, Long> getReturnPerDayRecent(int days) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Get the date 28 days ago
        LocalDate startDate = today.minusDays(days);

        // Fetch transactions from the database (you might need to modify your DAO method to support this)
        List<Transaction> transactions = TransactionDAO.getInstance().getReturnsBetweenDates(startDate, today);

        // Group transactions by the date
        Map<LocalDate, Long> transactionCountPerDay = transactions.stream()
                .filter(transaction -> !transaction.getBorrowDate().isBefore(startDate)) // Make sure it's within the last 28 days
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getReturnDate(), // Use either borrow or return date
                        Collectors.counting()
                ));

        // Fill in the missing dates with 0 if no transactions exist for that date
        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            transactionCountPerDay.putIfAbsent(date, 0L);
        }

        return transactionCountPerDay;
    }

    public Integer updateTransactionDates(int transactionId, LocalDate borrowDate, LocalDate dueDate) {
        String sql = "UPDATE transactions SET borrow_date = ?, due_date = ? WHERE transaction_id = ?";
        return executeUpdate(sql, Date.valueOf(borrowDate), Date.valueOf(dueDate), transactionId);
    }
    

    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        return executeQueryForSingleEntity(sql, transactionId);
    }
}
