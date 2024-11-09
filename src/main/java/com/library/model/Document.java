package com.library.model;

import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;

public class Document {
    private int documentId;
    private String title; // Only title is required now
    private List<Integer> authorIds;
    private List<Integer> categoryIds;
    private int publisherId;
    private LocalDate publicationDate;
    private LocalDate dateAddedToLibrary;
    private int currentQuantity;
    private int totalQuantity;
    private String isbn;
    private int languageId;
    private String description;

    private final SimpleBooleanProperty isSelected; // For checkbox

    // Private constructor to prevent direct instantiation
    private Document(Builder builder) {
        this.documentId = builder.documentId;
        this.title = builder.title;
        this.authorIds = builder.authorIds;
        this.categoryIds = builder.categoryIds;
        this.publisherId = builder.publisherId;
        this.publicationDate = builder.publicationDate;
        this.dateAddedToLibrary = builder.dateAddedToLibrary;
        this.currentQuantity = builder.currentQuantity;
        this.totalQuantity = builder.totalQuantity;
        this.isbn = builder.isbn;
        this.languageId = builder.languageId;
        this.description = builder.description;
        this.isSelected = new SimpleBooleanProperty(false); // Default to false (not selected)
    }

    // Getters and setters

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Integer> authorIds) {
        this.authorIds = authorIds;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public LocalDate getDateAddedToLibrary() {
        return dateAddedToLibrary;
    }

    public void setDateAddedToLibrary(LocalDate dateAddedToLibrary) {
        this.dateAddedToLibrary = dateAddedToLibrary;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SimpleBooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public boolean isSelected() {
        return isSelected.get();
    }

    public void setSelected(boolean selected) {
        isSelected.set(selected);
    }


    // Static Builder class
    public static class Builder {
        private int documentId; // Optional, to be set by database
        private String title;   // Required
        private List<Integer> authorIds;
        private List<Integer> categoryIds;
        private int publisherId;
        private LocalDate publicationDate;
        private LocalDate dateAddedToLibrary;
        private int currentQuantity = 1; // Default value
        private int totalQuantity = 1;   // Default value
        private String isbn;
        private int languageId;
        private String description;

        // Required field constructor
        public Builder(String title) {
            this.title = title;
        }

        // Optional fields with setters

        public Builder documentId(int documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder authorIds(List<Integer> authorIds) {
            this.authorIds = authorIds;
            return this;
        }

        public Builder categoryIds(List<Integer> categoryIds) {
            this.categoryIds = categoryIds;
            return this;
        }

        public Builder publisherId(int publisherId) {
            this.publisherId = publisherId;
            return this;
        }

        public Builder publicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        public Builder dateAddedToLibrary(LocalDate dateAddedToLibrary) {
            this.dateAddedToLibrary = dateAddedToLibrary;
            return this;
        }

        public Builder currentQuantity(int currentQuantity) {
            this.currentQuantity = currentQuantity;
            return this;
        }

        public Builder totalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder languageId(int languageId) {
            this.languageId = languageId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        // Build the Document object
        public Document build() {
            return new Document(this);
        }
    }
}
