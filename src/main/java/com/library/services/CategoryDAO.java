package com.library.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.library.model.Category;

public class CategoryDAO extends BaseDAO<Category> {

    private static CategoryDAO instance;

    private CategoryDAO() {
        // private constructor to prevent instantiation
    }

    public static CategoryDAO getInstance() {
        if (instance == null) {
            synchronized (CategoryDAO.class) {
                if (instance == null) {
                    instance = new CategoryDAO();
                }
            }
        }
        return instance;
    }

    // Add a new category if it doesn't already exist
    public Integer add(Category category) {
        if (getCategoryByName(category.getName()) != null) {
            return getCategoryByName(category.getName()).getId();
        }

        String sql = "INSERT INTO categories (name) VALUES (?)";
        return executeUpdate(sql, category.getName());
    }

    // Get a category by its name
    public Category getCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        return executeQueryForSingleEntity(sql, name);
    }

    // Get a category by its ID
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        return executeQueryForSingleEntity(sql, id);
    }

    // Fetch categories by their IDs
    public List<Category> getCategoriesByIds(List<Integer> categoryIds) {
        String query = "SELECT * FROM categories WHERE category_id IN (" + String.join(",", java.util.Collections.nCopies(categoryIds.size(), "?")) + ")";
        return executeQueryForList(query, categoryIds.toArray());
    }

    // Get all categories
    public List<Category> getAllCategories() {
        String sql = "SELECT * FROM categories";
        return executeQueryForList(sql);
    }

    // Get categories by name (using partial matching for autocomplete)
    public List<Category> getCategoriesByName(String name) {
        String sql = "SELECT * FROM categories WHERE name LIKE ?";
        return executeQueryForList(sql, "%" + name + "%");
    }

    // Map a ResultSet row to a Category object
    @Override
    protected Category mapToEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("category_id");
        String name = rs.getString("name");
        return new Category(id, name);
    }
}
