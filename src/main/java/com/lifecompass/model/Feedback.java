package com.lifecompass.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Feedback {
    private String id; // Firestore document ID
    private String userId;
    private String userEmail;
    private String subject;
    private String content;
    private String timestamp; // Stored as ISO_LOCAL_DATE_TIME string for Firestore

    public Feedback() {
        // Default constructor for Firestore deserialization
    }

    public Feedback(String userId, String userEmail, String subject, String content) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.subject = subject;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // Helper to get LocalDateTime object if needed
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.parse(this.timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Method to convert Feedback object to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("userEmail", userEmail);
        map.put("subject", subject);
        map.put("content", content);
        map.put("timestamp", timestamp);
        return map;
    }

    // Static method to create Feedback object from Firestore Map
    public static Feedback fromMap(String id, Map<String, Object> map) {
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setUserId((String) map.get("userId"));
        feedback.setUserEmail((String) map.get("userEmail"));
        feedback.setSubject((String) map.get("subject"));
        feedback.setContent((String) map.get("content"));
        feedback.setTimestamp((String) map.get("timestamp"));
        return feedback;
    }
}