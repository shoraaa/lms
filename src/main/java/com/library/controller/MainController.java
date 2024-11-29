package com.library.controller;

import java.io.IOException;

import atlantafx.base.util.Animations;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

// https://github.com/mkpaz/atlantafx/releases
public class MainController {

    @FXML private AnchorPane contentPane;
    @FXML private Button dashboardButton;
    @FXML private Button documentButton;
    @FXML private Button userButton;
    @FXML private Button transactionButton;


    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> handleDashboardButton());
        documentButton.setOnAction(event -> handleDocumentButton());
        userButton.setOnAction(event -> handleUserButton());
        transactionButton.setOnAction(event -> handleTransactionButton());

        handleDashboardButton();
    }

    private void selectSidebarButton(Button selectedButton) {
        // Clear the 'selected' style class from all buttons
        dashboardButton.getStyleClass().remove("selected");
        userButton.getStyleClass().remove("selected");
        documentButton.getStyleClass().remove("selected");
        transactionButton.getStyleClass().remove("selected");
    
        // Add the 'selected' style class to the clicked button
        selectedButton.getStyleClass().add("selected");
    }

    private void handleDashboardButton() {
        selectSidebarButton(dashboardButton);
        loadContent("/com/library/views/Dashboard.fxml");
    }

    private void handleDocumentButton() {
        selectSidebarButton(documentButton);
        loadContent("/com/library/views/DocumentsView.fxml");
    }

    private void handleUserButton() {
        selectSidebarButton(userButton);
        loadContent("/com/library/views/UsersView.fxml");
    }

    private void handleTransactionButton() {
        selectSidebarButton(transactionButton);
        loadContent("/com/library/views/TransactionsView.fxml");
    }

        // Helper method to load the content dynamically
    private void loadContent(String fxmlFile) {
        try {
            
            // Start fade-out animation immediately
            if (!contentPane.getChildren().isEmpty()) {
                var fadeOutAnimation = Animations.fadeOut(contentPane.getChildren().get(0), Duration.seconds(0.25));
                fadeOutAnimation.playFromStart();

                // Set up the fade-in animation to play once fade-out completes and the content is loaded
                fadeOutAnimation.setOnFinished(event -> {
                    contentPane.getChildren().clear();
                    loadAndFadeInNewContent(fxmlFile);
                });
            } else {
                // If no content exists, directly load and fade in the new content
                loadAndFadeInNewContent(fxmlFile);
            }
        } catch (Exception e) {
            // Log the exception and show an error message to the user
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // Helper method to load new content and fade it in
    private void loadAndFadeInNewContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Region loadedPane = loader.load();
            loadedPane.prefWidthProperty().bind(contentPane.widthProperty());
            loadedPane.prefHeightProperty().bind(contentPane.heightProperty());
            // loadedPane.opacityProperty().set(0); // Start with 0 opacity

            contentPane.getChildren().add(loadedPane);

            // Fade in the new content
            // Animations.fadeIn(loadedPane, Duration.seconds(0.25)).playFromStart();
        } catch (IOException e) {
            // Log the exception and show an error message to the user
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
            }
        }

}


