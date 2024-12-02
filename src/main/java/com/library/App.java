package com.library;

import java.io.IOException;
import java.util.Optional;

import com.library.util.DatabaseInitializer;
import com.library.util.DatabaseSampler;
import com.library.util.ErrorHandler;
import com.library.util.SceneNavigator;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	private static final String APP_TITLE = "Library Management System";

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the database and set the application stylesheet
        DatabaseInitializer.initializeDatabase();
        // DatabaseSampler.generateSampleTransactions(50);

        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        // Set the initial loading screen
        scene = new Scene(SceneNavigator.loadFXML("/com/library/views/Loading", Optional.empty()));
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.show();

        ErrorHandler.setScene(scene);
        SceneNavigator.setScene(scene);

        // Load the login scene asynchronously
        loadSceneInBackground("/com/library/views/Login");
    }

    private void loadSceneInBackground(String fxml) {
        // Start the loading task
        Task<Parent> loadTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return SceneNavigator.loadFXML(fxml, Optional.empty()); // Load the desired FXML
            }

            @Override
            protected void succeeded() {
                updateScene(getValue());
            }

            @Override
            protected void failed() {
                ErrorHandler.showErrorDialog(getException());
            }
        };

        // Run the task in the background
        new Thread(loadTask).start();
    }

    private void updateScene(Parent newRoot) {
        // Update the scene with the newly loaded FXML
        scene.setRoot(newRoot);
        scene.getWindow().sizeToScene();
        scene.getWindow().centerOnScreen();
    }



    public static void main(String[] args) {
        launch();
    }
}
