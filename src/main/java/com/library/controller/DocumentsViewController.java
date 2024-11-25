package com.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;
import com.library.util.WindowUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

public class DocumentsViewController {

    @FXML private TableView<Document> documentTable;
    @FXML private Label totalDocumentsLabel;
    @FXML private Button addButton;
    @FXML private Button importButton;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private DocumentDAO documentDAO;
    private ObservableList<Document> documents;

    public void initialize() {
        documentDAO = new DocumentDAO();
        documents = FXCollections.observableList(documentDAO.getAllDocuments());

        initializeDocumentTable();
        setDocumentData(documents);
        updateTotalDocuments();

        addButton.setOnAction(event -> handleAddNewDocument());
        deleteButton.setOnAction(event -> handleDeleteSelected());
    }

    private void updateTotalDocuments() {
        int totalDocuments = documentDAO.countAllDocuments();
        totalDocumentsLabel.setText("Total Documents: " + totalDocuments);
    }

    private void handleAddNewDocument() {
        WindowUtil.openNewWindow("/com/library/views/AddDocumentWindow.fxml", "Add New Document");
    }

    private void handleDeleteSelected() {
        List<Integer> selectedDocumentIds = documentTable.getItems().stream()
            .filter(Document::isSelected)
            .map(Document::getDocumentId)
            .collect(Collectors.toList());

        if (!selectedDocumentIds.isEmpty()) {
            documentDAO.deleteDocuments(selectedDocumentIds);
            documents.setAll(documentDAO.getAllDocuments());
            updateTotalDocuments();
        }
    }

    private void initializeDocumentTable() {
        documentTable.setEditable(true);

        TableColumn<Document, Boolean> selectColumn = createSelectColumn();
        TableColumn<Document, String> nameColumn = createNameColumn();
        TableColumn<Document, String> authorsColumn = createAuthorsColumn();
        TableColumn<Document, String> categoriesColumn = createCategoriesColumn();
        TableColumn<Document, String> publisherColumn = createPublisherColumn();
        TableColumn<Document, String> isbnColumn = createIsbnColumn();
        TableColumn<Document, String> publishedDateColumn = createPublishedDateColumn();
        TableColumn<Document, String> quantityColumn = createQuantityColumn();
        TableColumn<Document, String> dateAddedColumn = createDateAddedColumn();

        documentTable.getColumns().addAll(selectColumn, nameColumn, authorsColumn, categoriesColumn, publisherColumn, isbnColumn, publishedDateColumn, quantityColumn, dateAddedColumn);
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

    private TableColumn<Document, Boolean> createSelectColumn() {
        TableColumn<Document, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        return selectColumn;
    }

    private TableColumn<Document, String> createNameColumn() {
        TableColumn<Document, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        return nameColumn;
    }

    private TableColumn<Document, String> createAuthorsColumn() {
        AuthorDAO authorDAO = new AuthorDAO();
        TableColumn<Document, String> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(cellData -> {
            List<Integer> authorIds = cellData.getValue().getAuthorIds();
            List<Author> authors = authorIds != null ? authorDAO.getAuthorsByIds(authorIds) : null;
            return new SimpleStringProperty(authors.stream()
                .map(Author::getName)
                .collect(Collectors.joining(", ")));
        });
        return authorsColumn;
    }

    private TableColumn<Document, String> createCategoriesColumn() {
        CategoryDAO categoryDAO = new CategoryDAO();
        TableColumn<Document, String> categoriesColumn = new TableColumn<>("Categories");
        categoriesColumn.setCellValueFactory(cellData -> {
            List<Integer> categoryIds = cellData.getValue().getCategoryIds();
            List<Category> categories = categoryIds != null ? categoryDAO.getCategoriesByIds(categoryIds) : null;
            return new SimpleStringProperty(categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", ")));
        });
        return categoriesColumn;
    }

    private TableColumn<Document, String> createPublisherColumn() {
        PublisherDAO publisherDAO = new PublisherDAO();
        TableColumn<Document, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cellData -> {
            int publisherId = cellData.getValue().getPublisherId();
            Publisher publisher = publisherId != 0 ? publisherDAO.getPublisherById(publisherId) : null;
            return publisher != null ? new SimpleStringProperty(publisher.getName()) : new SimpleStringProperty("N/A");
        });
        return publisherColumn;
    }

    private TableColumn<Document, String> createIsbnColumn() {
        TableColumn<Document, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        return isbnColumn;
    }

    private TableColumn<Document, String> createPublishedDateColumn() {
        TableColumn<Document, String> publishedDateColumn = new TableColumn<>("Published Date");
        publishedDateColumn.setCellValueFactory(cellData -> {
            LocalDate publishedDate = cellData.getValue().getPublicationDate();
            return new SimpleStringProperty(publishedDate != null ? publishedDate.toString() : "N/A");
        });
        return publishedDateColumn;
    }

    private TableColumn<Document, String> createQuantityColumn() {
        TableColumn<Document, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> {
            int quantity = cellData.getValue().getCurrentQuantity();
            return new SimpleStringProperty(String.valueOf(quantity) + "/" + String.valueOf(cellData.getValue().getTotalQuantity()));
        });
        return quantityColumn;
    }

    private TableColumn<Document, String> createDateAddedColumn() {
        TableColumn<Document, String> dateAddedColumn = new TableColumn<>("Date Added");
        dateAddedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateAddedToLibrary().toString()));
        return dateAddedColumn;
    }

    public void setDocumentData(ObservableList<Document> documents) {
        documentTable.setItems(documents);
    }
}
