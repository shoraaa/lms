package com.library.controller;

import java.time.LocalDate;
import java.util.List;

import com.library.model.Document;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;
import com.library.util.AutoCompletionTextField;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;

public class AddTransactionController extends BaseViewController {
    @FXML ImageView documentImageView;
    @FXML CustomTextField documentTitleTextFIeld;
    @FXML CustomTextField isbnTextField;
    @FXML CustomTextField userNameTextField;
    @FXML CustomTextField userIdTextField;
    @FXML DatePicker borrowDatePicker;
    @FXML DatePicker dueDatePicker;
    @FXML Button saveButton, cancelButton;

    @FXML
    public void initialize() {

        borrowDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        List<Document> documents = DocumentDAO.getInstance().getAllDocuments();
        List<String> documentName = documents.stream().map(Document::getTitle).collect(java.util.stream.Collectors.toList());
        List<String> documentISBNs = documents.stream().map(Document::getIsbn).collect(java.util.stream.Collectors.toList());
        new AutoCompletionTextField(documentTitleTextFIeld, documentName);
        new AutoCompletionTextField(isbnTextField, documentISBNs);

        documentTitleTextFIeld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (documentName.contains(newValue)) {
                Document document = documents.stream().filter(d -> d.getTitle().equals(newValue)).findFirst().get();
                isbnTextField.setText(document.getIsbn());
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
            } else {
                isbnTextField.clear();
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (documentISBNs.contains(newValue)) {
                Document document = documents.stream().filter(d -> d.getIsbn().equals(newValue)).findFirst().get();
                documentTitleTextFIeld.setText(document.getTitle());
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
            } else {
                documentTitleTextFIeld.clear();
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                documentTitleTextFIeld.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                isbnTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        List<User> users = UserDAO.getInstance().getAllUsers();
        List<String> userName = users.stream().map(User::getName).collect(java.util.stream.Collectors.toList());
        List<String> userId = users.stream().map(user -> String.valueOf(user.getUserId())).collect(java.util.stream.Collectors.toList());
        new AutoCompletionTextField(userNameTextField, userName);
        new AutoCompletionTextField(userIdTextField, userId);

        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (userName.contains(newValue)) {
                User user = users.stream().filter(d -> d.getName().equals(newValue)).findFirst().get();
                userIdTextField.setText(String.valueOf(user.getUserId()));
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                userNameTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
            } else {
                userIdTextField.clear();
                userNameTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        userIdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (userId.contains(newValue)) {
                User user = users.stream().filter(d -> d.getUserId() == Integer.parseInt(newValue)).findFirst().get();
                userNameTextField.setText(user.getName());
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                userNameTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
            } else {
                userNameTextField.clear();
                userNameTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                userIdTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        saveButton.setOnAction(event -> saveNewTransaction());
        // cancelButton.setOnAction(event -> closeWindow());
    }

    private void saveNewTransaction() {
        // Get the values from the form
        String documentTitle = documentTitleTextFIeld.getText();
        String isbn = isbnTextField.getText();
        String userName = userNameTextField.getText();
        String userId = userIdTextField.getText();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        // Validate inputs
        if (documentTitle.isEmpty() || isbn.isEmpty() || userName.isEmpty() || userId.isEmpty() || borrowDate == null || dueDate == null) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        // Save the transaction to the database
        TransactionDAO transactionDAO = TransactionDAO.getInstance();
        int documentId = DocumentDAO.getInstance().getDocumentsByTitle(documentTitle).get(0).getDocumentId();
        int transactionId = transactionDAO.add(new Transaction(Integer.parseInt(userId), documentId, borrowDate, dueDate));

        if (transactionId > -1) {
            showAlert("Success", "Transaction has been created successfully!", Alert.AlertType.INFORMATION);
            clearForm();  // Clear the form after successful save
        } else {
            showAlert("Error", "There was an issue adding the transaction.", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        documentTitleTextFIeld.clear();
        isbnTextField.clear();
        userNameTextField.clear();
        userIdTextField.clear();
        borrowDatePicker.setValue(null);
        dueDatePicker.setValue(null);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
