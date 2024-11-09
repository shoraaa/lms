package com.library.api;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.library.model.Document;
import com.library.services.AuthorDAO;


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

    // BookDetails class to hold book information
    public static class BookDetails {
        private String title;
        private String[] authors;
        private String publisher;
        private String[] categories;
        private String datePublished;

        public BookDetails(String title, String[] authors, String publisher, String[] categories, String datePublished) {
            this.title = title;
            this.authors = authors;
            this.publisher = publisher;
            this.categories = categories;
            this.datePublished = datePublished;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String[] getAuthors() {
            return authors;
        }

        public String getPublisher() {
            return publisher;
        }

        public String[] getCategories() {
            return categories;
        }

        public String getDatePublished() {
            return datePublished;
        }

        // To String for easy display
        @Override
        public String toString() {
            return "Title: " + title + "\n" +
                   "Authors: " + String.join(", ", authors) + "\n" +
                   "Publisher: " + publisher + "\n" +
                   "Categories: " + String.join(", ", categories) + "\n" +
                   "Date Published: " + datePublished;
        }
    }

    public static BookDetails fetchBookByIsbn(String isbn) {
        try {
            String urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + (isbn.length() == 10 ? isbn : isbn.replace("-", ""));
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (response.has("items")) {
                JsonObject bookInfo = response.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                
                // Get book title
                String title = bookInfo.get("title").getAsString();
                
                // Get authors
                String[] authors = new String[bookInfo.getAsJsonArray("authors").size()];
                for (int i = 0; i < authors.length; i++) {
                    authors[i] = bookInfo.getAsJsonArray("authors").get(i).getAsString();
                }
                
                // Get publisher
                String publisher = bookInfo.has("publisher") ? bookInfo.get("publisher").getAsString() : "N/A";
                
                // Get categories
                String[] categories = bookInfo.has("categories") ? new String[bookInfo.getAsJsonArray("categories").size()] : new String[0];
                for (int i = 0; i < categories.length; i++) {
                    categories[i] = bookInfo.getAsJsonArray("categories").get(i).getAsString();
                }
                
                // Get date published
                String datePublished = bookInfo.has("publishedDate") ? bookInfo.get("publishedDate").getAsString() : "N/A";
                
                // Return a BookDetails object
                return new BookDetails(title, authors, publisher, categories, datePublished);
            } else {
                System.out.println("No results found for ISBN: " + isbn);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
