package com.library.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    private static ResourceBundle resourceBundle;

    // Private constructor to prevent instantiation
    private Localization() {}

    // Public method to get the resource bundle
    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            // Load the resource bundle for the desired locale
            resourceBundle = ResourceBundle.getBundle("com.library.i18n.labels", new Locale("en", "US"));
        }
        return resourceBundle;
    }

    // Optionally, you can have a method to update the locale if needed
    public static void setLocale(java.util.Locale locale) {
        resourceBundle = ResourceBundle.getBundle("messages", locale);
    }
}
