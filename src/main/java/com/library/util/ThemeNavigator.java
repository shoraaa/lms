package com.library.util;

import javafx.application.Application;
import java.util.HashMap;
import java.util.Map;

/**
 * ThemeNavigator manages the switching of application themes.
 */
public class ThemeNavigator {

    private static ThemeNavigator instance; // Singleton instance
    private final Map<String, String> themeMap = new HashMap<>(); // Theme name to stylesheet map

    // Private constructor to prevent direct instantiation
    private ThemeNavigator() {
        themeMap.put("Primer Light", new atlantafx.base.theme.PrimerLight().getUserAgentStylesheet());
        themeMap.put("Primer Dark", new atlantafx.base.theme.PrimerDark().getUserAgentStylesheet());
        themeMap.put("Cupertino Light", new atlantafx.base.theme.CupertinoLight().getUserAgentStylesheet());
        themeMap.put("Cupertino Dark", new atlantafx.base.theme.CupertinoDark().getUserAgentStylesheet());
        themeMap.put("Nord Light", new atlantafx.base.theme.NordLight().getUserAgentStylesheet());
        themeMap.put("Nord Dark", new atlantafx.base.theme.NordDark().getUserAgentStylesheet());
        themeMap.put("Dracula", new atlantafx.base.theme.Dracula().getUserAgentStylesheet());
    }

    /**
     * Returns the singleton instance of ThemeNavigator.
     * 
     * @return the singleton instance of ThemeNavigator
     */
    public static ThemeNavigator getInstance() {
        if (instance == null) {
            synchronized (ThemeNavigator.class) {
                if (instance == null) {
                    instance = new ThemeNavigator();
                }
            }
        }
        return instance;
    }

    /**
     * Switches the application's theme to the specified theme.
     * 
     * @param themeName the name of the theme to apply
     */
    public void switchTheme(String themeName) {
        String themeStylesheet = themeMap.get(themeName);
        if (themeStylesheet != null) {
            Application.setUserAgentStylesheet(themeStylesheet);
        }
    }

    /**
     * Returns the available theme names.
     * 
     * @return an array of available theme names
     */
    public String[] getAvailableThemes() {
        return themeMap.keySet().toArray(new String[0]);
    }

    /**
     * Gets the current theme name based on the active stylesheet.
     * 
     * @return the current theme name
     */
    public String getCurrentTheme() {
        String currentTheme = Application.getUserAgentStylesheet();
        for (Map.Entry<String, String> entry : themeMap.entrySet()) {
            if (entry.getValue().equals(currentTheme)) {
                return entry.getKey();
            }
        }
        return "Primer Light"; // Default theme if no match found
    }
}
