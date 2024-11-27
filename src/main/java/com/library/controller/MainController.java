package com.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

// https://github.com/mkpaz/atlantafx/releases
public class MainController {

    @FXML private AnchorPane contentPane;
    @FXML private Button dashboardButton;
    @FXML private Button booksButton;
    @FXML private Button usersButton;
    @FXML private Button transactionsButton;


    @FXML
    public void initialize() {

        dashboardButton.setOnAction(event -> loadContent("/com/library/views/Dashboard.fxml"));
        booksButton.setOnAction(event -> loadContent("/com/library/views/DocumentsView.fxml"));
        usersButton.setOnAction(event -> loadContent("/com/library/views/UsersView.fxml"));
        transactionsButton.setOnAction(event -> loadContent("/com/library/views/TransactionsView.fxml"));

    }

    // Helper method to load the content dynamically
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        } catch (Exception e) {
            // Log the exception and show an error message to the user
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

}


