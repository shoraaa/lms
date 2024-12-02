package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

// import com.library.controller.EditTransactionController;
import com.library.model.Transaction;
import com.library.services.TransactionDAO;
import com.library.services.TransactionService;
import com.library.util.Localization;
import com.library.util.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;

public class TransactionTableView extends BaseTableView<Transaction> {

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

    protected TableColumn<Transaction, Void> createActionColumn() {
        boolean isAdmin = UserSession.isAdmin();
        TableColumn<Transaction, Void> actionColumn = new TableColumn<>(Localization.getInstance().getString("actions"));
    
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon(Material2AL.EDIT);
            private final FontIcon deleteIcon = new FontIcon(Material2AL.DELETE);
            private final FontIcon viewIcon = new FontIcon(Material2MZ.VISIBILITY);
            private final FontIcon returnIcon = new FontIcon(Material2AL.KEYBOARD_RETURN);
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button viewButton = new Button();
            private final Button returnButton = new Button();
            private final HBox pane = new HBox();
    
            {
                pane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                pane.setSpacing(10);
    
                // Configure buttons
                editButton.setGraphic(editIcon);
                deleteButton.setGraphic(deleteIcon);
                viewButton.setGraphic(viewIcon);
                returnButton.setGraphic(returnIcon);
    
                returnButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                viewButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
    
                returnButton.setOnAction(event -> returnDocument(getTableRow().getItem()));
                deleteButton.setOnAction(event -> deleteItem(getTableRow().getItem()));
                viewButton.setOnAction(event -> editItem(getTableRow().getItem()));
                editButton.setOnAction(event -> editItem(getTableRow().getItem()));
            }
    
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty) {
                    setGraphic(null);
                    return;
                }
    
                pane.getChildren().clear();
    
                if (isAdmin) {
                    pane.getChildren().addAll(editButton, deleteButton);
                } else {
                    pane.getChildren().add(viewButton);
                }

                if (!getTableRow().getItem().isReturned()) {
                    pane.getChildren().add(returnButton);
                }
    
                setGraphic(pane);
            }
        });
    
        return actionColumn;
    }

    private void returnDocument(Transaction transaction) {
        // Ensure transaction is valid
        if (transaction == null || transaction.isReturned()) {
            return;
        }
    
        // Update the transaction in the database
        TransactionService transactionService = TransactionService.getInstance();
        boolean success = transactionService.returnDocument(transaction) != null;
    
        if (success) {
            // Update the transaction object in memory
            transaction.setReturnDate(LocalDate.now());
            
            // Reload table data
            loadItemsAsync();
        } else {
            // Handle failure case, e.g., show an alert
            // showAlert("Error", "Failed to return the document. Please try again.");
        }
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

    @Override
    protected void viewItem(Transaction transaction) {
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
        if (documentId != null) {
            return TransactionDAO.getInstance().getTransactionsByDocumentId(documentId);
        } else if (userId != null) {
            return TransactionDAO.getInstance().getTransactionsByUserId(userId);
        }
        return TransactionDAO.getInstance().getAllEntries();
    }
}
