package com.library.util;

import java.io.IOException;
import java.util.Optional;

import com.library.App;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;

public class SceneNavigator {

    private static Scene scene;

    // Set the current scene
    public static void setScene(Scene newScene) {
        scene = newScene;
    }

    // Change the root of the current scene to a new FXML with an optional controller
    public static void setRoot(String fxml, Optional<Object> controller) {
        if (scene != null) {
            // Show the loading screen first
            ProgressIndicator progressIndicator = new ProgressIndicator();
            StackPane loadingPane = new StackPane(progressIndicator);
            loadingPane.setPrefSize(400, 400);

            Platform.runLater(() -> {
                scene.setRoot(loadingPane);
                scene.getWindow().sizeToScene();
                scene.getWindow().centerOnScreen();
            });

            // Load the new scene in the background using a Task
            loadSceneInBackground(fxml, controller);
        } else {
            ErrorHandler.showErrorDialog(new IllegalStateException("Scene is not set."));
        }
    }

    // Load FXML and optionally set the controller
    public static Parent loadFXML(String fxml, Optional<Object> controller) {
        // Check if the FXML is already cached
        Parent cachedParent = FXMLCache.getFXML(fxml);
        if (cachedParent != null) {
            return cachedParent; // Return cached FXML
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            controller.ifPresent(fxmlLoader::setController);
            fxmlLoader.setResources(Localization.getInstance().getResourceBundle());
            Parent parent = fxmlLoader.load();

            // Cache the loaded FXML
            FXMLCache.cacheFXML(fxml, parent);

            return parent;
        } catch (IOException e) {
            ErrorHandler.showErrorDialog(e);
            return null;
        }
    }

    private static void loadSceneInBackground(String fxml, Optional<Object> controller) {
        // Load the FXML in a background thread
        Task<Parent> loadTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return loadFXML(fxml, controller);
            }

            @Override
            protected void succeeded() {
                scene.setRoot(getValue());
                scene.getWindow().sizeToScene();
                scene.getWindow().centerOnScreen();
            }

            @Override
            protected void failed() {
                ErrorHandler.showErrorDialog(getException());
            }

        };

        // Run the task in a background thread
        new Thread(loadTask).start();
    }
}
