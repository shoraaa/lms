package com.library.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.library.model.Tag;
import com.library.util.LibraryDatabaseUtil;

public class TagDAO {


    // Method to add a tag if it doesn't already exist
    public int addTag(Tag tag) {
        // Check if the tag exists first
        if (getTagByName(tag.getName()) != null) {
            return -1; // Return -1 if tag already exists
        }

        // If it doesn't exist, insert the tag
        String sql = "INSERT INTO tags (name) VALUES (?)";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, tag.getName());
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

    // Method to get a tag by name
    public Tag getTagByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Tag(resultSet.getInt("tag_id"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no tag found
    }

    // Method to get a tag by ID
    public Tag getTagById(int id) {
        String sql = "SELECT * FROM tags WHERE id = ?";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Tag(resultSet.getInt("tag_id"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no tag found
    }

    // Fetch tags by their IDs
    public List<Tag> getTagsByIds(List<Integer> tagIds) {
        List<Tag> tags = new ArrayList<>();
        String query = "SELECT * FROM tags WHERE tag_id IN (" + String.join(",", Collections.nCopies(tagIds.size(), "?")) + ")";
        
        try (Connection connection = LibraryDatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < tagIds.size(); i++) {
                stmt.setInt(i + 1, tagIds.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(rs.getInt("tag_id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tags;
    }

    // Method to get all tags (if needed)
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags";
        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tags.add(new Tag(resultSet.getInt("tag_id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
