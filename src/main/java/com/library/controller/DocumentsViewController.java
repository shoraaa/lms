package com.library.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Document;
import com.library.services.DocumentDAO;
import com.library.util.WindowUtil;
import com.library.view.DocumentsView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DocumentsViewController {

    @FXML private AnchorPane contentPane;

    private DocumentsView documentsView;
    private DocumentDAO documentDAO;
    private ObservableList<Document> documents;

    public void initialize() {
        documentDAO = new DocumentDAO();
        documentsView = new DocumentsView();
        documents = FXCollections.observableList(documentDAO.getAllDocuments());

        documentsView.setDocumentData(documents);
        updateTotalDocuments();

        documentsView.getAddButton().setOnAction(event -> handleAddNewDocument());
        documentsView.getDeleteButton().setOnAction(event -> handleDeleteSelected());

        contentPane.getChildren().clear();
        contentPane.getChildren().add(documentsView.getMainLayout());
    }

    private void updateTotalDocuments() {
        int totalDocuments = documentDAO.countAllDocument();
        documentsView.getTotalDocumentsLabel().setText("Total Documents: " + totalDocuments);
    }

    private void handleAddNewDocument() {
        WindowUtil.openNewWindow("/com/library/views/AddDocumentWindow.fxml","Add New Document");
    }

    private void handleDeleteSelected() {
        List<Integer> selectedDocumentIds = documentsView.getDocumentTable().getItems().stream()
            .filter(Document::isSelected)
            .map(Document::getId)
            .collect(Collectors.toList());

        if (!selectedDocumentIds.isEmpty()) {
            documentDAO.deleteDocument(selectedDocumentIds);
            documents.setAll(documentDAO.getAllDocuments());
            updateTotalDocuments();
        }
    }
}

