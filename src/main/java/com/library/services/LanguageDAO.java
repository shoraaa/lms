package com.library.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.library.model.Language;
import com.library.util.LibraryDatabaseUtil;

public class LanguageDAO extends BaseDAO<Language> {

    // SQL queries
    private static final String INSERT_LANGUAGE_QUERY = "INSERT INTO languages (name) VALUES (?)";
    private static final String SELECT_LANGUAGE_BY_ID = "SELECT * FROM languages WHERE language_id = ?";
    private static final String SELECT_LANGUAGE_BY_NAME = "SELECT * FROM languages WHERE name = ?";
    private static final String SELECT_ALL_LANGUAGES = "SELECT * FROM languages";
    private static final String SELECT_LANGUAGES_BY_NAME_PARTIAL = "SELECT * FROM languages WHERE name LIKE ?";

    // Singleton instance
    private static LanguageDAO instance;

    // Private constructor to prevent instantiation
    private LanguageDAO() {}

    // Method to get the singleton instance
    public static synchronized LanguageDAO getInstance() {
        if (instance == null) {
            instance = new LanguageDAO();
        }
        return instance;
    }

    // Method to add a new language
    public Integer add(Language language) {
        // Check if the language already exists
        if (getLanguageByName(language.getName()) != null) {
            return getLanguageByName(language.getName()).getId();
        }

        try (Connection connection = LibraryDatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_LANGUAGE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, language.getName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated language ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if insertion failed
    }

    // Method to get a language by ID
    public Language getLanguageById(int id) {
        return executeQueryForSingleEntity(SELECT_LANGUAGE_BY_ID, id);
    }

    // Method to get a language by name
    public Language getLanguageByName(String name) {
        return executeQueryForSingleEntity(SELECT_LANGUAGE_BY_NAME, name);
    }

    // Method to get all languages
    public List<Language> getAllLanguages() {
        return executeQueryForList(SELECT_ALL_LANGUAGES);
    }

    // Method to get languages by partial name match
    public List<Language> getLanguagesByName(String name) {
        return executeQueryForList(SELECT_LANGUAGES_BY_NAME_PARTIAL, "%" + name + "%");
    }

    // Count all languages
    public int countAllLanguages() {
        String query = "SELECT COUNT(*) FROM languages";
        return executeQueryForSingleInt(query);
    }

    @Override
    protected Language mapToEntity(ResultSet rs) throws SQLException {
        int languageId = rs.getInt("language_id");
        String name = rs.getString("name");
        return new Language(languageId, name);
    }
}
