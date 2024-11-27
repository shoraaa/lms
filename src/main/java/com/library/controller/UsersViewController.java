package com.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.User;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.UserDAO;
import com.library.services.PublisherDAO;
import com.library.util.WindowUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

public class UsersViewController {

    @FXML private TableView<User> userTable;
    @FXML private Label totalUsersLabel;
    @FXML private Button addButton;
    @FXML private Button importButton;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private VBox mainLayout;

    private UserDAO userDAO;
    private ObservableList<User> users;

    public void initialize() {
        userDAO = new UserDAO();
        users = FXCollections.observableList(userDAO.getAllUsers());

        initializeUserTable();
        setUserData(users);
        updateTotalUsers();

        addButton.setOnAction(event -> handleAddNewUser());
        deleteButton.setOnAction(event -> handleDeleteSelected());
    }

    private void updateTotalUsers() {
        int totalUsers = userDAO.countAllUsers();
        totalUsersLabel.setText("Total Users: " + totalUsers);
    }

    private void handleAddNewUser() {
        WindowUtil.openNewWindow("/com/library/views/AddUserWindow.fxml", "Add New User");
    }

    private void handleDeleteSelected() {
        List<Integer> selectedUserIds = userTable.getItems().stream()
            .filter(User::isSelected)
            .map(User::getUserId)
            .collect(Collectors.toList());

        if (!selectedUserIds.isEmpty()) {
            userDAO.deleteUsers(selectedUserIds);
            users.setAll(userDAO.getAllUsers());
            updateTotalUsers();
        }
    }

    private void initializeUserTable() {
        userTable.setEditable(true);

        TableColumn<User, Boolean> selectColumn = createSelectColumn();
        TableColumn<User, String> nameColumn = createNameColumn();
        TableColumn<User, String> emailColumn = createEmailColumn();
        TableColumn<User, String> phoneNumberColumn = createPhoneNumberColumn();
        TableColumn<User, String> timeRegisteredColumn = createTimeRegisteredColumn();

        userTable.getColumns().addAll(selectColumn, nameColumn, emailColumn, phoneNumberColumn, timeRegisteredColumn);
        userTable.setPrefWidth(1000);

        int columnCount = userTable.getColumns().size();
        for (TableColumn<?, ?> column : userTable.getColumns()) {
            column.setPrefWidth(1000 / columnCount);
        }

        userTable.setOnMouseClicked(event -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Open an edit dialog
                // openEditDialog(selectedUser);
            }
        });
    }

    private TableColumn<User, Boolean> createSelectColumn() {
        TableColumn<User, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        return selectColumn;
    }

    private TableColumn<User, String> createNameColumn() {
        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        return nameColumn;
    }

    private TableColumn<User, String> createEmailColumn() {
        TableColumn<User, String> nameColumn = new TableColumn<>("Email");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        return nameColumn;
    }

    private TableColumn<User, String> createPhoneNumberColumn() {
        TableColumn<User, String> nameColumn = new TableColumn<>("Phone Number");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
        return nameColumn;
    }

    private TableColumn<User, String> createTimeRegisteredColumn() {
        TableColumn<User, String> dateAddedColumn = new TableColumn<>("Time Registered");
        dateAddedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeRegistered().toString()));
        return dateAddedColumn;
    }

    public void setUserData(ObservableList<User> users) {
        userTable.setItems(users);
    }
}
