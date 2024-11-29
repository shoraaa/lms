package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// import com.library.controller.EditTransactionController;
import com.library.model.Transaction;
import com.library.services.TransactionDAO;

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
        return List.of(
            createSelectColumn(),
            // createImageColumn(),
            createTextColumn("User ID", transaction -> new SimpleStringProperty(String.valueOf(transaction.getUserId()))),
            createTextColumn("Document ID", transaction -> new SimpleStringProperty(String.valueOf(transaction.getDocumentId()))),
            createDateColumn("Borrow Date", transaction -> transaction.getBorrowDate()),
            createDateColumn("Return Date", transaction -> transaction.getReturnDate()),
            createTextColumn("Is Returned", transaction -> new SimpleStringProperty(transaction.isReturned() ? "Yes" : "No")),
            createActionColumn()
        );
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
