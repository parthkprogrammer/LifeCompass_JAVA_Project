package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.Appointment;
import com.lifecompass.view.Psycologiestview.AppointmentsView;
import javafx.application.Platform;
import java.time.LocalDate;
import java.time.ZoneId; // Needed for date conversion
import java.util.Date; // Needed for date conversion

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.Timestamp; // Needed for Timestamp type

public class AppointmentsController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentsController.class);

    private final AppointmentsView view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public AppointmentsController(AppointmentsView view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("AppointmentsController initialized for ID: {}", psychologistId);
    }

    public void initialize() {
        view.setRescheduleHandler(this::handleReschedule);
        logger.debug("Reschedule handler set for AppointmentsView.");
        loadAppointments();
    }

    private void loadAppointments() {
        logger.info("Loading appointments for psychologist ID: {}", psychologistId);
        view.showLoading(true);

        // This method call should now correctly resolve in PsychologistDao.java
        psychologistDao.getAppointmentsForPsychologist(psychologistId)
            .thenAccept(appointments -> {
                Platform.runLater(() -> {
                    view.populateAppointmentsList(appointments);
                    view.showLoading(false);
                    logger.info("Appointments list populated with {} items.", appointments.size());
                });
            })
            .exceptionally(e -> {
                logger.error("Failed to load appointments for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to load appointments: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    private void handleReschedule(Appointment appointment, LocalDate newDate, String newTime) {
        logger.info("Handling reschedule for appointment ID: {} to new date: {} and time: {}", appointment.getId(), newDate, newTime);
        view.showLoading(true); 

        // CRITICAL FIX: Convert LocalDate to com.google.cloud.Timestamp for the model
        if (newDate != null) {
            appointment.setDate(Timestamp.of(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))); 
        } else {
            appointment.setDate(null); // Or handle as error if date is null
        }
        
        appointment.setTime(newTime);
        appointment.setStatus("Rescheduled"); 

        psychologistDao.updateAppointment(appointment)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    view.showSuccess("Appointment rescheduled successfully!"); // This method is provided in AppointmentsView
                    loadAppointments(); 
                    logger.info("Appointment {} rescheduled and list refreshed.", appointment.getId());
                });
            })
            .exceptionally(e -> {
                logger.error("Failed to reschedule appointment {}: {}", appointment.getId(), e.getMessage(), e);
                Platform.runLater(() -> view.showError("Failed to reschedule: " + e.getMessage())); // This method is provided in AppointmentsView
                return null;
            });
    }
}