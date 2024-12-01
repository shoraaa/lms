package com.library.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.library.App;
// import com.library.controller.EditUserController;
import com.library.model.User;
import com.library.model.Publisher;
import com.library.model.Transaction;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.UserDAO;
import com.library.services.PublisherDAO;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserTableView extends BaseTableView<User> {

    private final Map<Integer, String> authorsCache = new HashMap<>();
    private final Map<Integer, String> categoriesCache = new HashMap<>();
    private final Map<Integer, String> publishersCache = new HashMap<>();

    public UserTableView(TableView<User> tableView) {
        super(tableView);

        tableView.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                User rowData = row.getItem();
                editItem(rowData);
            }
            });
            return row;
        });
    }

    @Override
    protected List<TableColumn<User, ?>> createColumns() {
        List<java.util.function.Supplier<TableColumn<User, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            () -> createTextColumn("ID", user -> new SimpleStringProperty(String.valueOf(user.getUserId()))),
            () -> createTextColumn("Name", user -> new SimpleStringProperty(user.getName())),
            () -> createTextColumn("E-mail", user -> new SimpleStringProperty(user.getEmail())),
            () -> createTextColumn("Phone Number", user -> new SimpleStringProperty(user.getPhoneNumber())),
            () -> createDateColumn("Registration Date", User::getRegistrationDate),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(java.util.function.Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    protected void deleteItem(User user) {
        UserDAO.getInstance().deleteUser(user.getUserId());
        data.remove(user); // Remove the user from the displayed data
    }

    @Override
    public void loadData() {
        List<User> allUsers = UserDAO.getInstance().getAllUsers();
        // Clear caches before reloading data
        authorsCache.clear();
        categoriesCache.clear();
        publishersCache.clear();
        setData(FXCollections.observableArrayList(allUsers));
    }

    @Override
    protected void editItem(User user) {
        // App.openDialog("/com/library/views/EditUserWindow.fxml", new EditUserController(user), this::loadData);
    }

    private TableColumn<User, Boolean> createSelectColumn() {
        CheckBox selectAll = new CheckBox();
        TableColumn<User, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAll);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        selectAll.setOnAction(event -> tableView.getItems().forEach(doc -> doc.isSelectedProperty().set(selectAll.isSelected())));
        return selectColumn;
    }

    protected TableColumn<User, ImageView> createImageColumn() {
        TableColumn<User, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageUrl();
            ImageView imageView = new ImageView(imageUrl != null ? new Image(imageUrl) : null);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return new SimpleObjectProperty<>(imageView);
        });
        return imageColumn;
    }

    private TableColumn<User, String> createDateColumn(String title, java.util.function.Function<User, LocalDate> dateGetter) {
        return createTextColumn(title, doc -> {
            LocalDate date = dateGetter.apply(doc);
            return new SimpleStringProperty(date != null ? date.toString() : "N/A");
        });
    }

    @Override
    public void deleteSelectedItems() {
        List<User> selectedUsers = tableView.getItems().stream()
            .filter(User::isSelected)
            .collect(Collectors.toList());

        if (!selectedUsers.isEmpty()) {
            selectedUsers.forEach(this::deleteItem);
        }
        loadData();  // Reload the data after deletion
    }
}
