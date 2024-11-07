package com.library.controller;

import java.io.IOException;
import java.util.List;

import com.library.dao.DocumentDAO;
import com.library.model.Document;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BooksViewLoader {

    private final VBox mainContent;
    DocumentDAO documentDAO;

    public BooksViewLoader(VBox mainContent) {
        this.mainContent = mainContent;
        documentDAO = new DocumentDAO();
    }

    // Set button alignment to center
    public void loadBooksView() {
        
        mainContent.getChildren().clear();

        mainContent.setStyle("-fx-padding: 10px; -fx-font-size: 12px; -fx-background-color: #f0f0f0;");

        // Add button to add new book
        Button addNewBookButton = new Button("Add New Book");

        addNewBookButton.setStyle("-fx-padding: 10px; -fx-font-size: 15px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        // Set button alignment to center
        VBox buttonContainer = new VBox(addNewBookButton);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        mainContent.getChildren().add(buttonContainer);

        addNewBookButton.setOnAction(event -> loadAddNewBookView());

        // Get book data and populate TableView
        List<Document> documents = documentDAO.getAllDocuments();
        ObservableList<Document> bookData = FXCollections.observableList(documents);

        TableView<Document> booksTable = new TableView<>(bookData);

        TableColumn<Document, String> nameColumn = new TableColumn<>("Document Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        booksTable.getColumns().add(nameColumn);

        mainContent.getChildren().add(booksTable);
    }

    private void loadAddNewBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/AddBookWindow.fxml"));
            Parent root = loader.load();
            Scene addBookScene = new Scene(root);
            Stage addBookStage = new Stage();
            addBookStage.setScene(addBookScene);
            addBookStage.show();

            // Reload books view when Add Book window is closed
            addBookStage.setOnHidden(event -> loadBooksView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
