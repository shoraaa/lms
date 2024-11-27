package com.library;

import java.io.IOException;

import com.library.util.DatabaseInitializer;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        initializeDatabase();
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        scene = new Scene(loadFXML("/com/library/views/Main"));
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}