package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.Label;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import com.library.model.document.Book;
import com.library.model.user.User;

public class DashboardController {

    @FXML private VBox mainContent; // Reference to the VBox in the center of the BorderPane
    @FXML private Button booksButton;
    @FXML private Button usersButton;
    @FXML private Button transactionsButton;

    @FXML
    public void initialize() {
        // Set event handlers for buttons
        booksButton.setOnAction(event -> loadBooksView());
        usersButton.setOnAction(event -> loadUsersView());
        transactionsButton.setOnAction(event -> loadTransactionsView());
    }

    private void loadAddNewBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddBookWindow.fxml"));
            Parent root = loader.load();
            Scene addBookScene = new Scene(root);
            Stage addBookStage = new Stage();
            addBookStage.setScene(addBookScene);
            addBookStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load Books view
    private void loadBooksView() {
        // Clear the main content
        mainContent.getChildren().clear();

        Button addNewBookButton = new Button("Add New Book");
        mainContent.getChildren().add(addNewBookButton);

        addNewBookButton.setOnAction(event -> loadAddNewBookView());
        
        // Create and display the books content (e.g., a TableView for books)
        TableView<Book> booksTable = new TableView<>();
        // Setup columns and data for booksTable (assuming Book class and columns are already defined)
        // booksTable.getColumns().addAll(createBookColumns());
        
        mainContent.getChildren().add(booksTable);
    }


    // Method to load Users view
    private void loadUsersView() {
        // Clear the main content
        mainContent.getChildren().clear();
        
        // Create and display the users content (e.g., a TableView for users)
        TableView<User> usersTable = new TableView<>();
        // Setup columns and data for usersTable (assuming User class and columns are already defined)
        // usersTable.getColumns().addAll(createUserColumns());
        
        mainContent.getChildren().add(usersTable);
    }

    // Method to load Transactions view
    private void loadTransactionsView() {
        // Clear the main content
        mainContent.getChildren().clear();
        
        // Create and display the transactions content (e.g., a TableView for transactions)
        // TableView<Transaction> transactionsTable = new TableView<>();
        // Setup columns and data for transactionsTable (assuming Transaction class and columns are already defined)
        // transactionsTable.getColumns().addAll(createTransactionColumns());
        
        // mainContent.getChildren().add(transactionsTable);
    }
}
