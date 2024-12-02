package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Author;
import com.library.model.Category;
import com.library.model.Document;
import com.library.model.Publisher;
import com.library.services.AuthorDAO;
import com.library.services.CategoryDAO;
import com.library.services.DocumentDAO;
import com.library.services.PublisherDAO;
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

        gridButton.setOnAction(event -> handleToggleGrid());
    }

    protected void handleToggleGrid() {
        ((DocumentTableView) itemTableView).toggleView(gridScrollPane);
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

    @Override
    protected List<String> getAllEntriesField(String field) {
        switch (field) {
            case "Author":
                return AuthorDAO.getInstance().getAllEntries().stream().map(Author::getName).collect(Collectors.toList());
            case "Category":
                return CategoryDAO.getInstance().getAllEntries().stream().map(Category::getName).collect(Collectors.toList());
            case "Publisher":
                return PublisherDAO.getInstance().getAllEntries().stream().map(Publisher::getName).collect(Collectors.toList());
            case "ID":
                return DocumentDAO.getInstance().getAllEntries().stream().map(document -> String.valueOf(document.getDocumentId())).collect(Collectors.toList());
            case "ISBN":
                return DocumentDAO.getInstance().getAllEntries().stream().map(Document::getIsbn).collect(Collectors.toList());
            case "Title":
                return DocumentDAO.getInstance().getAllEntries().stream().map(Document::getTitle).collect(Collectors.toList());
            default:
                return DocumentDAO.getInstance().getFieldOfAll(field);
        }
    }
}
