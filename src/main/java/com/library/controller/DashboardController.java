package com.library.controller;

import org.kordamp.ikonli.material2.Material2MZ;

import com.library.model.Document;
import com.library.model.User;
import com.library.view.DocumentTableView;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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

    private DocumentTableView documentTableView;

    public void initialize() {
        helloLabel.setText("Hello, Admin");

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy | EEEE, h:mm a");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        dateTimeLabel.setText(formattedDateTime);

        setCardValue(newBookCard, "New Books", "10", Material2MZ.PERSON);
        setCardValue(newUserCard, "New Users", "5", Material2MZ.PERSON);
        setCardValue(borrowedBookCard, "Borrowed Books", "3", Material2MZ.PERSON);
        setCardValue(returnedBookCard, "Returned Books", "2", Material2MZ.PERSON);

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
    }

    private void setCardValue(Card card, String body, String header, Material2MZ icon) {
        Text headerText = new Text(header);
        headerText.getStyleClass().addAll(Styles.TITLE_1);
        card.setHeader(headerText);

        Text bodyText = new Text(body);
        bodyText.getStyleClass().addAll(Styles.TITLE_3);
        card.setBody(bodyText);
    }
}


