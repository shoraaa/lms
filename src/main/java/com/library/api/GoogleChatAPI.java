package com.library.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.util.ErrorHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GoogleChatAPI {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyAEZRZEQLmVJCnJKio9KW1ciGSPRzQ1J2Y";
    private static final String PROMPT =
            "You are a helpful librarian chat bot. Give the user directly the answer.\n";

    /**
     * Sends a prompt to the Google Chat API and returns the response.
     *
     * @param prompt The input message to send to the API.
     * @return The response from the API.
     */
    public static String sendToApi(String prompt) {
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
                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", prompt);

            // Send JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br =
                             new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return parseResponse(response.toString());
                }
            } else {
                ErrorHandler.showErrorDialog(new Throwable("Error: " + responseCode));
            }
        } catch (Exception e) {
            ErrorHandler.showErrorDialog(e);
        }
        return "";
    }

    /**
     * Parses the API response to extract the text answer.
     *
     * @param response The raw JSON response from the API.
     * @return The extracted text answer.
     */
    private static String parseResponse(String response) {
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject content = firstCandidate.getAsJsonObject("content");
        JsonArray parts = content.getAsJsonArray("parts");
        JsonObject firstPart = parts.get(0).getAsJsonObject();
        return firstPart.get("text").getAsString();
    }

    /**
     * Prepares the context and sends it to the API to get a response.
     *
     * @param context A list of context messages to include in the API request.
     * @return The response from the API.
     * @throws Exception If an error occurs while sending the request.
     */
    public static String getAiResponse(List<String> context) throws Exception {
        // Build the context string by removing special characters
        StringBuilder contextString = new StringBuilder();
        for (String msg : context) {
            msg = msg.replaceAll("[^a-zA-Z0-9\\s]", ""); // Remove special characters
            contextString.append(msg).append("\n");
        }

        // Prepare the final payload
        String payload = PROMPT + "Context:\n" + contextString;
        System.out.println("Payload: " + payload);

        // Send the payload to the API and return the response
        return sendToApi(payload);
    }
}
