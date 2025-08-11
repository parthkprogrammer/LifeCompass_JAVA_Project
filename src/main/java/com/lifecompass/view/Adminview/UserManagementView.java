package com.lifecompass.view.Adminview;

import javafx.application.Platform; // For Platform.runLater for alerts
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.google.firebase.auth.UserRecord;

import java.text.SimpleDateFormat; // For formatting date
import java.util.ArrayList;
import java.util.Date; // For working with Date objects from model
import java.util.List;
import java.util.stream.Collectors; // For stream operations

import com.lifecompass.controller.UserManagementController; // Import the controller

/**
 * This class creates the UI for the Admin Portal's "User Management" section.
 * It is now a UI component that returns a Node for integration into a larger dashboard.
 * It interacts with UserManagementController to fetch and update data.
 */
public class UserManagementView {

    private List<UserRecord> allUserRecords = new ArrayList<>(); // To hold current filtered/displayed data
    private final int INITIAL_DISPLAY_COUNT = 5; // Initial number of records to display
    private int currentDisplayedUserCount = INITIAL_DISPLAY_COUNT;
    private VBox userRecordCardsContainer; // Container for user record cards
    private Button viewMoreButton; // Button to show more records
    private Button viewLessButton; // Button to show fewer records
    private TextField searchField; // Reference to the search field

    private Stage primaryStage; // Reference to the main application's primary stage, needed for showAlert
    private UserManagementController controller; // MVC: Controller instance

