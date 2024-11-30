package com.library.controller;

import com.library.App;
import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.PasswordUtil; // Assuming PasswordUtil is a utility class for password hashing

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

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
        String username = usernameTextField.getText();
        String password = passwordTextField.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Username and Password cannot be empty.");
            return;
        }

        // Retrieve the user by username
        UserDAO userDAO = UserDAO.getInstance();
        User user = userDAO.getUsersByName(username).stream().findFirst().orElse(null);

        if (user == null) {
            showAlert(AlertType.ERROR, "Login Failed", "User not found.");
            return;
        }

        // System.out.println(user.getName() + " " + password + " " + user.getPasswordHash());

        // Verify the entered password against the stored hash
        if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            // Successfully logged in, navigate to the main screen
            App.setRoot("/com/library/views/Main", new MainController(user));
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Incorrect password.");
        }
    }

    // Handle registration action
    private void handleRegister() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Registration Failed", "Username and Password cannot be empty.");
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
        User user = new User(username, "", ""); // Add email, phone, etc., as needed
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        // System.out.println(user.getName() + " " + password + " " + user.getPasswordHash());

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
