package com.library.util;

import java.io.IOException;
import java.util.Optional;

import com.library.App;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import atlantafx.base.util.Animations;

public class PaneNavigator {

    private static ScrollPane content;

    // Set the current scene (as ScrollPane)
    public static void setContent(ScrollPane newContent) {
        content = newContent;
    }

    // Change the root of the current scene to a new FXML with an optional controller
    public static void setRoot(String fxml, Optional<Object> controller) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setPrefSize(content.getViewportBounds().getWidth(), content.getViewportBounds().getHeight());

        Platform.runLater(() -> {
            // Add a fade-out effect to the existing content (inside ScrollPane)
            if (content.getContent() != null) {
                Animations.fadeOut(content.getContent(), Duration.seconds(0.25)).play();
            }
            content.setContent(loadingPane);
        });

        // Load the new scene in the background using a Task
        loadSceneInBackground(fxml, controller);
    }

    // Load FXML and optionally set the controller
    public static Parent loadFXML(String fxml, Optional<Object> controller) {
        // Check if the FXML is already cached
        Parent cachedParent = FXMLCache.getFXML(fxml);
        if (cachedParent != null) {
            return cachedParent;  // Return cached FXML
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
                Platform.runLater(() -> {
                    Parent loadedContent = getValue();
                    content.setContent(loadedContent);

                    // Apply fade-in effect after content is loaded
                    Animations.fadeIn(loadedContent, Duration.seconds(0.25)).play();
                });
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
