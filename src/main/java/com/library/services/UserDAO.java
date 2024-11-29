package com.library.services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.library.model.User;

public class UserDAO extends BaseDAO<User> {

    private static UserDAO instance;

    private UserDAO() {
        // Private constructor to prevent instantiation
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }

    // Map ResultSet to User entity
    @Override
    protected User mapToEntity(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone_number")
        );
        user.setUserId(rs.getInt("user_id"));
        user.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        return user;
    }

    // Method to add a new user
    public Integer add(User user) {
        String sql = "INSERT INTO users (name, email, phone_number, registration_date) VALUES (?, ?, ?, ?)";
        System.out.println(user.getName() + ", " + user.getEmail() + ", " + user.getPhoneNumber());
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), Date.valueOf(user.getRegistrationDate()));
    }

    // Method to retrieve a user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return executeQueryForSingleEntity(sql, id);
    }

    public List<User> getUsersByTitle(String title) {
        String sql = "SELECT * FROM users WHERE title LIKE ?";
        return executeQueryForList(sql, title);
    }

    // Method to update user details
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone_number = ? WHERE id = ?";
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), user.getUserId()) > 0;
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
