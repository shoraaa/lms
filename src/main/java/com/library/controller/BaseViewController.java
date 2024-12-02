
package com.library.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.library.util.ErrorHandler;
import com.library.view.BaseTableView;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public abstract class BaseViewController<T> extends BaseController {

    @FXML protected TableView<T> tableView;
    @FXML protected Button addButton;
    @FXML protected Button filterButton;
    @FXML protected TextField searchTextField;
    @FXML protected Button deleteButton;
    @FXML protected ChoiceBox<String> searchChoiceBox;
    @FXML protected Pagination tablePagination;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    protected BaseTableView<T> itemTableView;

    // Abstract methods to be implemented by child controllers
    protected abstract List<T> performInitialLoad();
    protected abstract void handleAddNewItem();
    protected abstract void handleDeleteSelectedItems();
    protected abstract List<T> performSearchQuery(String query);
    protected abstract void initializeItemTableView();
    protected abstract void initializeSearchChoiceBox();

    @FXML public void initialize() {
        // initializeItemTableView() should be implemented by child class
        initializeItemTableView();

        initializeSearchChoiceBox();

        // Load initial data asynchronously
        loadItemsAsync();

        // Configure UI button actions
        configureButtonActions();

        // Add search field listener with debounce
        configureSearchField();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewItem());
        deleteButton.setOnAction(event -> handleDeleteSelectedItems());
        filterButton.setOnAction(event -> handleFilterItems());
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
        if (query.isEmpty()) {
            loadItemsAsync();  // Reload full list when the search text is empty
        } else {
            // Execute the search in a background thread
            executorService.submit(() -> {
                List<T> result = performSearchQuery(query);
                Platform.runLater(() -> itemTableView.setData(FXCollections.observableList(result)));
            });
        }
    }

    protected void loadItemsAsync() {
        Task<List<T>> loadTask = new Task<>() {
            @Override
            protected List<T> call() {
                return performInitialLoad();  // Perform initial data load (to be implemented by child class)
            }

            @Override
            protected void succeeded() {
                List<T> items = getValue();
                itemTableView.setData(FXCollections.observableList(items));
            }

            @Override
            protected void failed() {
                ErrorHandler.showErrorDialog(new Exception("Failed to load items"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    private void handleFilterItems() {
        // Placeholder: Implement filter logic here if needed
    }
}
