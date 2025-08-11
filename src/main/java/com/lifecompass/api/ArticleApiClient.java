package com.lifecompass.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifecompass.model.Article;

import java.net.URI;
import java.net.URLEncoder; // Required for URL encoding query parameters
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets; // For URL encoding charset
import java.time.LocalDate; // For date filtering if desired
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArticleApiClient {

    // --- IMPORTANT: REPLACE WITH YOUR ACTUAL NEWSAPI.ORG API KEY ---
    private static final String API_KEY = "4e35f30f6c1644d590c00cd2ae09486b"; // <--- YOUR ACTUAL API KEY
    // -----------------------------------------------------------------

    private static final String NEWS_API_BASE_URL = "https://newsapi.org/v2/everything";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ArticleApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches articles from NewsAPI.org based on a query.
     *
     * @param query Keywords or phrases to search for (e.g., "mental health OR anxiety")
     * @return A CompletableFuture that will complete with a list of Article objects.
     */
    public CompletableFuture<List<Article>> fetchArticles(String query) {
        // --- FIX START ---
        // This condition should check if the API_KEY is still the default placeholder,
        // NOT if it matches the actual key you've put in.
        // A better check would be if it's empty or the original placeholder.
        if (API_KEY.isEmpty() || API_KEY.equals("YOUR_NEWSAPI_ORG_API_KEY_HERE")) { // Changed condition
            System.err.println("NewsAPI.org API Key is not set or is still the placeholder. Please replace 'YOUR_NEWSAPI_ORG_API_KEY_HERE' in ArticleApiClient.java with your actual key.");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        // --- FIX END ---

        // URL encode the query
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // Optional: Filter by date (e.g., last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        String fromDate = thirtyDaysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Build the URL with parameters
        // Example: https://newsapi.org/v2/everything?q=mental+health&language=en&sortBy=relevancy&pageSize=10&apiKey=YOUR_KEY
        String url = String.format("%s?q=%s&language=en&sortBy=relevancy&pageSize=10&from=%s&apiKey=%s",
                                   NEWS_API_BASE_URL, encodedQuery, fromDate, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "LifeCompassApp/1.0") // Good practice to identify your application
                // Alternatively, you can pass API key in header: .header("X-Api-Key", API_KEY)
                .GET()
                .build();

        System.out.println("Fetching articles from NewsAPI.org: " + url); // For debugging

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseNewsApiArticles)
                .exceptionally(e -> {
                    System.err.println("Error fetching articles from NewsAPI.org: " + e.getMessage());
                    e.printStackTrace(); // Print full stack trace for debugging
                    return new ArrayList<>(); // Return empty list on error
                });
    }

    private List<Article> parseNewsApiArticles(String responseBody) {
        List<Article> articles = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            if (rootNode.has("status") && "ok".equals(rootNode.get("status").asText())) {
                JsonNode articlesArray = rootNode.get("articles");
                if (articlesArray != null && articlesArray.isArray()) {
                    for (JsonNode articleNode : articlesArray) {
                        // NewsAPI.org provides 'url' as the unique identifier for the article link
                        String id = articleNode.has("url") && !articleNode.get("url").isNull() ? articleNode.get("url").asText() : "about:blank";
                        String title = articleNode.has("title") && !articleNode.get("title").isNull() ? articleNode.get("title").asText() : "No Title";
                        String description = articleNode.has("description") && !articleNode.get("description").isNull() ? articleNode.get("description").asText() : "No description available.";
                        String author = articleNode.has("author") && !articleNode.get("author").isNull() ? articleNode.get("author").asText() : "Unknown Author";
                        // NewsAPI's 'publishedAt' is an ISO 8601 string, you might want to format it
                        // For simplicity, we'll just use it directly or extract date part
                        String publishedAt = articleNode.has("publishedAt") && !articleNode.get("publishedAt").isNull() ? articleNode.get("publishedAt").asText() : "";
                        String category = "Mental Health"; // NewsAPI doesn't provide a direct 'category' for general search, set a default

                        // Simple estimation for read time (e.g., 1 min per 150-200 words)
                        String content = articleNode.has("content") && !articleNode.get("content").isNull() ? articleNode.get("content").asText() : "";
                        int wordCount = content.split("\\s+").length;
                        String readTime = (wordCount > 0) ? (Math.max(1, wordCount / 180) + " min read") : "N/A";

                        // Add only if URL is valid to avoid "about:blank" articles
                        if (!id.equals("about:blank") && !title.equals("No Title")) {
                            articles.add(new Article(id, category, title, author, readTime, description));
                        }
                    }
                }
            } else {
                String error = rootNode.has("message") ? rootNode.get("message").asText() : "Unknown error from NewsAPI.org";
                System.err.println("NewsAPI.org Error Response: " + error);
            }
        } catch (Exception e) {
            System.err.println("Error parsing NewsAPI.org response: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }
}
