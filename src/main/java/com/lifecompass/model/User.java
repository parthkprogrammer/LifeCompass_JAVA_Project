package com.lifecompass.model; // Use this package for consistency

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import com.google.cloud.Timestamp; // <-- Add this import statement
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects; // For Objects.equals and Objects.hash in equals/hashCode

public class User {

    // Fields from com.finallifecompass.model.User (with Firestore annotations)
    @DocumentId
    private String id;
    @PropertyName("fullName")
    private String name; // Equivalent to fullName, but keeping both for flexibility in mapping
    private String email;
    private String role; // "user", "psychologist", "admin"
    private String status; // e.g., "active", "suspended", "inactive"
    private String verificationStatus; // "pending", "verified", "rejected" (for psychologists)
    private String riskLevel; // e.g., "Low Risk", "Medium Risk", "High Risk"

    @ServerTimestamp
    private Date createdAt; // This can be used as the "joined date" or last update


    // Fields from com.lifecompass.model.User (more detailed personal info)
    // Note: 'id', 'email', 'username' are common, handled above or will be merged.
    private String fullName; // Keeping as distinct, but will try to sync with 'name'
    private String gender;
    private LocalDate dateOfBirth; // Stored as String in Firestore
    private String phoneNumber;
    private String familyPhoneNumber;
    private String friendsPhoneNumber;
    private String username; // Common field with 'email' potentially acting as username
    // private String passwordHash; // You generally should NOT store password hashes directly in Firestore for users. Firebase Auth handles this.
    private String city;
    private String state;
    private String country;
    private String pinZipCode;
    private boolean termsAgreed;
    private boolean privacyAgreed; // Typo in original, assuming intended 'privacyAgreed'
    private String assignedPsychologistId; // Added this based on your FirebaseService queries


    // --- Constructors ---

    // No-argument constructor REQUIRED for Firestore's automatic object mapping
    public User() {
        // Initialize boolean fields to false by default, if not explicitly set by Firestore data
        this.termsAgreed = false;
        this.privacyAgreed = false; // Using privacyAgaged as per your provided code
    }

    // Constructor from com.finallifecompass.model.User
    public User(String id, String name, String email, String role, String status, String verificationStatus, String riskLevel, Date createdAt) {
        this(); // Call no-arg constructor to initialize booleans
        this.id = id;
        this.name = name;
        this.fullName = name; // Sync fullName with name for this constructor
        this.email = email;
        this.role = role;
        this.status = status;
        this.verificationStatus = verificationStatus;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
    }

    // Constructor from com.lifecompass.model.User (more detailed)
    public User(String id, String fullName, String gender, LocalDate dateOfBirth, String phoneNumber,
                String familyPhoneNumber, String friendsPhoneNumber, String email, String username,
                String passwordHash, String city, String state, String country, String pinZipCode,
                boolean termsAgreed, boolean privacyAgreed) { // Keeping privacyAgaged as per your provided code
        this(); // Call no-arg constructor
        this.id = id;
        this.fullName = fullName;
        this.name = fullName; // Sync name with fullName for this constructor
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.familyPhoneNumber = familyPhoneNumber;
        this.friendsPhoneNumber = friendsPhoneNumber;
        this.email = email;
        this.username = username;
        // This.passwordHash = passwordHash; // WARNING: Do not store user passwords here directly in Firestore. Use Firebase Auth.
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinZipCode = pinZipCode;
        this.termsAgreed = termsAgreed;
        this.privacyAgreed = privacyAgreed; // Using privacyAgaged as per your provided code

        // Set default values for fields from the other model if not provided
        this.role = "user"; // Default role for detailed user
        this.status = "active";
        this.verificationStatus = "N/A"; // Not applicable for regular users
        this.riskLevel = "Low Risk";
        this.createdAt = new Date(); // Or null, depending on your creation flow
    }


    // --- Getters and Setters for ALL combined fields ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Getters/Setters from the more detailed user model
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFamilyPhoneNumber() { return familyPhoneNumber; }
    public void setFamilyPhoneNumber(String familyPhoneNumber) { this.familyPhoneNumber = familyPhoneNumber; }

