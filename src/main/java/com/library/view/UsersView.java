package com.library.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.User;
import com.library.model.Author;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.PublisherDAO;

public class UsersView {
    private final TableView<User> userTable;
    private final Label totalUsersLabel;
    private final Button addButton;
    private final Button searchButton;
    private final Button deleteButton;
    private VBox mainLayout;

    public UsersView() {
        this.userTable = new TableView<>();
        this.totalUsersLabel = new Label("Total Users: 0");
        this.addButton = new Button("Add New User");
        this.searchButton = new Button("Search User");
        this.deleteButton = new Button("Delete Selected");
        
        initializeUI();
    }

    private void initializeUI() {
        userTable.setEditable(true);

        // User table
        initializeUserTable();

        // Layout for buttons
        HBox buttonBox = new HBox(10, addButton, searchButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(10));

        // Main layout
        mainLayout = new VBox(10, userTable, totalUsersLabel, buttonBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new javafx.geometry.Insets(20));
    }

    private void initializeUserTable() {
        // Define columns
        TableColumn<User, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Add all columns to the TableView
        userTable.getColumns().addAll(selectColumn, nameColumn);
        userTable.setPrefWidth(1000);
    }

    // Methods to access UI components for controller
    public VBox getMainLayout() {
        return mainLayout;
    }

    public TableView<User> getUserTable() {
        return userTable;
    }

    public Label getTotalUsersLabel() {
        return totalUsersLabel;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    // Method to update table data
    public void setUserData(ObservableList<User> documents) {
        userTable.setItems(documents);
    }
}
