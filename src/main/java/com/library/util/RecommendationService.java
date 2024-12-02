package com.library.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.library.model.Document;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;

public class RecommendationService {

    // Find the most similar users based on transaction history
    public List<Document> recommendBooks(User user) {
        List<Transaction> transactions = TransactionDAO.getInstance().getTransactionsByUserId(user.getUserId());
        List<Integer> borrowedBookIds = transactions.stream()
                .map(Transaction::getDocumentId)
                .collect(Collectors.toList());

        // Fetch all users' transaction history
        List<User> allUsers = UserDAO.getInstance().getAllEntries();
        Map<User, List<Transaction>> userTransactionMap = new HashMap<>();

        // Populate user transaction history map
        for (User otherUser : allUsers) {
            if (otherUser.getUserId() != user.getUserId()) {
                userTransactionMap.put(otherUser, TransactionDAO.getInstance().getTransactionsByUserId(otherUser.getUserId()));
            }
        }

        // Calculate similarity scores for all users
        Map<User, Double> similarityScores = new HashMap<>();
        for (Map.Entry<User, List<Transaction>> entry : userTransactionMap.entrySet()) {
            User otherUser = entry.getKey();
            double similarity = calculateSimilarity(transactions, entry.getValue());
            similarityScores.put(otherUser, similarity);
        }

        // Sort users by similarity (descending) and get the top N similar users
        List<User> similarUsers = similarityScores.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5) // Top 5 similar users
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Recommend books based on similar users' borrowing history
        Set<Integer> recommendedBookIds = new HashSet<>();
        for (User similarUser : similarUsers) {
            List<Transaction> similarUserTransactions = userTransactionMap.get(similarUser);
            for (Transaction transaction : similarUserTransactions) {
                if (!borrowedBookIds.contains(transaction.getDocumentId())) {
                    recommendedBookIds.add(transaction.getDocumentId());
                }
            }
        }

        // Convert recommended book IDs to Document objects
        return DocumentDAO.getInstance().getDocumentsByIds(new ArrayList<>(recommendedBookIds));
    }

    private double calculateSimilarity(List<Transaction> userTransactions, List<Transaction> otherUserTransactions) {
        // Get all documents (books) that have been borrowed by either user
        Set<Integer> allDocumentIds = new HashSet<>();
        for (Transaction transaction : userTransactions) {
            allDocumentIds.add(transaction.getDocumentId());
        }
        for (Transaction transaction : otherUserTransactions) {
            allDocumentIds.add(transaction.getDocumentId());
        }
    
        // Create borrowing vectors for each user
        Map<Integer, Integer> userVector = new HashMap<>();
        for (Transaction transaction : userTransactions) {
            userVector.put(transaction.getDocumentId(), 1);  // User borrowed this book
        }
    
        Map<Integer, Integer> otherUserVector = new HashMap<>();
        for (Transaction transaction : otherUserTransactions) {
            otherUserVector.put(transaction.getDocumentId(), 1);  // Other user borrowed this book
        }
    
        // Now calculate the cosine similarity
        double dotProduct = 0.0;
        double userMagnitude = 0.0;
        double otherUserMagnitude = 0.0;
    
        for (Integer documentId : allDocumentIds) {
            // Get the borrowing status (0 or 1) for both users for this document
            int userBorrowed = userVector.getOrDefault(documentId, 0);
            int otherUserBorrowed = otherUserVector.getOrDefault(documentId, 0);
    
            // Calculate dot product
            dotProduct += userBorrowed * otherUserBorrowed;
    
            // Calculate magnitudes (sum of squares)
            userMagnitude += Math.pow(userBorrowed, 2);
            otherUserMagnitude += Math.pow(otherUserBorrowed, 2);
        }
    
        // Calculate the cosine similarity
        double denominator = Math.sqrt(userMagnitude) * Math.sqrt(otherUserMagnitude);
        if (denominator == 0) {
            return 0.0;  // Avoid division by zero (if both users haven't borrowed anything in common)
        }
        return dotProduct / denominator;
    }
    
}
