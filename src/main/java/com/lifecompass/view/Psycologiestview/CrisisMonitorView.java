package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.CrisisAlert;
import com.lifecompass.model.psychologist.Patient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrisisMonitorView {

    private static final Logger logger = LoggerFactory.getLogger(CrisisMonitorView.class);

    private Stage parentStage;
    private Consumer<Node> contentSwitcher;
    private VBox patientRiskListContainer;
    private VBox activeAlertsContainer;
    private Button viewMorePatientsRiskButton;
    private int displayedPatientsRiskCount = 4;

    private List<CrisisAlert> crisisAlerts = new ArrayList<>();
    private List<Patient> patientsWithRisk = new ArrayList<>();

    private Consumer<CrisisAlert> contactPatientHandler;
    private Consumer<CrisisAlert> emergencyProtocolHandler;
    
    private Region loadingOverlayRegion;
    private StackPane rootContentArea;

    public CrisisMonitorView(Stage parentStage, Consumer<Node> contentSwitcher) {
        this.parentStage = parentStage;
        this.contentSwitcher = contentSwitcher;
        logger.info("CrisisMonitorView initialized.");
    }

    public ScrollPane getView() {
        logger.debug("Getting view for CrisisMonitorView.");
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #f0f2f5;");

        VBox mainCard = new VBox(20);
        mainCard.setPadding(new Insets(20));
        mainCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        mainCard.getChildren().addAll(
                createCrisisDashboardHeaderContent(),
                createActiveCrisisAlertsSection(),
                createPatientRiskLevelsSection()
        );
        logger.debug("Crisis Monitor main card built.");
        content.getChildren().add(mainCard);

        loadingOverlayRegion = new Region();
        loadingOverlayRegion.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);");
        loadingOverlayRegion.setManaged(false);
        loadingOverlayRegion.setVisible(false);

        rootContentArea = new StackPane(content, loadingOverlayRegion);
        
        ScrollPane scrollPane = new ScrollPane(rootContentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f0f2f5;");
        return scrollPane;
    }

    private VBox createCrisisDashboardHeaderContent() {
        VBox headerBox = new VBox(5);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label alertIcon = new Label("\u26A0");
        alertIcon.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alertIcon.setTextFill(Color.web("#B71C1C"));

        Label title = new Label("Crisis Monitoring Dashboard");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#B71C1C"));

        titleRow.getChildren().addAll(alertIcon, title);

        Label subtitle = new Label("Monitor patients at risk and coordinate emergency responses");
        subtitle.setFont(Font.font("Inter", 14));
        subtitle.setTextFill(Color.web("#777777"));

        headerBox.getChildren().addAll(titleRow, subtitle);
        return headerBox;
    }

    private VBox createActiveCrisisAlertsSection() {
        VBox section = new VBox(10);
        Label heading = new Label("Active Crisis Alerts");
        heading.setFont(Font.font("Inter", FontWeight.MEDIUM, 16));
        heading.setTextFill(Color.web("#333333"));
        section.getChildren().add(heading);

        activeAlertsContainer = new VBox(10);
        refreshCrisisAlertsDisplay();
        
        section.getChildren().add(activeAlertsContainer);
        logger.debug("Active Crisis Alerts section built.");
        return section;
    }

    private void refreshCrisisAlertsDisplay() {
        logger.debug("Refreshing crisis alerts display. Current alert count: {}", crisisAlerts.size());
        activeAlertsContainer.getChildren().clear();
        if (crisisAlerts.isEmpty()) {
            Label noAlertsLabel = new Label("No active crisis alerts at this time.");
            noAlertsLabel.setFont(Font.font("Inter", 14));
            noAlertsLabel.setTextFill(Color.web("#777777"));
            activeAlertsContainer.getChildren().add(noAlertsLabel);
            logger.info("No crisis alerts to display. Showing 'No alerts' message.");
        } else {
            for (CrisisAlert alert : crisisAlerts) {
                activeAlertsContainer.getChildren().add(createCrisisAlertCard(alert));
            }
            logger.debug("Displayed {} crisis alerts.", crisisAlerts.size());
        }
    }

    private HBox createCrisisAlertCard(CrisisAlert alert) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 15, 10, 15));
        card.setStyle("-fx-background-color: #fffafa; -fx-background-radius: 8; -fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 8;");

        VBox patientInfo = new VBox(3);
        Label nameLabel = new Label(alert.getPatientName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.BLACK);

        Label descLabel = new Label(alert.getTrigger() + (alert.getDescription() != null && !alert.getDescription().isEmpty() ? ": " + alert.getDescription() : ""));
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        descLabel.setTextFill(Color.GRAY);

        Label timeLabel = new Label(alert.getTimeAgo());
        timeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        timeLabel.setTextFill(Color.DARKGRAY);

        patientInfo.getChildren().addAll(nameLabel, descLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button contactPatientButton = new Button("Contact Patient");
        contactPatientButton.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 12));
        contactPatientButton.setTextFill(Color.WHITE);
        contactPatientButton.setStyle("-fx-background-color: #DC2626; -fx-background-radius: 6; -fx-padding: 8 15;");
        contactPatientButton.setOnMouseEntered(e -> contactPatientButton.setStyle("-fx-background-color: #C51A1A; -fx-background-radius: 6; -fx-padding: 8 15;"));
        contactPatientButton.setOnMouseExited(e -> contactPatientButton.setStyle("-fx-background-color: #DC2626; -fx-background-radius: 6; -fx-padding: 8 15;"));
        contactPatientButton.setOnAction(e -> {
            logger.info("Contact Patient button clicked for: {}", alert.getPatientName());
            if (contactPatientHandler != null) {
                contactPatientHandler.accept(alert);
            } else {
                logger.warn("Contact Patient handler not set. Opening default chat window.");
                openChatWindow(alert.getPatientName());
            }
        });

        Button emergencyProtocolButton = new Button("Emergency Protocol");
        emergencyProtocolButton.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 12));
        emergencyProtocolButton.setTextFill(Color.web("#333333"));
        emergencyProtocolButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 6; -fx-padding: 8 15; -fx-border-color: #D4D4D8; -fx-border-width: 1;");
        emergencyProtocolButton.setOnMouseEntered(e -> emergencyProtocolButton.setStyle("-fx-background-color: #F4F4F5; -fx-background-radius: 6; -fx-padding: 8 15; -fx-border-color: #A1A1AA; -fx-border-width: 1;"));
        emergencyProtocolButton.setOnMouseExited(e -> emergencyProtocolButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 6; -fx-padding: 8 15; -fx-border-color: #D4D4D8; -fx-border-width: 1;"));
        emergencyProtocolButton.setOnAction(e -> {
            logger.info("Emergency Protocol button clicked for: {}", alert.getPatientName());
            if (emergencyProtocolHandler != null) {
                emergencyProtocolHandler.accept(alert);
            } else {
                logger.warn("Emergency Protocol handler not set. Showing default dialog.");
                Alert dialog = new Alert(AlertType.CONFIRMATION);
                dialog.initOwner(parentStage);
                dialog.setTitle("Emergency Protocol");
                dialog.setHeaderText("Initiate Emergency Protocol for " + alert.getPatientName() + "?");
                dialog.setContentText("This action will trigger an immediate notification to emergency contacts and relevant authorities.");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    logger.warn("Emergency Protocol initiated (default action) for {}", alert.getPatientName());
                } else {
                    logger.debug("Emergency Protocol initiation cancelled (default action) for {}.", alert.getPatientName());
                }
            }
        });

        HBox buttons = new HBox(8, contactPatientButton, emergencyProtocolButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(patientInfo, spacer, buttons);

        String originalCardStyle = "-fx-background-color: #fffafa; -fx-background-radius: 8; -fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 8;";
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #ffe0e0; -fx-background-radius: 8; -fx-border-color: #ef9a9a; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);"));
        card.setOnMouseExited(e -> card.setStyle(originalCardStyle));

        return card;
    }

    private VBox createPatientRiskLevelsSection() {
        VBox section = new VBox(10);
        Label heading = new Label("Patient Risk Levels");
        heading.setFont(Font.font("Inter", FontWeight.MEDIUM, 16));
        heading.setTextFill(Color.web("#333333"));
        section.getChildren().add(heading);

        patientRiskListContainer = new VBox(8);
        refreshPatientRiskListDisplay();

        section.getChildren().add(patientRiskListContainer);

        viewMorePatientsRiskButton = new Button("View More Patients");
        viewMorePatientsRiskButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMorePatientsRiskButton.setTextFill(Color.web("#6a1b9a"));
        viewMorePatientsRiskButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMorePatientsRiskButton.setOnMouseEntered(e -> viewMorePatientsRiskButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMorePatientsRiskButton.setOnMouseExited(e -> viewMorePatientsRiskButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMorePatientsRiskButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(viewMorePatientsRiskButton, new Insets(10, 0, 0, 0));
        viewMorePatientsRiskButton.setOnAction(e -> {
            logger.info("View More Patients (Risk) button clicked. Displaying all.");
            displayedPatientsRiskCount = patientsWithRisk.size();
            refreshPatientRiskListDisplay();
            viewMorePatientsRiskButton.setVisible(false);
            viewMorePatientsRiskButton.setManaged(false);
        });
        section.getChildren().add(viewMorePatientsRiskButton);
        
        logger.debug("Patient Risk Levels section built.");
        return section;
    }

    private void refreshPatientRiskListDisplay() {
        logger.debug("Refreshing patient risk list display. Current patient with risk count: {}", patientsWithRisk.size());
        patientRiskListContainer.getChildren().clear();
        int count = Math.min(displayedPatientsRiskCount, patientsWithRisk.size());
        if (patientsWithRisk.isEmpty()) {
            Label noRiskPatientsLabel = new Label("No patient risk data available.");
            noRiskPatientsLabel.setFont(Font.font("Inter", 14));
            noRiskPatientsLabel.setTextFill(Color.web("#777777"));
            patientRiskListContainer.getChildren().add(noRiskPatientsLabel);
            logger.info("No patients with risk to display. Showing 'No data' message.");
        } else {
            for (int i = 0; i < count; i++) {
                patientRiskListContainer.getChildren().add(createPatientRiskItem(patientsWithRisk.get(i)));
            }
            logger.debug("Displayed {} out of {} patients with risk.", count, patientsWithRisk.size());
        }

        if (viewMorePatientsRiskButton != null) {
            boolean shouldShow = patientsWithRisk.size() > displayedPatientsRiskCount;
            viewMorePatientsRiskButton.setVisible(shouldShow);
            viewMorePatientsRiskButton.setManaged(shouldShow);
            logger.debug("View More Patients (Risk) button visibility set to {}.", shouldShow);
        }
    }

    private HBox createPatientRiskItem(Patient patient) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8;");

        Label initialsLabel = new Label(patient.getInitials());
        initialsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setPrefSize(40, 40);
        initialsLabel.setAlignment(Pos.CENTER);
        initialsLabel.setStyle("-fx-background-color: #6A1B9A; -fx-background-radius: 20;");
        Circle clip = new Circle(20, 20, 20);
        initialsLabel.setClip(clip);

        Label nameLabel = new Label(patient.getName());
        nameLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        nameLabel.setTextFill(Color.web("#333333"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox riskDetails = new VBox(2);
        riskDetails.setAlignment(Pos.CENTER_RIGHT);
        Label riskTextLabel = new Label("Risk Level");
        riskTextLabel.setFont(Font.font("Inter", 12));
        riskTextLabel.setTextFill(Color.web("#777777"));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(80);
        progressBar.setStyle("-fx-accent: " + getProgressBarColor(patient.getRisk()) + ";");
        progressBar.setProgress(getProgressBarValue(patient.getRisk()));

        riskDetails.getChildren().addAll(riskTextLabel, progressBar);

        Label riskBadge = new Label(patient.getRisk());
        riskBadge.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        riskBadge.setTextFill(Color.WHITE);
        riskBadge.setStyle("-fx-background-color: " + getRiskColor(patient.getRisk()) + "; -fx-background-radius: 5; -fx-padding: 3 8;");

        item.getChildren().addAll(initialsLabel, nameLabel, spacer, riskDetails, riskBadge);

        String originalItemStyle = "-fx-background-color: #F9FAFB; -fx-background-radius: 8;";
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 8;"));
        item.setOnMouseExited(e -> item.setStyle(originalItemStyle));

        return item;
    }

    private String getRiskColor(String risk) {
        return switch (risk.toLowerCase()) {
            case "low" -> "#22C55E";
            case "medium" -> "#F59E0B";
            case "high" -> "#EF4444";
            default -> "#777777";
        };
    }

    private String getProgressBarColor(String risk) {
        return switch (risk.toLowerCase()) {
            case "low" -> "#22C55E";
            case "medium" -> "#F59E0B";
            case "high" -> "#EF4444";
            default -> "#D1D5DB";
        };
    }

    private double getProgressBarValue(String risk) {
        return switch (risk.toLowerCase()) {
            case "low" -> 0.2;
            case "medium" -> 0.5;
            case "high" -> 0.9;
            default -> 0.0;
        };
    }

    private void openChatWindow(String patientName) {
        logger.info("Opening chat window for patient: {}", patientName);
        Stage chatStage = new Stage();
        chatStage.initModality(Modality.WINDOW_MODAL);
        chatStage.initOwner(parentStage);
        chatStage.setTitle("Chat with " + patientName);

        VBox chatContent = new VBox(10);
        chatContent.setPadding(new Insets(20));
        chatContent.setAlignment(Pos.CENTER);
        chatContent.getChildren().add(new Label("Chat interface for " + patientName));
        chatContent.getChildren().add(new TextField("Type message..."));
        chatContent.getChildren().add(new Button("Send"));

        Scene chatScene = new Scene(chatContent, 400, 300);
        chatStage.setScene(chatScene);
        chatStage.show();
    }
    
    // --- METHODS FOR THE CONTROLLER ---
    public void populateCrisisAlerts(List<CrisisAlert> alerts) {
        logger.info("Populating crisis alerts in view with {} items.", alerts.size());
        this.crisisAlerts = alerts;
        refreshCrisisAlertsDisplay();
        logger.debug("Crisis alerts population complete.");
    }

    public void populatePatientRiskLevels(List<Patient> patients) {
        logger.info("Populating patient risk levels in view with {} items.", patients.size());
        this.patientsWithRisk = patients;
        refreshPatientRiskListDisplay();
        logger.debug("Patient risk levels population complete.");
    }
    
    public void showLoading(boolean isLoading) {
        logger.debug("Setting loading state for CrisisMonitorView to: {}", isLoading);
        if (loadingOverlayRegion != null) {
            loadingOverlayRegion.setVisible(isLoading);
            loadingOverlayRegion.setManaged(isLoading);
        }

        if (isLoading) {
            if (activeAlertsContainer != null) activeAlertsContainer.getChildren().clear();
            if (patientRiskListContainer != null) patientRiskListContainer.getChildren().clear();
            if (activeAlertsContainer != null) activeAlertsContainer.getChildren().add(new Label("Loading active alerts..."));
            if (patientRiskListContainer != null) patientRiskListContainer.getChildren().add(new Label("Loading patient risks..."));
        } else {
            if (activeAlertsContainer != null) {
                activeAlertsContainer.getChildren().clear();
                if (crisisAlerts.isEmpty()) {
                    activeAlertsContainer.getChildren().add(new Label("No active crisis alerts at this time."));
                    logger.info("Loading finished for alerts. No alerts found.");
                } else {
                    refreshCrisisAlertsDisplay();
                }
            }
            
            if (patientRiskListContainer != null) {
                patientRiskListContainer.getChildren().clear();
                if (patientsWithRisk.isEmpty()) {
                    patientRiskListContainer.getChildren().add(new Label("No patient risk data available."));
                    logger.info("Loading finished for patient risks. No patients with risk found.");
                } else {
                    refreshPatientRiskListDisplay();
                }
            }
        }
        
        if (viewMorePatientsRiskButton != null) {
            boolean shouldShow = !isLoading && patientsWithRisk.size() > displayedPatientsRiskCount;
            viewMorePatientsRiskButton.setVisible(shouldShow);
            viewMorePatientsRiskButton.setManaged(shouldShow);
        }
    }

    public void showError(String message) {
        logger.error("Displaying error in CrisisMonitorView: {}", message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(parentStage);
            alert.setTitle("Error");
            alert.setHeaderText("Crisis Monitor Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
        showLoading(false);
    }

    public void setContactPatientHandler(Consumer<CrisisAlert> handler) {
        this.contactPatientHandler = handler;
    }

    public void setEmergencyProtocolHandler(Consumer<CrisisAlert> handler) {
        this.emergencyProtocolHandler = handler;
    }
}