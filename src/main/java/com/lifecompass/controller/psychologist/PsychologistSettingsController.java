package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.PsychologistSettings;
import com.lifecompass.view.Psycologiestview.PsychologistSettingsScreen;
import javafx.application.Platform;
import javafx.scene.Node; // Added for getView() method
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Optional;
import com.lifecompass.controller.psychologist.PsychologistNotificationController;
 // Ensure this import is correct
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistSettingsController.class);

    private final PsychologistSettingsScreen view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public PsychologistSettingsController(PsychologistSettingsScreen view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("PsychologistSettingsController initialized for ID: {}", psychologistId);
    }

    public void initialize() {
        view.setSaveHandler(this::handleSaveChanges);
        view.setChangePasswordHandler(this::handleChangePassword);
        logger.debug("Settings save and password change handlers set.");
        loadSettings();
    }

    private void loadSettings() {
        view.showLoading(true);
        logger.info("Loading settings for ID: {}", psychologistId);
        psychologistDao.getPsychologistSettings(psychologistId)
            .thenAccept(settings -> {
                Platform.runLater(() -> {
                    if (settings != null) {
                        view.populateSettings(settings);
                        logger.info("Settings data successfully populated for ID: {}", psychologistId);
                    } else {
                        logger.warn("No settings found for ID: {}. Populating default settings.", psychologistId);
                        view.populateSettings(new PsychologistSettings()); 
                    }
                    view.showLoading(false);
                });
            })
            .exceptionally(e -> {
                logger.error("Error loading settings for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to load settings: " + e.getMessage());
                    view.showLoading(false);
                    view.populateSettings(new PsychologistSettings()); 
                });
                return null;
            });
    }

    private void handleSaveChanges(PsychologistSettings updatedSettings) {
        view.showLoading(true);
        updatedSettings.setPsychologistId(psychologistId); 
        logger.info("Saving settings data for ID: {}", psychologistId);
        psychologistDao.updatePsychologistSettings(updatedSettings)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    view.showSuccess("Settings saved successfully!");
                    logger.info("Settings saved successfully for ID: {}.", psychologistId);
                });
            })
            .exceptionally(e -> {
                logger.error("Error saving settings for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to save settings: " + e.getMessage());
                });
                return null;
            })
            .thenRun(() -> Platform.runLater(() -> view.showLoading(false)));
    }
    
    private void handleChangePassword(String currentPass, String newPass, String confirmPass) {
        logger.info("Handling password change request for ID: {}", psychologistId);
        
        psychologistDao.changePassword(psychologistId, currentPass, newPass)
            .thenAccept(success -> {
                Platform.runLater(() -> {
                    if (success) {
                        view.showSuccess("Password changed successfully.");
                        logger.info("Password changed successfully for ID: {}.", psychologistId);
                    } else {
                        view.showError("Password change failed. Please check your current password.");
                        logger.warn("Password change failed (incorrect current password?) for ID: {}", psychologistId);
                    }
                });
            })
            .exceptionally(e -> {
                logger.error("An error occurred during password change for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> view.showError("An error occurred during password change: " + e.getMessage()));
                return null;
            });
    }

    // Corrected method to get the view's content Node
    public Node getView() {
        return view.getContent();
    }
}