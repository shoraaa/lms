package com.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.kordamp.ikonli.material2.Material2MZ;

import com.library.App;
import com.library.model.Document;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;
import com.library.view.DocumentTableView;
import com.library.view.UserTableView;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class DashboardController extends BaseViewController {

    @FXML private Label helloLabel;
    @FXML private Label dateTimeLabel;

    @FXML private Card newBookCard;
    @FXML private Card newUserCard;
    @FXML private Card borrowedBookCard;
    @FXML private Card returnedBookCard;
    @FXML private TableView<User> usersList;
    @FXML private TableView<Document> documentsList;

    @FXML private HBox popularDocumentHBox;

    private DocumentTableView documentTableView;
    private UserTableView userTableView;

    public void initialize() {
        helloLabel.setText("Hello, Admin");

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy | EEEE, h:mm a");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        dateTimeLabel.setText(formattedDateTime);

        setCardValue(newBookCard, "Total Books", DocumentDAO.getInstance().countAllDocuments(), Material2MZ.PERSON);
        setCardValue(newUserCard, "Total Users", UserDAO.getInstance().countAllUsers(), Material2MZ.PERSON);
        List<Transaction> transactions = TransactionDAO.getInstance().getAllTransactions();
        setCardValue(borrowedBookCard, "Borrowed Books", transactions.size(), Material2MZ.PERSON);
        setCardValue(returnedBookCard, "Returned Books", (int) transactions.stream().filter(Transaction::isReturned).count(), Material2MZ.PERSON);

        documentTableView = new DocumentTableView(documentsList);
        documentTableView.setParentController(this);
        documentTableView.removeColumn("Categories");
        documentTableView.removeColumn("Publisher");
        documentTableView.removeColumn("Publication Date");
        documentTableView.removeColumn("Registration Date");
        documentTableView.removeColumn("");
        documentTableView.removeColumn("Image");
        documentTableView.removeColumn("ISBN");
        documentTableView.loadData();

        userTableView = new UserTableView(usersList);
        userTableView.removeColumn("Registration Date");
        userTableView.removeColumn("");


        List<Transaction> mostBorrowedTransaction = TransactionDAO.getInstance().getMostBorrowedTransaction(5);
        List<Document> mostBorrowedBooks = mostBorrowedTransaction.stream()
                                            .map(transaction -> DocumentDAO.getInstance().getDocumentById(transaction.getDocumentId()))
                                            .collect(Collectors.toList());
        for (Document document : mostBorrowedBooks) {
            VBox vbox = new VBox();
            ImageView imageView = new ImageView(document.getImageUrl());
            imageView.setFitHeight(200);
            imageView.setFitWidth(160);
            Label titleLabel = new Label(document.getTitle());
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(160);
            String authors =  document.getAuthorIds().stream()
                                .map(authorId -> AuthorDAO.getInstance().getAuthorById(authorId).getName())
                                .collect(Collectors.joining(", "));
            Label authorLabel = new Label(authors);
            vbox.getChildren().addAll(imageView, titleLabel, authorLabel);
            vbox.setOnMouseClicked(event -> {
                // Handle the click event, e.g., show document details
                mainController.showDialog("/com/library/views/EditDocumentWindow.fxml", null, new EditDocumentController(document));
            });
            popularDocumentHBox.getChildren().add(vbox);
        }

    }

    private void setCardValue(Card card, String body, int header, Material2MZ icon) {
        Text headerText = new Text(String.valueOf(header));
        headerText.getStyleClass().addAll(Styles.TITLE_1);
        card.setHeader(headerText);

        Text bodyText = new Text(body);
        bodyText.getStyleClass().addAll(Styles.TITLE_3);
        card.setBody(bodyText);
    }

    private void handleAddNewDocument() {
        if (mainController == null) {
            App.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddDocumentWindow.fxml", null, null);
    }
}


