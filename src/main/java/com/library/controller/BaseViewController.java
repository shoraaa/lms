package com.library.controller;

public class BaseViewController extends BaseController {
    protected MainController mainController;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public MainController getMainController() {
        return mainController;
    }
}
