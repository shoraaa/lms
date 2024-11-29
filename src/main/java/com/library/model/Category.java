package com.library.model;
/**
 * Category model class
 * 
 * @author Dat
 */
public class Category {

    /**
     * Category ID
     */
    private int id;

    /**
     * Category name
     */
    private String name;

    /**
     * Constructor
     * 
     * @param id   Category ID
     * @param name Category name
     */
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name) {
        this.id = -1;
        this.name = name;
    }

    /**
     * Get category ID
     * 
     * @return Category ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set category ID
     * 
     * @param id Category ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get category name
     * 
     * @return Category name
     */
    public String getName() {
        return name;
    }

    /**
     * Set category name
     * 
     * @param name Category name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}

