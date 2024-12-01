package com.library.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    // Singleton instance
    private static Localization instance;

    // Current locale
    private Locale locale;

    // Resource bundle
    private ResourceBundle resourceBundle;

    // Private constructor to prevent instantiation
    private Localization() {
        // Default locale is set to English (can be customized)
        this.locale = new Locale("en", "US");
        loadResourceBundle();
    }

    // Method to get the singleton instance
    public static Localization getInstance() {
        if (instance == null) {
            instance = new Localization();
        }
        return instance;
    }

    // Method to get the current resource bundle
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    // Method to get a specific localized string from the resource bundle
    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    // Method to set a new locale
    public void setLocale(Locale locale) {
        this.locale = locale;
        loadResourceBundle();
    }

    // Method to get the current locale
    public Locale getLocale() {
        return locale;
    }

    // Load the resource bundle based on the current locale
    private void loadResourceBundle() {
        // Assuming the resource bundle is named 'labels' and is in the 'com.library.i18n' package
        resourceBundle = ResourceBundle.getBundle("com.library.i18n.labels", locale);
    }
}
