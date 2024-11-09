package com.library.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Document;
import com.library.model.Publisher;
import com.library.model.Tag;
import com.library.model.document.Author;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;
import com.library.services.TagDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class AddNewBookController {

    @FXML private TextField titleTextField;
    @FXML private TextField isbn13TextField;
    @FXML private TextField isbn10TextField;
    @FXML private TextField publisherTextField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private ComboBox<Author> authorComboBox;
    @FXML private ComboBox<Author> tagComboBox;
    @FXML private VBox authorContainer;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;
    
    private AuthorDAO authorDAO;

    // List to keep track of authors added
    private List<Author> selectedAuthors;

    public AddNewBookController() {
        authorDAO = new AuthorDAO();
        selectedAuthors = new ArrayList<>();
    }

    // This method will be called after the FXML is loaded
    @FXML
    public void initialize() {
        // Initialize ComboBox for author search (auto-complete)
        initializeAuthorComboBox();

        // Initialize the save button action
        saveButton.setOnAction(event -> saveNewBook());
    }

    // Initialize ComboBox with a listener for the text input to perform auto-completion
    private void initializeAuthorComboBox() {
        // Set the string converter for the ComboBox to show the author's name
        authorComboBox.setConverter(new StringConverter<Author>() {
            @Override
            public String toString(Author author) {
                return author == null ? "" : author.getName();
            }

            @Override
            public Author fromString(String string) {
                // Create a new author if necessary, or return null to rely on database suggestions
                return new Author(-1, string, "");
            }
        });

        // Add a listener to update suggestions as the user types
        authorComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                List<Author> authors = authorDAO.searchAuthorsByName(newValue);
                authorComboBox.getItems().setAll(authors);
                authorComboBox.show();
            } else {
                authorComboBox.hide();
            }
        });
    }

    // Method to show an alert dialog
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to clear the form fields after successful addition
    private void clearForm() {
        titleTextField.clear();
        isbn13TextField.clear();
        isbn10TextField.clear();
        publisherTextField.clear();
        publishedDatePicker.setValue(null);
        selectedAuthors.clear();
    }

    // Method to save the new book to the database
    @FXML
    private void saveNewBook() {

        String title = titleTextField.getText();
        String isbn13 = isbn13TextField.getText();
        String isbn10 = isbn10TextField.getText();
        String publisherName = publisherTextField.getText();
        String authorName = authorComboBox.getEditor().getText(); // Assuming you have a ComboBox for authors
        String tagName = tagComboBox.getEditor().getText(); // Assuming you have a ComboBox for tags
        LocalDate publishedDate = publishedDatePicker.getValue();

        // Validate inputs
        if (title.isEmpty() || isbn13.isEmpty() || isbn10.isEmpty() || publisherName.isEmpty() || publishedDate == null || authorName.isEmpty() || tagName.isEmpty()) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        // Check if publisher exists, if not, add it
        PublisherDAO publisherDAO = new PublisherDAO();
        Publisher publisher = publisherDAO.getPublisherByName(publisherName);
        if (publisher == null) {
            publisher = new Publisher(0, publisherName);  // Create new publisher if not found
            int publisherId = publisherDAO.addPublisher(publisher);
            publisher.setId(publisherId);
        }

        // Check if author exists, if not, add it
        AuthorDAO authorDAO = new AuthorDAO();
        Author author = authorDAO.searchAuthorsByName(authorName).stream().findFirst().orElse(null);
        if (author == null) {
            author = new Author(0, authorName, "");  // Create new author if not found
            int authorId = authorDAO.addAuthor(author);
            author.setId(authorId);
        }

        // Check if tag exists, if not, add it
        TagDAO tagDAO = new TagDAO();
        Tag tag = tagDAO.getTagByName(tagName);
        if (tag == null) {
            tag = new Tag(0, tagName);  // Create new tag if not found
            int tagId = tagDAO.addTag(tag);
            tag.setId(tagId);
        }

        // Create the document with the publisher, author, and tag IDs
        List<Integer> authorIds = new ArrayList<>();
        authorIds.add(author.getId());  // Assuming one author per document

        List<Integer> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());  // Assuming one tag per document

        Document document = new Document(title, authorIds, tagIds, publisher.getId(), isbn10, isbn13, publishedDate, LocalDate.now(), 0, 1);

        // Save document (you need to implement saving logic)
        DocumentDAO documentDAO = new DocumentDAO();
        int documentId = documentDAO.addDocument(document);

        if (documentId > -1) {
            showAlert("Success", "Book has been added successfully!", Alert.AlertType.INFORMATION);
            clearForm();  // Clear the form after successful save
        } else {
            showAlert("Error", "There was an issue adding the book.", Alert.AlertType.ERROR);
        }
    }


    // Get the list of author IDs from the selected authors
    private List<Integer> getAuthorIds() {
        List<Integer> authorIds = new ArrayList<>();
        for (Author author : selectedAuthors) {
            authorIds.add(author.getId());
        }
        return authorIds;
    }

    // Method to close the window
    @FXML
    public void closeWindow() {
        clearForm();
        saveButton.getScene().getWindow().hide();
    }
}
