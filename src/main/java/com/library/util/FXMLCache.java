package com.library.util;

import javafx.scene.Parent;
import java.util.HashMap;
import java.util.Map;

public class FXMLCache {

    // Map to hold the cache of FXML files
    private static final Map<String, Parent> cache = new HashMap<>();

    // Get a cached FXML if available
    public static Parent getFXML(String fxml) {
        return cache.get(fxml);
    }

    // Cache the loaded FXML
    public static void cacheFXML(String fxml, Parent parent) {
        cache.put(fxml, parent);
    }

    // Check if the FXML is cached
    public static boolean isCached(String fxml) {
        return cache.containsKey(fxml);
    }

    // Clear the cache (optional, for cache management)
    public static void clearCache() {
        cache.clear();
    }
}
