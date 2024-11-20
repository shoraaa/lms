package com.library.view;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.User;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.PublisherDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UsersView {
    private final TableView<User> userTable;
    private final Label totalUsersLabel;
    private final Button addButton;
    private final Button importButton;
    private final Button searchButton;
    private final Button deleteButton;
    private VBox mainLayout;

    public UsersView() {
        this.userTable = new TableView<>();
        this.totalUsersLabel = new Label("Total Users: 0");
        this.addButton = new Button("Add New Book");
        this.importButton = new Button("Import Book(s) from JSON");
        this.searchButton = new Button("Search Book");
        this.deleteButton = new Button("Delete Selected");
        
        initializeUI();
    }

    private void initializeUI() {
        userTable.setEditable(true);

        // User table
        initializeUserTable();

        // Layout for buttons
        HBox buttonBox = new HBox(10, addButton, importButton, searchButton, deleteButton);
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
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<User, String> nameColumn = new TableColumn<>("User Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<User, String> emailColumn = new TableColumn<>("E-mail");
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        TableColumn<User, String> timeRegisteredColumn = new TableColumn<>("Time Registered");
        timeRegisteredColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeRegistered().toString()));

        // Add all columns to the TableView
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

    // private void openEditDialog(User user) {
    //     WindowUtil.openNewWindow("/com/library/views/EditUserWindow.fxml", "Edit User");
    //     EditUserController controller = loader.getController();
    //     controller.setUser(user); // Pass the user to the controller
    // }


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

    public Button getImportButton() {
        return importButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    // Method to update table data
    public void setUserData(ObservableList<User> users) {
        userTable.setItems(users);
    }
}
