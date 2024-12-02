package com.library.controller;

import java.util.Optional;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.PasswordUtil; // Assuming PasswordUtil is a utility class for password hashing
import com.library.util.SceneNavigator;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class RegisterController {

    @FXML private CustomTextField surNameTextField;
    @FXML private CustomTextField firstNameTextField;
    @FXML private CustomTextField phoneNumberTextField;
    @FXML private CustomTextField emailTextField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    
    @FXML private PasswordTextField passwordTextField, repeatPasswordTextField;
    @FXML private Button backButton;
    @FXML private Button registerButton;

    @FXML public void initialize() {
        roleChoiceBox.getItems().addAll("Admin", "Librarian", "Member");
        roleChoiceBox.setValue("Member");

        backButton.setOnAction(event -> handleBack());
        registerButton.setOnAction(event -> handleRegister());

        addIconToPasswordField(passwordTextField);
        addIconToPasswordField(repeatPasswordTextField);
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
        SceneNavigator.setRoot("/com/library/views/Login", Optional.empty());
    }

    // Handle registration action
    private void handleRegister() {
        String username = firstNameTextField.getText().trim() + "-" + surNameTextField.getText().trim();
        String password = passwordTextField.getPassword();
        String repeatPassword = repeatPasswordTextField.getPassword();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();
        String role = roleChoiceBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Registration Failed", "Username and Password cannot be empty.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            showAlert(AlertType.ERROR, "Registration Failed", "Passwords do not match.");
            return;
        }

        // Check if the user already exists
        UserDAO userDAO = UserDAO.getInstance();
        User existingUser = userDAO.getUsersByName(username).stream().findFirst().orElse(null);

        if (existingUser != null) {
            showAlert(AlertType.ERROR, "Registration Failed", "Username is already taken.");
            return;
        }


        // Create a new user and save it to the database
        User user = new User(username, email, phoneNumber); 
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(role);

        int userId = userDAO.add(user);

        if (userId > 0) {
            showAlert(AlertType.INFORMATION, "Registration Successful", "User has been registered successfully.");
            // Optionally, auto-login after registration or navigate to login screen
        } else {
            showAlert(AlertType.ERROR, "Registration Failed", "Something went wrong. Please try again.");
        }
    }

    // Helper method to show alerts
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
