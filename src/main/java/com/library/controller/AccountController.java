package com.library.controller;

import java.util.List;

import com.library.model.Document;
import com.library.util.RecommendationService;
import com.library.util.UserSession;

import javafx.fxml.FXML;

public class AccountController extends DocumentsViewController {

    @Override
    @FXML public void initializeItemTableView() {
        super.initializeItemTableView();
        handleToggleGrid();
    }

    @Override
    protected List<Document> performInitialLoad() {
        RecommendationService recommendationService = new RecommendationService();
        return recommendationService.recommendBooks(UserSession.getUser());
    }
}
