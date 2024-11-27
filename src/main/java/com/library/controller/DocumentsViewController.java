package com.library.controller;

import java.io.File;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocumentsViewController {

    @FXML private TableView<Document> documentTable;
    @FXML private Label totalDocumentsLabel;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private TextField searchTextField;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private DocumentDAO documentDAO;
    private ObservableList<Document> documents;

    public void initialize() {
        documentDAO = DocumentDAO.getInstance();

        initializeDocumentTable();
        updateTotalDocuments();

        addButton.setOnAction(event -> handleAddNewDocument());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                documents = FXCollections.observableList(DocumentDAO.getInstance().getDocumentsByKeyword(newValue));
                setDocumentData(documents);
            } else {
                documents = FXCollections.observableList(documentDAO.getAllDocuments());
                setDocumentData(documents);
            }
        });
        filterButton.setOnAction(event -> handleFilterDocuments());
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

    private void handleFilterDocuments() {
        
    }

    private void initializeDocumentTable() {
        documentTable.setEditable(true);

        var selectAll = new CheckBox();
        var selectColumn = createSelectColumn(selectAll);
        var nameColumn = createNameColumn();
        var authorsColumn = createAuthorsColumn();
        var categoriesColumn = createCategoriesColumn();
        var publisherColumn = createPublisherColumn();
        var isbnColumn = createIsbnColumn();
        var publishedDateColumn = createPublishedDateColumn();
        var quantityColumn = createQuantityColumn();
        var dateAddedColumn = createDateAddedColumn();
        var actionColumn = createActionColumn();

        documentTable.getColumns().setAll(selectColumn, nameColumn, authorsColumn, categoriesColumn, publisherColumn, isbnColumn, publishedDateColumn, quantityColumn, dateAddedColumn, actionColumn);
        documentTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        selectAll.setOnAction(event -> {
            documentTable.getItems().forEach(
                item -> item.isSelectedProperty().set(selectAll.isSelected())
            );
        });

        documentTable.setOnMouseClicked(event -> {
            Document selectedDocument = documentTable.getSelectionModel().getSelectedItem();
            if (selectedDocument != null) {
                // Open an edit dialog
                // openEditDialog(selectedDocument);
            }
        });

        documents = FXCollections.observableList(documentDAO.getAllDocuments());
        setDocumentData(documents);
    }

    private TableColumn<Document, Boolean> createSelectColumn(CheckBox selectAll) {    
        TableColumn<Document, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAll);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        return selectColumn;
    }

    private TableColumn<Document, String> createNameColumn() {
        TableColumn<Document, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        return nameColumn;
    }

    private TableColumn<Document, String> createAuthorsColumn() {
        AuthorDAO authorDAO = AuthorDAO.getInstance();
        TableColumn<Document, String> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(cellData -> {
            List<Integer> authorIds = cellData.getValue().getAuthorIds();
            List<Author> authors = authorIds != null ? authorDAO.getAuthorsByIds(authorIds) : null;
            return new SimpleStringProperty(authors != null ? authors.stream()
                .map(Author::getName)
                .collect(Collectors.joining(", ")) : "N/A");
        });
        return authorsColumn;
    }

    private TableColumn<Document, String> createCategoriesColumn() {
        CategoryDAO categoryDAO = CategoryDAO.getInstance();
        TableColumn<Document, String> categoriesColumn = new TableColumn<>("Categories");
        categoriesColumn.setCellValueFactory(cellData -> {
            List<Integer> categoryIds = cellData.getValue().getCategoryIds();
            List<Category> categories = categoryIds != null ? categoryDAO.getCategoriesByIds(categoryIds) : null;
            return new SimpleStringProperty(categories != null ? categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", ")) : "N/A");
        });
        return categoriesColumn;
    }

    private TableColumn<Document, String> createPublisherColumn() {
        PublisherDAO publisherDAO = PublisherDAO.getInstance();
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

    private TableColumn<Document, Void> createActionColumn() {
        TableColumn<Document, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Document document = getTableView().getItems().get(getIndex());
                    // Open edit dialog
                    // openEditDialog(document);
                });

                deleteButton.setOnAction(event -> {
                    Document document = getTableView().getItems().get(getIndex());
                    documentDAO.deleteDocument(document.getDocumentId());
                    documents.setAll(documentDAO.getAllDocuments());
                    updateTotalDocuments();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
        return actionColumn;
    }

    public void setDocumentData(ObservableList<Document> documents) {
        documentTable.setItems(documents);
    }
}
