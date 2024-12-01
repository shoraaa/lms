package com.library;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.library.util.DatabaseInitializer;
import com.library.util.Localization;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
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

        scene = new Scene(loadFXML("/com/library/views/Login", null));
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    public static void setRoot(String fxml, Object controller) {
        scene.setRoot(loadFXML(fxml, controller));
        scene.getWindow().sizeToScene();
        scene.getWindow().centerOnScreen();
    }

    public static Parent loadFXML(String fxml, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            if (controller != null) fxmlLoader.setController(controller);
            fxmlLoader.setResources(Localization.getInstance().getResourceBundle());
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

    public static void openDialog(String fxmlPath, Object controller, Runnable onClose) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scene.getWindow());
        dialog.setDialogPane(new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        });

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
        if (controller != null) fxmlLoader.setController(controller);
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            App.showErrorDialog(e);
            return;
        }

        dialog.showAndWait();
        onClose.run();
    }
    

    public static void main(String[] args) {
        launch();
    }

}