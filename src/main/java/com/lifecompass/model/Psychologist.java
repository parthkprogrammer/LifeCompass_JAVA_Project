package com.lifecompass.model; // Use this package as per your first file and project structure

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects; // For Objects.equals and Objects.hash in equals/hashCode

public class Psychologist {

    // Fields from the first Psychologist class (with Firestore annotations)
    @DocumentId
    private String id; // This will likely be the same as their Firebase Auth UID
    private String name; // From first file: "name"
    private String email;
    private String status; // e.g., "active", "suspended"
    private String verificationStatus; // "pending", "verified", "rejected"
    private String licenseNumber;
    private String bio; // Renamed from 'qualification' or 'profileDescription' potentially. Used 'bio' here.
    private List<String> specialties; // From first file: "specialties". From second file: "specializations". Combining as 'specialties'.
    private int sessionCount; // Example metric from first file
    private double rating; // Example metric from first file
    @ServerTimestamp
    private Date createdAt; // For Firestore's automatic timestamping

    // Fields from the second Psychologist class (more detailed profile)
    // Note: 'id' and 'email' are already handled above.
    // 'name' vs 'fullName': Consolidating to 'name' but providing 'getFullName' for compatibility if needed.
    private String fullName; // Redundant if 'name' is used, but keeping for clarity in mapping.
    private String gender;
    private LocalDate dateOfBirth; // Stored as String in Firestore when using toMap/fromMap
    private String phoneNumber;
    private String profilePictureUrl;
    private String qualification; // Reintroduced, distinct from 'bio' if needed for specific data.
    private int yearsOfExperience;
    private String issuingAuthority;
    private List<String> languagesKnown;
    private String workMode;
    private String availability;
    private double consultationFee;
    private String clinicName;
    private String clinicAddress;
    private String clinicCity;
    private String clinicState;
    private String clinicPinCode;
    private String googleMapsLink;
    private String username;
    private Map<String, String> uploadedDocumentUrls;


    // --- Constructors ---

    // No-argument constructor is REQUIRED for Firestore's automatic object mapping (toObject method)
    public Psychologist() {
        // Initialize lists/maps to avoid NullPointerExceptions later
        this.specialties = new ArrayList<>();
        this.languagesKnown = new ArrayList<>();
        this.uploadedDocumentUrls = new HashMap<>();
        this.verificationStatus = "pending"; // Default value for new psychologists
        this.status = "inactive"; // Default status for new psychologists
    }

    // Constructor matching the fields from the first provided Psychologist class
    public Psychologist(String id, String name, String email, String status, String verificationStatus,
                        String licenseNumber, String bio, List<String> specialties, int sessionCount, double rating, Date createdAt) {
        this(); // Call no-arg constructor to initialize lists/maps
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.verificationStatus = verificationStatus;
        this.licenseNumber = licenseNumber;
        this.bio = bio;
        this.specialties.addAll(specialties); // Use addAll for lists
        this.sessionCount = sessionCount;
        this.rating = rating;
        this.createdAt = createdAt;
       // Assuming this is part of the first model, if not, can be removed
        // Initialize new fields to defaults or null
        this.fullName = name; // Assuming name is full name
        this.gender = null;
        this.dateOfBirth = null;
        this.phoneNumber = null;
        this.profilePictureUrl = null;
        this.qualification = null;
        this.yearsOfExperience = 0;
        this.issuingAuthority = null;
        // languagesKnown already initialized by this()
        this.workMode = null;
        this.availability = null;
        this.consultationFee = 0.0;
        this.clinicName = null;
        this.clinicAddress = null;
        this.clinicCity = null;
        this.clinicState = null;
        this.clinicPinCode = null;
        this.googleMapsLink = null;
        this.username = null;
        // uploadedDocumentUrls already initialized by this()
    }

    // Constructor matching your dummy data loading from the second provided class
    public Psychologist(String id, String fullName, String qualification,
                        List<String> specializations, int yearsOfExperience,
                        String profilePictureUrl) {
        this(); // Call no-arg constructor to initialize lists/maps
        this.id = id;
        this.fullName = fullName;
        this.name = fullName; // Populate 'name' field from fullName for consistency
        this.qualification = qualification;
        this.specialties.addAll(specializations); // Use addAll for lists (using 'specialties' as the combined field)
        this.yearsOfExperience = yearsOfExperience;
        this.profilePictureUrl = profilePictureUrl;

        // Initialize other fields to default or null as appropriate
        this.gender = null;
        this.dateOfBirth = null;
        this.phoneNumber = null;
        this.email = null;
        this.status = "pending"; // Default status for new psychologist. Adjust if needed.
        this.verificationStatus = "pending"; // Default verification status. Adjust if needed.
        this.licenseNumber = null;
        this.bio = null; // Assuming bio is distinct from qualification in this context
        this.sessionCount = 0;
        this.rating = 0.0;
        this.createdAt = new Date(); // Set creation date if not provided
        this.issuingAuthority = null;
        // languagesKnown already initialized by this()
        this.workMode = null;
        this.availability = null;
        this.consultationFee = 0.0;
        this.clinicName = null;
        this.clinicAddress = null;
        this.clinicCity = null;
        this.clinicState = null;
        this.clinicPinCode = null;
        this.googleMapsLink = null;
        this.username = null;
        // uploadedDocumentUrls already initialized by this()
    }


