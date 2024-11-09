package com.library.services;

import com.library.model.Transaction;
import com.library.util.LibraryDatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public void createTransaction(Transaction transaction) {
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO transactions (user_id, document_id, borrow_date, is_returned) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, transaction.getUserId());
            stmt.setInt(2, transaction.getDocumentId());
            stmt.setDate(3, Date.valueOf(transaction.getBorrowDate()));
            stmt.setBoolean(4, transaction.isReturned());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAsReturned(int transactionId) {
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE transactions SET return_date = ?, is_returned = ? WHERE id = ?")) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setBoolean(2, true);
            stmt.setInt(3, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getUserTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM transactions WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction(rs.getInt("user_id"),
                        rs.getInt("document_id"), rs.getDate("borrow_date").toLocalDate());
                transaction.setId(rs.getInt("id"));
                transaction.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
                transaction.setReturned(rs.getBoolean("is_returned"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
