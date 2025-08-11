package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;

public class PsychologistNotification {
    @DocumentId
    private String id;
    private String title;
    private String message;
    private String time; 
    private String type; // e.g., "APPOINTMENT", "CRISIS", "INFO"
    private boolean isRead;
    private long timestamp; // For ordering notifications

    public PsychologistNotification() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}