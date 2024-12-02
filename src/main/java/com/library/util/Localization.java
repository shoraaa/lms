package com.library.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Localization {

    // Singleton instance
    private static Localization instance;

    // Current locale
    private Locale locale;

    // Resource bundle
    private ResourceBundle resourceBundle;

    private final List<String> availableLanguages = Arrays.asList("en-US", "vi-VN");

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

    /**
     * Returns a list of available languages, displayed in their native languages.
     *
     * @return list of available languages in their native languages
     */
    public List<String> getAvailableLanguages() {
        return availableLanguages.stream()
                .map(lang -> {
                    Locale tempLocale = new Locale(lang.split("-")[0], lang.split("-")[1]);
                    return tempLocale.getDisplayLanguage(tempLocale); // Get the name in its native language
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets the current language of the application.
     *
     * @return the current language
     */
    public String getCurrentLanguage() {
        return locale.getDisplayLanguage(locale);
    }

    /**
     * Sets the language of the application by converting the display language to a corresponding Locale.
     *
     * @param displayLanguage the native display name of the language
     */
    public void setLanguage(String displayLanguage) {
        String localeStr = getLocaleStringFromNativeDisplayLanguage(displayLanguage);
        if (localeStr != null) {
            String[] parts = localeStr.split("-");
            this.locale = new Locale(parts[0], parts[1]);
            loadResourceBundle();
        } else {
            throw new IllegalArgumentException("Language not found: " + displayLanguage);
        }
    }

    /**
     * Converts the native display language to the corresponding locale string (language-country).
     *
     * @param displayLanguage the native display name of the language
     * @return the corresponding locale string (e.g., "en-US" for "English")
     */
    private String getLocaleStringFromNativeDisplayLanguage(String displayLanguage) {
        for (String lang : availableLanguages) {
            Locale tempLocale = new Locale(lang.split("-")[0], lang.split("-")[1]);
            if (tempLocale.getDisplayLanguage(tempLocale).equalsIgnoreCase(displayLanguage)) {
                return lang;
            }
        }
        return null; // Return null if no match found
    }

    /**
     * Gets the current resource bundle.
     *
     * @return the current ResourceBundle
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Gets a specific localized string from the resource bundle.
     *
     * @param key the key for the localized string
     * @return the localized string
     */
    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Gets the current locale.
     *
     * @return the current locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Loads the resource bundle for the current locale.
     */
    private void loadResourceBundle() {
        // Assuming the resource bundle is named 'labels' and is in the 'com.library.i18n' package
        resourceBundle = ResourceBundle.getBundle("com.library.i18n.labels", locale);
    }
}
