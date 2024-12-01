package com.library.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ErrorHandler {

    private static Scene scene;

    public static void setScene(Scene newScene) {
        scene = newScene;
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
}
