package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;

public class PsychologistSettings {
    @DocumentId
    private String psychologistId;

    private String sessionDuration;
    private boolean autoApproveAppointments;
    private String preferredMode;
    private boolean allowRescheduling;

    private boolean receiveCrisisAlerts;
    private String alertMode;
    private boolean crisisProtocolShortcut;

    private String accessUserJournals;
    private String moodGraphVisibility;
    private String autoDeleteData;

    private boolean appointmentReminders;
    private boolean sessionFeedbackRequests;
    private boolean adminAnnouncements;

    private String language;
    private String theme;
    private String fontSize;

    public PsychologistSettings() {}

    // Getters and Setters
    public String getPsychologistId() { return psychologistId; }
    public void setPsychologistId(String psychologistId) { this.psychologistId = psychologistId; }
    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }
    public boolean isAutoApproveAppointments() { return autoApproveAppointments; }
    public void setAutoApproveAppointments(boolean autoApproveAppointments) { this.autoApproveAppointments = autoApproveAppointments; }
    public String getPreferredMode() { return preferredMode; }
    public void setPreferredMode(String preferredMode) { this.preferredMode = preferredMode; }
    public boolean isAllowRescheduling() { return allowRescheduling; }
    public void setAllowRescheduling(boolean allowRescheduling) { this.allowRescheduling = allowRescheduling; }
    public boolean isReceiveCrisisAlerts() { return receiveCrisisAlerts; }
    public void setReceiveCrisisAlerts(boolean receiveCrisisAlerts) { this.receiveCrisisAlerts = receiveCrisisAlerts; }
    public String getAlertMode() { return alertMode; }
    public void setAlertMode(String alertMode) { this.alertMode = alertMode; }
    public boolean isCrisisProtocolShortcut() { return crisisProtocolShortcut; }
    public void setCrisisProtocolShortcut(boolean crisisProtocolShortcut) { this.crisisProtocolShortcut = crisisProtocolShortcut; }
    public String getAccessUserJournals() { return accessUserJournals; }
    public void setAccessUserJournals(String accessUserJournals) { this.accessUserJournals = accessUserJournals; }
    public String getMoodGraphVisibility() { return moodGraphVisibility; }
    public void setMoodGraphVisibility(String moodGraphVisibility) { this.moodGraphVisibility = moodGraphVisibility; }
    public String getAutoDeleteData() { return autoDeleteData; }
    public void setAutoDeleteData(String autoDeleteData) { this.autoDeleteData = autoDeleteData; }
    public boolean isAppointmentReminders() { return appointmentReminders; }
    public void setAppointmentReminders(boolean appointmentReminders) { this.appointmentReminders = appointmentReminders; }
    public boolean isSessionFeedbackRequests() { return sessionFeedbackRequests; }
    public void setSessionFeedbackRequests(boolean sessionFeedbackRequests) { this.sessionFeedbackRequests = sessionFeedbackRequests; }
    public boolean isAdminAnnouncements() { return adminAnnouncements; }
    public void setAdminAnnouncements(boolean adminAnnouncements) { this.adminAnnouncements = adminAnnouncements; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getTheme() { return theme; }
    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }

    public void setTheme(String value) {
        if (value == null || value.isEmpty()) {
            this.theme = "default"; // Set a default theme if none is provided
        } else {
            this.theme = value;
        }
    }
}