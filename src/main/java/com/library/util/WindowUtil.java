package com.library.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class WindowUtil {

    public static Parent loadFXML(String fxmlPath) {

        try {

            return FXMLLoader.load(WindowUtil.class.getResource(fxmlPath));

        } catch (IOException e) {

            e.printStackTrace();

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
            e.printStackTrace();
        }
    }
}

