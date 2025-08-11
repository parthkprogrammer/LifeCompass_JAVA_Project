package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.CrisisAlert;
import com.lifecompass.model.psychologist.Patient;
import com.lifecompass.view.Psycologiestview.CrisisMonitorView;
import javafx.application.Platform;
import java.util.concurrent.CompletableFuture;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrisisMonitorController {

    private static final Logger logger = LoggerFactory.getLogger(CrisisMonitorController.class);

    private final CrisisMonitorView view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public CrisisMonitorController(CrisisMonitorView view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("CrisisMonitorController initialized for ID: {}", psychologistId);
        
        this.view.setContactPatientHandler(this::handleContactPatient);
        this.view.setEmergencyProtocolHandler(this::handleEmergencyProtocol);
        logger.info("CrisisMonitorView action handlers set.");
    }

    public void initialize() {
        loadCrisisData();
    }

    private void loadCrisisData() {
        view.showLoading(true);
        logger.info("Loading crisis data for ID: {}", psychologistId);

        CompletableFuture<List<CrisisAlert>> activeAlertsFuture = 
            psychologistDao.getActiveCrisisAlerts(psychologistId)
                .exceptionally(e -> {
                    logger.error("Error fetching active crisis alerts: {}", e.getMessage(), e);
                    Platform.runLater(() -> view.showError("Failed to load active crisis alerts: " + e.getMessage()));
                    return List.of();
                });

        CompletableFuture<List<Patient>> patientsWithRiskFuture = 
            psychologistDao.getPatientsWithRiskLevels(psychologistId)
                .exceptionally(e -> {
                    logger.error("Error fetching patients with risk levels: {}", e.getMessage(), e);
                    Platform.runLater(() -> view.showError("Failed to load patient risk levels: " + e.getMessage()));
                    return List.of();
                });

        CompletableFuture.allOf(activeAlertsFuture, patientsWithRiskFuture)
            .thenAccept(__ -> {
                Platform.runLater(() -> {
                    view.populateCrisisAlerts(activeAlertsFuture.join());
                    view.populatePatientRiskLevels(patientsWithRiskFuture.join());
                    view.showLoading(false);
                    logger.info("Crisis data loaded and view populated.");
                });
            })
            .exceptionally(e -> {
                logger.error("Unexpected error after crisis data loading: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("An unexpected error occurred: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    private void handleContactPatient(CrisisAlert alert) {
        logger.info("Controller: Handling 'Contact Patient' for alert ID: {} (Patient: {})", alert.getId(), alert.getPatientName());
        
        view.showLoading(true);
        psychologistDao.updateCrisisAlertStatus(alert.getId(), "Acknowledged")
            .thenRun(() -> {
                logger.info("Controller: Crisis alert {} status updated to Acknowledged.", alert.getId());
                Platform.runLater(() -> {
                    view.showError("Crisis alert for " + alert.getPatientName() + " has been acknowledged.");
                    loadCrisisData();
                });
            })
            .exceptionally(e -> {
                logger.error("Controller: Failed to acknowledge alert {}: {}", alert.getId(), e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to acknowledge alert for " + alert.getPatientName() + ": " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    private void handleEmergencyProtocol(CrisisAlert alert) {
        logger.warn("Controller: Handling 'Emergency Protocol' for alert ID: {} (Patient: {})", alert.getId(), alert.getPatientName());
        
        view.showLoading(true);
        psychologistDao.updateCrisisAlertStatus(alert.getId(), "Emergency Initiated")
            .thenRun(() -> {
                logger.warn("Controller: Emergency protocol initiated for alert {}. Status updated.", alert.getId());
                Platform.runLater(() -> {
                    view.showError("Emergency Protocol initiated for " + alert.getPatientName() + ". Notifications sent (simulated).");
                    loadCrisisData();
                });
            })
            .exceptionally(e -> {
                logger.error("Controller: Failed to initiate emergency protocol for alert {}: {}", alert.getId(), e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to initiate emergency protocol for " + alert.getPatientName() + ": " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }
}