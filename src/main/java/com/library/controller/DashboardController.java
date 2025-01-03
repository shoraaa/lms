package com.library.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kordamp.ikonli.Ikon;

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
import com.library.view.DocumentIssuedTableView;
import com.library.view.DocumentTableView;
import com.library.view.OverdueTransactionTableView;
import com.library.view.UserTableView;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
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

    @FXML private Label totalBookLabel, totalUserLabel, borrowedBookLabel, returnedBookLabel;
    @FXML private Button seeAllUsersButton, seeAllDocumentsButton;
    @FXML private TableView<User> usersList;
    @FXML private TableView<Document> documentsList;
    @FXML private TableView<Transaction> overDueTable;
    @FXML private TableView<Transaction> bookIssuedTable;
    @FXML private BarChart<String, Number> barChart;

    @FXML private HBox popularDocumentHBox;

    private DocumentTableView documentTableView;
    private UserTableView userTableView;
    private OverdueTransactionTableView overdueTransactionTableView;
    private DocumentIssuedTableView documentIssuedTableView;

    private static final String DATE_TIME_PATTERN = "MMMM dd, yyyy | EEEE, h:mm a";

    public void initialize() {
        setupGreeting();
        initializeButton();
        setupDateTime();
        setupDashboardCards();
        initializeTables();
        loadPopularDocuments();
        loadBarChart();
    }

    private void setupGreeting() {
        helloLabel.setText(helloLabel.getText().replace("{0}", UserSession.getUser().getName()));
    }

    private void initializeButton() {
        seeAllUsersButton.setOnAction(event -> mainController.handleUserButton());
        seeAllDocumentsButton.setOnAction(event -> mainController.handleDocumentButton());
    }

    private void setupDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Localization.getInstance().getLocale());
        String formattedDateTime = LocalDateTime.now().format(formatter);
        dateTimeLabel.setText(formattedDateTime);
    }

    private void setupDashboardCards() {
        totalBookLabel.setText(String.valueOf(DocumentDAO.getInstance().countAllDocuments()));
        totalUserLabel.setText(String.valueOf(UserDAO.getInstance().countAllUsers()));
        List<Transaction> transactions = TransactionDAO.getInstance().getAllTransactions();
        borrowedBookLabel.setText(String.valueOf(transactions.size()));
        returnedBookLabel.setText(String.valueOf(transactions.stream().filter(Transaction::isReturned).count()));
    }

    private void initializeTables() {
        setupDocumentTable();
        setupUserTable();
        setupOverdueTransactionTable();
        setupDocumentIssuedTable();
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

    private void setupDocumentIssuedTable() {
        documentIssuedTableView = new DocumentIssuedTableView(bookIssuedTable);
        documentIssuedTableView.setParentController(this);
        loadItemsAsync(TransactionDAO.getInstance()::getIssuingTransaction, documentIssuedTableView);
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
        ImageView imageView = (imageUrl != null && !imageUrl.isEmpty()) ? new ImageView(new Image(imageUrl)) : new ImageView();
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

    public XYChart.Series<String, Number> getBorrowStatisticsSeries() {
        // Fetch transaction statistics
        Map<LocalDate, Long> transactionsPerMonth = TransactionDAO.getInstance().getBorrowPerDayRecent(7);

        // Create series for the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrows Per Day (Last 7 Days)");

        transactionsPerMonth.forEach((date, count) -> {
            series.getData().add(new XYChart.Data<>(date.getDayOfWeek().toString(), count));
        });

        return series;
    }

    public XYChart.Series<String, Number> getReturnStatisticsSeries() {
        // Fetch transaction statistics
        Map<LocalDate, Long> transactionsPerMonth = TransactionDAO.getInstance().getReturnPerDayRecent(7);

        // Create series for the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Returns Per Day (Last 7 Days)");

        transactionsPerMonth.forEach((date, count) -> {
            series.getData().add(new XYChart.Data<>(date.getDayOfWeek().toString(), count));
        });

        return series;
    }

    private void loadBarChart() {
        List<XYChart.Series<String, Number>> seriesList = List.of(getBorrowStatisticsSeries(), getReturnStatisticsSeries());
        barChart.getData().addAll(seriesList);
    }

    private void handleAddNewDocument() {
        if (mainController == null) {
            ErrorHandler.showErrorDialog(new Exception("MainController not set"));
            return;
        }

        mainController.showDialog("/com/library/views/AddDocumentWindow.fxml", null, null);
    }
}
