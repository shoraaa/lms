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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private ExecutorService executorService;

    public void initialize() {
        documentTableView = new DocumentTableView(documentTable);
        executorService = Executors.newCachedThreadPool();
        updateTotalDocuments();

        addButton.setOnAction(event -> handleAddNewDocument());
        deleteButton.setOnAction(event -> handleDeleteSelected());

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if (newValue.length() > 0) {
                    loadDocumentsByKeyword(newValue);
                } else {
                    loadAllDocuments();
                }
            });
            pause.playFromStart();
        });
        
        filterButton.setOnAction(event -> handleFilterDocuments());
        importFromCSVButton.setOnAction(event -> documentTableView.importToCSV());
        exportToCSVButton.setOnAction(event -> documentTableView.exportToCSV());

    }

    private void updateTotalDocuments() {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return DocumentDAO.getInstance().countAllDocuments();
            }
        };

        task.setOnSucceeded(event -> totalDocumentsLabel.setText("Total Documents: " + task.getValue()));
        executorService.submit(task);
    }

    private void handleAddNewDocument() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainLayout.getScene().getWindow());
        dialog.setTitle("Add New Document");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/library/views/AddDocumentWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            App.showErrorDialog(e);
            return;
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait();

        loadAllDocuments();
        updateTotalDocuments();
    }

    private void handleDeleteSelected() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                documentTableView.deleteSelectedDocuments();
                return null;
            }
        };

        task.setOnSucceeded(event -> updateTotalDocuments());
        executorService.submit(task);
    }

    private void handleFilterDocuments() {
        // Implement filter logic here
    }

    private void loadAllDocuments() {
        Task<List<Document>> task = new Task<>() {
            @Override
            protected List<Document> call() {
                return DocumentDAO.getInstance().getAllDocuments();
            }
        };

        task.setOnSucceeded(event -> documentTableView.setDocumentData(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(event -> task.getException().printStackTrace());

        executorService.submit(task);
    }

    private void loadDocumentsByKeyword(String keyword) {
        Task<List<Document>> task = new Task<>() {
            @Override
            protected List<Document> call() {
                return DocumentDAO.getInstance().getDocumentsByKeyword(keyword);
            }
        };

        task.setOnSucceeded(event -> documentTableView.setDocumentData(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(event -> task.getException().printStackTrace());

        executorService.submit(task);
    }
}
