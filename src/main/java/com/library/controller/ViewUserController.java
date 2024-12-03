package com.library.controller;

import java.util.Optional;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.Localization;
import com.library.util.PasswordUtil; // Assuming PasswordUtil is a utility class for password hashing
import com.library.util.SceneNavigator;
import com.library.util.UserSession;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class ViewUserController extends BaseController {

    @FXML private CustomTextField surNameTextField;
    @FXML private CustomTextField firstNameTextField;
    @FXML private CustomTextField phoneNumberTextField;
    @FXML private CustomTextField emailTextField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    
    @FXML private PasswordTextField passwordTextField;
    @FXML private Button cancelButton;

    private User user;

    public ViewUserController(User user) {
        this.user = user;
    }

    @FXML public void initialize() {
        firstNameTextField.setText(user.getFirstName());
        surNameTextField.setText(user.getSurName());
        phoneNumberTextField.setText(user.getPhoneNumber());
        emailTextField.setText(user.getEmail());

        roleChoiceBox.getItems().addAll("Admin", "Librarian", "Member");
        roleChoiceBox.setValue(user.getRole());

        cancelButton.setOnAction(event -> handleBack());

        addIconToPasswordField(passwordTextField);

    }

    private void addIconToPasswordField(PasswordTextField passwordField) {
        FontIcon icon = new FontIcon(Feather.EYE_OFF);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(event -> {
            icon.setIconCode(passwordField.getRevealPassword() ? Feather.EYE_OFF : Feather.EYE);
            passwordField.setRevealPassword(!passwordField.getRevealPassword());
        });
        passwordField.setRight(icon);
    }

    private void handleBack() {
        mainController.reloadCurrentTab();
    }

}
