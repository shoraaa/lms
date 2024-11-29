package com.library;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import com.library.util.DatabaseInitializer;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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

    public static Parent loadFXML(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();
        } catch (IOException e) {
            showErrorDialog(e);
            return null;
        }
    }

     public static void showErrorDialog(Throwable error) {
        var alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(error.getMessage());
        alert.setContentText(error.getLocalizedMessage());

        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);

        var textArea = new TextArea(stringWriter.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        var content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(new Label("Full stacktrace:"), 0, 0);
        content.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(content);
        alert.initOwner(scene.getWindow());
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }

}