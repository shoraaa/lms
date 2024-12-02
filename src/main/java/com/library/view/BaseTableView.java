package com.library.view;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import com.library.controller.BaseController;
import com.library.util.ErrorHandler;
import com.library.util.Localization;
import com.library.util.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public abstract class BaseTableView<T> {

    protected final TableView<T> tableView;
    protected final ObservableList<T> data = FXCollections.observableArrayList();

    protected BaseController parentController;
    public ProgressIndicator progressIndicator = new ProgressIndicator();

    public BaseTableView(TableView<T> tableView) {
        this.tableView = tableView;
        initializeTable();

        progressIndicator.setProgress(-1);  // Set to indeterminate progress
        progressIndicator.setMaxSize(100, 100);  // Adjust size of the progress indicator
        HBox hBox = new HBox(progressIndicator);
        hBox.setAlignment(Pos.CENTER);
        tableView.setPlaceholder(hBox);
    }

    protected void initializeTable() {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getColumns().setAll(createColumns());
    }

    protected abstract List<TableColumn<T, ?>> createColumns();

    public void setParentController(BaseController parentController) {
        this.parentController = parentController;
    }

    public void setData(ObservableList<T> data) {
        this.data.setAll(data);
        tableView.setItems(this.data);
        progressIndicator.setVisible(false);
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
        boolean isAdmin = UserSession.isAdmin();
        TableColumn<T, Void> actionColumn = new TableColumn<>(Localization.getInstance().getString("actions"));
    
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon(Material2AL.EDIT);
            private final FontIcon deleteIcon = new FontIcon(Material2AL.DELETE);
            private final FontIcon viewIcon = new FontIcon(Material2MZ.VISIBILITY);
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button viewButton = new Button();
            private final HBox pane = new HBox();
    
            {
                pane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                pane.setSpacing(10);
    
                // Configure buttons
                editButton.setGraphic(editIcon);
                deleteButton.setGraphic(deleteIcon);
                viewButton.setGraphic(viewIcon);
    
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                viewButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
    
                editButton.setOnAction(event -> editItem(getTableRow().getItem()));
                deleteButton.setOnAction(event -> deleteItem(getTableRow().getItem()));
                viewButton.setOnAction(event -> viewItem(getTableRow().getItem()));
            }
    
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty) {
                    setGraphic(null);
                    return;
                }
    
                pane.getChildren().clear();
    
                if (isAdmin) {
                    pane.getChildren().addAll(viewButton, editButton, deleteButton);
                } else {
                    pane.getChildren().add(viewButton);
                }
    
                setGraphic(pane);
            }
        });
    
        return actionColumn;
    }
    
    

    // Delete selected items (to be implemented in subclass)
    public abstract void deleteSelectedItems();

    // Abstract method to handle editing an item (to be implemented in subclass)
    protected abstract void editItem(T item);

    protected abstract void viewItem(T item);

    // Abstract method to handle deleting an item (to be implemented in subclass)
    protected abstract void deleteItem(T item);

    protected abstract List<T> performInitialLoad();

    // Method to delete selected items
    public void deleteSelectedItems(ObservableList<T> selectedItems) {
        if (!selectedItems.isEmpty()) {
            selectedItems.forEach(this::deleteItem);
            loadItemsAsync();
        }
    }

    public void loadItemsAsync() {
        Task<List<T>> loadTask = new Task<>() {
            @Override
            protected List<T> call() {
                return performInitialLoad();  // Perform initial data load (to be implemented by child class)
            }

            @Override
            protected void succeeded() {
                List<T> items = getValue();
                setData(FXCollections.observableList(items));
            }

            @Override
            protected void failed() {
                ErrorHandler.showErrorDialog(new Exception("Failed to load items"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    
}
