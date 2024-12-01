package com.library.controller;

import java.util.List;

import com.library.model.Transaction;
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
        searchChoiceBox.getItems().addAll("ID", "User", "Document");
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

    // This method could be used to implement filter logic if needed
    private void handleFilterTransactions() {
        // Placeholder: Implement filter logic here
    }
}
