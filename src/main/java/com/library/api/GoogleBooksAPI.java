package com.library.api;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
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

    // BookDetails class to hold book information
    public static class BookDetails {
        private String title;
        private String[] authors;
        private String publisher;
        private String[] categories;
        private String datePublished;
        private String isbn;
        private String imageUrl;
        private String language;
        private String description;
        

        public BookDetails() {
            this.title = "N/A";
            this.authors = new String[0];
            this.publisher = "N/A";
            this.categories = new String[0];
            this.isbn = "N/A";
            this.datePublished = "N/A";
            this.imageUrl = "N/A";
            this.language = "N/A";
            this.description = "N/A";
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

        public String getIsbn() {
            return isbn;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getLanguage() {
            return language;
        }

        public String getDescription() {
            return description;
        }

        // To String for easy display
        @Override
        public String toString() {
            return "Title: " + title + "\n" +
                   "Authors: " + String.join(", ", authors) + "\n" +
                   "Publisher: " + publisher + "\n" +
                   "Categories: " + String.join(", ", categories) + "\n" +
                   "Date Published: " + datePublished + "\n" +
                   "ISBN: " + isbn + "\n" +
                   "Image URL: " + imageUrl;
        }
    }

    public static BookDetails fetchBookDetails(String isbnOrTitle, String type) {
        System.out.println(isbnOrTitle + " " + type);
        String urlString;

        if (type.equals("isbn")) {
            isbnOrTitle = isbnOrTitle.replace("-", "");
            urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnOrTitle;
        } else if (type.equals("title")) {
            urlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + isbnOrTitle;
        } else {
            return null;
        }

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (response.has("items")) {
                JsonObject bookInfo = response.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("volumeInfo");

                BookDetails bookDetails = new BookDetails();
                bookDetails.title = bookInfo.get("title").getAsString();
                bookDetails.authors = bookInfo.has("authors") ? bookInfo.getAsJsonArray("authors").toString().replace("[", "").replace("]", "").replace("\"", "").split(",") : new String[]{};
                bookDetails.publisher = bookInfo.has("publisher") ? bookInfo.get("publisher").getAsString() : "Unknown";
                bookDetails.categories = bookInfo.has("categories") ? bookInfo.getAsJsonArray("categories").toString().replace("[", "").replace("]", "").replace("\"", "").split(",") : new String[]{};
                bookDetails.datePublished = bookInfo.has("publishedDate") ? bookInfo.get("publishedDate").getAsString() : "Unknown";
                bookDetails.imageUrl = bookInfo.has("imageLinks") ? bookInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "No Image Available";
                bookDetails.language = bookInfo.has("language") ? bookInfo.get("language").getAsString() : "Unknown";
                bookDetails.description = bookInfo.has("description") ? bookInfo.get("description").getAsString() : "No Description Available";
                
                return bookDetails;
            } else {
                System.out.println("No results found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
