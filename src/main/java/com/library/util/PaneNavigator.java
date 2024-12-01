package com.library.util;

import java.io.IOException;
import java.util.Optional;

import com.library.App;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PaneNavigator {

    private static Pane content;

    // Set the current scene
    public static void setContent(Pane newContent) {
        content = newContent;
    }

    // Change the root of the current scene to a new FXML with an optional controller
    public static void setRoot(String fxml, Optional<Object> controller) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setPrefSize(400, 400);

        Platform.runLater(() -> {
            content.getChildren().clear();
            content.getChildren().add(loadingPane);
        });

        // Load the new scene in the background using a Task
        loadSceneInBackground(fxml, controller);
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
                content.getChildren().clear();
                content.getChildren().add(getValue());
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
