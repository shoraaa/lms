package com.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


public class MainController {

    @FXML private AnchorPane contentPane;
    @FXML private Button booksButton;
    @FXML private Button usersButton;
    @FXML private Button transactionsButton;


    @FXML
    public void initialize() {

        booksButton.setOnAction(event -> loadContent("/com/library/BooksView.fxml"));

    }

    // Helper method to load the content dynamically
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


