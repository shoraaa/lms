package com.library.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.WindowUtil;
import com.library.view.UsersView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

        usersView.getAddButton().setOnAction(event -> handleAddNewBook());
        usersView.getDeleteButton().setOnAction(event -> handleDeleteSelected());

        contentPane.getChildren().clear();
        contentPane.getChildren().add(usersView.getMainLayout());
    }

    private void updateTotalUsers() {
        int totalUsers = userDAO.countAllUsers();
        usersView.getTotalUsersLabel().setText("Total Users: " + totalUsers);
    }

    private void handleAddNewBook() {
        WindowUtil.openNewWindow("/com/library/views/AddBookWindow.fxml","Add New Book");
    }

    private void handleDeleteSelected() {
        List<Integer> selectedUserIds = usersView.getUserTable().getItems().stream()
            .filter(User::isSelected)
            .map(User::getId)
            .collect(Collectors.toList());

        if (!selectedUserIds.isEmpty()) {
            userDAO.deleteUsers(selectedUserIds);
            users.setAll(userDAO.getAllUsers());
            updateTotalUsers();
        }
    }
}

