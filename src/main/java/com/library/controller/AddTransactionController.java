package com.library.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.library.api.GoogleBooksAPI;
import com.library.api.GoogleBooksAPI.BookDetails;
import com.library.model.Author;
import com.library.model.Category;
import com.library.model.Publisher;
import com.library.model.Transaction;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.PublisherDAO;
import com.library.services.TransactionDAO;

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

public class AddTransactionController {

    @FXML private TextField titleTextField;
    @FXML private TextField isbnTextField;
    @FXML private TextField publisherTextField;
    @FXML private DatePicker publishedDatePicker;

    @FXML private ComboBox<Author> authorComboBox;
    @FXML private ListView<Author> authorListView;

    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private ListView<Category> categoryListView;

    @FXML private Button saveButton;
    @FXML private Button fetchButton;
    @FXML private Button clearButton;

    @FXML private Label statusLabel;
    
    private ObservableList<Author> authorList;
    private ObservableList<Category> categoryList;

    public AddTransactionController() {
        authorList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
    }

    // This method will be called after the FXML is loaded
    @FXML
    public void initialize() {
        authorListView.setItems(authorList);
        categoryListView.setItems(categoryList);

        // Initialize ComboBox for author search (auto-complete)
        initializeAuthorComboBox();

        initializeCategoryComboBox();

        // Initialize the save button action
        saveButton.setOnAction(event -> saveNewTransaction());

        fetchButton.setOnAction(event -> fetchButtonAction());

        clearButton.setOnAction(event -> clearForm());
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
                List<Author> authors = AuthorDAO.getInstance().searchAuthorsByName(newValue);
                if (authors.isEmpty()) return;
                
                authorComboBox.getItems().setAll(authors);
                authorComboBox.show();
            } else {
                authorComboBox.hide();
            }
        });


        // Listen for when the user presses Enter in the ComboBox
        authorComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String enteredAuthorName = authorComboBox.getEditor().getText().trim();
                if (enteredAuthorName.isEmpty()) return;

                Author author = new Author(-1, enteredAuthorName, "");
                if (authorList.contains(author)) return;

                // Add the author to the ListView and clear the ComboBox
                authorList.add(author);
                authorComboBox.getEditor().clear();
                authorComboBox.hide();

            }
        });
    }

    private void initializeCategoryComboBox() {
        // Set the string converter for the ComboBox to show the category's name
        categoryComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                // Create a new category if necessary, or return null to rely on database suggestions
                return new Category(-1, string);
            }
        });

        // Add a listener to update suggestions as the user types
        categoryComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                List<Category> categories = CategoryDAO.getInstance().getCategoriesByName(newValue);
                if (categories.isEmpty()) return;
                
                categoryComboBox.getItems().setAll(categories);
                categoryComboBox.show();
            } else {
                categoryComboBox.hide();
            }
        });


        // Listen for when the user presses Enter in the ComboBox
        categoryComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String enteredCategoryName = categoryComboBox.getEditor().getText().trim();
                if (enteredCategoryName.isEmpty()) return;

                Category category = new Category(-1, enteredCategoryName);
                if (categoryList.contains(category)) return;

                // Add the category to the ListView and clear the ComboBox
                categoryList.add(category);
                categoryComboBox.getEditor().clear();

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
        categoryList.clear();
    }

    // Method to save the new book to the database
    private void saveNewTransaction() {

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
        PublisherDAO publisherDAO = PublisherDAO.getInstance();
        Publisher publisher = publisherDAO.getPublisherByName(publisherName);
        if (publisher == null) {
            publisher = new Publisher(0, publisherName);  // Create new publisher if not found
            int publisherId = publisherDAO.add(publisher);
            publisher.setId(publisherId);
        }


        List<Integer> authorIds = new ArrayList<>();
        for (Author author : authorList) {
            int authorId = AuthorDAO.getInstance().add(author);
            authorIds.add(authorId);
        }

        List<Integer> categoryIds = new ArrayList<>();
        for (Category category : categoryList) {
            int categoryId = CategoryDAO.getInstance().add(category);
            categoryIds.add(categoryId);
        }

        Transaction transaction = new Transaction(0, 0, LocalDate.now());


        // Save transaction (you need to implement saving logic)
        int transactionId = TransactionDAO.getInstance().add(transaction);

        if (transactionId > -1) {
            showAlert("Success", "Transaction has been added successfully!", Alert.AlertType.INFORMATION);
            clearForm();  // Clear the form after successful save
        } else {
            showAlert("Error", "There was an issue adding the book.", Alert.AlertType.ERROR);
        }
    }

    private void fetchButtonAction() {
        String isbn = isbnTextField.getText(); // Get ISBN from the input field
        if (!isbn.isEmpty()) {
            fillBookDetails(GoogleBooksAPI.fetchBookDetails(isbn, "isbn"));
            return; 
        }

        String title = titleTextField.getText();
        if (!title.isEmpty()) {
            fillBookDetails(GoogleBooksAPI.fetchBookDetails(title, "title"));
            return;
        }
    }

    private void fillBookDetails(BookDetails bookDetails) {
        if (bookDetails == null) {
            titleTextField.setText("No details found");
            authorList.clear();
            publisherTextField.setText("");
            publishedDatePicker.setValue(null);
            return;
        }

        titleTextField.setText(bookDetails.getTitle() != null ? bookDetails.getTitle() : "");

        authorList.clear();
        if (bookDetails.getAuthors() != null) {
            for (String authorName : bookDetails.getAuthors()) {
                AuthorDAO authorDAO = AuthorDAO.getInstance();
                List<Author> authors = authorDAO.searchAuthorsByName(authorName);
                Author author = authors.isEmpty() ? authorDAO.getAuthorById(authorDAO.add(new Author(-1, authorName, ""))) : authors.get(0);
                authorList.add(author);
            }
        }

        publisherTextField.setText(bookDetails.getPublisher() != null ? bookDetails.getPublisher() : "No publisher available");

        categoryList.clear();
        if (bookDetails.getCategories() != null) {
            for (String categoryName : bookDetails.getCategories()) {
                CategoryDAO categoryDAO = CategoryDAO.getInstance();
                Category category = categoryDAO.getCategoryByName(categoryName);
                if (category == null) {
                    category = categoryDAO.getCategoryById(categoryDAO.add(new Category(-1, categoryName)));
                }
                categoryList.add(category);
            }
        }

        isbnTextField.setText(bookDetails.getIsbn() != null ? bookDetails.getIsbn() : "");

        String publishedDate = bookDetails.getDatePublished();
        if (publishedDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(publishedDate, formatter);
                publishedDatePicker.setValue(date);
            } catch (DateTimeParseException e) {
                publishedDatePicker.setValue(null);
                System.out.println("Invalid date format: " + publishedDate);
            }
        } else {
            publishedDatePicker.setValue(null);
        }
    }

     

    // Method to close the window
    @FXML
    public void closeWindow() {
        clearForm();
        saveButton.getScene().getWindow().hide();
    }
}
