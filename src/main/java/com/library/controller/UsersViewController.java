package com.library.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.library.App;
import com.library.model.User;
import com.library.services.UserDAO;
import com.library.view.UserTableView;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UsersViewController extends BaseViewController {

    @FXML private TableView<User> userTable;
    @FXML private Label totalUsersLabel;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private TextField searchTextField;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private UserTableView userTableView;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);  // Using a fixed thread pool

    public void initialize() {
        userTableView = new UserTableView(userTable);

        // Load initial data asynchronously
        loadUsersAsync();

        // Configure UI button actions
        configureButtonActions();

        // Add search field listener with debounce
        configureSearchField();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewUser());
        deleteButton.setOnAction(event -> handleDeleteSelected());
        filterButton.setOnAction(event -> handleFilterUsers());
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
            loadUsersAsync();  // Reload full list when the search text is empty
        } else {
            // Execute the search in a background thread
            executorService.submit(() -> {
                List<User> result = UserDAO.getInstance().getUsersByTitle(query);
                Platform.runLater(() -> userTableView.setData(FXCollections.observableList(result)));
            });
        }
    }

    private void loadUsersAsync() {
        Task<List<User>> loadTask = new Task<>() {
            @Override
            protected List<User> call() {
                return UserDAO.getInstance().getAllUsers();  // Database call in background
            }

            @Override
            protected void succeeded() {
                List<User> users = getValue();
                userTableView.setData(FXCollections.observableList(users));
                updateTotalUsers(users.size());  // Update the total count
            }

            @Override
            protected void failed() {
                App.showErrorDialog(new Exception("Failed to load users"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    private void updateTotalUsers(int count) {
        totalUsersLabel.setText("Total Users: " + count);
    }

    private void handleAddNewUser() {
        App.openDialog("/com/library/views/AddUserWindow.fxml", null, this::loadUsersAsync);
    }

    private void handleDeleteSelected() {
        userTableView.deleteSelectedItems();
        loadUsersAsync();  // Reload data after deletion
    }

    private void handleFilterUsers() {
        // Placeholder: Implement filter logic here
    }
}
