package com.library.controller;

import java.io.IOException;

import com.library.App;
import com.library.util.WindowUtil;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.util.Animations;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;

// https://github.com/mkpaz/atlantafx/releases
public class MainController {

    @FXML private AnchorPane contentPane;
    @FXML private Button dashboardButton;
    @FXML private Button documentButton;
    @FXML private Button userButton;
    @FXML private Button transactionButton;

    @FXML private Label currentTabLabel;

    @FXML private ModalPane modalPane;


    @FXML
    public void initialize() {
        modalPane.displayProperty().addListener((obs, old, val) -> {
            if (!val) {
                modalPane.setAlignment(Pos.CENTER);
                modalPane.usePredefinedTransitionFactories(null);
            }
        });

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
        currentTabLabel.setText("Dashboard");
        loadContent("/com/library/views/Dashboard.fxml", null);
    }

    private void handleDocumentButton() {
        selectSidebarButton(documentButton);
        currentTabLabel.setText("Documents");
        loadContent("/com/library/views/DocumentsView.fxml", null);
    }

    private void handleUserButton() {
        selectSidebarButton(userButton);
        currentTabLabel.setText("Users");
        loadContent("/com/library/views/UsersView.fxml", null);
    }

    private void handleTransactionButton() {
        selectSidebarButton(transactionButton);
        currentTabLabel.setText("Transactions");
        loadContent("/com/library/views/TransactionsView.fxml", null);
    }

        // Helper method to load the content dynamically
    private void loadContent(String fxmlFile, Object controller) {
        try {
            
            // Start fade-out animation immediately
            if (!contentPane.getChildren().isEmpty()) {
                var fadeOutAnimation = Animations.fadeOut(contentPane.getChildren().get(0), Duration.seconds(0.1));
                fadeOutAnimation.playFromStart();

                // Set up the fade-in animation to play once fade-out completes and the content is loaded
                fadeOutAnimation.setOnFinished(event -> {
                    contentPane.getChildren().clear();
                    loadAndFadeInNewContent(fxmlFile, controller);
                });
            } else {
                // If no content exists, directly load and fade in the new content
                loadAndFadeInNewContent(fxmlFile, controller);
            }
        } catch (Exception e) {
            // Log the exception and show an error message to the user
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // Helper method to load new content and fade it in
    private void loadAndFadeInNewContent(String fxmlFile, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (controller != null) {
                loader.setController(controller);
            }
            Region loadedPane = loader.load();
            loadedPane.prefWidthProperty().bind(contentPane.widthProperty());
            loadedPane.prefHeightProperty().bind(contentPane.heightProperty());
            AnchorPane.setTopAnchor(loadedPane, 0.0);
            AnchorPane.setBottomAnchor(loadedPane, 0.0);
            AnchorPane.setLeftAnchor(loadedPane, 0.0);
            AnchorPane.setRightAnchor(loadedPane, 0.0);

            contentPane.getChildren().add(loadedPane);

            BaseViewController loaderController = loader.getController();
            loaderController.setMainController(this);

            // Fade in the new content
            // Animations.fadeIn(loadedPane, Duration.seconds(0.25)).playFromStart();
        } catch (IOException e) {
            // Log the exception and show an error message to the user
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void showDialog(String fxmlPath, Runnable onClose, Object controller) {
        loadContent(fxmlPath, controller);
        onClose.run();
    }

}


