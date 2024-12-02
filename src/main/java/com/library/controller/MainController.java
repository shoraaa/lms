package com.library.controller;

import java.io.IOException;
import java.util.Optional;

import com.library.util.Localization;
import com.library.util.PaneNavigator;
import com.library.util.SceneNavigator;
import com.library.util.UserSession;

import atlantafx.base.controls.Tile;
import atlantafx.base.util.Animations;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

    @FXML private ScrollPane contentPane;  // Change from AnchorPane to ScrollPane
    @FXML private Button dashboardButton;
    @FXML private Button documentButton;
    @FXML private Button userButton;
    @FXML private Button transactionButton;
    @FXML private Button chatButton;
    @FXML private Button accountButton;
    @FXML private Button logoutButton;
    @FXML private Button settingButton;
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
    }

    private void initializeButtonActions() {
        dashboardButton.setOnAction(event -> handleDashboardButton());
        documentButton.setOnAction(event -> handleDocumentButton());
        userButton.setOnAction(event -> handleUserButton());
        transactionButton.setOnAction(event -> handleTransactionButton());
        chatButton.setOnAction(event -> handleChatButton());
        settingButton.setOnAction(event -> handleSettingButton());
        accountButton.setOnAction(event -> handleAccountButton());

        logoutButton.setOnAction(event -> {
            UserSession.clearSession();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setMaximized(false);
            SceneNavigator.setRoot("/com/library/views/Login", Optional.empty());
        });
    }

    private void selectSidebarButton(Button selectedButton) {
        removeSelectedStyleFromButtons();
        selectedButton.getStyleClass().add("selected");
    }

    private void removeSelectedStyleFromButtons() {
        dashboardButton.getStyleClass().remove("selected");
        documentButton.getStyleClass().remove("selected");
        userButton.getStyleClass().remove("selected");
        transactionButton.getStyleClass().remove("selected");
        chatButton.getStyleClass().remove("selected");
        settingButton.getStyleClass().remove("selected");
        accountButton.getStyleClass().remove("selected");
    }

    private void handleChatButton() {
        handleTabSelection(chatButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.chat"));
        loadContent("/com/library/views/ChatView.fxml", null);
    }

    private void handleDashboardButton() {
        handleTabSelection(dashboardButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.dashboard"));
        loadContent("/com/library/views/Dashboard.fxml", null);
    }

    public void handleDocumentButton() {
        handleTabSelection(documentButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.books"));
        loadContent("/com/library/views/DocumentsView.fxml", null);
    }

    public void handleUserButton() {
        handleTabSelection(userButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.users"));
        loadContent("/com/library/views/UsersView.fxml", null);
    }

    private void handleTransactionButton() {
        handleTabSelection(transactionButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.transactions"));
        loadContent("/com/library/views/TransactionsView.fxml", null);
    }

    public void handleSettingButton() {
        handleTabSelection(settingButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.setting"));
        SettingController controller = new SettingController();
        controller.setMainController(this);
        loadContent("/com/library/views/Setting.fxml", controller);
    }

    private void handleAccountButton() {
        handleTabSelection(accountButton);
        currentTabLabel.setText(Localization.getInstance().getString("sidebar.account"));
        AccountController controller = new AccountController();
        controller.setMainController(this);
        loadContent("/com/library/views/Account.fxml", null);
    }

    public void reloadCurrentTab() {
        Button selectedButton = null;
    
        for (Button button : new Button[] { dashboardButton, documentButton, userButton, transactionButton, chatButton, settingButton, accountButton }) {
            if (button.getStyleClass().contains("selected")) {
                selectedButton = button;
                break;
            }
        }
    
        if (selectedButton != null) {
            if (selectedButton == dashboardButton) handleDashboardButton();
            else if (selectedButton == documentButton) handleDocumentButton();
            else if (selectedButton == userButton) handleUserButton();
            else if (selectedButton == transactionButton) handleTransactionButton();
            else if (selectedButton == chatButton) handleChatButton();
            else if (selectedButton == settingButton) handleSettingButton();
            else if (selectedButton == accountButton) handleAccountButton();
        }
    }

    private void handleTabSelection(Button button) {
        selectSidebarButton(button);
    }

    private void loadContent(String fxmlFile, Object controller) {
        try {
            if (contentPane.getContent() != null) {
                var fadeOutAnimation = Animations.fadeOut(contentPane.getContent(), Duration.seconds(0.1));
                fadeOutAnimation.playFromStart();

                fadeOutAnimation.setOnFinished(event -> {
                    contentPane.setContent(null);
                    loadAndFadeInNewContent(fxmlFile, controller);
                });
            } else {
                loadAndFadeInNewContent(fxmlFile, controller);
            }
        } catch (Exception e) {
            logError("Error loading FXML file: " + fxmlFile, e);
        }
    }

    private void loadAndFadeInNewContent(String fxmlFile, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (controller != null) loader.setController(controller);
            loader.setResources(Localization.getInstance().getResourceBundle());
            Region loadedPane = loader.load();
            loadedPane.prefWidthProperty().bind(contentPane.widthProperty());
            loadedPane.prefHeightProperty().bind(contentPane.heightProperty());
            contentPane.setContent(loadedPane);

            BaseController loaderController = loader.getController();
            loaderController.setMainController(this);

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
        if (onClose != null) onClose.run();
    }
}
