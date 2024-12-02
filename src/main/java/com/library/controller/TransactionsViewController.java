package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Transaction;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.util.ErrorHandler;
import com.library.view.TransactionTableView;

public class TransactionsViewController extends BaseViewController<Transaction> {

    @Override
    protected void initializeItemTableView() {
        itemTableView = new TransactionTableView(tableView);
    }

    @Override
    protected void initializeSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("ID", "User", "Document", "Status");
        searchChoiceBox.setValue("User");
    }

    @Override
    protected void handleAddNewItem() {
        if (mainController == null) {
            ErrorHandler.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddTransactionWindow.fxml", this::loadItemsAsync, null);
    }

    @Override
    protected void handleDeleteSelectedItems() {
        itemTableView.deleteSelectedItems();
        loadItemsAsync();  // Reload data after deletion
    }

    @Override
    protected List<Transaction> performSearchQuery(String query) {
        // Implement the search logic for transactions
        return TransactionDAO.getInstance().getTransactionsByKeyword(query, searchChoiceBox.getValue());
    }

    @Override
    protected List<Transaction> performInitialLoad() {
        return TransactionDAO.getInstance().getAllTransactions();
    }

    @Override
    protected List<String> getAllEntriesField(String field) {
        switch (field) {
            case "User":
                return TransactionDAO.getInstance().getAllEntries().stream().map(transaction -> transaction.getUser().getName()).collect(Collectors.toList());
            case "Document":
                return TransactionDAO.getInstance().getAllEntries().stream().map(transaction -> transaction.getDocument().getTitle()).collect(Collectors.toList());
            case "ID":
                return TransactionDAO.getInstance().getAllEntries().stream().map(transaction -> String.valueOf(transaction.getTransactionId())).collect(Collectors.toList());
            case "Status":
                return TransactionDAO.getInstance().getAllEntries().stream().map(transaction -> String.valueOf(transaction.getStatus())).collect(Collectors.toList());
            default:
                return List.of();
        }
            
    }
}
