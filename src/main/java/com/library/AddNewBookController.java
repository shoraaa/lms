package com.library;

import java.time.LocalDate;
import java.util.ArrayList;

import com.library.model.document.RawDocumentInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewBookController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private Label statusLabel;

    @FXML
    private void handleAddBook() {

        String title;
        String author;
        String isbn;
        String publisher;
        String year;

        if (titleField != null) {
            title = titleField.getText().trim();
        } else {
            title = "";
        }

        if (authorField != null) {
            author = authorField.getText().trim();
        } else {
            author = "";
        }

        if (isbnField != null) {
            isbn = isbnField.getText().trim();
        } else {
            isbn = "";
        }

        if (publisherField != null) {
            publisher = publisherField.getText().trim();
        } else {
            publisher = "";
        }

        if (yearField != null) {
            year = yearField.getText().trim();
        } else {
            year = "";
        }

        System.out.println(title + " " + author + " " + isbn + " " + publisher + " " + year);

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
        } else {
            // Add the book to the database or perform necessary actions
            statusLabel.setText("Book added successfully!");
            statusLabel.setStyle("-fx-text-fill: green;");

            RawDocumentInfo rawDocumentInfo = new RawDocumentInfo();
            rawDocumentInfo.name = title;
            rawDocumentInfo.authorIds = new ArrayList<>();
            rawDocumentInfo.tagIds = new ArrayList<>();
            rawDocumentInfo.isbn_10 = isbn;
            rawDocumentInfo.isbn_13 = isbn;
            rawDocumentInfo.publishedDate = LocalDate.now();
            rawDocumentInfo.publisher = publisher;
            App.getDocumentManager().registerDocument(rawDocumentInfo);

            
            // Close the window (if desired)
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        // Close the window without adding the book
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}
