package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.PsychologistProfile;
import com.lifecompass.view.Psycologiestview.PsychologistProfileView;
import javafx.application.Platform;
import javafx.scene.Node; // For getView()

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistProfileController {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistProfileController.class);

    private final PsychologistProfileView view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public PsychologistProfileController(PsychologistProfileView view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("PsychologistProfileController initialized for ID: {}", psychologistId);

        // Set the handler for the save button in the view
        this.view.setSaveHandler(this::handleSaveChanges); // Now passes the profile directly
        logger.debug("Profile save handler set.");
    }

    /**
     * Called to initialize the profile screen, typically when the tab is switched to.
     */
    public void initialize() {
        loadProfileData();
    }

    /**
     * Fetches the psychologist's profile data from Firestore and populates the view.
     */
    private void loadProfileData() {
        view.showLoading(true); // Show loading indicator
        logger.info("Loading profile data for ID: {}", psychologistId);

        psychologistDao.getPsychologistProfile(psychologistId)
            .thenAccept(profile -> {
                Platform.runLater(() -> { // Ensure UI updates are on the JavaFX Application Thread
                    if (profile != null) {
                        view.populateProfileData(profile);
                        logger.info("Profile data successfully populated for ID: {}", psychologistId);
                    } else {
                        logger.warn("No profile found for ID: {}. Populating default/empty profile.", psychologistId);
                        // If no profile exists, create a new empty one to allow filling the form
                        PsychologistProfile emptyProfile = new PsychologistProfile();
                        emptyProfile.setId(psychologistId); // Set ID for new profile
                        view.populateProfileData(emptyProfile); // Populate with empty data
                        view.showError("No profile found. Please fill in your details and save.");
                    }
                    view.showLoading(false); // Hide loading
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> { // Ensure UI updates are on the JavaFX Application Thread
                    logger.error("Error loading profile data for ID {}: {}", psychologistId, ex.getMessage(), ex);
                    view.showError("Failed to load profile: " + ex.getMessage());
                    view.showLoading(false);
                    // On error loading, also populate with an empty profile to allow new entry
                    PsychologistProfile emptyProfile = new PsychologistProfile();
                    emptyProfile.setId(psychologistId);
                    view.populateProfileData(emptyProfile);
                });
                return null;
            });
    }

    /**
     * Handles the "Save Changes" action, receiving the collected data from the view
     * and saving it to Firestore.
     * @param updatedProfile The PsychologistProfile object collected from the view.
     */
    private void handleSaveChanges(PsychologistProfile updatedProfile) {
        logger.info("Saving profile data for ID: {}", psychologistId);
        view.showLoading(true); // Show loading

        // Set the ID of the profile from the controller's psychologistId
        // This ensures the correct document is updated in Firestore.
        updatedProfile.setId(psychologistId); 

        // --- Basic Validation (Add more as needed) ---
        if (updatedProfile.getFullName() == null || updatedProfile.getFullName().trim().isEmpty()) {
            Platform.runLater(() -> {
                view.showError("Full Name cannot be empty.");
                view.showLoading(false);
            });
            return;
        }
        if (updatedProfile.getQualifications() == null || updatedProfile.getQualifications().trim().isEmpty()) {
            Platform.runLater(() -> {
                view.showError("Qualifications cannot be empty.");
                view.showLoading(false);
            });
            return;
        }
        // Add more validation for other critical fields here

        psychologistDao.updatePsychologistProfile(updatedProfile)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    view.showSuccess("Profile updated successfully!");
                    logger.info("Profile saved successfully for ID: {}", psychologistId);
                    // After saving, you might want to refresh elements on the dashboard, e.g., the therapist's name
                    // dashboardView.setTherapistName(updatedProfile.getFullName()); // Requires passing dashboardView to this controller
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    logger.error("Error saving profile for ID {}: {}", psychologistId, ex.getMessage(), ex);
                    view.showError("Failed to save profile: " + ex.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    // This method is called by the DashboardController to get the UI Node for this tab.
    public Node getView() {
        return view.getView();
    }
}