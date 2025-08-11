package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Patient {
    @DocumentId
    private String id;
    @PropertyName("fullName")
    private String name;
    private String initials;
    private String lastContact;
    private String status;
    @PropertyName("riskLevel")
    private String risk;
    // For Firestore queries, ensure this field matches what's used in DAO
    private String assignedPsychologistId; 
    
    private String email;
    private String role;
    private String gender;
    private String dateOfBirth;
    private String city;
    private String state;
    private String country;
    private String pinZipCode;
    private String phoneNumber;
    private String familyPhoneNumber;
    private String friendsPhoneNumber;
    private boolean termsAgreed;
    @PropertyName("privacyAgaged") 
    private boolean privacyAgreed;
    private Date createdAt;

    private String username;
    private String verificationStatus;

    public Patient() {

     this.name = "";
        this.initials = ""; 
        this.lastContact = "N/A";
        this.status = "Unknown";
        this.risk = "Low Risk";
        this.email = "";
        this.role = "";
        this.gender = "";
        this.dateOfBirth = null; 
        this.city = "";
        this.state = "";
        this.country = "";
        this.pinZipCode = "";
        this.phoneNumber = "";
        this.familyPhoneNumber = "";
        this.friendsPhoneNumber = "";
        this.termsAgreed = false;
        this.privacyAgreed = false;
        this.createdAt = null;

        this.username = "";
        this.verificationStatus = "";
}
 public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        // Derive initials here, after name is set. This handles cases where 'toObject' uses the setter.
        if (name != null && !name.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            String[] parts = name.split(" ");
            if (parts.length > 0) {
                sb.append(parts[0].charAt(0));
                if (parts.length > 1) {
                    sb.append(parts[parts.length - 1].charAt(0));
                }
            }
            this.initials = sb.toString().toUpperCase();
        } else {
            this.initials = ""; // Ensure it's not null if name is null or empty
        }
    }

    public String getInitials() {
        // Fallback derivation if somehow 'name' was set directly bypassing setName
        if (this.initials == null && this.name != null) {
            StringBuilder sb = new StringBuilder();
            String[] parts = this.name.split(" ");
            if (parts.length > 0) {
                sb.append(parts[0].charAt(0));
                if (parts.length > 1) {
                    sb.append(parts[parts.length - 1].charAt(0));
                }
            }
            this.initials = sb.toString().toUpperCase();
        }
        return initials;
    }

    public String getLastContact() { return lastContact; }
    public void setLastContact(String lastContact) { this.lastContact = lastContact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public String getAssignedPsychologistId() { return assignedPsychologistId; }
    public void setAssignedPsychologistId(String assignedPsychologistId) { this.assignedPsychologistId = assignedPsychologistId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // public LocalDate getDateOfBirth() { return dateOfBirth; }
    // public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getDateOfBirth() { return dateOfBirth; } // <--- Getter now returns String
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; } // <--- Setter now accepts String

    // Helper to get LocalDate if needed by UI
    public LocalDate getLocalDateOfBirth() {
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            try {
                return LocalDate.parse(dateOfBirth);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing dateOfBirth String to LocalDate: " + dateOfBirth + " - " + e.getMessage());
            }
        }
        return null;
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPinZipCode() { return pinZipCode; }
    public void setPinZipCode(String pinZipCode) { this.pinZipCode = pinZipCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFamilyPhoneNumber() { return familyPhoneNumber; }
    public void setFamilyPhoneNumber(String familyPhoneNumber) { this.familyPhoneNumber = familyPhoneNumber; }

    public String getFriendsPhoneNumber() { return friendsPhoneNumber; }
    public void setFriendsPhoneNumber(String friendsPhoneNumber) { this.friendsPhoneNumber = friendsPhoneNumber; }

    public boolean isTermsAgreed() { return termsAgreed; }
    public void setTermsAgreed(boolean termsAgreed) { this.termsAgreed = termsAgreed; }

    public boolean isPrivacyAgreed() { return privacyAgreed; }
    public void setPrivacyAgreed(boolean privacyAgreed) { this.privacyAgreed = privacyAgreed; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // CHANGE: Added getter and setter for the new 'verificationStatus' field.
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}