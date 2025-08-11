package com.lifecompass.model.psychologist;

import com.google.cloud.firestore.annotation.DocumentId;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Added for safe List initialization in constructor/getters
import java.util.HashMap; // Added for safe Map initialization in constructor/getters

public class PsychologistProfile {
    @DocumentId
    private String id;
    private String fullName;
    private String qualifications;
    private List<String> specializations;
   // private String yearsOfExperience; // Stays as String as per your model
    private int yearsOfExperience; 
    private String shortBio;
    private double consultationFee;
    private String sessionDuration;
    private String modeOfTherapy;
    private String availableDaysTime;
    private List<String> languagesSpoken;
    private String degreesInstitutions;
    private String licenseNumber;
    private String licenseIssuingBody;
    private String certificationsWorkshops;
    private boolean isVerified;
    private String profilePicUrl;
    private Map<String, String> clinicInfo;
    private String dateOfBirth;
    public PsychologistProfile() {
        // Initialize lists and map to prevent NullPointerExceptions
        this.specializations = new ArrayList<>();
        this.languagesSpoken = new ArrayList<>();
        this.clinicInfo = new HashMap<>();
        this.yearsOfExperience = 0; // Initialize to default for int
        this.dateOfBirth = null;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    
    public List<String> getSpecializations() { 
        return specializations != null ? specializations : new ArrayList<>(); // Ensure not null
    }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }
    
    // public String getYearsOfExperience() { return yearsOfExperience; }
    // public void setYearsOfExperience(String yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public int getYearsOfExperience() { return yearsOfExperience; } // <--- Update getter return type
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; } // <--- Update setter parameter type
    
    public String getShortBio() { return shortBio; }
    public void setShortBio(String shortBio) { this.shortBio = shortBio; }
    
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    
    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }
    
    public String getModeOfTherapy() { return modeOfTherapy; }
    public void setModeOfTherapy(String modeOfTherapy) { this.modeOfTherapy = modeOfTherapy; }
    
    public String getAvailableDaysTime() { return availableDaysTime; }
    public void setAvailableDaysTime(String availableDaysTime) { this.availableDaysTime = availableDaysTime; }
    
    public List<String> getLanguagesSpoken() { 
        return languagesSpoken != null ? languagesSpoken : new ArrayList<>(); // Ensure not null
    }
    public void setLanguagesSpoken(List<String> languagesSpoken) { this.languagesSpoken = languagesSpoken; }
    
    public String getDegreesInstitutions() { return degreesInstitutions; }
    public void setDegreesInstitutions(String degreesInstitutions) { this.degreesInstitutions = degreesInstitutions; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getLicenseIssuingBody() { return licenseIssuingBody; }
    public void setLicenseIssuingBody(String licenseIssuingBody) { this.licenseIssuingBody = licenseIssuingBody; }
    
    public String getCertificationsWorkshops() { return certificationsWorkshops; }
    public void setCertificationsWorkshops(String certificationsWorkshops) { this.certificationsWorkshops = certificationsWorkshops; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
    
    public Map<String, String> getClinicInfo() { 
        return clinicInfo != null ? clinicInfo : new HashMap<>(); // Ensure not null
    }
    public void setClinicInfo(Map<String, String> clinicInfo) { this.clinicInfo = clinicInfo; }

    public String getDateOfBirth() { return dateOfBirth; } // <--- Getter now returns String
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; } // <--- Setter now accepts String

    // Helper to get LocalDate if needed by UI (not for Firestore mapping)
    public LocalDate getLocalDateOfBirth() {
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            try {
                return LocalDate.parse(dateOfBirth);
            } catch (DateTimeParseException e) {
                // Log error or handle gracefully
                System.err.println("Error parsing dateOfBirth String to LocalDate: " + dateOfBirth + " - " + e.getMessage());
            }
        }
        return null;
    }
}