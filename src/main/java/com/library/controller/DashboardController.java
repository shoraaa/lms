package com.library.controller;

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

    private BooksViewLoader booksViewLoader;

    @FXML
    public void initialize() {
        booksViewLoader = new BooksViewLoader(mainContent);

        booksButton.setOnAction(event -> booksViewLoader.loadBooksView());
        usersButton.setOnAction(event -> loadUsersView());
        transactionsButton.setOnAction(event -> loadTransactionsView());
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


