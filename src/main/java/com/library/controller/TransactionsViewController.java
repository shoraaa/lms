package com.library.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.library.App;
import com.library.model.Transaction;
import com.library.services.TransactionDAO;
import com.library.view.TransactionTableView;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TransactionsViewController extends BaseViewController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private Label totalTransactionsLabel;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private TextField searchTextField;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private TransactionTableView transactionTableView;

    private final ExecutorService executorService = Executors.newCachedThreadPool();  // Using a fixed thread pool

    public void initialize() {
        transactionTableView = new TransactionTableView(transactionTable);

        // Load initial data asynchronously
        loadTransactionsAsync();

        // Configure UI button actions
        configureButtonActions();

        // Add search field listener with debounce
        configureSearchField();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewTransaction());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        filterButton.setOnAction(event -> handleFilterTransactions());
    }

    private void configureSearchField() {
        // Debounce mechanism for search
        PauseTransition pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(event -> performSearch(searchTextField.getText()));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.playFromStart();  // Restart the pause timer
        });
    }

    private void performSearch(String query) {
        // if (query.isEmpty()) {
        //     loadTransactionsAsync();  // Reload full list when the search text is empty
        // } else {
        //     // Execute the search in a background thread
        //     executorService.submit(() -> {
        //         List<Transaction> result = TransactionDAO.getInstance().getTransactionsByTitle(query);
        //         Platform.runLater(() -> transactionTableView.setData(FXCollections.observableList(result)));
        //     });
        // }
    }

    private void loadTransactionsAsync() {
        Task<List<Transaction>> loadTask = new Task<>() {
            @Override
            protected List<Transaction> call() {
                return TransactionDAO.getInstance().getAllTransactions();  // Database call in background
            }

            @Override
            protected void succeeded() {
                List<Transaction> transactions = getValue();
                transactionTableView.setData(FXCollections.observableList(transactions));
                updateTotalTransactions(transactions.size());  // Update the total count
            }

            @Override
            protected void failed() {
                App.showErrorDialog(new Exception("Failed to load transactions"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    private void updateTotalTransactions(int count) {
        totalTransactionsLabel.setText("Total Transactions: " + count);
    }

    private void handleAddNewTransaction() {
        if (mainController == null) {
            App.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddTransactionWindow.fxml", this::loadTransactionsAsync, null);
    }

    private void handleDeleteSelected() {
        transactionTableView.deleteSelectedItems();
        loadTransactionsAsync();  // Reload data after deletion
    }

    private void handleFilterTransactions() {
        // Placeholder: Implement filter logic here
    }
}
