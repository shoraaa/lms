package com.library.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.library.App;
import com.library.model.Document;
import com.library.services.DocumentDAO;
import com.library.util.WindowUtil;
import com.library.view.DocumentTableView;

import atlantafx.base.controls.ModalPane;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DocumentsViewController extends BaseViewController {

    @FXML private TableView<Document> documentTable;
    @FXML private Label totalDocumentsLabel;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private TextField searchTextField;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private DocumentTableView documentTableView;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void initialize() {
        documentTableView = new DocumentTableView(documentTable);
        documentTableView.setParentController(this);

        // Load initial data asynchronously
        loadDocumentsAsync();

        // Configure UI button actions
        configureButtonActions();

        // Add search field listener with debounce
        configureSearchField();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewDocument());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        filterButton.setOnAction(event -> handleFilterDocuments());
    }

    private void configureSearchField() {
        // Debounce mechanism for search
        PauseTransition pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(event -> performSearch(searchTextField.getText()));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.playFromStart();  // Restart the pause timer
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadDocumentsAsync();  // Reload full list when the search text is empty
        } else {
            // Execute the search in a background thread
            executorService.submit(() -> {
                List<Document> result = DocumentDAO.getInstance().getDocumentsByTitle(query);
                Platform.runLater(() -> documentTableView.setData(FXCollections.observableList(result)));
            });
        }
    }

    private void loadDocumentsAsync() {
        Task<List<Document>> loadTask = new Task<>() {
            @Override
            protected List<Document> call() {
                return DocumentDAO.getInstance().getAllDocuments();  // Database call in background
            }

            @Override
            protected void succeeded() {
                List<Document> documents = getValue();
                documentTableView.setData(FXCollections.observableList(documents));
                updateTotalDocuments(documents.size());  // Update the total count
            }

            @Override
            protected void failed() {
                App.showErrorDialog(new Exception("Failed to load documents"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    private void updateTotalDocuments(int count) {
        totalDocumentsLabel.setText("Total Documents: " + count);
    }

    private void handleAddNewDocument() {
        // App.openDialog("/com/library/views/AddDocumentWindow.fxml", null, this::loadDocumentsAsync);
        // WindowUtil.showDialog("/com/library/views/AddDocumentWindow.fxml", modalPane, this::loadDocumentsAsync);
        // how to open a dialog window in a modalPane in the mainController
        if (mainController == null) {
            App.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddDocumentWindow.fxml", this::loadDocumentsAsync, null);
    }

    private void handleDeleteSelected() {
        documentTableView.deleteSelectedItems();
        loadDocumentsAsync();  // Reload data after deletion
    }

    private void handleFilterDocuments() {
        // Placeholder: Implement filter logic here
    }
}
