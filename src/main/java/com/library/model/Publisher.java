package com.library.model;

public class Publisher {
    private int id;
    private String name;

    /**
     * Constructor for Publisher.
     * 
     * @param id   The ID of the publisher.
     * @param name The name of the publisher.
     */
    public Publisher(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID of the publisher.
     * 
     * @return The publisher ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the publisher.
     * 
     * @param id The publisher ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the publisher.
     * 
     * @return The publisher name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the publisher.
     * 
     * @param name The publisher name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
