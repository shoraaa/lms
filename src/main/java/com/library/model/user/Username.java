package com.library.model.user;

public class Username {
    private String firstName;
    private String lastName;

    public Username(String firstName, String lastName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public String getName() {
        return (firstName + " " + lastName).toLowerCase();
    }
}
