package com.library.services;

import com.library.model.User;

import java.sql.*;
import java.util.List;

public class UserDAO extends BaseDAO<User> {

    // Map ResultSet to User entity
    @Override
    protected User mapToEntity(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phoneNumber")
        );
        user.setId(rs.getInt("id"));
        user.setTimeRegistered(rs.getTimestamp("timeRegistered").toLocalDateTime());
        return user;
    }

    // Method to add a new user
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (name, email, phoneNumber, timeRegistered) VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), Timestamp.valueOf(user.getTimeRegistered())) > 0;
    }

    // Method to retrieve a user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return executeQueryForSingleEntity(sql, id);
    }

    // Method to update user details
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phoneNumber = ? WHERE id = ?";
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), user.getId()) > 0;
    }

    // Method to delete a user by ID
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    // Method to delete multiple users
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
        String sql = "SELECT * FROM users";
        return executeQueryForList(sql);
    }

    // Refactored method to count all users using BaseDAO's count method
    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return executeQueryForSingleInt(sql);  // Reuse count method from BaseDAO
    }
}
