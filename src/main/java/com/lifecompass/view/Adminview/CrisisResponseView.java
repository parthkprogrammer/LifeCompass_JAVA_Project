package com.lifecompass.view.Adminview;

import javafx.application.Platform; // For Platform.runLater for alerts
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert; // Ensure Alert is imported
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.SimpleDateFormat; // For formatting Date objects
import java.util.ArrayList;
import java.util.Date; // For working with Date objects from model
import java.util.List;
import java.util.stream.Collectors; // For stream operations

// MVC Imports
import com.lifecompass.controller.CrisisResponseController; // Import the new controller
import com.lifecompass.model.CrisisSituation; // Import the model class

/**
 * This class creates the UI for the Admin Portal's "Crisis Response" section.
 * It is now a UI component that returns a Node for integration into a larger dashboard.
 * It interacts with CrisisResponseController to fetch and update data.
 */
public class CrisisResponseView {

    private List<CrisisEvent> currentDisplayedEvents; // To hold crisis events currently shown
    private final int INITIAL_DISPLAY_COUNT = 3; // Initial number of events to display
    private int currentMaxDisplayCount = INITIAL_DISPLAY_COUNT; // How many to show right now
    private VBox crisisEventCardsContainer; // Container for crisis event cards
    private Button viewMoreButton; // Button to show more events

    private Stage primaryStage; // Reference to the main application's primary stage, needed for showAlert
    private CrisisResponseController controller; // MVC: Controller instance

