package com.library.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import com.library.model.Document;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.services.AuthorDAO;
import com.library.services.DocumentDAO;
import com.library.services.TransactionDAO;
import com.library.services.UserDAO;
import com.library.util.ErrorHandler;
import com.library.util.Localization;
import com.library.util.UserSession;
import com.library.view.BaseTableView;
import com.library.view.DocumentTableView;
import com.library.view.OverdueTransactionTableView;
import com.library.view.UserTableView;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardController extends BaseController {

    @FXML private Label helloLabel;
    @FXML private Label dateTimeLabel;

    @FXML private Card newBookCard;
    @FXML private Card newUserCard;
    @FXML private Card borrowedBookCard;
    @FXML private Card returnedBookCard;
    @FXML private TableView<User> usersList;
    @FXML private TableView<Document> documentsList;
    @FXML private TableView<Transaction> overDueTable;

    @FXML private HBox popularDocumentHBox;

    private DocumentTableView documentTableView;
    private UserTableView userTableView;
    private OverdueTransactionTableView overdueTransactionTableView;

    private static final String DATE_TIME_PATTERN = "MMMM dd, yyyy | EEEE, h:mm a";

    public void initialize() {
        setupGreeting();
        setupDateTime();
        setupDashboardCards();
        initializeTables();
        loadPopularDocuments();
    }

    private void setupGreeting() {
        helloLabel.setText(helloLabel.getText().replace("{0}", UserSession.getUser().getName()));
    }

    private void setupDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Localization.getInstance().getLocale());
        String formattedDateTime = LocalDateTime.now().format(formatter);
        dateTimeLabel.setText(formattedDateTime);
    }

    private void setupDashboardCards() {
        ResourceBundle bundle = Localization.getInstance().getResourceBundle();
        setCardValue(newBookCard, bundle.getString("totalBooks"), DocumentDAO.getInstance().countAllDocuments(), Material2AL.BOOK);
        setCardValue(newUserCard, bundle.getString("totalUsers"), UserDAO.getInstance().countAllUsers(), Material2MZ.PERSON);
        
        List<Transaction> transactions = TransactionDAO.getInstance().getAllTransactions();
        setCardValue(borrowedBookCard, bundle.getString("borrowedBooks"), transactions.size(), Material2AL.BOOKMARK);
        setCardValue(returnedBookCard, bundle.getString("returnedBooks"), 
            (int) transactions.stream().filter(Transaction::isReturned).count(), Material2AL.CHECK_CIRCLE);
    }

    private void initializeTables() {
        setupDocumentTable();
        setupUserTable();
        setupOverdueTransactionTable();
    }

    protected <T> void loadItemsAsync(Supplier<List<T>> performInitialLoad, BaseTableView<T> itemTableView) {
            Task<List<T>> loadTask = new Task<>() {
                @Override
                protected List<T> call() {
                    return performInitialLoad.get();
                }

            @Override
            protected void succeeded() {
                List<T> items = getValue();
                itemTableView.setData(FXCollections.observableList(items));
            }

            @Override
            protected void failed() {
                ErrorHandler.showErrorDialog(new Exception("Failed to load items"));
            }
        };

        new Thread(loadTask).start();  // Run in a background thread
    }

    private void setupDocumentTable() {
        documentTableView = new DocumentTableView(documentsList);
        documentTableView.setParentController(this);

        // Columns to remove
        Localization localization = Localization.getInstance();
        String[] columnsToRemove = {
            "categories", "publisher", "publicationDate", 
            "registrationDate", "image", "isbn"
        };
        for (String column : columnsToRemove) {
            documentTableView.removeColumn(localization.getString(column));
        }
        documentTableView.removeColumn(""); // check box column

        loadItemsAsync(DocumentDAO.getInstance()::getAllEntries, documentTableView);
    }

    private void setupUserTable() {
        userTableView = new UserTableView(usersList);
        userTableView.setParentController(this);

        // Columns to remove
        userTableView.removeColumn(Localization.getInstance().getString("registrationDate"));
        userTableView.removeColumn("");

        loadItemsAsync(UserDAO.getInstance()::getAllEntries, userTableView);
    }

    private void setupOverdueTransactionTable() {
        overdueTransactionTableView = new OverdueTransactionTableView(overDueTable);
        overdueTransactionTableView.setParentController(this);
        loadItemsAsync(TransactionDAO.getInstance()::getOverdueTransactions, overdueTransactionTableView);
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
                if (document == null) {
                    continue;
                }
                popularDocumentHBox.getChildren().add(createDocumentCard(document));
            }
        });

        loadTask.setOnFailed(event -> {
            ErrorHandler.showErrorDialog(new Exception("Failed to load popular documents", loadTask.getException()));
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
        ImageView imageView = new ImageView(new Image(imageUrl));
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
            ErrorHandler.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddDocumentWindow.fxml", null, null);
    }
}
