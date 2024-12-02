package com.library.controller;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.Document;
import com.library.model.Language;
import com.library.model.Publisher;
import com.library.model.Transaction;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.LanguageDAO;
import com.library.services.PublisherDAO;
import com.library.util.Localization;
import com.library.view.TransactionTableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ViewDocumentController extends BaseController {

    @FXML private TextField titleTextField;
    @FXML private TextField isbnTextField;
    @FXML private TextField publisherTextField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private ListView<Author> authorListView;
    @FXML private TextField authorTextField;
    @FXML private ListView<Category> categoryListView;
    @FXML private TextField categoryTextField;
    @FXML private ImageView documentImageView;
    @FXML private TextField languageTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button cancelButton;
    @FXML private TableView<Transaction> transactionTable;

    private ObservableList<Author> authorList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private String selectedImagePath;
    private Document document;


    private TransactionTableView transactionTableView;

    public ViewDocumentController(Document document) {
        authorList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
        this.document = document;

    }

    @FXML
    public void initialize() {
        authorListView.setItems(authorList);
        categoryListView.setItems(categoryList);

        // Initialize button actions
        setUpButtonActions();

        // Populate form fields if editing an existing document
        populateFormFields();

        transactionTableView = new TransactionTableView(transactionTable);
        transactionTableView.setDocumentId(document.getDocumentId());
        transactionTableView.removeColumn(Localization.getInstance().getString("document"));
        transactionTableView.removeColumn(Localization.getInstance().getString("dueDate"));
        transactionTableView.removeColumn(Localization.getInstance().getString("actions"));
        transactionTableView.removeColumn("");
        transactionTableView.loadItemsAsync();
    
    }

    private void populateFormFields() {
        if (document == null) return;

        titleTextField.setText(document.getTitle());
        isbnTextField.setText(document.getIsbn());
        Publisher publisher = PublisherDAO.getInstance().getPublisherById(document.getPublisherId());
        publisherTextField.setText(publisher != null ? publisher.getName() : "No publisher available");
        publishedDatePicker.setValue(document.getPublicationDate());
        authorList.setAll(AuthorDAO.getInstance().getAuthorsByIds(document.getAuthorIds()));
        categoryList.setAll(CategoryDAO.getInstance().getCategoriesByIds(document.getCategoryIds()));
        Language language = LanguageDAO.getInstance().getLanguageById(document.getLanguageId());
        languageTextField.setText(language != null ? language.getName() : "No language available");
        descriptionTextArea.setText(document.getDescription());
        selectedImagePath = document.getImageUrl();
        if (selectedImagePath != null) documentImageView.setImage(new Image(selectedImagePath));
    }


    private void setUpButtonActions() {
        cancelButton.setOnAction(event -> mainController.reloadCurrentTab());
    }

    @FXML
    public void closeWindow() {
        mainController.reloadCurrentTab();
    }
}

@FunctionalInterface
interface EntityCreator<T> {
    T createEntity(int id, String name);
}
