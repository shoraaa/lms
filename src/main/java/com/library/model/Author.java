package com.library.model;
public class Author {
    private int id;
    private String name;
    private String email;

    public Author(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
        this.email = "Not available";
    }

    public Author(String name) {
        this.id = -1;
        this.name = name;
        this.email = "Not available";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String toString() {
        return name;
    }
}
