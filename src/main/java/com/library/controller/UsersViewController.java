package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.WindowUtil;
import com.library.view.UsersView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class UsersViewController {

    @FXML private AnchorPane contentPane;

    private UsersView usersView;
    private UserDAO userDAO;
    private ObservableList<User> users;

    public void initialize() {
        userDAO = new UserDAO();
        usersView = new UsersView();
        users = FXCollections.observableList(userDAO.getAllUsers());

        usersView.setUserData(users);
        updateTotalUsers();

        usersView.getAddButton().setOnAction(event -> handleAddNewUser());
        usersView.getImportButton().setOnAction(event -> handleImportUser());
        usersView.getDeleteButton().setOnAction(event -> handleDeleteSelected());

        contentPane.getChildren().clear();
        contentPane.getChildren().add(usersView.getMainLayout());
    }

    private void updateTotalUsers() {
        int totalUsers = userDAO.countAllUsers();
        usersView.getTotalUsersLabel().setText("Total Users: " + totalUsers);
    }

    private void handleAddNewUser() {
        WindowUtil.openNewWindow("/com/library/views/AddUserWindow.fxml","Add New User");
    }

    private void handleImportUser() {
        WindowUtil.openNewWindow("/com/library/views/ImportUsersWindow.fxml","Import Users");
    }

    private void handleDeleteSelected() {
        List<Integer> selectedUserIds = usersView.getUserTable().getItems().stream()
            .filter(User::isSelected)
            .map(User::getUserId)
            .collect(Collectors.toList());

        if (!selectedUserIds.isEmpty()) {
            userDAO.deleteUsers(selectedUserIds);
            users.setAll(userDAO.getAllUsers());
            updateTotalUsers();
        }
    }
}

