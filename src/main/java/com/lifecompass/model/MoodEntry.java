// package com.lifecompass.model; // Corrected package name

// import com.google.cloud.firestore.DocumentSnapshot; // Not directly used in MoodEntry itself, but useful context
// import java.time.LocalDate; // Not directly used in MoodEntry itself, but useful context
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.Map;
// import java.util.HashMap;

// public class MoodEntry {
//     private String id; // Firestore document ID
//     private String userId; // ID of the user who logged this mood
//     private String moodEmoji;
//     private int intensity;
//     private List<String> tags;
//     private String notes;
//     private LocalDateTime timestamp;

//     // Default constructor for Firestore deserialization
//     public MoodEntry() {}

//     public MoodEntry(String id, String userId, String moodEmoji, int intensity, List<String> tags, String notes, LocalDateTime timestamp) {
//         this.id = id;
//         this.userId = userId;
//         this.moodEmoji = moodEmoji;
//         this.intensity = intensity;
//         this.tags = tags;
//         this.notes = notes;
//         this.timestamp = timestamp;
//     }

//     // Getters
//     public String getId() { return id; }
//     public String getUserId() { return userId; }
//     public String getMoodEmoji() { return moodEmoji; }
//     public int getIntensity() { return intensity; }
//     public List<String> getTags() { return tags; }
//     public String getNotes() { return notes; }
//     public LocalDateTime getTimestamp() { return timestamp; }

//     // Setters
//     public void setId(String id) { this.id = id; }
//     public void setUserId(String userId) { this.userId = userId; }
//     public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }
//     public void setIntensity(int intensity) { this.intensity = intensity; }
//     public void setTags(List<String> tags) { this.tags = tags; }
//     public void setNotes(String notes) { this.notes = notes; }
//     public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

//     // Convert MoodEntry object to a Map for Firestore
//     public Map<String, Object> toMap() {
//         Map<String, Object> map = new HashMap<>();
//         map.put("userId", userId);
//         map.put("moodEmoji", moodEmoji);
//         map.put("intensity", intensity);
//         map.put("tags", tags);
//         map.put("notes", notes);
//         map.put("timestamp", timestamp.toString()); // Store as String, or use Timestamp type if preferred
//         return map;
//     }

//     // Create MoodEntry object from Firestore Map
//     public static MoodEntry fromMap(String id, Map<String, Object> map) {
//         MoodEntry entry = new MoodEntry();
//         entry.setId(id);
//         entry.setUserId((String) map.get("userId"));
//         entry.setMoodEmoji((String) map.get("moodEmoji"));
//         entry.setIntensity(((Long) map.get("intensity")).intValue()); // Firestore stores numbers as Long
//         entry.setTags((List<String>) map.get("tags"));
//         entry.setNotes((String) map.get("notes"));
//         entry.setTimestamp(LocalDateTime.parse((String) map.get("timestamp"))); // Parse String back to LocalDateTime
//         return entry;
//     }

//     public String getFormattedDate() {
//         LocalDateTime now = LocalDateTime.now();
//         if (timestamp.toLocalDate().isEqual(now.toLocalDate())) {
//             return "Today, " + timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
//         } else if (timestamp.toLocalDate().isEqual(now.toLocalDate().minusDays(1))) {
//             return "Yesterday, " + timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
//         } else {
//             return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a"));
//         }
//     }
// }


package com.lifecompass.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MoodEntry {
    private String id;
    private String userId;
    private String moodEmoji;
    private int intensity;
    private int energyLevel; // NEW: Energy Level field
    private List<String> tags;
    private String notes;
    private LocalDateTime timestamp;

    public MoodEntry() {}

    public MoodEntry(String id, String userId, String moodEmoji, int intensity, int energyLevel, List<String> tags, String notes, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.moodEmoji = moodEmoji;
        this.intensity = intensity;
        this.energyLevel = energyLevel; // NEW: Initialize energyLevel
        this.tags = tags;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getMoodEmoji() { return moodEmoji; }
    public int getIntensity() { return intensity; }
    public int getEnergyLevel() { return energyLevel; } // NEW: Getter for energyLevel
    public List<String> getTags() { return tags; }
    public String getNotes() { return notes; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public void setEnergyLevel(int energyLevel) { this.energyLevel = energyLevel; } // NEW: Setter for energyLevel
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Convert MoodEntry object to a Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("moodEmoji", moodEmoji);
        map.put("intensity", intensity);
        map.put("energyLevel", energyLevel); // NEW: Add energyLevel to map
        map.put("tags", tags);
        map.put("notes", notes);
        map.put("timestamp", timestamp.toString());
        return map;
    }

    // Create MoodEntry object from Firestore Map
    public static MoodEntry fromMap(String id, Map<String, Object> map) {
        MoodEntry entry = new MoodEntry();
        entry.setId(id);
        entry.setUserId((String) map.get("userId"));
        entry.setMoodEmoji((String) map.get("moodEmoji"));
        entry.setIntensity(((Long) map.get("intensity")).intValue());
        // Handle potential null/missing 'energyLevel' for older entries or if field wasn't always present
        entry.setEnergyLevel(map.containsKey("energyLevel") ? ((Long) map.get("energyLevel")).intValue() : 5); // NEW: Get energyLevel, default to 5
        entry.setTags((List<String>) map.get("tags"));
        entry.setNotes((String) map.get("notes"));
        entry.setTimestamp(LocalDateTime.parse((String) map.get("timestamp")));
        return entry;
    }

    public String getFormattedDate() {
        LocalDateTime now = LocalDateTime.now();
        if (timestamp.toLocalDate().isEqual(now.toLocalDate())) {
            return "Today, " + timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (timestamp.toLocalDate().isEqual(now.toLocalDate().minusDays(1))) {
            return "Yesterday, " + timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else {
            return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a"));
        }
    }
}