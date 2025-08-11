package com.lifecompass.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;

public class CrisisSituation {
    @DocumentId
    private String id;
    private String userId;
    private String description;
    private String severity; // "High", "Medium", "Low"
    private String status; // "Active Response", "Monitoring", "Emergency Protocol Activated", "Resolved", "Follow-up Needed"
    private String assignedToPsychologistId; // Store as name for simplicity, or ID for lookup
    private Boolean protocolActivated; // New field for Emergency Protocol button state
    private Boolean authoritiesContacted; // New field for Contact Authorities button state

    @ServerTimestamp
    private Date timestamp; // Represents when it was reported
    private Date lastUpdatedAt; // To track last status change or action


    public CrisisSituation() {}

    public CrisisSituation(String id, String userId, String description, String severity, String status, String assignedToPsychologistId, Boolean protocolActivated, Boolean authoritiesContacted, Date timestamp, Date lastUpdatedAt) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.assignedToPsychologistId = assignedToPsychologistId;
        this.protocolActivated = protocolActivated;
        this.authoritiesContacted = authoritiesContacted;
        this.timestamp = timestamp;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    // Getters and Setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedToPsychologistId() { return assignedToPsychologistId; }
    public void setAssignedToPsychologistId(String assignedToPsychologistId) { this.assignedToPsychologistId = assignedToPsychologistId; }
    public Boolean getProtocolActivated() { return protocolActivated; }
    public void setProtocolActivated(Boolean protocolActivated) { this.protocolActivated = protocolActivated; }
    public Boolean getAuthoritiesContacted() { return authoritiesContacted; }
    public void setAuthoritiesContacted(Boolean authoritiesContacted) { this.authoritiesContacted = authoritiesContacted; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public Date getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Date lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
}