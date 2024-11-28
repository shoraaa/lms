package com.library.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.Doc;

import com.library.api.GoogleBooksAPI;
import com.library.api.GoogleBooksAPI.BookDetails;
import com.library.model.Document;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.util.ISBNGenerator;
import com.library.util.WindowUtil;
import com.library.view.DocumentTableView;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                loadDocumentsByKeyword(newValue);
            } else {
                loadAllDocuments();
            }
        });
        filterButton.setOnAction(event -> handleFilterDocuments());
        importFromCSVButton.setOnAction(event -> documentTableView.importToCSV());
        exportToCSVButton.setOnAction(event -> documentTableView.exportToCSV());

        // add random isbn
        // List<String> isbns = ISBNGenerator.generateISBNs(100);
        // for (String isbn : isbns) {
        //     BookDetails bookDetails = GoogleBooksAPI.fetchBookDetails(isbn, "isbn");
        //     Document document = new Document.Builder(bookDetails.getTitle())
        //                                     .authorIds(AuthorDAO.getInstance().getbookDetails.getAuthors())
        //                                     .build();

        //     DocumentDAO.getInstance().add(document);
        // }
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
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainLayout.getScene().getWindow());
        dialog.getDialogPane().setContent(WindowUtil.loadFXML("/com/library/views/AddDocumentWindow.fxml"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
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
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                documentTableView.loadAllDocuments();
                return null;
            }
        };

        executorService.submit(task);
    }

    private void loadDocumentsByKeyword(String keyword) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                documentTableView.loadDocumentsByKeyword(keyword);
                return null;
            }
        };

        executorService.submit(task);
    }
}
