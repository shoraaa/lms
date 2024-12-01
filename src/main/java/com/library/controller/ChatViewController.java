package com.library.controller;

import java.util.ArrayList;
import java.util.List;

import com.library.api.GoogleChatAPI;
import com.library.util.ErrorHandler;
import com.library.util.UserSession;

import atlantafx.base.controls.Tile;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatViewController extends BaseViewController {
    @FXML private Button addButton, sendButton;
    @FXML private TextField chatTextField;
    @FXML private ListView<Tile> chatListView;

    private List<String> messageHistory = new ArrayList<>();


    @FXML public void initialize() {
        // Configure UI button actions
        configureButtonActions();
        handleAddNewChat();
    }

    private void configureButtonActions() {
        addButton.setOnAction(event -> handleAddNewChat());
        sendButton.setOnAction(event -> handleSendChat());
    }

    private void handleAddNewChat() {
        messageHistory.clear();
        chatListView.getItems().clear();
        addChat("Chat Bot", "Hello, " + UserSession.getUser().getName() + ", how can I help you today?");
    }

    private void handleSendChat() {
        String message = chatTextField.getText();
        if (!message.isEmpty()) {
            addChat("User", message);
            chatTextField.setEditable(false);

            // Create a new task to send the message
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return GoogleChatAPI.getAIResponse(messageHistory);
                }
            };

            // Set the task's onSucceeded and onFailed handlers
            task.setOnSucceeded(event -> {
                addChat("Chat Bot", task.getValue());
                chatTextField.setEditable(true);
                chatTextField.clear();
            });

            task.setOnFailed(event -> {
                ErrorHandler.showErrorDialog(new Throwable("There was a problem when sending API."));
            });

            // Start the task in a new thread
            new Thread(task).start();
        }


    }

    private void addChat(String sender, String chat) {
        Tile botTile = new Tile(sender, chat);
        Platform.runLater(() -> chatListView.getItems().add(botTile));
        messageHistory.add(sender + ": " + chat);
    }
}