    // Date formatter for displaying joined date (moved here for view-specific formatting)
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * Constructor for the UserManagementView component.
     * @param primaryStage The primary stage of the main application (admindashaboardView).
     * Needed for spawning new Alert dialogs.
     */
    public UserManagementView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // MVC: Initialize the controller and pass a reference to this view
        this.controller = new UserManagementController(this);
        // Data will be loaded by the controller, not dummy data here.
    }

    /**
     * Returns the root Node of the User Management UI.
     * This method replaces the functionality previously in `start()`.
     * @return A Node representing the complete User Management UI.
     */
    public Node getView() {
        ScrollPane scrollPane = createUserManagementContent();
        scrollPane.setStyle("-fx-background-color: #f9fafb;"); // Ensure background matches dashboard's main content area
        // MVC: Trigger data load when the view is requested (e.g., when tab is opened)
        controller.loadUsers(null); // Load all users initially
        return scrollPane;
    }

    private ScrollPane createUserManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0)); // Padding controlled by parent mainContentArea in dashboard
        content.setStyle("-fx-background-color: #f9fafb;"); // Match dashboard background

        Label headerTitle = new Label("User Management");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("Manage user accounts and monitor platform activity");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        content.getChildren().add(headerBox);

        // Search, Filter, Export Row
        HBox searchFilterExportBox = new HBox(10);
        searchFilterExportBox.setAlignment(Pos.CENTER_LEFT);
        searchFilterExportBox.setPadding(new Insets(0, 0, 20, 0));

        searchField = new TextField(); // Assign to field
        searchField.setPromptText("Search users by ID, email, or name...");
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8 15;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button filterButton = createUtilityButton("Filter");
        filterButton.setOnAction(e -> controller.handleFilter()); // Hook to controller

        Button exportButton = createUtilityButton("Export");
        exportButton.setOnAction(e -> controller.handleExport()); // Hook to controller

        searchFilterExportBox.getChildren().addAll(searchField, filterButton, exportButton);
        content.getChildren().add(searchFilterExportBox);

        // Add listener to search field for real-time (or on-enter) filtering
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            // Option 1: Filter on every keystroke (can be heavy for large datasets)
            // controller.loadUsers(newText); // Not active here, but the listener is present.
        });
        // Adding an explicit search button or on-action for search field is recommended
        // For simplicity, let's trigger search on typing for now
        searchField.setOnAction(e -> controller.loadUsers(searchField.getText())); // Trigger search on Enter

        // User Record Cards List
        userRecordCardsContainer = new VBox(15);
        // refreshUserRecordListDisplay(); // This will be called by controller via updateUserRecords
        content.getChildren().add(userRecordCardsContainer);

        // View More/Less Buttons
        HBox viewButtonsBox = new HBox(10);
        viewButtonsBox.setAlignment(Pos.CENTER);
        VBox.setMargin(viewButtonsBox, new Insets(20, 0, 0, 0));

        viewMoreButton = createViewButton("View More Users");
        viewMoreButton.setOnAction(e -> {
            currentDisplayedUserCount = allUserRecords.size(); // Show all
            refreshUserRecordListDisplay();
        });

        viewLessButton = createViewButton("View Less Users");
        viewLessButton.setOnAction(e -> {
            currentDisplayedUserCount = INITIAL_DISPLAY_COUNT; // Show initial count
            refreshUserRecordListDisplay();
        });

        viewButtonsBox.getChildren().addAll(viewMoreButton, viewLessButton);
        content.getChildren().add(viewButtonsBox);


        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f9fafb; -fx-border-color: transparent;");
        return scrollPane;
    }

    private Button createUtilityButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Inter", 12));
        button.setTextFill(Color.web("#555555"));
        button.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 15; -fx-border-color: #cccccc; -fx-border-width: 1;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 8; -fx-padding: 8 15; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 15; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        return button;
    }

    private Button createViewButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#D32F2F")); // Admin red
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #FFCDD2; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        return button;
    }

    /**
     * MVC: Updates the list of user records displayed in the UI.
     * This method is called by the controller after fetching data.
     * @param userRecords The list of UserRecord objects to display.
     */
    public void updateUserRecords(List<UserRecord> userRecords) {
        this.allUserRecords = userRecords; // Update the internal data source
        refreshUserRecordListDisplay(); // Refresh the UI based on the new data
    }

    /**
     * Refreshes the display of user record cards based on currentDisplayedUserCount.
     */
    private void refreshUserRecordListDisplay() {
        if (userRecordCardsContainer == null) {
            userRecordCardsContainer = new VBox(15);
        }
        userRecordCardsContainer.getChildren().clear();
        int count = Math.min(currentDisplayedUserCount, allUserRecords.size());
        for (int i = 0; i < count; i++) {
            UserRecord record = allUserRecords.get(i);
            // Pass UserRecord to the card creation method
            userRecordCardsContainer.getChildren().add(createUserRecordCard(record));
        }

        // Manage visibility of "View More" and "View Less" buttons
        if (viewMoreButton != null) {
            viewMoreButton.setVisible(currentDisplayedUserCount < allUserRecords.size());
            viewMoreButton.setManaged(currentDisplayedUserCount < allUserRecords.size());
        }
        if (viewLessButton != null) {
            viewLessButton.setVisible(currentDisplayedUserCount == allUserRecords.size() && allUserRecords.size() > INITIAL_DISPLAY_COUNT);
            viewLessButton.setManaged(currentDisplayedUserCount == allUserRecords.size() && allUserRecords.size() > INITIAL_DISPLAY_COUNT);
        }
    }

    private HBox createUserRecordCard(UserRecord userRecord) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // User ID (large number on the left)
        Label idLabel = new Label(extractUserIdNumber(userRecord.id())); // Use helper to get number part
        idLabel.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        idLabel.setTextFill(Color.web("#6a1b9a")); // Purple color
        idLabel.setPrefWidth(60); // Fixed width for alignment
        idLabel.setAlignment(Pos.CENTER);

        // User Details (Email, Joined Date)
        VBox detailsBox = new VBox(2);
        Label emailLabel = new Label(userRecord.email());
        emailLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        emailLabel.setTextFill(Color.web("#333333"));

        Label joinedDateLabel = new Label("Joined: " + userRecord.joinedDate());
        joinedDateLabel.setFont(Font.font("Inter", 12));
        joinedDateLabel.setTextFill(Color.web("#777777"));

        detailsBox.getChildren().addAll(emailLabel, joinedDateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status and Risk Tags
        HBox tagsBox = new HBox(5);
        Label statusTag = new Label(userRecord.status());
        statusTag.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        statusTag.setTextFill(Color.WHITE);
        statusTag.setStyle("-fx-background-color: " + getStatusColor(userRecord.status()) + "; -fx-background-radius: 5; -fx-padding: 3 8;");

        Label riskTag = new Label(userRecord.riskLevel());
        riskTag.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        riskTag.setTextFill(Color.WHITE);
        riskTag.setStyle("-fx-background-color: " + getRiskColor(userRecord.riskLevel()) + "; -fx-background-radius: 5; -fx-padding: 3 8;");
        tagsBox.getChildren().addAll(statusTag, riskTag);

        // Action Buttons
        VBox actionButtonsBox = new VBox(5); // Vertical layout for buttons
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsButton = createActionButton("View Details", "#f0f2f5", Color.web("#555555"));
        viewDetailsButton.setStyle(viewDetailsButton.getStyle() + "; -fx-border-color: #cccccc; -fx-border-width: 1;");
        viewDetailsButton.setOnMouseEntered(e -> viewDetailsButton.setStyle(viewDetailsButton.getStyle().replace("#f0f2f5", "#e0e0e0")));
        viewDetailsButton.setOnMouseExited(e -> viewDetailsButton.setStyle(viewDetailsButton.getStyle().replace("#e0e0e0", "#f0f2f5")));
        viewDetailsButton.setOnAction(e -> controller.handleViewDetails(userRecord.id())); // Hook to controller

        Button actionsButton = createActionButton("Actions", "#f0f2f5", Color.web("#555555"));
        actionsButton.setStyle(actionsButton.getStyle() + "; -fx-border-color: #cccccc; -fx-border-width: 1;");
        actionsButton.setOnMouseEntered(e -> actionsButton.setStyle(actionsButton.getStyle().replace("#f0f2f5", "#e0e0e0")));
        actionsButton.setOnMouseExited(e -> actionsButton.setStyle(actionsButton.getStyle().replace("#e0e0e0", "#f0f2f5")));
        actionsButton.setOnAction(e -> controller.handleUserActions(userRecord.id(), userRecord.status(), userRecord.riskLevel())); // Hook to controller

        actionButtonsBox.getChildren().addAll(viewDetailsButton, actionsButton);

        card.getChildren().addAll(idLabel, detailsBox, spacer, tagsBox, actionButtonsBox);
        return card;
    }

    private Button createActionButton(String text, String bgColorHex, Color textColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Inter", 12));
        button.setTextFill(textColor);
        button.setStyle("-fx-background-color: " + bgColorHex + "; -fx-background-radius: 8; -fx-padding: 8 12;");
        return button;
    }

    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "active" -> "#4CAF50"; // Green
            case "suspended" -> "#F44336"; // Red
            default -> "#777777"; // Default gray
        };
    }

    private String getRiskColor(String riskLevel) {
        return switch (riskLevel.toLowerCase()) {
            case "high risk" -> "#F44336"; // Red
            case "medium risk" -> "#FFC107"; // Amber
            case "low risk" -> "#4CAF50"; // Green
            default -> "#777777"; // Default gray
        };
    }

    // Helper method to extract the number from "User_1234"
    private String extractUserIdNumber(String userId) {
        if (userId != null && userId.contains("_")) {
            return userId.substring(userId.indexOf("_") + 1);
        }
        return userId; // Return original if format doesn't match
    }

    /**
     * Helper to show a generic alert dialog.
     */
    public void showAlert(String title, String message) {
        // Use Platform.runLater to ensure UI updates are on the JavaFX Application Thread
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(primaryStage); // Make alert dialog modal to primary stage
            alert.showAndWait();
        });
    }

    /**
     * Helper to show an error alert dialog.
     */
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

    // Moved UserRecord here as it's a view-specific representation
    public record UserRecord(String id, String email, String joinedDate, String status, String riskLevel) {}
}