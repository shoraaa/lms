package com.library.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class SettingController extends BaseController {
    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private ChoiceBox<String> languageChoiceBox;

    Map<String, String> themeMap = new HashMap<>();

    @FXML public void initialize() {
        themeMap.put("Primer Light", new PrimerLight().getUserAgentStylesheet());
        themeMap.put("Primer Dark", new PrimerDark().getUserAgentStylesheet());
        themeMap.put("Cupertino Light", new CupertinoLight().getUserAgentStylesheet());
        themeMap.put("Cupertino Dark", new CupertinoDark().getUserAgentStylesheet());
        themeMap.put("Nord Light", new NordLight().getUserAgentStylesheet());
        themeMap.put("Nord Dark", new NordDark().getUserAgentStylesheet());
        themeMap.put("Dracula", new Dracula().getUserAgentStylesheet());

        List<String> sortedThemes = new ArrayList<>(themeMap.keySet());
        Collections.sort(sortedThemes);
        themeChoiceBox.getItems().addAll(sortedThemes);
        themeChoiceBox.setValue(getCurrentTheme());

        languageChoiceBox.getItems().addAll("English", "Việt Nam");
        languageChoiceBox.setValue("English");

        themeChoiceBox.setOnAction(event -> handleThemeChange());
        languageChoiceBox.setOnAction(event -> handleLanguageChange());

    }

    private String getCurrentTheme() {
        String currentTheme = Application.getUserAgentStylesheet();
        for (Map.Entry<String, String> entry : themeMap.entrySet()) {
            if (entry.getValue().equals(currentTheme)) {
                return entry.getKey();
            }
        }
        return "Primer Light";
    }

    private void handleThemeChange() {
        String theme = themeChoiceBox.getValue();
        Application.setUserAgentStylesheet(themeMap.get(theme));
        
    }

    private void handleLanguageChange() {
        String language = languageChoiceBox.getValue();
        System.out.println("Language changed to: " + language);
    }
}
