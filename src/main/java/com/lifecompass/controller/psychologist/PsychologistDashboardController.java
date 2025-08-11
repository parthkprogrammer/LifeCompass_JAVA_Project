package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.Appointment;
import com.lifecompass.model.psychologist.CrisisAlert;
import com.lifecompass.model.psychologist.Patient;
import com.lifecompass.view.Psycologiestview.PsychologistDashboard;
import com.lifecompass.view.Psycologiestview.PatientsView;
import com.lifecompass.view.Psycologiestview.AppointmentsView;
import com.lifecompass.view.Psycologiestview.CrisisMonitorView;
import com.lifecompass.view.Psycologiestview.AnalyticsScreen;
import com.lifecompass.view.Psycologiestview.PsychologistProfileView;
import com.lifecompass.view.Psycologiestview.PsychologistNotificationScreen;
import com.lifecompass.view.Psycologiestview.PsychologistSettingsScreen; // Import the Settings View

//import com.finallifecompass.controller.AnalyticsController;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage; // Ensure Stage is imported

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PsychologistDashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(PsychologistDashboardController.class);

    private final PsychologistDashboard dashboardView;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    //private AnalyticsController analyticsController;
    private PsychologistProfileController psychologistProfileController;
    private PsychologistSettingsController psychologistSettingsController;

    public PsychologistDashboardController(PsychologistDashboard dashboardView, String psychologistId) {
        this.dashboardView = dashboardView;
        this.psychologistDao = new PsychologistDao();
        this.psychologistId = psychologistId;
        logger.info("PsychologistDashboardController initialized for ID: {}", psychologistId);
    }

    public void initialize() {
        dashboardView.setNavigationHandler(this::switchContent);
        logger.info("Dashboard navigation handler set.");
        
        dashboardView.setCrisisRespondHandler(this::handleCrisisRespond);
        logger.info("Crisis respond handler set.");

        loadPsychologistHeaderInfo();
        switchContent("Overview");
    }

    private void loadPsychologistHeaderInfo() {
        logger.info("Loading psychologist header info for ID: {}", psychologistId);
        psychologistDao.getPsychologistName(psychologistId)
            .thenAccept(name -> {
                Platform.runLater(() -> dashboardView.setTherapistName(name));
                logger.info("Psychologist name updated to: {}", name);
            })
            .exceptionally(e -> {
                logger.error("Error loading psychologist header info for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> dashboardView.setTherapistName("Error Loading Name"));
                return null;
            });
    }

    private void loadDashboardMetrics() {
        logger.info("Loading dynamic dashboard metrics for psychologist ID: {}", psychologistId);

        psychologistDao.getPatientsForPsychologist(psychologistId)
            .thenAccept(patients -> {
                Platform.runLater(() -> {
                    dashboardView.setTotalPatientsCount(patients.size());
                    dashboardView.setRecentPatientActivity(patients);
                    logger.info("Total patients and recent activity updated.");
                });
            })
            .exceptionally(e -> {
                logger.error("Error loading total patients and recent activity: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    dashboardView.setTotalPatientsCount(0);
                    dashboardView.setRecentPatientActivity(Collections.emptyList());
                });
                return null;
            });

        psychologistDao.getActiveChatsCount(psychologistId)
            .thenAccept(count -> {
                Platform.runLater(() -> dashboardView.setActiveChatsCount(count));
                logger.info("Active chats count updated: {}", count);
            })
            .exceptionally(e -> {
                logger.error("Error loading active chats count: {}", e.getMessage(), e);
                Platform.runLater(() -> dashboardView.setActiveChatsCount(0));
                return null;
            });

        psychologistDao.getAppointmentsForToday(psychologistId, LocalDate.now())
            .thenAccept(appointmentsToday -> {
                Platform.runLater(() -> {
                    dashboardView.setAppointmentsTodayCount(appointmentsToday.size());
                    dashboardView.setTodaysSchedule(appointmentsToday);
                    logger.info("Appointments today count and schedule updated.");
                });
            })
            .exceptionally(e -> {
                logger.error("Error loading appointments today: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    dashboardView.setAppointmentsTodayCount(0);
                    dashboardView.setTodaysSchedule(Collections.emptyList());
                });
                return null;
            });

        psychologistDao.getActiveCrisisAlerts(psychologistId)
            .thenAccept(crisisAlerts -> {
                Platform.runLater(() -> {
                    dashboardView.setActiveCrisisAlerts(crisisAlerts);
                    dashboardView.setCrisisAlertsCount(crisisAlerts.size());
                    logger.info("Active crisis alerts updated: {}", crisisAlerts.size());
                });
            })
            .exceptionally(e -> {
                logger.error("Error loading active crisis alerts: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    dashboardView.setActiveCrisisAlerts(Collections.emptyList());
                    dashboardView.setCrisisAlertsCount(0);
                });
                return null;
            });
    }

    private void handleCrisisRespond(CrisisAlert alert) {
        logger.info("Responding to crisis alert: {} for patient: {}", alert.getAlertId(), alert.getPatientName());
        
        psychologistDao.updateCrisisAlertStatus(alert.getId(), "Acknowledged")
            .thenRun(() -> {
                logger.info("Crisis alert {} status updated to Acknowledged.", alert.getId());
                loadDashboardMetrics();
                Platform.runLater(() -> dashboardView.showSuccessMessage("Crisis Alert for " + alert.getPatientName() + " acknowledged."));
            })
            .exceptionally(e -> {
                logger.error("Failed to update status for crisis alert {}: {}", alert.getId(), e.getMessage(), e);
                Platform.runLater(() -> dashboardView.showErrorMessage("Failed to acknowledge alert for " + alert.getPatientName() + "."));
                return null;
            });
    }

    private void displayContent(Node contentNode) {
        Platform.runLater(() -> dashboardView.getMainContentArea().getChildren().setAll(contentNode));
        logger.debug("Content updated in main content area.");
    }

    private void handleContentDisplay(Node contentNode) {
        logger.debug("Invoked handleContentDisplay for a Node.");
        displayContent(contentNode);
    }
    
    public void switchContent(String tabName) {
        logger.info("Request to switch content to tab: {}", tabName);
        Node contentToShow = null;

        switch (tabName) {
            case "Overview":
                contentToShow = dashboardView.buildOverviewContent();
                loadDashboardMetrics();
                logger.info("Overview content built and ready. Metrics loading triggered.");
                break;

            case "Patients":
                PatientsView patientsView = new PatientsView(this::handleContentDisplay); 
                PatientsController patientsController = new PatientsController(patientsView, psychologistDao, psychologistId);
                patientsController.initialize();
                contentToShow = patientsView.getView();
                logger.info("Patients screen initialized and retrieved.");
                break;

            case "Appointments":
                AppointmentsView appointmentsView = new AppointmentsView(dashboardView.getPrimaryStage(), this::handleContentDisplay);
                AppointmentsController appointmentsController = new AppointmentsController(appointmentsView, psychologistDao, psychologistId);
                appointmentsController.initialize();
                contentToShow = appointmentsView.getView();
                logger.info("Appointments screen initialized and retrieved.");
                break;

            case "Crisis Monitor":
                CrisisMonitorView crisisView = new CrisisMonitorView(dashboardView.getPrimaryStage(), this::handleContentDisplay);
                CrisisMonitorController crisisController = new CrisisMonitorController(crisisView, psychologistDao, psychologistId);
                crisisController.initialize();
                contentToShow = crisisView.getView();
                logger.info("Crisis Monitor screen initialized and retrieved.");
                break;

            case "Analytics":
                AnalyticsScreen analyticsScreen = new AnalyticsScreen();
                contentToShow = analyticsScreen.getView();
                logger.info("Analytics screen initialized and retrieved (static content).");
                break;

            case "Profile":
                if (psychologistProfileController == null) {
                    PsychologistProfileView profileView = new PsychologistProfileView(dashboardView.getPrimaryStage(), this::handleContentDisplay);
                    psychologistProfileController = new PsychologistProfileController(profileView, psychologistDao, psychologistId);
                    logger.info("New PsychologistProfileController instance created.");
                }
                psychologistProfileController.initialize();
                contentToShow = psychologistProfileController.getView();
                logger.info("Profile screen initialized and retrieved.");
                break;

            case "Notifications":
                PsychologistNotificationScreen notificationScreen = new PsychologistNotificationScreen(() -> switchContent("Overview"));
                PsychologistNotificationController notificationController = new PsychologistNotificationController(notificationScreen, psychologistDao, psychologistId);
                notificationController.initialize();
                contentToShow = notificationScreen.getContent();
                logger.info("Notifications screen initialized and retrieved.");
                break; 

            case "Settings":
                // CORRECTED LOGIC FOR SETTINGS TAB
                if (psychologistSettingsController == null) {
                    // Pass dashboardView.getPrimaryStage() to the settings view constructor
                    PsychologistSettingsScreen settingsView = new PsychologistSettingsScreen(() -> switchContent("Overview"), dashboardView.getPrimaryStage());
                    psychologistSettingsController = new PsychologistSettingsController(settingsView, psychologistDao, psychologistId);
                    logger.info("New PsychologistSettingsController instance created.");
                }
                psychologistSettingsController.initialize();
                contentToShow = psychologistSettingsController.getView();
                logger.info("Settings screen initialized and retrieved.");
                break;

            default:
                contentToShow = new Label("Content for " + tabName + " not found. Please check tab name spelling.");
                logger.warn("Attempted to switch to unknown tab: {}", tabName);
                break;
        }
        
        if (contentToShow != null) {
            displayContent(contentToShow);
        } else {
            logger.error("Content to show was null after switch case for tab: {}. This should not happen.", tabName);
        }
    }
}