    public String getFriendsPhoneNumber() { return friendsPhoneNumber; }
    public void setFriendsPhoneNumber(String friendsPhoneNumber) { this.friendsPhoneNumber = friendsPhoneNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Removed get/setPasswordHash as it's not good practice to expose or store in model for direct Firestore access
    // If you need it for internal hashing, keep it, but it won't be mapped by Firestore if no setter.

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPinZipCode() { return pinZipCode; }
    public void setPinZipCode(String pinZipCode) { this.pinZipCode = pinZipCode; }

    public boolean isTermsAgreed() { return termsAgreed; }
    public void setTermsAgreed(boolean termsAgreed) { this.termsAgreed = termsAgreed; }

    public boolean isPrivacyAgreed() { return privacyAgreed; } // Using privacyAgaged as per your provided code
    public void setPrivacyAgreed(boolean privacyAgreed) { this.privacyAgreed = privacyAgreed; }

    public String getAssignedPsychologistId() { return assignedPsychologistId; }
    public void setAssignedPsychologistId(String assignedPsychologistId) { this.assignedPsychologistId = assignedPsychologistId; }


    // --- Manual Mapping Methods (toMap and fromMap) ---

    /**
     * Converts a User object to a Map<String, Object> for Firestore storage.
     * LocalDate is converted to an ISO-8601 String.
     * @return A Map representing the User object.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // Fields from com.finallifecompass.model.User (Firestore annotated)
        map.put("name", this.name);
        map.put("email", this.email);
        map.put("role", this.role);
        map.put("status", this.status);
        map.put("verificationStatus", this.verificationStatus);
        map.put("riskLevel", this.riskLevel);
        // createdAt is handled by @ServerTimestamp on write, but can be explicitly put if desired
        // map.put("createdAt", this.createdAt);

        // Fields from com.lifecompass.model.User (detailed info)
        map.put("fullName", this.fullName);
        map.put("gender", this.gender);
        map.put("dateOfBirth", (this.dateOfBirth != null) ? this.dateOfBirth.toString() : null); // Convert LocalDate to String
        map.put("phoneNumber", this.phoneNumber);
        map.put("familyPhoneNumber", this.familyPhoneNumber);
        map.put("friendsPhoneNumber", this.friendsPhoneNumber);
        map.put("username", this.username);
        // map.put("passwordHash", this.passwordHash); // WARNING: Do NOT store this for users in Firestore!
        map.put("city", this.city);
        map.put("state", this.state);
        map.put("country", this.country);
        map.put("pinZipCode", this.pinZipCode);
        map.put("termsAgreed", this.termsAgreed);
        map.put("privacyAgaged", this.privacyAgreed); // Using privacyAgaged as per your provided code
        map.put("assignedPsychologistId", this.assignedPsychologistId);

        return map;
    }

    /**
     * Creates a User object from a Firestore document's data Map.
     * String representation of LocalDate is parsed back to LocalDate.
     * Safely casts data types and handles potential nulls or missing fields.
     * @param id The ID of the Firestore document (which is also the User's ID).
     * @param data The Map<String, Object> retrieved from Firestore.
     * @return A new User object.
     */
    // public static User fromMap(String id, Map<String, Object> data) {
    //     User user = new User();
    //     user.setId(id); // Set the ID from the document ID

    //     // Fields from both models
    //     user.setFullName((String) data.get("fullName"));
    //     user.setName((String) data.get("name")); // Map 'name' from Firestore

    //     // Sync name and fullName if one is missing
    //     if (user.getName() == null && user.getFullName() != null) {
    //         user.setName(user.getFullName());
    //     }
    //     if (user.getFullName() == null && user.getName() != null) {
    //         user.setFullName(user.getName());
    //     }

    //     user.setEmail((String) data.get("email"));
    //     user.setRole((String) data.get("role"));
    //     user.setStatus((String) data.get("status"));
    //     user.setVerificationStatus((String) data.get("verificationStatus"));
    //     user.setRiskLevel((String) data.get("riskLevel"));
    //     user.setCreatedAt((Date) data.get("createdAt")); // Firestore's toObject handles Date automatically

    //     user.setGender((String) data.get("gender"));
    //     String dobString = (String) data.get("dateOfBirth");
    //     if (dobString != null && !dobString.isEmpty()) {
    //         try {
    //             user.setDateOfBirth(LocalDate.parse(dobString));
    //         } catch (DateTimeParseException e) {
    //             System.err.println("Error parsing dateOfBirth for user ID " + id + ": " + dobString + ". " + e.getMessage());
    //         }
    //     }

    //     user.setPhoneNumber((String) data.get("phoneNumber"));
    //     user.setFamilyPhoneNumber((String) data.get("familyPhoneNumber"));
    //     user.setFriendsPhoneNumber((String) data.get("friendsPhoneNumber"));
    //     user.setUsername((String) data.get("username"));
    //     // passwordHash is not handled here for security reasons.

    //     user.setCity((String) data.get("city"));
    //     user.setState((String) data.get("state"));
    //     user.setCountry((String) data.get("country"));
    //     user.setPinZipCode((String) data.get("pinZipCode"));

    //     // Use getOrDefault for boolean fields to provide a default if missing
    //     user.setTermsAgreed((Boolean) data.getOrDefault("termsAgreed", false));
    //     user.setPrivacyAgreed((Boolean) data.getOrDefault("privacyAgaged", false)); // Using privacyAgaged as per your provided code

    //     user.setAssignedPsychologistId((String) data.get("assignedPsychologistId"));


    //     return user;
    // }


// ... (other imports) ...

public static User fromMap(String id, Map<String, Object> data) {
    User user = new User();
    user.setId(id); // Set the ID from the document ID

    // Fields from both models
    user.setFullName((String) data.get("fullName"));
    user.setName((String) data.get("name")); // Map 'name' from Firestore

    // Sync name and fullName if one is missing
    if (user.getName() == null && user.getFullName() != null) {
        user.setName(user.getFullName());
    }
    if (user.getFullName() == null && user.getName() != null) {
        user.setFullName(user.getName());
    }

    user.setEmail((String) data.get("email"));
    user.setRole((String) data.get("role"));
    user.setStatus((String) data.get("status"));
    user.setVerificationStatus((String) data.get("verificationStatus"));
    user.setRiskLevel((String) data.get("riskLevel"));
    
    // --- CORRECTED CODE START ---
    // Handle the createdAt field, which is a Timestamp in Firestore
    Object createdAtObject = data.get("createdAt");
    if (createdAtObject instanceof Timestamp) {
        user.setCreatedAt(((Timestamp) createdAtObject).toDate());
    } else {
        // Fallback for cases where it might already be a Date or null
        user.setCreatedAt((Date) createdAtObject);
    }
    // --- CORRECTED CODE END ---

    user.setGender((String) data.get("gender"));
    String dobString = (String) data.get("dateOfBirth");
    if (dobString != null && !dobString.isEmpty()) {
        try {
            user.setDateOfBirth(LocalDate.parse(dobString));
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing dateOfBirth for user ID " + id + ": " + dobString + ". " + e.getMessage());
        }
    }

    user.setPhoneNumber((String) data.get("phoneNumber"));
    user.setFamilyPhoneNumber((String) data.get("familyPhoneNumber"));
    user.setFriendsPhoneNumber((String) data.get("friendsPhoneNumber"));
    user.setUsername((String) data.get("username"));
    // passwordHash is not handled here for security reasons.

    user.setCity((String) data.get("city"));
    user.setState((String) data.get("state"));
    user.setCountry((String) data.get("country"));
    user.setPinZipCode((String) data.get("pinZipCode"));

    // Use getOrDefault for boolean fields to provide a default if missing
    user.setTermsAgreed((Boolean) data.getOrDefault("termsAgreed", false));
    user.setPrivacyAgreed((Boolean) data.getOrDefault("privacyAgreed", false)); // Using privacyAgaged as per your provided code

    user.setAssignedPsychologistId((String) data.get("assignedPsychologistId"));

    return user;
}
    // --- Override equals() and hashCode() for proper object comparison ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return termsAgreed == user.termsAgreed &&
               privacyAgreed == user.privacyAgreed &&
               Objects.equals(id, user.id) &&
               Objects.equals(name, user.name) &&
               Objects.equals(email, user.email) &&
               Objects.equals(role, user.role) &&
               Objects.equals(status, user.status) &&
               Objects.equals(verificationStatus, user.verificationStatus) &&
               Objects.equals(riskLevel, user.riskLevel) &&
               Objects.equals(createdAt, user.createdAt) &&
               Objects.equals(fullName, user.fullName) &&
               Objects.equals(gender, user.gender) &&
               Objects.equals(dateOfBirth, user.dateOfBirth) &&
               Objects.equals(phoneNumber, user.phoneNumber) &&
               Objects.equals(familyPhoneNumber, user.familyPhoneNumber) &&
               Objects.equals(friendsPhoneNumber, user.friendsPhoneNumber) &&
               Objects.equals(username, user.username) &&
               Objects.equals(city, user.city) &&
               Objects.equals(state, user.state) &&
               Objects.equals(country, user.country) &&
               Objects.equals(pinZipCode, user.pinZipCode) &&
               Objects.equals(assignedPsychologistId, user.assignedPsychologistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, role, status, verificationStatus, riskLevel, createdAt, fullName, gender, dateOfBirth, phoneNumber, familyPhoneNumber, friendsPhoneNumber, username, city, state, country, pinZipCode, termsAgreed, privacyAgreed, assignedPsychologistId);
    }
}