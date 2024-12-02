package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import com.library.model.Document;
// import com.library.controller.EditTransactionController;
import com.library.model.Transaction;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.TransactionService;
import com.library.util.Localization;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;

public class TransactionTableView extends BaseTableView<Transaction> {

    private final Map<Integer, String> authorsCache = new HashMap<>();
    private final Map<Integer, String> categoriesCache = new HashMap<>();
    private final Map<Integer, String> publishersCache = new HashMap<>();

    private Integer documentId = null;
    private Integer userId = null;

    public TransactionTableView(TableView<Transaction> tableView) {
        super(tableView);

        tableView.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                Transaction rowData = row.getItem();
                editItem(rowData);
            }
            });
            return row;
        });
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    protected List<TableColumn<Transaction, ?>> createColumns() {
        Localization localization = Localization.getInstance();
        List<Supplier<TableColumn<Transaction, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            () -> createTextColumn("ID", user -> new SimpleStringProperty(String.valueOf(user.getUserId()))),
            () -> createTextColumn(localization.getString("user"), transaction -> new SimpleStringProperty(transaction.getUserName())),
            () -> createTextColumn(localization.getString("document"), transaction -> new SimpleStringProperty(transaction.getDocumentTitle())),
            () -> createDateColumn(localization.getString("borrowDate"), transaction -> transaction.getBorrowDate()),
            () -> createDateColumn(localization.getString("dueDate"), transaction -> transaction.getDueDate()),
            () -> createDateColumn(localization.getString("returnDate"), transaction -> transaction.getReturnDate()),
            () -> createTextColumn(localization.getString("status"), transaction -> new SimpleStringProperty(transaction.getStatus())),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    protected TableColumn<Transaction, Void> createActionColumn() {
        TableColumn<Transaction, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon(Material2AL.KEYBOARD_RETURN);
            private final FontIcon deleteIcon = new FontIcon(Material2AL.DELETE);
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox pane = new HBox(editButton, deleteButton);
            {
                pane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                
                editButton.setGraphic(editIcon);
                deleteButton.setGraphic(deleteIcon);

                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                editButton.setOnAction(event -> returnDocument(getTableRow().getItem()));
                deleteButton.setOnAction(event -> deleteItem(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return actionColumn;
    }

    private void returnDocument(Transaction transaction) {
        TransactionService.getInstance().returnDocument(transaction);
        loadItemsAsync();
    }

    @Override
    protected void deleteItem(Transaction transaction) {
        TransactionDAO.getInstance().deleteTransaction(transaction.getTransactionId());
        data.remove(transaction); // Remove the transaction from the displayed data
    }

    @Override
    protected void editItem(Transaction transaction) {
        // App.openDialog("/com/library/views/EditTransactionWindow.fxml", new EditTransactionController(transaction), this::loadData);
    }

    protected TableColumn<Transaction, Boolean> createSelectColumn() {
        CheckBox selectAll = new CheckBox();
        TableColumn<Transaction, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAll);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        selectAll.setOnAction(event -> tableView.getItems().forEach(doc -> doc.isSelectedProperty().set(selectAll.isSelected())));
        return selectColumn;
    }

    protected TableColumn<Transaction, String> createDateColumn(String title, java.util.function.Function<Transaction, LocalDate> dateGetter) {
        return createTextColumn(title, doc -> {
            LocalDate date = dateGetter.apply(doc);
            return new SimpleStringProperty(date != null ? date.toString() : "N/A");
        });
    }

    @Override
    public void deleteSelectedItems() {
        List<Transaction> selectedTransactions = tableView.getItems().stream()
            .filter(Transaction::isSelected)
            .collect(Collectors.toList());

        if (!selectedTransactions.isEmpty()) {
            selectedTransactions.forEach(this::deleteItem);
        }
        loadItemsAsync();
    }

    @Override
    public List<Transaction> performInitialLoad() {
        return TransactionDAO.getInstance().getAllEntries();
    }
}
