package com.library.controller;

import java.util.ResourceBundle;

import com.library.App;
import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.Localization;
import com.library.util.PasswordUtil; // Assuming PasswordUtil is a utility class for password hashing
import com.library.util.UserSession;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoginController {


    @FXML private CustomTextField usernameTextField;
    @FXML private PasswordTextField passwordTextField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> handleRegister());
    }


    // Handle login action
    private void handleLogin() {
        String identifier = usernameTextField.getText();
        String password = passwordTextField.getPassword();

        if (identifier.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Email/Phone number and Password cannot be empty.");
            return;
        }

        // Retrieve the user by email or phone number
        UserDAO userDAO = UserDAO.getInstance();
        User user = userDAO.getUserByEmailOrPhone(identifier);

        if (user == null) {
            showAlert(AlertType.ERROR, "Login Failed", "User not found.");
            return;
        }
        // System.out.println(user.getName() + " " + password + " " + user.getPasswordHash());

        // Verify the entered password against the stored hash
        if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            // Successfully logged in, navigate to the main screen
            UserSession.setUser(user);
            App.setRoot("/com/library/views/Main", null);
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Incorrect password.");
        }
    }

    // Handle registration action
    private void handleRegister() {
        App.setRoot("/com/library/views/Register", null);
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
