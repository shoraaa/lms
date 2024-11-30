package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.library.model.Document;
// import com.library.controller.EditTransactionController;
import com.library.model.Transaction;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

public class TransactionTableView extends BaseTableView<Transaction> {

    private final Map<Integer, String> authorsCache = new HashMap<>();
    private final Map<Integer, String> categoriesCache = new HashMap<>();
    private final Map<Integer, String> publishersCache = new HashMap<>();

    public TransactionTableView(TableView<Transaction> tableView) {
        super(tableView);
        loadData();

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

    @Override
    protected List<TableColumn<Transaction, ?>> createColumns() {
        List<java.util.function.Supplier<TableColumn<Transaction, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            () -> createTextColumn("ID", user -> new SimpleStringProperty(String.valueOf(user.getUserId()))),
            () -> createTextColumn("User", transaction -> new SimpleStringProperty(transaction.getUserName())),
            () -> createTextColumn("Document", transaction -> new SimpleStringProperty(transaction.getDocumentTitle())),
            () -> createDateColumn("Borrow Date", transaction -> transaction.getBorrowDate()),
            () -> createDateColumn("Due Date", transaction -> transaction.getDueDate()),
            () -> createDateColumn("Return Date", transaction -> transaction.getReturnDate()),
            () -> createTextColumn("Status", transaction -> new SimpleStringProperty(transaction.getStatus())),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(java.util.function.Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    protected void deleteItem(Transaction transaction) {
        TransactionDAO.getInstance().deleteTransaction(transaction.getTransactionId());
        data.remove(transaction); // Remove the transaction from the displayed data
    }

    @Override
    public void loadData() {
        List<Transaction> allTransactions = TransactionDAO.getInstance().getAllTransactions();
        // Clear caches before reloading data
        authorsCache.clear();
        categoriesCache.clear();
        publishersCache.clear();
        data.setAll(allTransactions);
        tableView.setItems(data);
    }

    @Override
    protected void editItem(Transaction transaction) {
        // App.openDialog("/com/library/views/EditTransactionWindow.fxml", new EditTransactionController(transaction), this::loadData);
    }

    private TableColumn<Transaction, Boolean> createSelectColumn() {
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

    private TableColumn<Transaction, String> createDateColumn(String title, java.util.function.Function<Transaction, LocalDate> dateGetter) {
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
        loadData();  // Reload the data after deletion
    }
}
