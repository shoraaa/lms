package com.library.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Author;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.model.Tag;
import com.library.services.AuthorDAO;
import com.library.services.PublisherDAO;
import com.library.services.TagDAO;
import com.library.util.WindowUtil;

public class DocumentsView {
    private final TableView<Document> documentTable;
    private final Label totalDocumentsLabel;
    private final Button addButton;
    private final Button searchButton;
    private final Button deleteButton;
    private VBox mainLayout;

    public DocumentsView() {
        this.documentTable = new TableView<>();
        this.totalDocumentsLabel = new Label("Total Documents: 0");
        this.addButton = new Button("Add New Book");
        this.searchButton = new Button("Search Book");
        this.deleteButton = new Button("Delete Selected");
        
        initializeUI();
    }

    private void initializeUI() {
        documentTable.setEditable(true);

        // Document table
        initializeDocumentTable();

        // Layout for buttons
        HBox buttonBox = new HBox(10, addButton, searchButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(10));

        // Main layout
        mainLayout = new VBox(10, documentTable, totalDocumentsLabel, buttonBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new javafx.geometry.Insets(20));
    }

    private void initializeDocumentTable() {
        // Define columns
        TableColumn<Document, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<Document, String> nameColumn = new TableColumn<>("Name");
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

        // ISBN  Column
        TableColumn<Document, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));

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
        documentTable.getColumns().addAll(selectColumn, nameColumn, authorsColumn, tagsColumn, publisherColumn, isbnColumn, publishedDateColumn, quantityColumn);
        documentTable.setPrefWidth(1000);

        int columnCount = documentTable.getColumns().size();
        for (TableColumn<?, ?> column : documentTable.getColumns()) {
            column.setPrefWidth(1000 / columnCount);
        }

        documentTable.setOnMouseClicked(event -> {
            Document selectedDocument = documentTable.getSelectionModel().getSelectedItem();
            if (selectedDocument != null) {
                // Open an edit dialog
                // openEditDialog(selectedDocument);
            }
        });
        
    }

    // private void openEditDialog(Document document) {
    //     WindowUtil.openNewWindow("/com/library/views/EditDocumentWindow.fxml", "Edit Document");
    //     EditDocumentController controller = loader.getController();
    //     controller.setDocument(document); // Pass the document to the controller
    // }


    // Methods to access UI components for controller
    public VBox getMainLayout() {
        return mainLayout;
    }

    public TableView<Document> getDocumentTable() {
        return documentTable;
    }

    public Label getTotalDocumentsLabel() {
        return totalDocumentsLabel;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    // Method to update table data
    public void setDocumentData(ObservableList<Document> documents) {
        documentTable.setItems(documents);
    }
}
