package com.library.services;

import com.library.model.User;
import com.library.util.LibraryDatabaseUtil;
import com.library.util.LibraryDatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Method to add a new user
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (name, email, phoneNumber, timeRegistered) VALUES (?, ?, ?, ?)";
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getTimeRegistered()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve a user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phoneNumber")
                );
                user.setId(rs.getInt("id"));
                user.setTimeRegistered(rs.getTimestamp("timeRegistered").toLocalDateTime());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to update user details
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phoneNumber = ? WHERE id = ?";
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setInt(4, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete a user
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUsers(List<Integer> userIds) {
        boolean success = true;
        for (int userId : userIds) {
            if (!deleteUser(userId)) {
                success = false;
            }
        }
        return success;
    }   

    // Method to retrieve all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = LibraryDatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phoneNumber")
                );
                user.setId(rs.getInt("id"));
                user.setTimeRegistered(rs.getTimestamp("timeRegistered").toLocalDateTime());
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int countAllUsers() {
        String query = "SELECT COUNT(*) FROM users";  // Assuming you want the count directly from the DB
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            ResultSet resultSet = stmt.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);  // Getting the count directly from the result set
            }
        } catch (SQLException e) {
            // Log the error instead of just printing the stack trace
            System.err.println("Error counting users: " + e.getMessage());
            e.printStackTrace(); // Optionally, log this using a logging framework
        }
        return 0;
    }
}

