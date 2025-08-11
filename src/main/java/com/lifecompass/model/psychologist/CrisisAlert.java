package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CrisisAlert {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private String psychologistId;
    private String severity;
    private String trigger;
    private String description;
    
    @ServerTimestamp
    private Date timestamp;
    
    private String status;

    public CrisisAlert() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAlertId() { return id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getPsychologistId() { return psychologistId; }
    public void setPsychologistId(String psychologistId) { this.psychologistId = psychologistId; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getTimeAgo() {
        if (timestamp == null) {
            return "N/A";
        }
        Instant alertInstant = timestamp.toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(alertInstant, now);

        if (duration.toMinutes() < 1) {
            return "just now";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + " minutes ago";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + " hours ago";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + " days ago";
        } else if (duration.toDays() < 30) {
            return (duration.toDays() / 7) + " weeks ago";
        } else if (duration.toDays() < 365) {
            return (duration.toDays() / 30) + " months ago";
        } else {
            return (duration.toDays() / 365) + " years ago";
        }
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}