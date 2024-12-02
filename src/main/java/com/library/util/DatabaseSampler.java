package com.library.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import com.library.model.Document;
import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.UserDAO;

public class DatabaseSampler {
    public static void generateSampleTransactions(int sampleCount) {
        List<User> users = UserDAO.getInstance().getAllEntries();  // Get all users from UserDAO
        List<Document> documents = DocumentDAO.getInstance().getAllEntries();  // Get all documents from DocumentDAO

        if (users.isEmpty() || documents.isEmpty()) {
            System.out.println("No users or documents available to generate sample transactions.");
            return;
        }

        String insertTransactionSQL = "INSERT INTO transactions (user_id, document_id, borrow_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/library/database/library.db");
            PreparedStatement pstmt = connection.prepareStatement(insertTransactionSQL)) {
            
            Random random = new Random();
            
            for (int i = 0; i < sampleCount; i++) {
                // Random user and document
                User randomUser = users.get(random.nextInt(users.size()));
                Document randomDocument = documents.get(random.nextInt(documents.size()));
                
                // Random borrow date within the last 30 days
                LocalDate borrowDate = LocalDate.now().minusDays(random.nextInt(28));
                // Due date 14 days after borrow date
                LocalDate dueDate = borrowDate.plusDays(14);

                boolean isReturned = random.nextBoolean();
                if (isReturned) {
                    // Return date is within 28 days after borrow date
                    pstmt.setDate(5, Date.valueOf(borrowDate.plusDays(random.nextInt(28))));
                } else {
                    pstmt.setNull(5, java.sql.Types.DATE);
                }
                
                // Prepare the statement
                pstmt.setInt(1, randomUser.getUserId());
                pstmt.setInt(2, randomDocument.getDocumentId());
                pstmt.setDate(3, Date.valueOf(borrowDate));
                pstmt.setDate(4, Date.valueOf(dueDate));
                // pstmt.setDate(5, Date.valueOf(returnDate));
                
                pstmt.addBatch();  // Add to batch
            }
            
            // Execute the batch insert
            pstmt.executeBatch();
            System.out.println(sampleCount + " sample transactions generated.");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error generating sample transactions.");
        }
    }

    public static void generateSampleUsers(int sampleCount) {
        String insertUserSQL = "INSERT INTO users (password, name, email, phone_number, registration_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/library/database/library.db");
             PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {

            Random random = new Random();
            for (int i = 0; i < sampleCount; i++) {
                // Random user data
                String password = "password" + random.nextInt(10000000); // Simple random password
                String hashedPassword = PasswordUtil.hashPassword(password); // Hash the password
                String firstName = "User" + random.nextInt(1000); // Random first name
                String surName = "Surname" + random.nextInt(1000); // Random surname
                String name = firstName + "-" + surName;
                String email = name.toLowerCase() + "@example.com"; // Random email
                String phoneNumber = "+1-" + (random.nextInt(900000000) + 100000000); // Random phone number
                LocalDate registrationDate = LocalDate.now().minusDays(random.nextInt(365)); // Random registration date

                // Set the values in the PreparedStatement
                pstmt.setString(1, hashedPassword);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, phoneNumber);
                pstmt.setDate(5, Date.valueOf(registrationDate));

                // Execute the batch
                pstmt.addBatch();
            }

            // Execute all insertions
            pstmt.executeBatch();
            System.out.println(sampleCount + " sample users generated.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error generating sample users.");
        }
    }
}
