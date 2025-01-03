package com.library.controller;

import com.google.gson.JsonObject;
import com.library.api.GoogleBooksAPI;
import com.library.model.Author;
import com.library.model.Category;
import com.library.model.Document;
import com.library.model.Language;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.BaseDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.LanguageDAO;
import com.library.services.PublisherDAO;
import com.library.util.AutoCompletionTextField;
import com.library.util.ImageDownloader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddDocumentController extends BaseController {

    @FXML private TextField titleTextField;
    @FXML private TextField isbnTextField;
    @FXML private TextField publisherTextField;
    @FXML private DatePicker publishedDatePicker;

    @FXML private ListView<Author> authorListView;
    @FXML private TextField authorTextField;

    @FXML private ListView<Category> categoryListView;
    @FXML private TextField categoryTextField;

    @FXML private ImageView documentImageView;
    @FXML private TextField languageTextField, quantityTextField;
    @FXML private TextArea descriptionTextArea;

    @FXML private Button saveButton;
    @FXML private Button fetchTitleButton, fetchISBNButton;
    @FXML private Button clearButton, cancelButton;
    @FXML private Button selectImageButton;

    private ObservableList<Author> authorList;
    private ObservableList<Category> categoryList;
    private String selectedImagePath;

    public AddDocumentController() {
        authorList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        authorListView.setItems(authorList);
        categoryListView.setItems(categoryList);

        // Initialize auto-completion for fields
        initializeAutoCompletionTextField(authorTextField, AuthorDAO.getInstance().getAllAuthors().stream()
                .map(Author::getName).collect(Collectors.toList()));
        initializeAutoCompletionTextField(categoryTextField, CategoryDAO.getInstance().getAllCategories().stream()
                .map(Category::getName).collect(Collectors.toList()));
        initializeAutoCompletionTextField(languageTextField, LanguageDAO.getInstance().getAllLanguages().stream()
                .map(Language::getName).collect(Collectors.toList()));

        // Set up event listeners for adding authors and categories
        setUpAuthorCategoryListeners();

        // Initialize button actions
        setUpButtonActions();
    }

    private void initializeAutoCompletionTextField(TextField textField, List<String> entries) {
        new AutoCompletionTextField(textField, entries);
    }

    private void setUpAuthorCategoryListeners() {
        authorTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addItemToList(authorTextField.getText(), authorList, AuthorDAO.getInstance(), Author::new);
                authorTextField.clear();
            }
        });

        categoryTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addItemToList(categoryTextField.getText(), categoryList, CategoryDAO.getInstance(), Category::new);
                categoryTextField.clear();
            }
        });
    }

    private <T> void addItemToList(String name, ObservableList<T> list, Object dao, EntityCreator<T> creator) {
        if (name.isEmpty()) return;
        T item = creator.createEntity(-1, name); // Create new entity
        if (!list.contains(item)) {
            list.add(item);
        }
    }

    private void setUpButtonActions() {
        saveButton.setOnAction(event -> saveNewDocument());
        fetchTitleButton.setOnAction(event -> fetchButtonAction("title"));
        fetchISBNButton.setOnAction(event -> fetchButtonAction("isbn"));
        clearButton.setOnAction(event -> clearForm());
        selectImageButton.setOnAction(event -> handleSelectImage());
        cancelButton.setOnAction(event -> mainController.reloadCurrentTab());
    }

    private void clearForm() {
        titleTextField.clear();
        isbnTextField.clear();
        publisherTextField.clear();
        publishedDatePicker.setValue(null);
        authorList.clear();
        categoryList.clear();
        languageTextField.clear();
        descriptionTextArea.clear();
        documentImageView.setImage(null);
    }

    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedImagePath = selectedFile.toURI().toString();
            documentImageView.setImage(new Image(selectedImagePath));
        }
    }

    private void saveNewDocument() {
        String title = titleTextField.getText();
        String isbn = isbnTextField.getText();
        String publisherName = publisherTextField.getText();
        LocalDate publishedDate = publishedDatePicker.getValue();
        int quantity = Integer.parseInt(quantityTextField.getText());

        // Validate inputs
        if (title.isEmpty()) {
            showAlert("Error", "At least title is required!", Alert.AlertType.ERROR);
            return;
        }

        Publisher publisher = getOrAddPublisher(publisherName);

        List<Integer> authorIds = addEntitiesToList(authorList, AuthorDAO.getInstance());
        List<Integer> categoryIds = addEntitiesToList(categoryList, CategoryDAO.getInstance());

        String languageName = languageTextField.getText();
        Language language = getOrAddLanguage(languageName);

        Document document = new Document.Builder(title)
                .authorIds(authorIds)
                .categoryIds(categoryIds)
                .publisherId(publisher.getId())
                .isbn(isbn)
                .publicationDate(publishedDate)
                .dateAddedToLibrary(LocalDate.now())
                .currentQuantity(quantity)
                .totalQuantity(quantity)
                .languageId(language.getId())
                .description(descriptionTextArea.getText())
                .imageUrl(selectedImagePath)
                .build();

        // Save document to database
        int documentId = DocumentDAO.getInstance().add(document);

        if (documentId > -1) {
            showAlert("Success", "Document has been added successfully!", Alert.AlertType.INFORMATION);
            clearForm();
        } else {
            showAlert("Error", "There was an issue adding the book.", Alert.AlertType.ERROR);
        }
    }

    private Publisher getOrAddPublisher(String publisherName) {
        Publisher publisher = PublisherDAO.getInstance().getPublisherByName(publisherName);
        if (publisher == null) {
            publisher = new Publisher(0, publisherName);
            publisher.setId(PublisherDAO.getInstance().add(publisher));
        }
        return publisher;
    }

    private Language getOrAddLanguage(String languageName) {
        Language language = LanguageDAO.getInstance().getLanguageByName(languageName);
        if (language == null) {
            language = new Language(-1, languageName);
            language.setId(LanguageDAO.getInstance().add(language));
        }
        return language;
    }

    private <T> List<Integer> addEntitiesToList(List<T> list, BaseDAO<T> dao) {
        List<Integer> ids = new ArrayList<>();
        for (T item : list) {
            int id = dao.add(item);
            ids.add(id);
        }
        return ids;
    }

    private void fetchButtonAction(String type) {
        String isbn = isbnTextField.getText();
        String title = titleTextField.getText();

        if (type.equals("isbn") && !isbn.isEmpty()) {
            fillDocument(GoogleBooksAPI.fetchDocumentJson(isbn, type));
        } else if (type.equals("title") && !title.isEmpty()) {
            fillDocument(GoogleBooksAPI.fetchDocumentJson(title, type));
        }
    }

    private void fillDocument(JsonObject bookDetails) {
        if (bookDetails == null) {
            clearForm();
            showAlert("Error", "No books found", Alert.AlertType.ERROR);
            return;
        }

        // Set title
        titleTextField.setText(bookDetails.has("title") ? bookDetails.get("title").getAsString() : "Unknown");

        // Set authors
        authorList.clear();
        if (bookDetails.has("authors")) {
            for (var author : bookDetails.getAsJsonArray("authors")) {
                authorList.add(new Author(author.getAsString()));
            }
        }

        // Set publisher
        publisherTextField.setText(
                bookDetails.has("publisher") ? bookDetails.get("publisher").getAsString() : "No publisher available"
        );

        // Set categories
        categoryList.clear();
        if (bookDetails.has("categories")) {
            for (var category : bookDetails.getAsJsonArray("categories")) {
                categoryList.add(new Category(category.getAsString()));
            }
        }

        // Set ISBN
        isbnTextField.setText(bookDetails.has("industryIdentifiers")
                ? bookDetails.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString()
                : "No ISBN available");

        // Set language
        languageTextField.setText(bookDetails.has("language") ? bookDetails.get("language").getAsString() : "No language available");

        // Set description
        descriptionTextArea.setText(
                bookDetails.has("description") ? bookDetails.get("description").getAsString() : "No description available"
        );

        // Set published date
        try {
            String date = bookDetails.has("publishedDate") ? bookDetails.get("publishedDate").getAsString() : null;
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                publishedDatePicker.setValue(LocalDate.parse(date, formatter));
            } else {
                publishedDatePicker.setValue(null);
            }
        } catch (Exception e) {
            publishedDatePicker.setValue(null);
        }

        // Set image
        if (bookDetails.has("imageLinks")) {
            String imageUrl = bookDetails.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
            selectedImagePath = ImageDownloader.setImageFromCache(imageUrl, documentImageView, titleTextField.getText());
        }
    }
}
