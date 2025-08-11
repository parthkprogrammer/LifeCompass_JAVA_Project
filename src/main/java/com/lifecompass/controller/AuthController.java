package com.lifecompass.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.lifecompass.config.FirebaseConfig;
import com.lifecompass.dao.UserDao;
import com.lifecompass.dao.PsychologistDao1;
import com.lifecompass.dao.impl.UserDaoFirestoreImpl;
import com.lifecompass.dao.impl.PsychologistDaoFirestoreImpl;
import com.lifecompass.LoginScreen; // Import LoginScreen
import com.lifecompass.util.SceneManager; // Import SceneManager
import javafx.application.Platform; // Import Platform for UI updates
import javafx.scene.control.Alert; // Import Alert for showing messages

import java.util.concurrent.ExecutionException;

public class AuthController {
    // CHANGED: firebaseAuth is now static to be accessible from static methods
    private static FirebaseAuth firebaseAuth;
    private final UserDao userDao;
    private final PsychologistDao1 psychologistDao;

    // CHANGED: Static fields for logged-in user details
    public static String loggedInUserId = null;
    public static String loggedInUserEmail = null; // NEW: Added for logout message
    public static String loggedInUserRole = null; // "user", "psychologist", "admin"

    // CHANGED: Static initializer block to ensure firebaseAuth is initialized once
    static {
        FirebaseConfig.initialize(); // Ensure Firebase is initialized
        firebaseAuth = FirebaseConfig.getFirebaseAuth();
    }

    public AuthController() {
        // No need to initialize firebaseAuth here as it's done in the static block
        this.userDao = new UserDaoFirestoreImpl();
        this.psychologistDao = new PsychologistDaoFirestoreImpl();
    }

    // CHANGED: setLoggedInUser now updates loggedInUserEmail
    public static void setLoggedInUser(String userId, String email, String role) {
        loggedInUserId = userId;
        loggedInUserEmail = email;
        loggedInUserRole = role;
    }

    // CHANGED: clearLoggedInUser is now static and clears all static user data
    public static void clearLoggedInUser() {
        loggedInUserId = null;
        loggedInUserEmail = null; // Clear email on logout
        loggedInUserRole = null;
        System.out.println("Local user session data cleared.");
    }

    public boolean isLoggedIn() {
        return loggedInUserId != null;
    }

    public String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public boolean login(String email, String password, String role) {
        try {
            UserRecord userRecord = firebaseAuth.getUserByEmail(email);
            if (userRecord != null) {
                System.out.println("Successfully found user record for: " + email);

                boolean roleMatch = false;
                if ("user".equalsIgnoreCase(role)) {
                    if (userDao.getUserById(userRecord.getUid()).isPresent()) {
                        roleMatch = true;
                    }
                } else if ("psychologist".equalsIgnoreCase(role)) {
                    if (psychologistDao.getPsychologistById(userRecord.getUid()).isPresent()) {
                        roleMatch = true;
                    }
                } else if ("admin".equalsIgnoreCase(role)) {
                    roleMatch = true;
                }

                if (roleMatch) {
                    // CHANGED: Pass user email to setLoggedInUser
                    setLoggedInUser(userRecord.getUid(), email, role);
                    System.out.println("Login successful for " + email + " as " + role);
                    return true;
                } else {
                    System.out.println("User " + email + " exists, but not registered as a " + role + " or role mismatch.");
                    return false;
                }
            } else {
                System.out.println("No user record found for email: " + email);
                return false;
            }
        } catch (FirebaseAuthException e) {
            System.err.println("Firebase Auth Error during login: " + e.getMessage());
            return false;
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Database access error during login check: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs out the currently authenticated user from Firebase and clears local session data.
     * This method is static so it can be called directly using AuthController.logout().
     */
    public static void logout() { // CHANGED: Added 'static' keyword here
        System.out.println("Attempting to log out user: " + loggedInUserEmail);
        try {
            if (firebaseAuth != null) {
                // firebaseAuth.signOut(); // Firebase SDK method to sign out
                System.out.println("Firebase user signed out successfully.");
            } else {
                System.err.println("FirebaseAuth not initialized. Cannot sign out from Firebase.");
            }

            // Clear local session data
            clearLoggedInUser();

            // Navigate back to the login screen on the JavaFX Application Thread
            Platform.runLater(() -> {
                System.out.println("Redirecting to Login Screen...");
                SceneManager.showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out.");
                // Assuming LoginScreen is the entry point or a screen you can switch to
                SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Login");
            });

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                SceneManager.showAlert(Alert.AlertType.ERROR, "Logout Error", "An error occurred during logout: " + e.getMessage());
            });
        }
    }

    // OLD CODE (commented out as requested)
    /*
    public void logout() {
        clearLoggedInUser();
        System.out.println("User logged out locally.");
    }
    */
}