// DocumentTableView.java
package com.library.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.LanguageDAO;
import com.library.services.PublisherDAO;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocumentTableView {

    private TableView<Document> documentTable;
    private DocumentDAO documentDAO;
    private ObservableList<Document> documents;

    public DocumentTableView(TableView<Document> documentTable) {
        this.documentTable = documentTable;
        this.documentDAO = DocumentDAO.getInstance();
        this.documents = FXCollections.observableArrayList();
        initializeDocumentTable();
        setDocumentData(documents);
    }

    private void initializeDocumentTable() {
        documentTable.setEditable(true);

        var selectAll = new CheckBox();
        var selectColumn = createSelectColumn(selectAll);

        var imageColumn = createImageColumn();
        var nameColumn = createNameColumn();
        var authorsColumn = createAuthorsColumn();
        var categoriesColumn = createCategoriesColumn();
        var publisherColumn = createPublisherColumn();
        var isbnColumn = createIsbnColumn();
        var publishedDateColumn = createPublishedDateColumn();
        var quantityColumn = createQuantityColumn();
        var dateAddedColumn = createDateAddedColumn();
        var actionColumn = createActionColumn();

        documentTable.getColumns().setAll(selectColumn, imageColumn, nameColumn, authorsColumn, categoriesColumn, publisherColumn, isbnColumn, publishedDateColumn, quantityColumn, dateAddedColumn, actionColumn);
        documentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        selectAll.setOnAction(event -> {
            documentTable.getItems().forEach(item -> item.isSelectedProperty().set(selectAll.isSelected()));
        });

        loadAllDocuments();
    }

    private TableColumn<Document, ImageView> createImageColumn() {
        TableColumn<Document, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageUrl();
            Image image = imageUrl != null ? new Image(imageUrl) : null;
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return new SimpleObjectProperty<>(imageView);
        });
        return imageColumn;
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
        TableColumn<Document, String> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(cellData -> {
            List<Integer> authorIds = cellData.getValue().getAuthorIds();
            List<String> authors = authorIds != null ? authorIds.stream().map(id -> AuthorDAO.getInstance().getAuthorById(id).getName()).collect(Collectors.toList()) : null;
            return new SimpleStringProperty(authors != null ? String.join(", ", authors) : "N/A");
        });
        return authorsColumn;
    }

    private TableColumn<Document, String> createCategoriesColumn() {
        TableColumn<Document, String> categoriesColumn = new TableColumn<>("Categories");
        categoriesColumn.setCellValueFactory(cellData -> {
            List<Integer> categoryIds = cellData.getValue().getCategoryIds();
            List<String> categories = categoryIds != null ? categoryIds.stream().map(id -> CategoryDAO.getInstance().getCategoryById(id).getName()).collect(Collectors.toList()) : null;
            return new SimpleStringProperty(categories != null ? String.join(", ", categories) : "N/A");
        });
        return categoriesColumn;
    }

    private TableColumn<Document, String> createPublisherColumn() {
        TableColumn<Document, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cellData -> {
            int publisherId = cellData.getValue().getPublisherId();
            Publisher publisher = publisherId != 0 ? PublisherDAO.getInstance().getPublisherById(publisherId) : null;
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
                    // updateTotalDocuments(); // This should be handled by the controller
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

    public ObservableList<Document> getDocuments() {
        return documents;
    }

    public void loadAllDocuments() {
        List<Document> allDocuments = documentDAO.getAllDocuments();
        Platform.runLater(() -> documents.setAll(allDocuments));
    }

    public void deleteSelectedDocuments() {
        List<Integer> selectedDocumentIds = documentTable.getItems().stream()
            .filter(Document::isSelected)
            .map(Document::getDocumentId)
            .collect(Collectors.toList());

        if (!selectedDocumentIds.isEmpty()) {
            documentDAO.deleteDocuments(selectedDocumentIds);
            loadAllDocuments();
        }
    }

    public void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Documents to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) documentTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            String csvFile = file.getAbsolutePath();
            try (FileWriter writer = new FileWriter(csvFile)) {
                writer.append("Name,Authors,Categories,Publisher,ISBN,Published Date,Quantity,Date Added,Language,Image URL,Description\n");
                for (Document document : documents) {
                    String authors = document.getAuthorIds().stream()
                        .map(id -> AuthorDAO.getInstance().getAuthorById(id).getName())
                        .collect(Collectors.joining(", "));
                    String categories = document.getCategoryIds().stream()
                        .map(id -> CategoryDAO.getInstance().getCategoryById(id).getName())
                        .collect(Collectors.joining(", "));
                    String publisher = PublisherDAO.getInstance().getPublisherById(document.getPublisherId()).getName();
                    String language = LanguageDAO.getInstance().getLanguageById(document.getLanguageId()).getName();
                    writer.append(String.format("%s,%s,%s,%s,%s,%s,%d/%d,%s,%s,%s,%s\n",
                        document.getTitle(),
                        authors,
                        categories,
                        publisher,
                        document.getIsbn(),
                        document.getPublicationDate() != null ? document.getPublicationDate().toString() : "N/A",
                        document.getCurrentQuantity(),
                        document.getTotalQuantity(),
                        document.getDateAddedToLibrary().toString(),
                        language,
                        document.getImageUrl(),
                        document.getDescription() != null ? document.getDescription() : "N/A"
                    ));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void importToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            List<Document> importedDocuments = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    Document document = new Document.Builder(values[0])
                        .authorIds(parseIds(values[1]))
                        .categoryIds(parseIds(values[2]))
                        .publisherId(Integer.parseInt(values[3]))
                        .publicationDate(LocalDate.parse(values[4]))
                        .dateAddedToLibrary(LocalDate.parse(values[5]))
                        .currentQuantity(Integer.parseInt(values[6]))
                        .totalQuantity(Integer.parseInt(values[7]))
                        .isbn(values[8])
                        .languageId(Integer.parseInt(values[9]))
                        .description(values[10])
                        .build();
                    importedDocuments.add(document);
                }
                Platform.runLater(() -> documents.setAll(importedDocuments));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> parseIds(String ids) {
        List<Integer> idList = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(";");
            for (String id : idArray) {
                idList.add(Integer.parseInt(id));
            }
        }
        return idList;
    }
}
