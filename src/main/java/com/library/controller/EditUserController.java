package com.library.controller;

import java.util.Optional;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import com.library.model.User;
import com.library.services.UserDAO;
import com.library.util.Localization;
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

public class EditUserController extends BaseController {

    @FXML private CustomTextField surNameTextField;
    @FXML private CustomTextField firstNameTextField;
    @FXML private CustomTextField phoneNumberTextField;
    @FXML private CustomTextField emailTextField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    
    @FXML private PasswordTextField passwordTextField;
    @FXML private Button editButton;
    @FXML private Button cancelButton;
    private boolean isEditing = false;

    private User user;

    public EditUserController(User user) {
        System.out.println(user.getUserId() + " " + user.getFirstName() + " " + user.getSurName() + " " + user.getPhoneNumber() + " " + user.getEmail() + " " + user.getRole()); 
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
        editButton.setOnAction(event -> handleEditOrSave());

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

    // Handle registration action
    private void handleEditOrSave() {
        if (isEditing) {
            saveUser();
        } else {
            isEditing = true;
            editButton.setText(Localization.getInstance().getString("save"));
            enableFields();
        }
    }

    private void enableFields() {
        surNameTextField.setDisable(false);
        firstNameTextField.setDisable(false);
        phoneNumberTextField.setDisable(false);
        emailTextField.setDisable(false);
        roleChoiceBox.setDisable(false);
        passwordTextField.setDisable(false);
    }

    private void saveUser() {
        String surName = surNameTextField.getText();
        String firstName = firstNameTextField.getText();

        String username = firstNameTextField.getText().trim() + " " + surNameTextField.getText().trim();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();
        String role = roleChoiceBox.getValue();
        String password = passwordTextField.getPassword();

        if (surName.isEmpty() || firstName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Edit User Failed", "All fields are required.");
            return;
        }

        User editedUser = new User(username, phoneNumber, email);
        editedUser.setRole(role);
        if (!password.isEmpty()) editedUser.setPasswordHash(PasswordUtil.hashPassword(password));
        else editedUser.setPasswordHash(user.getPasswordHash());
        editedUser.setUserId(user.getUserId());

        UserDAO userDAO = UserDAO.getInstance();
        userDAO.deleteUser(user.getUserId());
        userDAO.add(editedUser);

        showAlert(AlertType.INFORMATION, "Edit User", "User details updated successfully.");
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
