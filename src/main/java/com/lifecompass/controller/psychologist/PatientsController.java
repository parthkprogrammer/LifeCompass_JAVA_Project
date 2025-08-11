package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.view.Psycologiestview.PatientsView;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientsController {

    private static final Logger logger = LoggerFactory.getLogger(PatientsController.class);

    private final PatientsView view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public PatientsController(PatientsView view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("PatientsController initialized for ID: {}", psychologistId);
    }

    public void initialize() {
        loadPatients();
    }

    private void loadPatients() {
        view.showLoading(true);
        logger.info("Loading patients for ID: {}", psychologistId);
        psychologistDao.getPatientsForPsychologist(psychologistId)
            .thenAccept(patients -> {
                Platform.runLater(() -> {
                    view.populatePatientList(patients);
                    view.showLoading(false);
                    logger.info("Patients list populated with {} items.", patients.size());
                });
            })
            .exceptionally(e -> {
                 logger.error("Error loading patients for ID {}: {}", psychologistId, e.getMessage(), e);
                 Platform.runLater(() -> {
                    view.showError("Failed to load patients: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }
}