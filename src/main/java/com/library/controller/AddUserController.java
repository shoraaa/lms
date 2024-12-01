package com.library.controller;

import com.library.model.User;
import com.library.services.UserDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddUserController extends BaseViewController {

    @FXML private TextField userNameTextField;
    @FXML private TextField userPhoneNumberTextField;
    @FXML private TextField userEmailTextField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // This method will be called after the FXML is loaded
    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveNewUser());
        cancelButton.setOnAction(event -> closeWindow());
    }

    // Method to save the new book to the database
    private void saveNewUser() {

        String userName = userNameTextField.getText();
        String userEmail = userEmailTextField.getText();
        String userPhoneNumber = userPhoneNumberTextField.getText();

        // Validate inputs
        if (userName.isEmpty() || userEmail.isEmpty() || userPhoneNumber.isEmpty()) {
            showAlert("Error", "All field are required!", Alert.AlertType.ERROR);
            return;
        }

        User user = new User(userName, userEmail, userPhoneNumber);

        UserDAO userDAO = UserDAO.getInstance();
        int userId = userDAO.add(user);

        if (userId > -1) {
            showAlert("Success", "User has been created successfully!", Alert.AlertType.INFORMATION);
            clearForm();  // Clear the form after successful save
        } else {
            showAlert("Error", "There was an issue adding the book.", Alert.AlertType.ERROR);
        }


    }

    public void clearForm() {
        userNameTextField.clear();
        userEmailTextField.clear();
        userPhoneNumberTextField.clear();
    }

    // Method to close the window
    public void closeWindow() {
        clearForm();
        saveButton.getScene().getWindow().hide();
    }
}
