package com.library.controller;

import java.util.Optional;

import com.library.util.SceneNavigator;

import javafx.scene.Parent;
import javafx.scene.control.Alert;

public abstract class BaseController {

    protected MainController mainController;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public MainController getMainController() {
        return mainController;
    }

    protected void setRoot(String fxml, Optional<Object> controller) {
        SceneNavigator.setRoot(fxml, controller);
    }

    protected Parent loadFXML(String fxml, Optional<Object> controller) {
        return SceneNavigator.loadFXML(fxml, controller);
    }

    protected void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
