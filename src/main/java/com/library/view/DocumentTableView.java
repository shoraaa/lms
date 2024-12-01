package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.library.controller.BaseController;
import com.library.controller.EditDocumentController;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DocumentTableView extends BaseTableView<Document> {

    private final Map<Integer, String> authorsCache = new HashMap<>();
    private final Map<Integer, String> categoriesCache = new HashMap<>();
    private final Map<Integer, String> publishersCache = new HashMap<>();

    private BaseController parentController;

    public DocumentTableView(TableView<Document> tableView) {
        super(tableView);

        tableView.setRowFactory(tv -> {
            TableRow<Document> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                Document rowData = row.getItem();
                editItem(rowData);
            }
            });
            return row;
        });
    }

    public void setParentController(BaseController parentController) {
        this.parentController = parentController;
    }

    @Override
    protected List<TableColumn<Document, ?>> createColumns() {
        // Define the column creation tasks
        List<Supplier<TableColumn<Document, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            this::createImageColumn,
            () -> createTextColumn("ID", doc -> new SimpleStringProperty(String.valueOf(doc.getDocumentId()))),
            () -> createTextColumn("Name", doc -> new SimpleStringProperty(doc.getTitle())),
            this::createAuthorsColumn,
            this::createCategoriesColumn,
            this::createPublisherColumn,
            () -> createTextColumn("ISBN", doc -> new SimpleStringProperty(doc.getIsbn())),
            () -> createDateColumn("Publication Date", Document::getPublicationDate),
            this::createQuantityColumn,
            () -> createDateColumn("Registration Date", Document::getDateAddedToLibrary),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(java.util.function.Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    protected void deleteItem(Document document) {
        DocumentDAO.getInstance().deleteDocument(document.getDocumentId());
        data.remove(document); // Remove the document from the displayed data
    }

    @Override
    public void loadData() {
        List<Document> allDocuments = DocumentDAO.getInstance().getAllEntries();
        // Clear caches before reloading data
        authorsCache.clear();
        categoriesCache.clear();
        publishersCache.clear();
        setData(FXCollections.observableArrayList(allDocuments));
    }

    @Override
    protected void editItem(Document document) {
        parentController.getMainController().showDialog("/com/library/views/EditDocumentWindow.fxml", this::loadData, new EditDocumentController(document));
    }

    private TableColumn<Document, Boolean> createSelectColumn() {
        CheckBox selectAll = new CheckBox();
        TableColumn<Document, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAll);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        selectAll.setOnAction(event -> tableView.getItems().forEach(doc -> doc.isSelectedProperty().set(selectAll.isSelected())));
        return selectColumn;
    }

    protected TableColumn<Document, ImageView> createImageColumn() {
        TableColumn<Document, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageUrl();
            ImageView imageView = new ImageView(imageUrl != null ? new Image(imageUrl) : null);
            imageView.setFitWidth(60);
            imageView.setFitHeight(75);
            return new SimpleObjectProperty<>(imageView);
        });
        return imageColumn;
    }

    private TableColumn<Document, String> createAuthorsColumn() {
        return createTextColumn("Authors", doc -> {
            // Cache the authors to avoid querying the database repeatedly
            List<String> authors = doc.getAuthorIds().stream()
                .map(id -> authorsCache.computeIfAbsent(id, k -> AuthorDAO.getInstance().getAuthorById(k).getName()))
                .collect(Collectors.toList());
            return new SimpleStringProperty(String.join(", ", authors));
        });
    }

    private TableColumn<Document, String> createCategoriesColumn() {
        return createTextColumn("Categories", doc -> {
            // Cache the categories to avoid querying the database repeatedly
            List<String> categories = doc.getCategoryIds().stream()
                .map(id -> categoriesCache.computeIfAbsent(id, k -> CategoryDAO.getInstance().getCategoryById(k).getName()))
                .collect(Collectors.toList());
            return new SimpleStringProperty(String.join(", ", categories));
        });
    }

    private TableColumn<Document, String> createPublisherColumn() {
        return createTextColumn("Publisher", doc -> {
            // Cache the publisher to avoid querying the database repeatedly
            String publisherName = publishersCache.computeIfAbsent(doc.getPublisherId(),
                k -> Optional.ofNullable(PublisherDAO.getInstance().getPublisherById(k))
                             .map(Publisher::getName)
                             .orElse("N/A"));
            return new SimpleStringProperty(publisherName);
        });
    }

    private TableColumn<Document, String> createDateColumn(String title, java.util.function.Function<Document, LocalDate> dateGetter) {
        return createTextColumn(title, doc -> {
            LocalDate date = dateGetter.apply(doc);
            return new SimpleStringProperty(date != null ? date.toString() : "N/A");
        });
    }

    private TableColumn<Document, String> createQuantityColumn() {
        return createTextColumn("Quantity", doc -> new SimpleStringProperty(doc.getCurrentQuantity() + "/" + doc.getTotalQuantity()));
    }

    @Override
    public void deleteSelectedItems() {
        List<Document> selectedDocuments = tableView.getItems().stream()
            .filter(Document::isSelected)
            .collect(Collectors.toList());

        if (!selectedDocuments.isEmpty()) {
            selectedDocuments.forEach(this::deleteItem);
        }
        loadData();  // Reload the data after deletion
    }
}
