package com.lifecompass.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Article {
    private String id; // Will store the article URL
    private String category;
    private String title;
    private String author;
    private String readTime;
    private String description;
    // You might also want to add urlToImage if you plan to display images
    // private String urlToImage;

    public Article() {
        // Default constructor needed for Jackson deserialization
    }

    public Article(String id, String category, String title, String author, String readTime, String description) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.author = author;
        this.readTime = readTime;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getReadTime() { return readTime; }
    public String getDescription() { return description; }
    // public String getUrlToImage() { return urlToImage; } // If added

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setReadTime(String readTime) { this.readTime = readTime; }
    public void setDescription(String description) { this.description = description; }
    // public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; } // If added
}