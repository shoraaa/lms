package com.library.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageDownloader {

    // Method to download image and cache it
    public static File downloadImageToCache(String imageUrl, String fileName) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        try {
            // Create a URL object
            URL url = new URL(imageUrl);
            // Open an input stream to the URL
            InputStream inputStream = url.openStream();
            
            // Create a file to store the image locally in the cache directory
            Path cacheDir = Paths.get("./cache/images");
            Files.createDirectories(cacheDir);  // Create the cache directory if it doesn't exist
            // Normalize the file name to remove special characters
            String normalizedFileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            Path outputPath = cacheDir.resolve(normalizedFileName);

            // Save the image to the local file
            Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            
            return outputPath.toFile();  // Return the cached file

        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Return null if any error occurs
        }
    }

    // Method to set the downloaded image in the ImageView
    public static String setImageFromCache(String imageUrl, ImageView imageView, String filename) {
        File cachedImage = downloadImageToCache(imageUrl, filename);
        if (cachedImage != null) {
            imageView.setImage(new Image(cachedImage.toURI().toString()));
            return cachedImage.toURI().toString();
        } else {
            imageView.setImage(null);  // Set to null if image couldn't be downloaded
            return null;
        }
    }
}
