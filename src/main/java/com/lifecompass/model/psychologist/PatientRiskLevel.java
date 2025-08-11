package com.lifecompass.model.psychologist;

public class PatientRiskLevel {
    private String patientId;
    private String patientName;
    private String initials; // e.g., "PA" for Patient A
    private String riskLevel; // e.g., "low", "medium", "high"
    private String lastUpdated; // e.g., "2 days ago" or a date/time string

    // Default constructor for Firestore deserialization (if needed)
    public PatientRiskLevel() {
    }

    public PatientRiskLevel(String patientId, String patientName, String initials, String riskLevel, String lastUpdated) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.initials = initials;
        this.riskLevel = riskLevel;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return "PatientRiskLevel{" +
               "patientId='" + patientId + '\'' +
               ", patientName='" + patientName + '\'' +
               ", initials='" + initials + '\'' +
               ", riskLevel='" + riskLevel + '\'' +
               ", lastUpdated='" + lastUpdated + '\'' +
               '}';
    }
}