package com.library.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import com.library.App;
import com.library.model.Document;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;
import com.library.util.UserSession;
import com.library.view.DocumentTableView;
import com.library.view.UserTableView;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import javafx.concurrent.Task;
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

    private static final String DATE_TIME_PATTERN = "MMMM dd, yyyy | EEEE, h:mm a";

    public void initialize() {
        setupGreeting();
        setupDateTime();
        setupDashboardCards();
        initializeTables();
        loadPopularDocuments();
    }

    private void setupGreeting() {
        helloLabel.setText("Hello, " + UserSession.getUser().getName() + "!");
    }

    private void setupDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.US);
        String formattedDateTime = LocalDateTime.now().format(formatter);
        dateTimeLabel.setText(formattedDateTime);
    }

    private void setupDashboardCards() {
        setCardValue(newBookCard, "Total Books", DocumentDAO.getInstance().countAllDocuments(), Material2AL.BOOK);
        setCardValue(newUserCard, "Total Users", UserDAO.getInstance().countAllUsers(), Material2MZ.PERSON);
        
        List<Transaction> transactions = TransactionDAO.getInstance().getAllTransactions();
        setCardValue(borrowedBookCard, "Borrowed Books", transactions.size(), Material2AL.BOOKMARK);
        setCardValue(returnedBookCard, "Returned Books", 
            (int) transactions.stream().filter(Transaction::isReturned).count(), Material2AL.CHECK_CIRCLE);
    }

    private void initializeTables() {
        setupDocumentTable();
        setupUserTable();
    }

    private void setupDocumentTable() {
        documentTableView = new DocumentTableView(documentsList);
        documentTableView.setParentController(this);

        // Columns to remove
        String[] columnsToRemove = {
            "Categories", "Publisher", "Publication Date", 
            "Registration Date", "Image", "ISBN", ""
        };
        for (String column : columnsToRemove) {
            documentTableView.removeColumn(column);
        }

        documentTableView.loadData();
    }

    private void setupUserTable() {
        userTableView = new UserTableView(usersList);

        // Columns to remove
        String[] columnsToRemove = { "Registration Date", "" };
        for (String column : columnsToRemove) {
            userTableView.removeColumn(column);
        }
    }

    private void loadPopularDocuments() {
        Task<List<Document>> loadTask = new Task<>() {
            @Override
            protected List<Document> call() {
                List<Transaction> topBorrowed = TransactionDAO.getInstance().getMostBorrowedTransaction(5);
                return topBorrowed.stream()
                    .map(transaction -> DocumentDAO.getInstance().getDocumentById(transaction.getDocumentId()))
                    .collect(Collectors.toList());
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<Document> popularDocuments = loadTask.getValue();
            for (Document document : popularDocuments) {
                popularDocumentHBox.getChildren().add(createDocumentCard(document));
            }
        });

        loadTask.setOnFailed(event -> {
            App.showErrorDialog(new Exception("Failed to load popular documents", loadTask.getException()));
        });

        new Thread(loadTask).start();
    }


    private VBox createDocumentCard(Document document) {
        VBox card = new VBox();
        card.getChildren().addAll(
            createImageView(document.getImageUrl()),
            createLabel(document.getTitle(), 160, true),
            createLabel(getDocumentAuthors(document), 160, false)
        );

        card.setOnMouseClicked(event -> mainController.showDialog(
            "/com/library/views/EditDocumentWindow.fxml", null, 
            new EditDocumentController(document)
        ));
        return card;
    }

    private ImageView createImageView(String imageUrl) {
        ImageView imageView = new ImageView(imageUrl);
        imageView.setFitHeight(200);
        imageView.setFitWidth(160);
        return imageView;
    }

    private Label createLabel(String text, double maxWidth, boolean wrapText) {
        Label label = new Label(text);
        label.setMaxWidth(maxWidth);
        label.setWrapText(wrapText);
        return label;
    }

    private String getDocumentAuthors(Document document) {
        return document.getAuthorIds().stream()
            .map(authorId -> AuthorDAO.getInstance().getAuthorById(authorId).getName())
            .collect(Collectors.joining(", "));
    }

    private void setCardValue(Card card, String body, int header, Ikon icon) {
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
