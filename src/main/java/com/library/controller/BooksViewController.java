package com.library.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.dao.AuthorDAO;
import com.library.dao.DocumentDAO;
import com.library.dao.PublisherDAO;
import com.library.dao.TagDAO;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.model.Tag;
import com.library.model.document.Author;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BooksViewController {

    @FXML private AnchorPane contentPane;

    private Label totalBooksLabel;
    TableView<Document> booksTable;

    private TableView<Document> getDocumentTableView() {
        // Get book data and populate TableView
        DocumentDAO documentDAO = new DocumentDAO();
        List<Document> documents = documentDAO.getAllDocuments();
        ObservableList<Document> bookData = FXCollections.observableList(documents);

        TableView<Document> booksTable = new TableView<>(bookData);

        // Document Name Column
        TableColumn<Document, String> nameColumn = new TableColumn<>("Document Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Authors Column
        AuthorDAO authorDAO = new AuthorDAO();
        TableColumn<Document, String> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(cellData -> {
            // Join the author names into a single string
            List<Integer> authorIds = cellData.getValue().getAuthorIds();
            List<Author> authors = authorIds != null ? authorDAO.getAuthorsByIds(authorIds) : null;
            return new SimpleStringProperty(authors.stream()
                .map(Author::getName)
                .collect(Collectors.joining(", ")));
        });

        // Tags Column
        TagDAO tagDAO = new TagDAO();
        TableColumn<Document, String> tagsColumn = new TableColumn<>("Tags");
        tagsColumn.setCellValueFactory(cellData -> {
            // Join the tag names into a single string
            List<Integer> tagIds = cellData.getValue().getTagIds();
            List<Tag> tags = tagIds != null ? tagDAO.getTagsByIds(tagIds) : null;
            return new SimpleStringProperty(tags.stream().map(Tag::getName).collect(Collectors.joining(", ")));
        });

        // Publisher Column
        PublisherDAO publisherDAO = new PublisherDAO();
        TableColumn<Document, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cellData -> {
            // Get the publisher name from the document
            int publisherId = cellData.getValue().getPublisherId();
            Publisher publisher = publisherId != 0 ? publisherDAO.getPublisherById(publisherId) : null;
            return publisher != null ? new SimpleStringProperty(publisher.getName()) : new SimpleStringProperty("N/A");
        });

        // ISBN 13 Column
        TableColumn<Document, String> isbnColumn = new TableColumn<>("ISBN 13");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn13()));

        // ISBN 10 Column
        TableColumn<Document, String> isbn10Column = new TableColumn<>("ISBN 10");
        isbn10Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn10()));

        // Published Date Column
        TableColumn<Document, String> publishedDateColumn = new TableColumn<>("Published Date");
        publishedDateColumn.setCellValueFactory(cellData -> {
            LocalDate publishedDate = cellData.getValue().getDatePublished();
            return new SimpleStringProperty(publishedDate != null ? publishedDate.toString() : "N/A");
        });

        // Quantity Column
        TableColumn<Document, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> {
            int quantity = cellData.getValue().getQuantityCurrent();
            return new SimpleStringProperty(String.valueOf(quantity) + "/" + String.valueOf(cellData.getValue().getQuantityTotal()));
        });

        // Add all columns to the TableView
        booksTable.getColumns().addAll(nameColumn, authorsColumn, tagsColumn, publisherColumn, isbnColumn, isbn10Column, publishedDateColumn, quantityColumn);

        booksTable.setPrefWidth(1000);


        return booksTable;
        // Add the TableView to the main content
        // contentPane.getChildren().add(booksTable);
    }

    public void initialize() {
        contentPane.getChildren().clear();

        // Label for displaying total number of books
        booksTable = getDocumentTableView();
        
        totalBooksLabel = new Label("Total Books: 0");
        updateTotalBooks();

         // Buttons for Add New Book and Search Book
        Button addButton = new Button("Add New Book");
        Button searchButton = new Button("Search Book");
 
        // Set button actions
        addButton.setOnAction(event -> handleAddNewBook());
        //  searchButton.setOnAction(event -> handleSearchBook());
        

        // Layout for the buttons and label
        HBox buttonBox = new HBox(10, addButton, searchButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(10));

        // Layout for the main content area
        VBox mainLayout = new VBox(10, booksTable, totalBooksLabel, buttonBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new javafx.geometry.Insets(20));


        contentPane.getChildren().add(mainLayout);

        // contentPane.setTopAnchor(mainLayout, 50.0);    // Offset from the top by 50 pixels
        // contentPane.setLeftAnchor(mainLayout, 30.0);   // Offset from the left by 30 pixels
        // contentPane.setRightAnchor(mainLayout, 10.0);  // Right offset of 10 pixels
        // contentPane.setBottomAnchor(mainLayout, 10.0); // Bottom offset of 10 pixels

        // Set up the scene and stage
        // Scene scene = new Scene(vbox, 600, 500);
        // primaryStage.setScene(scene);
        // primaryStage.show();
        
    }

    // Update the total number of books
    private void updateTotalBooks() {
        DocumentDAO documentDAO = new DocumentDAO();
        totalBooksLabel.setText("Total Books: " + documentDAO.countAllDocument());
    }


    private void handleAddNewBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/AddBookWindow.fxml"));
            Parent root = loader.load();
            Scene addBookScene = new Scene(root);
            Stage addBookStage = new Stage();
            addBookStage.setScene(addBookScene);
            addBookStage.show();

            // Reload books view when Add Book window is closed
            // addBookStage.setOnHidden(event -> loadBooksView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
