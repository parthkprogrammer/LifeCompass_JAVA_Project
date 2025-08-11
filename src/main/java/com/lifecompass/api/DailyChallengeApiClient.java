package com.lifecompass.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifecompass.model.DailyChallenge;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DailyChallengeApiClient {

    // --- IMPORTANT: REPLACE WITH YOUR ACTUAL GEMINI API KEY ---
    private static final String GEMINI_API_KEY = "AIzaSyAxj0rIEqBQQ0ValBcnnk5iiK5Uqj9IlHo"; // <--- PASTE YOUR GEMINI API KEY HERE
    // -----------------------------------------------------------------

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"; // Using gemini-pro for better text generation

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DailyChallengeApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches daily challenges by generating them using the Gemini API.
     * The 'theme' parameter acts as a prompt modifier (e.g., "for stress relief").
     *
     * @param theme An optional theme or focus for the daily challenge (e.g., "mindfulness", "stress relief").
     * Can be an empty string for a general challenge.
     * @return A CompletableFuture that will complete with a list of DailyChallenge objects.
     */
    public CompletableFuture<List<DailyChallenge>> fetchDailyChallenges(String theme) {
        if (GEMINI_API_KEY.equals("YOUR_GEMINI_API_KEY_HERE") || GEMINI_API_KEY.isEmpty()) {
            System.err.println("Gemini API Key is not set. Please replace 'YOUR_GEMINI_API_KEY_HERE' in DailyChallengeApiClient.java");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        List<DailyChallenge> challenges = new ArrayList<>();
        String actualTheme = theme != null && !theme.isEmpty() ? "Focus on " + theme + "." : "";
        String prompt = "Generate a single, concise, actionable daily mental health challenge. " + actualTheme + " The challenge should be a short sentence, suitable for a mobile app. Do NOT include any introductory or concluding phrases, or numbering, just the challenge itself. Example: 'Practice 5 minutes of mindful breathing.'";

        String requestBody = String.format(
            "{\"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"%s\"}]}]}",
            prompt.replace("\"", "\\\"").replace("\n", "\\n") // Escape quotes and newlines in prompt
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + GEMINI_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("Generating daily challenge using Gemini API...");

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(responseBody -> {
                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        if (rootNode.has("candidates") && rootNode.get("candidates").isArray()) {
                            JsonNode candidate = rootNode.get("candidates").get(0);
                            if (candidate.has("content") && candidate.get("content").has("parts") && candidate.get("content").get("parts").get(0).has("text")) {
                                String generatedText = candidate.get("content").get("parts").get(0).get("text").asText();
                                // Clean up generated text (remove extra quotes, leading/trailing spaces)
                                generatedText = generatedText.trim().replace("\"", "");

                                // Create a unique ID and common XP reward for generated challenges
                                challenges.add(new DailyChallenge(
                                    "gemini-challenge-" + System.currentTimeMillis(), // Unique ID
                                    generatedText,
                                    10, // Example XP reward
                                    false // Initially not completed
                                ));
                                System.out.println("Generated challenge: " + generatedText);
                            } else {
                                System.err.println("Gemini API response format unexpected: missing content parts.");
                            }
                        } else if (rootNode.has("error")) {
                            System.err.println("Gemini API Error: " + rootNode.get("error").get("message").asText());
                        } else {
                            System.err.println("Gemini API response format unexpected: missing candidates or error.");
                            System.err.println("Response: " + responseBody);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing Gemini API response: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return challenges;
                })
                .exceptionally(e -> {
                    System.err.println("Error communicating with Gemini API: " + e.getMessage());
                    e.printStackTrace();
                    return new ArrayList<>(); // Return empty list on network/API communication error
                });
    }
}