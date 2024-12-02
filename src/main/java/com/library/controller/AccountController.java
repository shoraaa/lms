package com.library.controller;

import com.library.util.Localization;
import com.library.util.ThemeNavigator;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class AccountController extends BaseController {

    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private ChoiceBox<String> languageChoiceBox;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML public void initialize() {
        // Initialize theme options using ThemeNavigator
        ThemeNavigator themeNavigator = ThemeNavigator.getInstance();
        themeChoiceBox.getItems().addAll(themeNavigator.getAvailableThemes());
        themeChoiceBox.setValue(themeNavigator.getCurrentTheme());

        // Language options
        languageChoiceBox.getItems().addAll(Localization.getInstance().getAvailableLanguages());
        languageChoiceBox.setValue(Localization.getInstance().getCurrentLanguage());

        // Event handlers for theme and language changes
        themeChoiceBox.setOnAction(event -> handleThemeChange());
        languageChoiceBox.setOnAction(event -> handleLanguageChange());
    }

    /**
     * Handles the theme change event.
     */
    private void handleThemeChange() {
        String theme = themeChoiceBox.getValue();
        ThemeNavigator.getInstance().switchTheme(theme);
    }

    /**
     * Handles the language change event.
     */
    private void handleLanguageChange() {
        String language = languageChoiceBox.getValue();
        Localization.getInstance().setLanguage(language);
        this.mainController.handleSettingButton();
    }
}
