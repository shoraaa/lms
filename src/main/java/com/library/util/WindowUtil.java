package com.library.util;

import java.io.IOException;

import com.library.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowUtil {

    public static Parent loadFXML(String fxmlPath) {

        try {

            return FXMLLoader.load(WindowUtil.class.getResource(fxmlPath));

        } catch (IOException e) {

            App.showErrorDialog(e);

            return null;

        }

    }


    public static void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            App.showErrorDialog(e);
        }
    }

}

