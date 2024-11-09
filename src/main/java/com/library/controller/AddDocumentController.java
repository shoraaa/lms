package com.library.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.library.api.GoogleBooksAPI;
import com.library.api.GoogleBooksAPI.BookDetails;
import com.library.model.Author;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

public class AddDocumentController {

    @FXML private TextField titleTextField;
    @FXML private TextField isbnTextField;
    @FXML private TextField publisherTextField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private ComboBox<Author> authorComboBox;
    @FXML private ComboBox<Author> tagComboBox;
    @FXML private ListView<Author> authorListView;
    @FXML private ListView<Author> tagListView;
    @FXML private Button saveButton;
    @FXML private Button fetchBookButton;
    @FXML private Label statusLabel;
    
    private AuthorDAO authorDAO;
    private ObservableList<Author> authorList;

    public AddDocumentController() {
        authorDAO = new AuthorDAO();
        authorList = FXCollections.observableArrayList();
    }

    // This method will be called after the FXML is loaded
    @FXML
    public void initialize() {
        authorListView.setItems(authorList);

        // Initialize ComboBox for author search (auto-complete)
        initializeAuthorComboBox();

        // Initialize the save button action
        saveButton.setOnAction(event -> saveNewDocument());

        fetchBookButton.setOnAction(event -> fetchBookButtonAction());
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
                if (authors.isEmpty()) return;
                
                authorComboBox.getItems().setAll(authors);
                authorComboBox.show();
            } else {
                authorComboBox.hide();
            }
        });


        // Listen for when the user presses Enter in the ComboBox
        authorComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                String enteredAuthorName = authorComboBox.getEditor().getText().trim();
                if (enteredAuthorName.isEmpty()) return;
                List<Author> authors = authorDAO.searchAuthorsByName(enteredAuthorName);
                if (authors.isEmpty()) return;
                Author author = authors.get(0);
                authorComboBox.getEditor().setText(author.getName());
            } else if (event.getCode() == KeyCode.ENTER) {
                String enteredAuthorName = authorComboBox.getEditor().getText().trim();
                if (enteredAuthorName.isEmpty()) return;

                Integer authorId = authorDAO.addAuthor(new Author(-1, enteredAuthorName, ""));
                Author author = authorDAO.getAuthorById(authorId);
                if (authorList.contains(author)) return;

                // Add the author to the ListView and clear the ComboBox
                authorList.add(author);
                authorComboBox.getEditor().clear();

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
        isbnTextField.clear();
        publisherTextField.clear();
        publishedDatePicker.setValue(null);
        authorList.clear();
    }

    // Method to save the new book to the database
    private void saveNewDocument() {

        String title = titleTextField.getText();
        String isbn = isbnTextField.getText();
        String publisherName = publisherTextField.getText();
        LocalDate publishedDate = publishedDatePicker.getValue();

        // Validate inputs
        if (title.isEmpty()) {
            showAlert("Error", "At least title are required!", Alert.AlertType.ERROR);
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

        // TODO: tag


        List<Integer> authorIds = authorList.stream().map(Author::getId).collect(Collectors.toList());

        List<Integer> tagIds = new ArrayList<>();  // TODO: tag

        Document document = new Document(title, authorIds, tagIds, publisher.getId(), isbn, publishedDate, LocalDate.now(), 0, 1);

        // Save document (you need to implement saving logic)
        DocumentDAO documentDAO = new DocumentDAO();
        int documentId = documentDAO.addDocument(document);

        if (documentId > -1) {
            showAlert("Success", "Document has been added successfully!", Alert.AlertType.INFORMATION);
            clearForm();  // Clear the form after successful save
        } else {
            showAlert("Error", "There was an issue adding the book.", Alert.AlertType.ERROR);
        }
    }

    private void fetchBookButtonAction() {
        String isbn = isbnTextField.getText(); // Get ISBN from the input field
        if (isbn.isEmpty()) {
            return; // If no ISBN is entered, do nothing
        }

        // Call the method to fetch book details
        BookDetails bookDetails = GoogleBooksAPI.fetchBookByIsbn(isbn);

        // Check if the API returned valid book details
        if (bookDetails != null) {
            // Check if title is not null before setting it
            if (bookDetails.getTitle() != null) {
                titleTextField.setText(bookDetails.getTitle());
            }

            // Check if authors are not null before setting it
            if (bookDetails.getAuthors() != null && bookDetails.getAuthors().length > 0) {
                // authorsTextField.setText(String.join(", ", bookDetails.getAuthors()));
                authorList.clear();
                for (String authorName : bookDetails.getAuthors()) {
                    List<Author> authors = authorDAO.searchAuthorsByName(authorName);
                    Author author;
                    if (authors.isEmpty()) {
                        Integer authorId = authorDAO.addAuthor(new Author(-1, authorName, ""));
                        author = authorDAO.getAuthorById(authorId);
                    } else {
                        author = authors.get(0);
                    }

                    // Add the author to the ListView and clear the ComboBox
                    authorList.add(author);    
                }
                
            }

            // Check if publisher is not null before setting it
            if (bookDetails.getPublisher() != null) {
                publisherTextField.setText(bookDetails.getPublisher());
            } else {
                publisherTextField.setText("No publisher available");
            }

            // Check if categories are not null before setting it
            // if (bookDetails.getCategories() != null && bookDetails.getCategories().length > 0) {
            //     categoriesTextField.setText(String.join(", ", bookDetails.getCategories()));
            // } else {
            //     categoriesTextField.setText("No categories available");
            // }

            // Check if date published is not null before setting it
            String publishedDate = bookDetails.getDatePublished();
            if (publishedDate != null) {
                try {
                    // Attempt to parse the date (you may need to adjust the format based on API response)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(publishedDate, formatter);
                    publishedDatePicker.setValue(date); // Set the parsed date into the DatePicker
                } catch (DateTimeParseException e) {
                    // If parsing fails, log the error and set a default or empty value
                    publishedDatePicker.setValue(null); // Clear the DatePicker
                    System.out.println("Invalid date format: " + publishedDate);
                }
            } else {
                // If no published date is available, clear the DatePicker
                publishedDatePicker.setValue(null);
            }
        } else {
            // If no book details are found, display a message or handle accordingly
            titleTextField.setText("No details found");
            authorList.clear();
            publisherTextField.setText("");
            // categoriesTextField.setText("");
            publishedDatePicker.setValue(null); // Clear the DatePicker
        }
    }

     

    // Method to close the window
    @FXML
    public void closeWindow() {
        clearForm();
        saveButton.getScene().getWindow().hide();
    }
}
