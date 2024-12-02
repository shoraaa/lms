package com.library.controller;

import java.util.List;

import com.library.model.Document;
import com.library.services.DocumentDAO;
import com.library.util.ErrorHandler;
import com.library.view.DocumentTableView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class DocumentsViewController extends BaseViewController<Document> {

    @FXML private Button gridButton;
    @FXML private GridPane gridPane;
    @FXML private ScrollPane gridScrollPane;

    @Override
    protected void initializeItemTableView() {
        tableView.setVisible(true);
        gridPane.setVisible(false);
        gridScrollPane.setVisible(false);
        
        itemTableView = new DocumentTableView(tableView);
        itemTableView.setParentController(this);
        ((DocumentTableView) itemTableView).setGridPane(gridPane);

        gridButton.setOnAction(event -> {
            ((DocumentTableView) itemTableView).toggleView(gridScrollPane);
        });
    }

    @Override
    protected void initializeSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("ID", "Title", "Author", "Category", "Publisher", "ISBN");
        searchChoiceBox.setValue("Title");
    }

    @Override
    protected void handleAddNewItem() {
        if (mainController == null) {
            ErrorHandler.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddDocumentWindow.fxml", this::loadItemsAsync, null);
    }

    @Override
    protected void handleDeleteSelectedItems() {
        itemTableView.deleteSelectedItems();
        loadItemsAsync();  // Reload data after deletion
    }

    @Override
    protected List<Document> performSearchQuery(String query) {
        return DocumentDAO.getInstance().getDocumentsByKeyword(query, searchChoiceBox.getValue());
    }

    @Override
    protected List<Document> performInitialLoad() {
        return DocumentDAO.getInstance().getAllEntries();
    }
}
