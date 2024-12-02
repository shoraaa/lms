package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.UserDAO;
import com.library.util.ErrorHandler;
import com.library.view.UserTableView;

public class UsersViewController extends BaseViewController<User> {

    @Override
    protected void initializeItemTableView() {
        itemTableView = new UserTableView(tableView);
    }

    @Override
    protected void initializeSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("ID", "Name", "Email", "Phone Number");
        searchChoiceBox.setValue("Name");
    }

    @Override
    protected void handleAddNewItem() {
        if (mainController == null) {
            ErrorHandler.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddUserWindow.fxml", this::loadItemsAsync, null);
    }

    @Override
    protected void handleDeleteSelectedItems() {
        itemTableView.deleteSelectedItems();
        loadItemsAsync();  // Reload data after deletion
    }

    @Override
    protected List<User> performSearchQuery(String query) {
        return UserDAO.getInstance().getUsersByName(query);
    }

    @Override
    protected List<User> performInitialLoad() {
        return UserDAO.getInstance().getAllUsers();
    }

    @Override
    protected List<String> getAllEntriesField(String field) {
        switch (field) {
            case "Name":
                return UserDAO.getInstance().getAllEntries().stream().map(User::getName).collect(Collectors.toList());
            case "Email":
                return UserDAO.getInstance().getAllEntries().stream().map(User::getEmail).collect(Collectors.toList());
            case "Phone Number":
                return UserDAO.getInstance().getAllEntries().stream().map(User::getPhoneNumber).collect(Collectors.toList());
            case "ID":
                return UserDAO.getInstance().getAllEntries().stream().map(user -> String.valueOf(user.getUserId())).collect(Collectors.toList());
            default:
                return List.of();
        }
    }
}
