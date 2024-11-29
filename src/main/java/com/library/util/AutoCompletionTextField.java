package com.library.util;

import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A utility class for adding auto-completion functionality to a TextField.
 */
public class AutoCompletionTextField {

    private final TextField textField;
    private final SortedSet<String> entries;
    private ContextMenu entriesPopup;

    private static final int MAX_ENTRIES = 10;

    /**
     * Constructor for AutoCompletionTextField with pre-supplied entries.
     *
     * @param textField The TextField to which auto-completion will be applied.
     * @param entries   A list of entries to be suggested.
     */
    public AutoCompletionTextField(TextField textField, List<String> entries) {
        this.textField = textField;
        this.entries = new TreeSet<>(entries);
        this.entriesPopup = new ContextMenu();

        setListener();
    }

    /**
     * Set up listeners to manage the text input and suggestions.
     */
    private void setListener() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                entriesPopup.hide();
            } else {
                filterAndShowSuggestions(newValue);
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                entriesPopup.hide();
            }
        });
    }

    /**
     * Filter the entries based on user input and display the suggestions.
     *
     * @param enteredText The current text input.
     */
    private void filterAndShowSuggestions(String enteredText) {
        List<String> filteredEntries = entries.stream()
                .filter(entry -> entry.toLowerCase().contains(enteredText.toLowerCase()))
                .collect(Collectors.toList());

        if (!filteredEntries.isEmpty()) {
            populatePopup(filteredEntries, enteredText);
            if (!entriesPopup.isShowing()) {
                // Ensure the textField is part of a scene before showing the popup
                if (textField.getScene() != null) {
                    entriesPopup.show(textField, Side.BOTTOM, 0, 0);
                } else {
                    textField.sceneProperty().addListener((observable, oldScene, newScene) -> {
                        if (newScene != null) {
                            entriesPopup.show(textField, Side.BOTTOM, 0, 0);
                        }
                    });
                }
            }
        } else {
            entriesPopup.hide();
        }
    }

    /**
     * Populate the context menu with filtered suggestions.
     *
     * @param filteredEntries The filtered entries that match the user input.
     * @param searchText      The text input entered by the user.
     */
    private void populatePopup(List<String> filteredEntries, String searchText) {
        entriesPopup.getItems().clear();
        List<CustomMenuItem> menuItems = new ArrayList<>();

        for (int i = 0; i < Math.min(filteredEntries.size(), MAX_ENTRIES); i++) {
            String result = filteredEntries.get(i);
            Label entryLabel = new Label();
            entryLabel.setGraphic(buildTextFlow(result, searchText));
            entryLabel.setPrefHeight(10); // Adjust this based on styling preferences.

            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(event -> {
                textField.setText(result);
                textField.positionCaret(result.length());
                entriesPopup.hide();
            });

            menuItems.add(item);
        }

        entriesPopup.getItems().addAll(menuItems);
    }

    /**
     * Build a TextFlow to highlight the matching part of the suggestion.
     *
     * @param text   The full text of the suggestion.
     * @param filter The text entered by the user to highlight.
     * @return A TextFlow containing the highlighted suggestion.
     */
    private TextFlow buildTextFlow(String text, String filter) {
        int filterIndex = text.toLowerCase().indexOf(filter.toLowerCase());

        if (filterIndex == -1) {
            return new TextFlow(new Text(text)); // No match found, return full text
        }

        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textMatch = new Text(text.substring(filterIndex, filterIndex + filter.length()));
        Text textAfter = new Text(text.substring(filterIndex + filter.length()));

        textMatch.setFill(Color.ORANGE);
        textMatch.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));

        return new TextFlow(textBefore, textMatch, textAfter);
    }

    /**
     * Get the set of autocomplete entries.
     *
     * @return The set of entries used for autocomplete.
     */
    public SortedSet<String> getEntries() {
        return entries;
    }

    /**
     * Add new entries to the autocomplete set.
     *
     * @param newEntries A list of new entries to be added.
     */
    public void addEntries(List<String> newEntries) {
        entries.addAll(newEntries);
    }
}
