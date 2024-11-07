package com.library;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.document.DocumentInfo;
import com.library.model.user.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class DashboardController {

    @FXML private VBox mainContent;
    @FXML private Button booksButton;
    @FXML private Button usersButton;
    @FXML private Button transactionsButton;

    @FXML
    public void initialize() {
        booksButton.setOnAction(event -> loadBooksView());
        usersButton.setOnAction(event -> loadUsersView());
        transactionsButton.setOnAction(event -> loadTransactionsView());
    }

    private void loadAddNewBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddBookWindow.fxml"));
            Parent root = loader.load();
            Scene addBookScene = new Scene(root);
            Stage addBookStage = new Stage();
            addBookStage.setScene(addBookScene);
            addBookStage.show();

            addBookStage.setOnHidden(event -> loadBooksView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBooksView() {
        mainContent.getChildren().clear();

        Button addNewBookButton = new Button("Add New Book");
        mainContent.getChildren().add(addNewBookButton);

        addNewBookButton.setOnAction(event -> loadAddNewBookView());

        List<Integer> documentIds = App.getDocumentManager().getAllDocumentIds();
        List<DocumentInfo> documentInfos = documentIds.stream()
            .map(App.getDocumentManager()::getDocumentInfoFromId)
            .collect(Collectors.toList());
        ObservableList<DocumentInfo> bookData = FXCollections.observableList(documentInfos);

        TableView<DocumentInfo> booksTable = new TableView<>(bookData);

        TableColumn<DocumentInfo, String> nameColumn = new TableColumn<>("Document Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        booksTable.getColumns().add(nameColumn);

        mainContent.getChildren().add(booksTable);
    }

    private void loadUsersView() {
        mainContent.getChildren().clear();

        TableView<User> usersTable = new TableView();
        mainContent.getChildren().add(usersTable);
    }

    private void loadTransactionsView() {
        mainContent.getChildren().clear();

    }
}


