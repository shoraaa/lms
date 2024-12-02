package com.library.view;

import java.util.List;

import com.library.model.Document;
import com.library.util.RecommendationService;
import com.library.util.UserSession;

import javafx.scene.control.TableView;

public class RecommendationDocumentTableView extends DocumentTableView {

    public RecommendationDocumentTableView(TableView<Document> tableView) {
        super(tableView);
    }

    @Override
    public List<Document> performInitialLoad() {
        RecommendationService recommendation = new RecommendationService();
        return recommendation.recommendBooks(UserSession.getUser());
    }

}
