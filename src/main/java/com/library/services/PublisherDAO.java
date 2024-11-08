package com.library.services;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.library.model.Publisher;
import com.library.util.LibraryDatabaseUtil;

public class PublisherDAO {

    // Method to add a publisher if it doesn't already exist
    public int addPublisher(Publisher publisher) {
        // Check if the publisher exists first
        if (getPublisherByName(publisher.getName()) != null) {
            return -1; // Return -1 if publisher already exists
        }

        // If it doesn't exist, insert the publisher
        String sql = "INSERT INTO publishers (name) VALUES (?)";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, publisher.getName());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if insert fails
    }

    // Method to get a publisher by name
    public Publisher getPublisherByName(String name) {
        String sql = "SELECT * FROM publishers WHERE name = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Publisher(resultSet.getInt("publisher_id"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no publisher found
    }

    // Method to get a publisher by ID
    public Publisher getPublisherById(int id) {
        String sql = "SELECT * FROM publishers WHERE publisher_id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Publisher(resultSet.getInt("publisher_id"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no publisher found
    }
}
