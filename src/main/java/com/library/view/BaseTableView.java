package com.library.view;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public abstract class BaseTableView<T> {

    protected final TableView<T> tableView;
    protected final ObservableList<T> data = FXCollections.observableArrayList();

    public BaseTableView(TableView<T> tableView) {
        this.tableView = tableView;
        initializeTable();
    }

    protected void initializeTable() {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.getColumns().setAll(createColumns());
    }

    protected abstract List<TableColumn<T, ?>> createColumns();

    protected void loadData() {
        // Abstract method to load data for the specific table view (e.g., DocumentTableView)
    }

    public void setData(ObservableList<T> data) {
        this.data.setAll(data);
        tableView.setItems(this.data);
    }

    public void removeColumn(String title) {
        tableView.getColumns().removeIf(column -> column.getText().equals(title));
    }

    protected TableColumn<T, String> createTextColumn(String title, java.util.function.Function<T, SimpleStringProperty> valueFunction) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> valueFunction.apply(cellData.getValue()));
        return column;
    }

    protected TableColumn<T, Void> createActionColumn() {
        TableColumn<T, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon(Material2AL.EDIT);
            private final FontIcon deleteIcon = new FontIcon(Material2AL.DELETE);
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                
                editButton.setGraphic(editIcon);
                deleteButton.setGraphic(deleteIcon);

                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                editButton.setOnAction(event -> editItem(getTableRow().getItem()));
                deleteButton.setOnAction(event -> deleteItem(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return actionColumn;
    }
    

    // Delete selected items (to be implemented in subclass)
    public abstract void deleteSelectedItems();

    // Abstract method to handle editing an item (to be implemented in subclass)
    protected abstract void editItem(T item);

    // Abstract method to handle deleting an item (to be implemented in subclass)
    protected abstract void deleteItem(T item);

    // Method to delete selected items
    public void deleteSelectedItems(ObservableList<T> selectedItems) {
        if (!selectedItems.isEmpty()) {
            selectedItems.forEach(this::deleteItem);
            loadData();
        }
    }
}
