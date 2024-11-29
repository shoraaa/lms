package com.library.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.library.App;
import com.library.model.Document;
import com.library.services.DocumentDAO;
import com.library.view.DocumentTableView;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DocumentsViewController {

    @FXML private TableView<Document> documentTable;
    @FXML private Label totalDocumentsLabel;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private TextField searchTextField;
    @FXML private Button deleteButton;
    @FXML private Button importFromCSVButton;
    @FXML private Button exportToCSVButton;
    @FXML private VBox mainLayout;

    private DocumentTableView documentTableView;
    private final PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void initialize() {
        documentTableView = new DocumentTableView(documentTable);

        // Load initial data
        loadAllDocuments();
        updateTotalDocuments();

        // Configure UI button actions
        configureButtonActions();

        // Add search field listener with debounce
        configureSearchField();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewDocument());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        filterButton.setOnAction(event -> handleFilterDocuments());
        importFromCSVButton.setOnAction(event -> documentTableView.importToCSV());
        exportToCSVButton.setOnAction(event -> documentTableView.exportToCSV());
    }

    private void configureSearchField() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if (newValue != null && !newValue.isEmpty()) {
                    loadDocuments(() -> DocumentDAO.getInstance().getDocumentsByTitle(newValue));
                } else if (oldValue != null && !oldValue.isEmpty()) {
                    loadAllDocuments();
                }
            });
            pause.playFromStart();
        });
    }

    private void updateTotalDocuments() {
        executeBackgroundTask(() -> DocumentDAO.getInstance().countAllDocuments(), 
            count -> totalDocumentsLabel.setText("Total Documents: " + count));
    }

    private void handleAddNewDocument() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainLayout.getScene().getWindow());
        dialog.setTitle("Add New Document");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/library/views/AddDocumentWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            App.showErrorDialog(e);
            return;
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait();

        // Refresh data
        loadAllDocuments();
        updateTotalDocuments();
    }

    private void handleDeleteSelected() {
        executeBackgroundTask(() -> {
            documentTableView.deleteSelectedDocuments();
            return null;
        }, result -> updateTotalDocuments());
    }

    private void handleFilterDocuments() {
        // Placeholder: Implement filter logic here
    }

    private void loadAllDocuments() {
        loadDocuments(() -> DocumentDAO.getInstance().getAllDocuments());
    }

    private void loadDocuments(LoaderFunction<List<Document>> loaderFunction) {
        executeBackgroundTask(loaderFunction, documents ->
            documentTableView.setDocumentData(FXCollections.observableArrayList(documents))
        );
    }

    private <T> void executeBackgroundTask(LoaderFunction<T> loaderFunction, TaskSuccessHandler<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return loaderFunction.load();
            }
        };

        task.setOnSucceeded(event -> onSuccess.handle(task.getValue()));
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            System.err.println("Task failed: " + exception.getMessage());
            exception.printStackTrace();
        });

        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }

    @FunctionalInterface
    private interface LoaderFunction<T> {
        T load() throws Exception;
    }

    @FunctionalInterface
    private interface TaskSuccessHandler<T> {
        void handle(T result);
    }
}
