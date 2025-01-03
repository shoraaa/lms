package com.library.services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.User;
import com.library.model.Publisher;
import com.library.util.PasswordUtil;

public class UserDAO extends BaseDAO<User> {

    private static UserDAO instance;

    @Override
    protected String getTableName() {
        return "users";
    }

    private UserDAO() {
        entriesCache = getAllUsers();
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
        user.setPasswordHash(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }

    // Method to add a new user
    public Integer add(User user) {
        String sql = "INSERT INTO users (name, email, phone_number, registration_date, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), Date.valueOf(user.getRegistrationDate()), user.getPasswordHash(), user.getRole());
    }

    // Verify the password for a user
    public boolean verifyPassword(int userId, String password) {
        User user = getUserById(userId);  // Retrieve the user by ID
        if (user != null) {
            return PasswordUtil.verifyPassword(password, user.getPasswordHash());
        }
        return false;
    }

    // Method to update user's password
    public boolean updatePassword(int userId, String newPassword) {
        String hashedPassword  = PasswordUtil.hashPassword(newPassword);;
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        return executeUpdate(sql, hashedPassword, userId) > 0;
    }

    // Method to retrieve a user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return executeQueryForSingleEntity(sql, id);
    }

    public User getUserByEmailOrPhone(String string) {
        String sql = "SELECT * FROM users WHERE email = ? OR phone_number = ?";
        return executeQueryForSingleEntity(sql, string, string);
    }

    public List<User> getUsersByName(String name) {
        return getEntitiesByField("name", name);
    }

    public List<User> getUsersByEmail(String email) {
        return getEntitiesByField("email", email);
    }

    public List<User> getUsersByPhoneNumber(String phoneNumber) {
        return getEntitiesByField("phone_number", phoneNumber);
    }

    public List<User> getUsersById(String id) {
        return getEntitiesByField("user_id", id);
    }

    // Method to update user details
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone_number = ? WHERE user_id = ?";
        return executeUpdate(sql, user.getName(), user.getEmail(), user.getPhoneNumber(), user.getUserId()) > 0;
    }

    // Method to delete a user by ID
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
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

    public List<User> getUsersByKeyword(String keyword, String type) {
        switch (type) {
            case "Name":
                return getUsersByName(keyword);
            case "E-mail":
                return getUsersByEmail(keyword);
            case "Phone Number":
                return getUsersByPhoneNumber(keyword);
            case "ID":
                return getUsersById(keyword);
            default:
                return Collections.emptyList();
        }
    }
}
