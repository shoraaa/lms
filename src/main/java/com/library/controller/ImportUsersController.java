package com.library.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.model.User;
import com.library.services.UserDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImportUsersController {

    @FXML private Button browseButton;
    @FXML private TextField filePathField;
    @FXML private Button importButton;
    @FXML private Button cancelButton;

    private final UserDAO userDAO = new UserDAO(); 

    public void initialize() {
        browseButton.setOnAction(event -> handleBrowseAction());
        importButton.setOnAction(event -> handleImportAction());
        cancelButton.setOnAction(event -> handleCancelAction());
    }

    @FXML
    private void handleBrowseAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleImportAction() {
        String filePath = filePathField.getText();

        if (filePath == null || filePath.isEmpty()) {
            System.out.println("Please select a file first.");
            return;
        }

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<List<User>>() {}.getType();

            List<User> users = gson.fromJson(reader, userListType);

            if (users == null) {
                System.out.println("The JSON file format is incorrect or empty. Please check the file.");
                return;
            }
            
            for (User user : users) {
                userDAO.add(user);
            }

            System.out.println("Users imported successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to import users from JSON file.");
        }
    }

    @FXML
    private void handleCancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
