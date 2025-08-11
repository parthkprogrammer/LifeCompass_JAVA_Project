package com.lifecompass.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single journal entry, adapted for Firebase Firestore.
 * Includes annotations for automatic mapping from Firestore documents.
 */
public class JournalEntry {

    @DocumentId
    private String id;
    private String userId;
    private String title;
    private String content;
    @ServerTimestamp
    private Date createdAt;
    private List<String> tags;
    private String mood;
    private String attachedImagePath;

    public JournalEntry() {
        // Default constructor for Firebase
    }

    public JournalEntry(String userId, String title, String content, List<String> tags, String mood, String attachedImagePath) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.mood = mood;
        this.attachedImagePath = attachedImagePath;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Date getCreatedAt() { return createdAt; }
    public List<String> getTags() { return tags; }
    public String getMood() { return mood; }
    public String getAttachedImagePath() { return attachedImagePath; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setMood(String mood) { this.mood = mood; }
    public void setAttachedImagePath(String attachedImagePath) { this.attachedImagePath = attachedImagePath; }

    public LocalDateTime getTimestampAsLocalDateTime() {
        return createdAt != null ? createdAt.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime() : null;
    }

    public String getFormattedDate() {
        LocalDateTime timestamp = getTimestampAsLocalDateTime();
        if (timestamp == null) {
            return "N/A";
        }

        LocalDateTime now = LocalDateTime.now();
        if (timestamp.toLocalDate().isEqual(now.toLocalDate())) {
            return "Today, " + timestamp.format(DateTimeFormatter.ofPattern("h:mm a"));
        } else if (timestamp.toLocalDate().isEqual(now.toLocalDate().minusDays(1))) {
            return "Yesterday, " + timestamp.format(DateTimeFormatter.ofPattern("h:mm a"));
        } else {
            return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a"));
        }
    }
}