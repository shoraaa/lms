package com.library.view;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.library.controller.EditUserController;
// import com.library.controller.EditUserController;
import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.Localization;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserTableView extends BaseTableView<User> {

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
        Localization localization = Localization.getInstance();
        List<Supplier<TableColumn<User, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            () -> createTextColumn("ID", user -> new SimpleStringProperty(String.valueOf(user.getUserId()))),
            () -> createTextColumn(localization.getString("name"), user -> new SimpleStringProperty(user.getName())),
            () -> createTextColumn("E-mail", user -> new SimpleStringProperty(user.getEmail())),
            () -> createTextColumn(localization.getString("phoneNumber"), user -> new SimpleStringProperty(user.getPhoneNumber())),
            () -> createDateColumn(localization.getString("registrationDate"), User::getRegistrationDate),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    protected void deleteItem(User user) {
        UserDAO.getInstance().deleteUser(user.getUserId());
        data.remove(user); // Remove the user from the displayed data
    }

    @Override
    protected void editItem(User user) {
        parentController.getMainController().showDialog("/com/library/views/EditUserWindow.fxml", this::loadItemsAsync, new EditUserController(user));
    }

    @Override
    protected void viewItem(User user) {
        // parentController.getMainController().showDialog("/com/library/views/EditUserWindow.fxml", this::loadItemsAsync, new EditUserController(user));
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
        TableColumn<User, ImageView> imageColumn = new TableColumn<>(Localization.getInstance().getString("image"));
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
        loadItemsAsync();
    }

    @Override
    public List<User> performInitialLoad() {
        return UserDAO.getInstance().getAllEntries();
    }
}