    // Date formatter for displaying reported time
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /**
     * Constructor for the CrisisResponseView component.
     * @param primaryStage The primary stage of the main application (AdminDashboardView).
     * Needed for spawning new Alert dialogs.
     */
    public CrisisResponseView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // MVC: Initialize the controller and pass a reference to this view
        this.controller = new CrisisResponseController(this);
        this.currentDisplayedEvents = new ArrayList<>(); // Initialize to empty list
        // Data loading will be handled by the controller's `loadCrisisEvents()` method
    }

    /**
     * Returns the root Node of the Crisis Response UI.
     * This method is typically called by AdminDashboardView to embed this component.
     * @return A Node representing the complete Crisis Response UI.
     */
    public Node getView() {
        ScrollPane scrollPane = createCrisisResponseContent();
        scrollPane.setStyle("-fx-background-color: #f9fafb;"); // Ensure background matches dashboard's main content area
        // MVC: Trigger data load when the view is requested (e.g., when tab is opened)
        controller.loadCrisisEvents(); // Load all crisis events initially
        return scrollPane;
    }

    private ScrollPane createCrisisResponseContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0)); // Padding controlled by parent mainContentArea in dashboard
        content.setStyle("-fx-background-color: #f9fafb;"); // Match dashboard background

        // Header Section
        Label headerTitle = new Label("Crisis Response Center");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("Coordinate emergency responses and monitor critical situations");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0));
        content.getChildren().addAll(headerBox);

        // Crisis Event Cards List Container
        crisisEventCardsContainer = new VBox(15);
        // refreshCrisisEventListDisplay will be called by updateCrisisEvents from controller
        content.getChildren().add(crisisEventCardsContainer);

        // "View More" Button Logic
        viewMoreButton = new Button("View More Crisis Events");
        viewMoreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreButton.setTextFill(Color.web("#D32F2F"));
        viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreButton.setOnMouseEntered(e -> viewMoreButton.setStyle("-fx-background-color: #FFCDD2; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setOnMouseExited(e -> viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));

        viewMoreButton.setOnAction(e -> {
            currentMaxDisplayCount = currentDisplayedEvents.size(); // Show all available events
            refreshCrisisEventListDisplay(); // Re-render the list with all items
            viewMoreButton.setVisible(false); // Hide the button after all are shown
            viewMoreButton.setManaged(false); // Remove it from layout considerations
        });
        content.getChildren().add(viewMoreButton);

        // Emergency Contacts & Protocols Section
        content.getChildren().add(createEmergencyContactsProtocolsSection());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f9fafb; -fx-border-color: transparent;");
        return scrollPane;
    }

    /**
     * MVC: Updates the list of crisis events displayed in the UI.
     * This method is called by the `CrisisResponseController` after fetching data.
     * @param events The list of `CrisisEvent` objects (view's inner class) to display.
     */
    public void updateCrisisEvents(List<CrisisEvent> events) {
        this.currentDisplayedEvents = events; // Update the internal data source
        refreshCrisisEventListDisplay(); // Trigger the UI refresh based on the new data
    }

    /**
     * Creates a single crisis event card for display.
     * @param event The `CrisisEvent` data object for this card.
     * @return An HBox representing the UI card.
     */
    private HBox createCrisisEventCard(CrisisEvent event) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // User ID and Risk Level & Current Status Tags
        VBox userRiskBox = new VBox(2);
        Label userIdLabel = new Label(event.userId());
        userIdLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        userIdLabel.setTextFill(Color.web("#333333"));

        HBox riskStatusBox = new HBox(5);
        Label riskLabel = new Label(event.riskLevel());
        riskLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        riskLabel.setTextFill(Color.WHITE);
        riskLabel.setStyle("-fx-background-color: " + getRiskColor(event.riskLevel()) + "; -fx-background-radius: 5; -fx-padding: 3 8;");

        Label statusLabel = new Label(event.status()); // Display current status
        statusLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web("#555555"));
        statusLabel.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5; -fx-padding: 3 8;");
        riskStatusBox.getChildren().addAll(riskLabel, statusLabel);

        userRiskBox.getChildren().addAll(userIdLabel, riskStatusBox);

        // Event Description and Reported Info
        VBox descriptionBox = new VBox(2);
        Label descriptionLabel = new Label(event.description());
        descriptionLabel.setFont(Font.font("Inter", 14));
        descriptionLabel.setTextFill(Color.web("#333333"));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(400);

        Label reportedInfoLabel = new Label("Reported: " + event.reportedTime() + " • Assigned: " + event.assignedPsychologist());
        reportedInfoLabel.setFont(Font.font("Inter", 12));
        reportedInfoLabel.setTextFill(Color.web("#777777"));
        descriptionBox.getChildren().addAll(descriptionLabel, reportedInfoLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action Buttons
        VBox actionButtonsBox = new VBox(10);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button emergencyProtocolButton = createCrisisActionButton("Emergency Protocol", "#D32F2F"); // Red
        emergencyProtocolButton.setOnAction(e -> controller.handleEmergencyProtocol(event)); // Hook to controller

        Button contactAuthoritiesButton = createCrisisActionButton("Contact Authorities", "#2196F3"); // Blue
        contactAuthoritiesButton.setOnAction(e -> controller.handleContactAuthorities(event)); // Hook to controller

        Button updateStatusButton = createCrisisActionButton("Update Status", "#FFC107"); // Amber
        updateStatusButton.setOnAction(e -> controller.handleUpdateStatus(event)); // Hook to controller

        actionButtonsBox.getChildren().addAll(emergencyProtocolButton, contactAuthoritiesButton, updateStatusButton);

        card.getChildren().addAll(userRiskBox, descriptionBox, spacer, actionButtonsBox);
        return card;
    }

    private Button createCrisisActionButton(String text, String colorHex) {
        Button button = new Button(text);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 8; -fx-padding: 8 12;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + getDarkerColor(colorHex) + "; -fx-background-radius: 8; -fx-padding: 8 12;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 8; -fx-padding: 8 12;"));
        return button;
    }

    /** Helper to get a slightly darker version of a color for hover effect */
    private String getDarkerColor(String hexColor) {
        Color color = Color.web(hexColor);
        // Darken by 10% and convert back to hex string
        return color.darker().deriveColor(0, 1, 0.9, 1).toString().replace("0x", "#");
    }

    private String getRiskColor(String riskLevel) {
        return switch (riskLevel.toLowerCase()) {
            case "high" -> "#F44336"; // Red
            case "medium" -> "#FFC107"; // Amber
            case "low" -> "#4CAF50"; // Green
            default -> "#777777"; // Default gray
        };
    }

    /**
     * Refreshes the display of crisis event cards based on currentMaxDisplayCount.
     */
    private void refreshCrisisEventListDisplay() {
        if (crisisEventCardsContainer == null) { // Defensive check, initialize if null
            crisisEventCardsContainer = new VBox(15);
        }
        crisisEventCardsContainer.getChildren().clear(); // Clear existing cards

        int count = Math.min(currentMaxDisplayCount, currentDisplayedEvents.size());
        for (int i = 0; i < count; i++) {
            crisisEventCardsContainer.getChildren().add(createCrisisEventCard(currentDisplayedEvents.get(i)));
        }

        if (viewMoreButton != null) {
            boolean showViewMore = currentDisplayedEvents.size() > INITIAL_DISPLAY_COUNT; // Only show if more exist than initially displayed
            viewMoreButton.setVisible(showViewMore);
            viewMoreButton.setManaged(showViewMore);
        }
    }

    // FIX: Changed showAlert from private to public, and its first parameter type
    public void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type); // 'type' is correctly passed from the parameter
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(primaryStage);
            alert.showAndWait();
        });
    }

    /**
     * Helper to show an error alert dialog.
     */
    // FIX: Changed showErrorAlert from private to public
    public void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(primaryStage);
            alert.showAndWait();
        });
    }

    /**
     * Creates the Emergency Contacts & Protocols section for the UI.
     * @return a VBox containing emergency contacts and protocol info.
     */
    private VBox createEmergencyContactsProtocolsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 0, 0));

        Label contactsHeader = new Label("Emergency Contacts & Protocols");
        contactsHeader.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        contactsHeader.setTextFill(Color.web("#333333"));

        Label contactsInfo = new Label(
            "• National Emergency: 911\n" +
            "• Suicide Prevention Lifeline: 988\n" +
            "• Local Police: (XXX) XXX-XXXX\n" +
            "• Hospital Emergency: (XXX) XXX-XXXX\n\n" +
            "Protocols:\n" +
            "• Activate Emergency Protocol for high-risk cases.\n" +
            "• Contact authorities if immediate danger is present.\n" +
            "• Assign psychologist for monitoring and follow-up."
        );
        contactsInfo.setFont(Font.font("Inter", 13));
        contactsInfo.setTextFill(Color.web("#555555"));
        contactsInfo.setWrapText(true);

        section.getChildren().addAll(contactsHeader, contactsInfo);
        section.setStyle("-fx-background-color: #fffde7; -fx-background-radius: 10; -fx-padding: 15;");
        return section;
    }

    /**
     * A record to hold crisis event data for the UI.
     * Includes the document ID and flags for protocol/authorities.
     */
    public static class CrisisEvent {
        private final String id; // Document ID of the crisis event
        private final String userId;
        private final String riskLevel;
        private final String status; // Current status, e.g., "Active Response", "Monitoring", "Resolved"
        private final String description;
        private final String reportedTime; // Formatted time string
        private final String assignedPsychologist;
        private final boolean protocolActivated; // Reflects state of emergency protocol
        private final boolean authoritiesContacted; // Reflects if authorities contacted

        public CrisisEvent(String id, String userId, String riskLevel, String status, String description, String reportedTime, String assignedPsychologist, boolean protocolActivated, boolean authoritiesContacted) {
            this.id = id;
            this.userId = userId;
            this.riskLevel = riskLevel;
            this.status = status;
            this.description = description;
            this.reportedTime = reportedTime;
            this.assignedPsychologist = assignedPsychologist;
            this.protocolActivated = protocolActivated;
            this.authoritiesContacted = authoritiesContacted;
        }

        // Getters for all fields
        public String id() { return id; }
        public String userId() { return userId; }
        public String riskLevel() { return riskLevel; }
        public String status() { return status; }
        public String description() { return description; }
        public String reportedTime() { return reportedTime; }
        public String assignedPsychologist() { return assignedPsychologist; }
        public boolean protocolActivated() { return protocolActivated; }
        public boolean authoritiesContacted() { return authoritiesContacted; }
    }
}