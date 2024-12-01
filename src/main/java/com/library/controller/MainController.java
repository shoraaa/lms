package com.library.controller;

import java.io.IOException;
import java.lang.ModuleLayer.Controller;
import java.util.Optional;

import com.library.util.Localization;
import com.library.util.PaneNavigator;
import com.library.util.SceneNavigator;
import com.library.util.UserSession;

import atlantafx.base.controls.Tile;
import atlantafx.base.util.Animations;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private Button chatButton;
    @FXML private Button logoutButton;
    @FXML private Button notificationButton;
    @FXML private Tile accountTile;

    @FXML private Label currentTabLabel;

    @FXML
    public void initialize() {
        PaneNavigator.setContent(contentPane);
        
        initializeAccountTile();
        initializeButtonActions();
        handleDashboardButton();  // Default to Dashboard
    }

    private void initializeAccountTile() {
        accountTile.setTitle(UserSession.getUser().getName());
        accountTile.setDescription(UserSession.getUser().getRole());

        boolean isAdmin = UserSession.getUser().getRole().equals("Admin");
        userButton.setDisable(!isAdmin);

        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/com/library/assets/user.png")));
        img.setFitWidth(50);
        img.setFitHeight(50);
        accountTile.setGraphic(img);
    }

    private void initializeButtonActions() {
        dashboardButton.setOnAction(event -> handleDashboardButton());
        documentButton.setOnAction(event -> handleDocumentButton());
        userButton.setOnAction(event -> handleUserButton());
        transactionButton.setOnAction(event -> handleTransactionButton());
        chatButton.setOnAction(event -> handleChatButton());

        logoutButton.setOnAction(event -> {
            UserSession.clearSession();
            SceneNavigator.setRoot("/com/library/views/Login", Optional.empty());
        });
    }

    private void selectSidebarButton(Button selectedButton) {
        // Clear the 'selected' style class from all buttons
        removeSelectedStyleFromButtons();

        // Add the 'selected' style class to the clicked button
        selectedButton.getStyleClass().add("selected");
    }

    private void removeSelectedStyleFromButtons() {
        dashboardButton.getStyleClass().remove("selected");
        documentButton.getStyleClass().remove("selected");
        userButton.getStyleClass().remove("selected");
        transactionButton.getStyleClass().remove("selected");
        chatButton.getStyleClass().remove("selected");
    }

    private void handleChatButton() {
        handleTabSelection(chatButton);
        loadContent("/com/library/views/ChatView.fxml", null);
    }

    private void handleDashboardButton() {
        handleTabSelection(dashboardButton);
        loadContent("/com/library/views/Dashboard.fxml", null);
    }

    private void handleDocumentButton() {
        handleTabSelection(documentButton);
        loadContent("/com/library/views/DocumentsView.fxml", null);
    }

    private void handleUserButton() {
        handleTabSelection(userButton);
        loadContent("/com/library/views/UsersView.fxml", null);
    }

    private void handleTransactionButton() {
        handleTabSelection(transactionButton);
        loadContent("/com/library/views/TransactionsView.fxml", null);
    }

    private void handleTabSelection(Button button) {
        selectSidebarButton(button);
        currentTabLabel.setText(button.getText());
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
            logError("Error loading FXML file: " + fxmlFile, e);
        }
    }

    // Helper method to load new content and fade it in
    private void loadAndFadeInNewContent(String fxmlFile, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (controller != null) {
                loader.setController(controller);
            }
            loader.setResources(Localization.getInstance().getResourceBundle());
            Region loadedPane = loader.load();
            loadedPane.prefWidthProperty().bind(contentPane.widthProperty());
            loadedPane.prefHeightProperty().bind(contentPane.heightProperty());
            AnchorPane.setTopAnchor(loadedPane, 0.0);
            AnchorPane.setBottomAnchor(loadedPane, 0.0);
            AnchorPane.setLeftAnchor(loadedPane, 0.0);
            AnchorPane.setRightAnchor(loadedPane, 0.0);

            contentPane.getChildren().add(loadedPane);

            BaseController loaderController = loader.getController();
            loaderController.setMainController(this);

            // Fade in the new content
            Animations.fadeIn(loadedPane, Duration.seconds(0.25)).playFromStart();
        } catch (IOException e) {
            logError("Error loading FXML file: " + fxmlFile, e);
        }
    }

    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }

    public void showDialog(String fxmlPath, Runnable onClose, Object controller) {
        loadContent(fxmlPath, controller);
        onClose.run();
    }
}
