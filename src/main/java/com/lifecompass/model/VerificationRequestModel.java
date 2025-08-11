package com.lifecompass.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class VerificationRequestModel {
    @DocumentId
    private String id; // Document ID of the request
    private String entityId; // The ID of the User or Psychologist associated with this request
    private String entityName;
    private String entityEmail;
    private String role; // "Psychologist" or "User"
    private String requestType; // e.g., "Licensed Clinical Psychologist", "Account Recovery Request"
    private String status; // "Under Review", "Documents Pending", "Approved", "Rejected"

    @ServerTimestamp
    private Date submittedAt;

    private Boolean documentsUploaded; // Optional, to indicate if docs are there
    private List<String> documentUrls; // Optional, if you store doc URLs in request


    // No-arg constructor required for Firestore deserialization
    public VerificationRequestModel() {}

    public VerificationRequestModel(String id, String entityId, String entityName, String entityEmail, String role, String requestType, String status, Date submittedAt, Boolean documentsUploaded, List<String> documentUrls) {
        this.id = id;
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityEmail = entityEmail;
        this.role = role;
        this.requestType = requestType;
        this.status = status;
        this.submittedAt = submittedAt;
        this.documentsUploaded = documentsUploaded;
        this.documentUrls = documentUrls;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public String getEntityEmail() { return entityEmail; }
    public void setEntityEmail(String entityEmail) { this.entityEmail = entityEmail; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }
    public Boolean getDocumentsUploaded() { return documentsUploaded; }
    public void setDocumentsUploaded(Boolean documentsUploaded) { this.documentsUploaded = documentsUploaded; }
    public List<String> getDocumentUrls() { return documentUrls; }
    public void setDocumentUrls(List<String> documentUrls) { this.documentUrls = documentUrls; }
}
