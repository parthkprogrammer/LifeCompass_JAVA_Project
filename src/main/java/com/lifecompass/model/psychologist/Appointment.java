package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.Timestamp; 
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date; 

public class Appointment {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    
    @PropertyName("psychologistId") // Maps to Firestore's "psychologistId" (lowercase 'id')
    private String psychologistId; 
    
    private String sessionType;
    private String time;
    private String duration;
    
    private Timestamp date; // Using Firestore's native Timestamp for direct mapping
    
    private String status;

    public Appointment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getPsychologistId() { return psychologistId; }
    public void setPsychologistId(String psychologistId) { this.psychologistId = psychologistId; }
    
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public Timestamp getDate() { return date; } 
    public void setDate(Timestamp date) { this.date = date; } 

    public LocalDate getLocalDate() {
        return (date != null) ? date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}