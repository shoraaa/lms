package com.library.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.library.model.Publisher;

public class PublisherDAO extends BaseDAO<Publisher> {

    // Implement the abstract method to map ResultSet to Publisher entity
    @Override
    protected Publisher mapToEntity(ResultSet rs) throws SQLException {
        return new Publisher(rs.getInt("publisher_id"), rs.getString("name"));
    }

    // Add a publisher if it doesn't already exist, leveraging the BaseDAO's methods
    public int addPublisher(Publisher publisher) {
        // Check if the publisher exists first
        Publisher existingPublisher = getPublisherByName(publisher.getName());
        if (existingPublisher != null) {
            return existingPublisher.getId(); // Return existing publisher's ID
        }

        // Insert the new publisher and return the generated ID
        String sql = "INSERT INTO publishers (name) VALUES (?)";
        return executeUpdate(sql, publisher.getName());
    }

    // Get a publisher by name
    public Publisher getPublisherByName(String name) {
        String sql = "SELECT * FROM publishers WHERE name = ?";
        return executeQueryForSingleEntity(sql, name);
    }

    // Get a publisher by ID
    public Publisher getPublisherById(int id) {
        String sql = "SELECT * FROM publishers WHERE publisher_id = ?";
        return executeQueryForSingleEntity(sql, id);
    }
}
