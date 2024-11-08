package com.library.model;
/**
 * Tag model class
 * 
 * @author Dat
 */
public class Tag {

    /**
     * Tag ID
     */
    private int id;

    /**
     * Tag name
     */
    private String name;

    /**
     * Constructor
     * 
     * @param id   Tag ID
     * @param name Tag name
     */
    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get tag ID
     * 
     * @return Tag ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set tag ID
     * 
     * @param id Tag ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get tag name
     * 
     * @return Tag name
     */
    public String getName() {
        return name;
    }

    /**
     * Set tag name
     * 
     * @param name Tag name
     */
    public void setName(String name) {
        this.name = name;
    }

}

