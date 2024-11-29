package com.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.SimpleBooleanProperty;

public class User {
    private int userId;  // Auto-generated ID
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate registrationDate;
    private String imageUrl;

    private final SimpleBooleanProperty selected; // For checkbox

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationDate = LocalDate.now(); // Automatically set to current time

        this.selected = new SimpleBooleanProperty(false);
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

        /**
     * @return The property for the checkbox
     */
    public SimpleBooleanProperty isSelectedProperty() {
        return selected;
    }

    /**
     * @return The selected status of the user
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * @param selected The selected status of the user
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", registrationDate=" + registrationDate +
               '}';
    }
}

