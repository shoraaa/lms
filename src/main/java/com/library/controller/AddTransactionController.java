package com.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.model.Document;
import com.library.model.User;
import com.library.services.DocumentDAO;
import com.library.services.TransactionService;
import com.library.services.UserDAO;
import com.library.util.AutoCompletionTextField;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
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

    private List<Document> documents;
    private List<User> users;

    @FXML
    public void initialize() {
        initializeForm();
        initializeAutoCompletionFields();
        setUpEventListeners();
    }

    private void initializeForm() {
        borrowDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        documents = DocumentDAO.getInstance().getAllDocuments();
        users = UserDAO.getInstance().getAllUsers();
    }

    private void initializeAutoCompletionFields() {
        List<String> documentNames = documents.stream().map(Document::getTitle).collect(Collectors.toList());
        List<String> documentISBNs = documents.stream().map(Document::getIsbn).collect(Collectors.toList());
        new AutoCompletionTextField(documentTitleTextFIeld, documentNames);
        new AutoCompletionTextField(isbnTextField, documentISBNs);

        List<String> userNames = users.stream().map(User::getName).collect(Collectors.toList());
        List<String> userIds = users.stream().map(user -> String.valueOf(user.getUserId())).collect(Collectors.toList());
        new AutoCompletionTextField(userNameTextField, userNames);
        new AutoCompletionTextField(userIdTextField, userIds);
    }

    private void setUpEventListeners() {
        setUpDocumentFieldsListeners();
        setUpUserFieldsListeners();
        saveButton.setOnAction(event -> saveNewTransaction());
    }

    private void setUpDocumentFieldsListeners() {
        documentTitleTextFIeld.textProperty().addListener((observable, oldValue, newValue) -> {
            handleDocumentFieldChange(newValue, documentTitleTextFIeld, isbnTextField);
        });

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleDocumentFieldChange(newValue, isbnTextField, documentTitleTextFIeld);
        });
    }

    private void handleDocumentFieldChange(String newValue, CustomTextField activeField, CustomTextField otherField) {
        Document document = findDocumentByField(newValue);
        if (document != null) {
            updateDocumentFields(document, activeField, otherField);
        } else {
            clearDocumentFields(activeField, otherField);
        }
    }

    private Document findDocumentByField(String value) {
        return documents.stream()
                .filter(d -> d.getTitle().equals(value) || d.getIsbn().equals(value))
                .findFirst()
                .orElse(null);
    }

    private void updateDocumentFields(Document document, CustomTextField activeField, CustomTextField otherField) {
        activeField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        activeField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        otherField.setText(document.getIsbn());
        updateDocumentImage(document);
    }

    private void clearDocumentFields(CustomTextField activeField, CustomTextField otherField) {
        activeField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
        activeField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        otherField.clear();
    }

    private void updateDocumentImage(Document document) {
        if (document.getImageUrl() != null) {
            documentImageView.setImage(new Image(document.getImageUrl()));
        }
    }

    private void setUpUserFieldsListeners() {
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleUserFieldChange(newValue, userNameTextField, userIdTextField);
        });

        userIdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleUserFieldChange(newValue, userIdTextField, userNameTextField);
        });
    }

    private void handleUserFieldChange(String newValue, CustomTextField activeField, CustomTextField otherField) {
        User user = findUserByField(newValue);
        if (user != null) {
            updateUserFields(user, activeField, otherField);
        } else {
            clearUserFields(activeField, otherField);
        }
    }

    private User findUserByField(String value) {
        return users.stream()
                .filter(u -> u.getName().equals(value) || String.valueOf(u.getUserId()).equals(value))
                .findFirst()
                .orElse(null);
    }

    private void updateUserFields(User user, CustomTextField activeField, CustomTextField otherField) {
        activeField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        activeField.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        otherField.setText(String.valueOf(user.getUserId()));
    }

    private void clearUserFields(CustomTextField activeField, CustomTextField otherField) {
        activeField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
        activeField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        otherField.clear();
    }

    private void saveNewTransaction() {
        String documentTitle = documentTitleTextFIeld.getText();
        String isbn = isbnTextField.getText();
        String userName = userNameTextField.getText();
        String userId = userIdTextField.getText();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (validateInputs(documentTitle, isbn, userName, userId, borrowDate, dueDate)) {
            int documentId = DocumentDAO.getInstance().getDocumentsByTitle(documentTitle).get(0).getDocumentId();
            int userIdInt = Integer.parseInt(userId);
            boolean success = TransactionService.getInstance().borrowDocument(userIdInt, documentId, borrowDate, dueDate);

            showTransactionResult(success);
        }
    }

    private boolean validateInputs(String documentTitle, String isbn, String userName, String userId, LocalDate borrowDate, LocalDate dueDate) {
        if (documentTitle.isEmpty() || isbn.isEmpty() || userName.isEmpty() || userId.isEmpty() || borrowDate == null || dueDate == null) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showTransactionResult(boolean success) {
        if (success) {
            showAlert("Success", "Transaction has been created successfully!", Alert.AlertType.INFORMATION);
            clearForm();
        } else {
            showAlert("Error", "The book is not available!", Alert.AlertType.ERROR);
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
}
