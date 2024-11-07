package com.library.api;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoogleBooksAPI {

/**
 * Fetches the ISBN-13 (and optionally ISBN-10) for a book given its title.
 *
 * This method sends an HTTP GET request to the Google Books API to search for books
 * matching the specified title. If a book is found, it retrieves and prints the ISBN-13
 * and, if available, the ISBN-10 of the first search result.
 *
 * @param title The title of the book for which to fetch the ISBN.
 */
    public static void fetchIsbnByTitle(String title) {
        try {
            String urlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title.replace(" ", "+");
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (response.has("items")) {
                JsonObject bookInfo = response.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                
                // Get ISBN
                JsonObject industryIdentifiers = bookInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject();
                String isbn13 = industryIdentifiers.get("identifier").getAsString(); // ISBN 13
                System.out.println("ISBN 13: " + isbn13);
                
                // You can also fetch ISBN 10 if needed
                String isbn10 = bookInfo.getAsJsonArray("industryIdentifiers").size() > 1 ? 
                                 bookInfo.getAsJsonArray("industryIdentifiers").get(1).getAsJsonObject().get("identifier").getAsString() 
                                 : null;
                if (isbn10 != null) {
                    System.out.println("ISBN 10: " + isbn10);
                }
            } else {
                System.out.println("No results found for book title: " + title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        fetchIsbnByTitle("Java Programming");  // Example title
    }
}
