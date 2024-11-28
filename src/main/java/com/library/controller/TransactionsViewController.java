package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Transaction;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;
import com.library.util.WindowUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

public class TransactionsViewController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private Label totalTransactionsLabel;
    @FXML private Button addButton;
    @FXML private Button importButton;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private TransactionDAO transactionDAO;
    private ObservableList<Transaction> transactions;

    public void initialize() {
        transactionDAO = TransactionDAO.getInstance();
        transactions = FXCollections.observableList(transactionDAO.getAllTransactions());

        initializeTransactionTable();
        setTransactionData(transactions);
        updateTotalTransactions();

        addButton.setOnAction(event -> handleAddNewTransaction());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        importButton.setOnAction(event -> handleImportTransactions());
    }

    private void updateTotalTransactions() {
        int totalTransactions = transactionDAO.countAllTransactions();
        totalTransactionsLabel.setText("Total Transactions: " + totalTransactions);
    }

    private void handleAddNewTransaction() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainLayout.getScene().getWindow());
        dialog.getDialogPane().setContent(WindowUtil.loadFXML("/com/library/views/AddTransactionWindow.fxml"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();

        transactions.setAll(transactionDAO.getAllTransactions());
        updateTotalTransactions();
    }

    private void handleDeleteSelected() {
        List<Integer> selectedTransactionIds = transactionTable.getItems().stream()
            .filter(Transaction::isSelected)
            .map(Transaction::getTransactionId)
            .collect(Collectors.toList());

        if (!selectedTransactionIds.isEmpty()) {
            transactionDAO.deleteTransactions(selectedTransactionIds);
            transactions.setAll(transactionDAO.getAllTransactions());
            updateTotalTransactions();
        }
    }

    private void handleImportTransactions() {
        
    }

    private void initializeTransactionTable() {
        transactionTable.setEditable(true);
        var selectAll = new CheckBox();

        var selectColumn = createSelectColumn(selectAll);
        var idColumn = createIdColumn();
        var userColumn = createUserColumn();
        var documentColumn = createDocumentColumn();
        var borrowDate = createBorrowDateColumn();
        var returnDate = createReturnDateColumn();
        var isReturnedColumn = createIsReturnedColumn();
        

        transactionTable.getColumns().addAll(selectColumn, idColumn, userColumn, documentColumn, borrowDate, returnDate, isReturnedColumn);
        transactionTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        selectAll.setOnAction(event -> {
            transactionTable.getItems().forEach(
                item -> item.isSelectedProperty().set(selectAll.isSelected())
            );
        });

        transactionTable.setOnMouseClicked(event -> {
            Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null) {
                // Open an edit dialog
                // openEditDialog(selectedTransaction);
            }
        });
    }

    private TableColumn<Transaction, Boolean> createSelectColumn(CheckBox selectAll) {
        
        TableColumn<Transaction, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAll);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        return selectColumn;
    }

    private TableColumn<Transaction, Integer> createIdColumn() {
        TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTransactionId()).asObject());
        return idColumn;
    }

    private TableColumn<Transaction, String> createUserColumn() {
        TableColumn<Transaction, String> userColumn = new TableColumn<>("User");
        UserDAO userDAO = UserDAO.getInstance();
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(userDAO.getUserById(cellData.getValue().getUserId()).getName()));
        return userColumn;
    }

    private TableColumn<Transaction, String> createDocumentColumn() {
        TableColumn<Transaction, String> userColumn = new TableColumn<>("Document");
        DocumentDAO userDAO = DocumentDAO.getInstance();
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(userDAO.getDocumentById(cellData.getValue().getDocumentId()).getTitle()));
        return userColumn;
    }

    private TableColumn<Transaction, String> createBorrowDateColumn() {
        TableColumn<Transaction, String> returnDateColumn = new TableColumn<>("Borrow Date");
        returnDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));
        return returnDateColumn;
    }

    private TableColumn<Transaction, String> createReturnDateColumn() {
        TableColumn<Transaction, String> borrowDateColumn = new TableColumn<>("Return Date");
        borrowDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));
        return borrowDateColumn;
    }

    private TableColumn<Transaction, Boolean> createIsReturnedColumn() {
        TableColumn<Transaction, Boolean> isReturnedColumn = new TableColumn<>("Is Returned?");
        isReturnedColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isReturned()));
        return isReturnedColumn;
    }


    public void setTransactionData(ObservableList<Transaction> transactions) {
        transactionTable.setItems(transactions);
    }
}
