package com.library.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.util.ErrorHandler;

public class GoogleChatAPI {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyAEZRZEQLmVJCnJKio9KW1ciGSPRzQ1J2Y";
    private static final String PROMPT = "You are a helpful librarian chat bot. Give user directly the answer.\n";

    public static String sendToAPI(String prompt) {
        try {
            // Create URL and connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set HTTP method to POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Prepare JSON payload
            String jsonInputString = String.format(
                "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                prompt
            );

            // Send JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
                    JsonObject firstPart = parts.get(0).getAsJsonObject();

                    return firstPart.get("text").getAsString();
                }
            } else {
                ErrorHandler.showErrorDialog(new Throwable("Error: " + responseCode));
            }
        } catch (Exception e) {
            ErrorHandler.showErrorDialog(e);
        }
        return "";
    }

    public static String getAIResponse(List<String> context) throws Exception {
        // Example: Include context in the API request payload
        StringBuilder contextString = new StringBuilder();
        for (String msg : context) {
            // Parse the message for the API
            msg = msg.replaceAll("[^a-zA-Z0-9\\s]", ""); // Remove special characters
            contextString.append(msg).append("\n");
        }
        String payload = PROMPT + "Context:\n" + contextString;
        System.out.println("Payload: " + payload);
    
        // Send payload to the API and return the response
        return sendToAPI(payload);
    }
    
}