    // --- Getters and Setters for ALL fields ---
    // (Ensure you have a getter and setter for every field to make Firestore's toObject/fromObject work)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getBio() { return bio; } // This field name should be consistent with Firestore if used for mapping
    public void setBio(String bio) { this.bio = bio; }

    // Using 'specialties' as the primary field for this list
    public List<String> getSpecialties() { return specialties; }
    public void setSpecialties(List<String> specialties) { this.specialties = (specialties != null) ? new ArrayList<>(specialties) : new ArrayList<>(); }

    public int getSessionCount() { return sessionCount; }
    public void setSessionCount(int sessionCount) { this.sessionCount = sessionCount; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // New Getters/Setters from the second model
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    // If 'specializations' was also a field in Firestore under a different key,
    // you might need a separate getter/setter for it or map it in toMap/fromMap.
    // For now, it's consolidated with 'specialties'.
    // You could alias it like this if needed for mapping flexibility:
    // @PropertyName("specializations") // If Firestore doc had 'specializations' field
    // public List<String> getSpecializationsAsAliased() { return this.specialties; }
    // public void setSpecializationsAsAliased(List<String> specializations) { this.specialties = specializations; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }

    public List<String> getLanguagesKnown() { return languagesKnown; }
    public void setLanguagesKnown(List<String> languagesKnown) { this.languagesKnown = (languagesKnown != null) ? new ArrayList<>(languagesKnown) : new ArrayList<>(); }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getClinicAddress() { return clinicAddress; }
    public void setClinicAddress(String clinicAddress) { this.clinicAddress = clinicAddress; }

    public String getClinicCity() { return clinicCity; }
    public void setClinicCity(String clinicCity) { this.clinicCity = clinicCity; }

    public String getClinicState() { return clinicState; }
    public void setClinicState(String clinicState) { this.clinicState = clinicState; }

    public String getClinicPinCode() { return clinicPinCode; }
    public void setClinicPinCode(String clinicPinCode) { this.clinicPinCode = clinicPinCode; }

    public String getGoogleMapsLink() { return googleMapsLink; }
    public void setGoogleMapsLink(String googleMapsLink) { this.googleMapsLink = googleMapsLink; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Map<String, String> getUploadedDocumentUrls() { return uploadedDocumentUrls; }
    public void setUploadedDocumentUrls(Map<String, String> uploadedDocumentUrls) { this.uploadedDocumentUrls = (uploadedDocumentUrls != null) ? new HashMap<>(uploadedDocumentUrls) : new HashMap<>(); }


    // --- Manual Mapping Methods (toMap and fromMap) ---

    /**
     * Converts a Psychologist object to a Map<String, Object> for Firestore storage.
     * LocalDate is converted to an ISO-8601 String.
     * Maps the combined fields to their respective Firestore document keys.
     * @return A Map representing the Psychologist object.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // Fields from first model
        map.put("name", this.name);
        map.put("email", this.email);
        map.put("status", this.status);
        map.put("verificationStatus", this.verificationStatus);
        map.put("licenseNumber", this.licenseNumber);
        map.put("bio", this.bio);
        map.put("specialties", this.specialties); // Using 'specialties' as the primary list for both
        map.put("sessionCount", this.sessionCount);
        map.put("rating", this.rating);
        // createdAt is handled by @ServerTimestamp, so no need to explicitly put it here unless for initial creation
        // map.put("createdAt", this.createdAt); // If you want to set it manually on first write

        // Fields from second model (some overlap, ensure consistency)
        map.put("fullName", this.fullName); // If you want to keep this distinct from 'name'
        map.put("gender", this.gender);
        map.put("dateOfBirth", (this.dateOfBirth != null) ? this.dateOfBirth.toString() : null); // Convert LocalDate to String
        map.put("phoneNumber", this.phoneNumber);
        // 'email' is already handled above
        map.put("profilePictureUrl", this.profilePictureUrl);
        map.put("qualification", this.qualification);
        // 'specializations' is mapped to 'specialties'
        map.put("yearsOfExperience", this.yearsOfExperience);
        map.put("issuingAuthority", this.issuingAuthority);
        map.put("languagesKnown", this.languagesKnown);
        map.put("workMode", this.workMode);
        map.put("availability", this.availability);
        map.put("consultationFee", this.consultationFee);
        map.put("clinicName", this.clinicName);
        map.put("clinicAddress", this.clinicAddress);
        map.put("clinicCity", this.clinicCity);
        map.put("clinicState", this.clinicState);
        map.put("clinicPinCode", this.clinicPinCode);
        map.put("googleMapsLink", this.googleMapsLink);
        map.put("username", this.username);
        map.put("uploadedDocumentUrls", this.uploadedDocumentUrls);
        return map;
    }

    /**
     * Creates a Psychologist object from a Firestore document's data Map.
     * String representation of LocalDate is parsed back to LocalDate.
     * Handles both older and newer field names/types by checking for existence and type.
     * @param id The ID of the Firestore document (which is also the Psychologist's ID).
     * @param data The Map<String, Object> retrieved from Firestore.
     * @return A new Psychologist object.
     */
    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast from Object to List/Map
    public static Psychologist fromMap(String id, Map<String, Object> data) {
        Psychologist psychologist = new Psychologist();
        psychologist.setId(id); // Set the ID from the document ID

        // Attempt to retrieve all fields, handling nulls and type conversions
        psychologist.setFullName((String) data.get("fullName"));
        psychologist.setName((String) data.get("name")); // Map 'name' from Firestore

        // If 'name' is empty but 'fullName' exists, use fullName for name
        if (psychologist.getName() == null && psychologist.getFullName() != null) {
            psychologist.setName(psychologist.getFullName());
        }
        // If 'fullName' is empty but 'name' exists, use name for fullName
        if (psychologist.getFullName() == null && psychologist.getName() != null) {
            psychologist.setFullName(psychologist.getName());
        }


        psychologist.setGender((String) data.get("gender"));
        String dobString = (String) data.get("dateOfBirth");
        if (dobString != null && !dobString.isEmpty()) {
            try {
                psychologist.setDateOfBirth(LocalDate.parse(dobString));
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing dateOfBirth for ID " + id + ": " + dobString + ". " + e.getMessage());
            }
        }

        psychologist.setPhoneNumber((String) data.get("phoneNumber"));
        psychologist.setEmail((String) data.get("email"));
        psychologist.setProfilePictureUrl((String) data.get("profilePictureUrl"));
        psychologist.setQualification((String) data.get("qualification"));

        // Prioritize 'specialties' but fall back to 'specializations' if present
        List<String> specialtiesList = (List<String>) data.get("specialties");
        if (specialtiesList != null) {
            psychologist.setSpecialties(new ArrayList<>(specialtiesList));
        } else {
            List<String> specializationsList = (List<String>) data.get("specializations");
            if (specializationsList != null) {
                psychologist.setSpecialties(new ArrayList<>(specializationsList));
            }
        }


        Object yearsOfExperienceObj = data.get("yearsOfExperience");
        if (yearsOfExperienceObj instanceof Long) {
            psychologist.setYearsOfExperience(((Long) yearsOfExperienceObj).intValue());
        } else if (yearsOfExperienceObj instanceof Integer) {
            psychologist.setYearsOfExperience((Integer) yearsOfExperienceObj);
        } else {
            psychologist.setYearsOfExperience(0);
        }

        // Fields from the first model
        psychologist.setStatus((String) data.get("status"));
        psychologist.setVerificationStatus((String) data.get("verificationStatus"));
        // 'licenseNumber' is common
        psychologist.setBio((String) data.get("bio"));
        Object sessionCountObj = data.get("sessionCount");
        if (sessionCountObj instanceof Long) {
            psychologist.setSessionCount(((Long) sessionCountObj).intValue());
        } else if (sessionCountObj instanceof Integer) {
            psychologist.setSessionCount((Integer) sessionCountObj);
        } else {
            psychologist.setSessionCount(0);
        }

        Object ratingObj = data.get("rating");
        if (ratingObj instanceof Double) {
            psychologist.setRating((Double) ratingObj);
        } else if (ratingObj instanceof Long) { // Handle case where it might be stored as integer
            psychologist.setRating(((Long) ratingObj).doubleValue());
        } else {
            psychologist.setRating(0.0);
        }
        //psychologist.setCreatedAt((Date) data.get("createdAt")); // Handled by @ServerTimestamp if using toObject
        Object createdAtObj = data.get("createdAt");
        if (createdAtObj instanceof com.google.cloud.Timestamp) {
            psychologist.setCreatedAt(((com.google.cloud.Timestamp) createdAtObj).toDate());
        } else if (createdAtObj instanceof Date) {
            // Fallback for cases where it might already be a java.util.Date (less common from Firestore directly)
            psychologist.setCreatedAt((Date) createdAtObj);
        } else {
            // Handle cases where the field is missing or has an unexpected type
            System.err.println("Warning: 'createdAt' field is not a Timestamp or Date for ID " + id);
            psychologist.setCreatedAt(null); // Or new Date() if you prefer a default
        }

        psychologist.setLicenseNumber((String) data.get("licenseNumber")); // From second model, common field.
        psychologist.setIssuingAuthority((String) data.get("issuingAuthority"));

        List<String> languagesKnownList = (List<String>) data.get("languagesKnown");
        if (languagesKnownList != null) {
            psychologist.setLanguagesKnown(new ArrayList<>(languagesKnownList));
        }

        psychologist.setWorkMode((String) data.get("workMode"));
        psychologist.setAvailability((String) data.get("availability"));

        Object consultationFeeObj = data.get("consultationFee");
        if (consultationFeeObj instanceof Double) {
            psychologist.setConsultationFee((Double) consultationFeeObj);
        } else if (consultationFeeObj instanceof Long) {
            psychologist.setConsultationFee(((Long) consultationFeeObj).doubleValue());
        } else {
            psychologist.setConsultationFee(0.0);
        }

        psychologist.setClinicName((String) data.get("clinicName"));
        psychologist.setClinicAddress((String) data.get("clinicAddress"));
        psychologist.setClinicCity((String) data.get("clinicCity"));
        psychologist.setClinicState((String) data.get("clinicState"));
        psychologist.setClinicPinCode((String) data.get("clinicPinCode"));
        psychologist.setGoogleMapsLink((String) data.get("googleMapsLink"));
        psychologist.setUsername((String) data.get("username"));

        Map<String, String> uploadedDocumentUrlsMap = (Map<String, String>) data.get("uploadedDocumentUrls");
        if (uploadedDocumentUrlsMap != null) {
            psychologist.setUploadedDocumentUrls(new HashMap<>(uploadedDocumentUrlsMap));
        }

        return psychologist;
    }

    // --- Override equals() and hashCode() for proper object comparison ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Psychologist that = (Psychologist) o;
        return sessionCount == that.sessionCount &&
               Double.compare(that.rating, rating) == 0 &&
               yearsOfExperience == that.yearsOfExperience &&
               Double.compare(that.consultationFee, consultationFee) == 0 &&
               Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(email, that.email) &&
               Objects.equals(status, that.status) &&
               Objects.equals(verificationStatus, that.verificationStatus) &&
               Objects.equals(licenseNumber, that.licenseNumber) &&
               Objects.equals(bio, that.bio) &&
               Objects.equals(specialties, that.specialties) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(fullName, that.fullName) &&
               Objects.equals(gender, that.gender) &&
               Objects.equals(dateOfBirth, that.dateOfBirth) &&
               Objects.equals(phoneNumber, that.phoneNumber) &&
               Objects.equals(profilePictureUrl, that.profilePictureUrl) &&
               Objects.equals(qualification, that.qualification) &&
               Objects.equals(issuingAuthority, that.issuingAuthority) &&
               Objects.equals(languagesKnown, that.languagesKnown) &&
               Objects.equals(workMode, that.workMode) &&
               Objects.equals(availability, that.availability) &&
               Objects.equals(clinicName, that.clinicName) &&
               Objects.equals(clinicAddress, that.clinicAddress) &&
               Objects.equals(clinicCity, that.clinicCity) &&
               Objects.equals(clinicState, that.clinicState) &&
               Objects.equals(clinicPinCode, that.clinicPinCode) &&
               Objects.equals(googleMapsLink, that.googleMapsLink) &&
               Objects.equals(username, that.username) &&
               Objects.equals(uploadedDocumentUrls, that.uploadedDocumentUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, status, verificationStatus, licenseNumber, bio, specialties, sessionCount, rating, createdAt, fullName, gender, dateOfBirth, phoneNumber, profilePictureUrl, qualification, yearsOfExperience, issuingAuthority, languagesKnown, workMode, availability, consultationFee, clinicName, clinicAddress, clinicCity, clinicState, clinicPinCode, googleMapsLink, username, uploadedDocumentUrls);
    }
}