package com.library.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.library.util.ErrorHandler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class GoogleBooksAPI {

    /**
     * Fetches the JSON document from Google Books API based on ISBN or title.
     *
     * @param isbnOrTitle The ISBN or title of the book.
     * @param type        The type of search ('isbn' or 'title').
     * @return The JSON object containing book information, or null if an error occurs.
     */
    public static JsonObject fetchDocumentJson(String isbnOrTitle, String type) {
        String urlString = buildUrl(isbnOrTitle, type);

        if (urlString == null) {
            return null;
        }

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();

                if (response.has("items")) {
                    JsonObject bookInfo = response.getAsJsonArray("items")
                            .get(0)
                            .getAsJsonObject()
                            .getAsJsonObject("volumeInfo");
                    return bookInfo;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            ErrorHandler.showErrorDialog(e); // Log the error if necessary
            return null;
        }
    }

    /**
     * Builds the URL for the Google Books API request based on the ISBN or title.
     *
     * @param isbnOrTitle The ISBN or title to search.
     * @param type        The type of search ('isbn' or 'title').
     * @return The URL string to be used in the request, or null if the type is invalid.
     */
    private static String buildUrl(String isbnOrTitle, String type) {
        String urlString;

        if ("isbn".equals(type)) {
            isbnOrTitle = isbnOrTitle.replace("-", "");
            urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnOrTitle;
        } else if ("title".equals(type)) {
            isbnOrTitle = isbnOrTitle.replace(" ", "%20");
            urlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + isbnOrTitle;
        } else {
            return null;
        }

        return urlString;
    }
}
