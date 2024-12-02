package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.library.controller.EditDocumentController;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;
import com.library.util.Localization;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DocumentTableView extends BaseTableView<Document> {

    private final Map<Integer, String> authorsCache = new HashMap<>();
    private final Map<Integer, String> categoriesCache = new HashMap<>();
    private final Map<Integer, String> publishersCache = new HashMap<>();

    private GridPane gridPane;
    private int columns = 10; // Number of columns for the grid view

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

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void toggleView(ScrollPane gridScrollPane) {
        // populateGrid(DocumentDAO.getInstance().getAllEntries());
        boolean tableMode = tableView.isVisible();
        gridPane.setVisible(tableMode);
        gridScrollPane.setVisible(tableMode);
        tableView.setVisible(!tableMode);
    }

    private void populateGrid(List<Document> documents) {
        gridPane.getChildren().clear();
        int row = 0;
        int column = 0;
        for (Document document : documents) {
            VBox documentCell = createDocumentCell(document);

            gridPane.add(documentCell, column, row);
            column++;
            if (column >= columns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createDocumentCell(Document document) {
        VBox cell = new VBox();
        cell.setSpacing(5);
        cell.setPadding(new Insets(10));
        // cell.getStyleClass().add("document-cell");

        ImageView imageView = new ImageView(document.getImageUrl() != null ? new Image(document.getImageUrl()) : null);
        imageView.setFitWidth(120);
        imageView.setFitHeight(150);

        Text title = new Text(document.getTitle());
        title.setWrappingWidth(120); 
        // title.getStyleClass().add("document-title");

        // Text authors = new Text("Authors: " + getAuthorsText(document));
        // authors.getStyleClass().add("document-authors");

        // Text categories = new Text("Categories: " + getCategoriesText(document));
        // categories.getStyleClass().add("document-categories");

        // Text publisher = new Text("Publisher: " + getPublisherText(document));
        // publisher.getStyleClass().add("document-publisher");

        // Text quantity = new Text("Quantity: " + document.getCurrentQuantity() + "/" + document.getTotalQuantity());
        // quantity.getStyleClass().add("document-quantity");

        // CheckBox selectCheckBox = new CheckBox("Select");
        // selectCheckBox.selectedProperty().bindBidirectional(document.isSelectedProperty());

        cell.getChildren().addAll(imageView, title);
        cell.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editItem(document);
            }
        });

        return cell;
    }

    @Override
    protected List<TableColumn<Document, ?>> createColumns() {
        Localization localization = Localization.getInstance();
        // Define the column creation tasks
        List<Supplier<TableColumn<Document, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            this::createImageColumn,
            () -> createTextColumn("ID", doc -> new SimpleStringProperty(String.valueOf(doc.getDocumentId()))),
            () -> createTextColumn(localization.getString("title"), doc -> new SimpleStringProperty(doc.getTitle())),
            this::createAuthorsColumn,
            this::createCategoriesColumn,
            this::createPublisherColumn,
            () -> createTextColumn("ISBN", doc -> new SimpleStringProperty(doc.getIsbn())),
            () -> createDateColumn(localization.getString("publicationDate"), Document::getPublicationDate),
            this::createQuantityColumn,
            () -> createDateColumn(localization.getString("registrationDate"), Document::getDateAddedToLibrary),
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
    protected void editItem(Document document) {
        parentController.getMainController().showDialog("/com/library/views/EditDocumentWindow.fxml", this::loadItemsAsync, new EditDocumentController(document));
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
        return createTextColumn(Localization.getInstance().getString("authors"), doc -> {
            // Cache the authors to avoid querying the database repeatedly
            List<String> authors = doc.getAuthorIds().stream()
                .map(id -> authorsCache.computeIfAbsent(id, k -> AuthorDAO.getInstance().getAuthorById(k).getName()))
                .collect(Collectors.toList());
            return new SimpleStringProperty(String.join(", ", authors));
        });
    }

    private TableColumn<Document, String> createCategoriesColumn() {
        return createTextColumn(Localization.getInstance().getString("categories"), doc -> {
            // Cache the categories to avoid querying the database repeatedly
            List<String> categories = doc.getCategoryIds().stream()
                .map(id -> categoriesCache.computeIfAbsent(id, k -> CategoryDAO.getInstance().getCategoryById(k).getName()))
                .collect(Collectors.toList());
            return new SimpleStringProperty(String.join(", ", categories));
        });
    }

    private TableColumn<Document, String> createPublisherColumn() {
        return createTextColumn(Localization.getInstance().getString("publisher"), doc -> {
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
        return createTextColumn(Localization.getInstance().getString("quantity"), doc -> new SimpleStringProperty(doc.getCurrentQuantity() + "/" + doc.getTotalQuantity()));
    }

    @Override
    public void deleteSelectedItems() {
        List<Document> selectedDocuments = tableView.getItems().stream()
            .filter(Document::isSelected)
            .collect(Collectors.toList());

        if (!selectedDocuments.isEmpty()) {
            selectedDocuments.forEach(this::deleteItem);
        }
        loadItemsAsync();  // Reload the data after deletion
    }

    @Override
    public List<Document> performInitialLoad() {
        return DocumentDAO.getInstance().getAllEntries();
    }

    @Override
    public void setData(ObservableList<Document> data) {
        super.setData(data);
        if (gridPane != null) {
            populateGrid(data);
        }
    }
}
