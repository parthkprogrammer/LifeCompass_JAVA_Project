
package com.lifecompass.view.Adminview;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // For working with Date objects from model
import java.util.Optional; // For Optional return types (if needed for dialogs, etc.)

import com.lifecompass.controller.VerificationController; // Import the controller
// Note: We don't import VerificationRequestModel here directly as AdminVerification.java
// works with its own inner class VerificationRequest for UI representation.
// The mapping from model to view's record happens in the controller.

/**
 * This class represents the UI for the Admin Portal's "Verifications" section.
 * It is now a UI component that returns a Node for integration into a larger dashboard.
 */
public class AdminVerification {

    // These hold the data that is currently displayed or available for display
    private List<VerificationRequest> currentDisplayedRequests;
    private final int INITIAL_DISPLAY_COUNT = 5; // Initial number of requests to display
    private int currentMaxDisplayCount = INITIAL_DISPLAY_COUNT; // How many to show right now on the UI
    private VBox requestCardsListContainer; // Container for request cards (the VBox that holds all the HBoxes for requests)
    private Button viewMoreButton; // Button to show more requests

    private Stage primaryStage; // Reference to the main application's primary stage, needed for dialogs
    private VerificationController controller; // MVC: Controller instance

    // Date formatter for displaying submission date consistently across the view
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * Constructor for the AdminVerification component.
     * Initializes the view and its associated controller.
     * @param primaryStage The primary stage of the main application. This is essential
     * for making dialogs modal to the main application window.
     */
    public AdminVerification(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Initialize the controller and pass a reference to this view so it can update the UI
        this.controller = new VerificationController(this);
        this.currentDisplayedRequests = new ArrayList<>(); // Initialize the list to empty
        // Data loading is now handled by the controller's loadVerificationRequests() method
    }

    /**
     * Returns the root Node of the Admin Verifications UI.
     * This method is typically called by the AdminDashboardView to embed this component.
     * It also triggers the initial loading of verification requests.
     * @return A Node representing the complete Admin Verifications UI.
     */
    public Node getView() {
        ScrollPane scrollPane = createVerificationRequestsContent();
        scrollPane.setStyle("-fx-background-color: #f9fafb;"); // Ensure background matches dashboard's main content area
        // Trigger data load when the view is requested (e.g., when the "Verifications" tab is opened)
        controller.loadVerificationRequests();
        return scrollPane;
    }

    /**
     * Builds the main content area of the Verification Requests screen.
     * @return A ScrollPane containing the entire UI for verification requests.
     */
    private ScrollPane createVerificationRequestsContent() {
        VBox content = new VBox(20); // Main container for all elements on this screen
        // Padding for the inner content is handled here, outer padding by AdminDashboardView
        content.setPadding(new Insets(0));
        content.setStyle("-fx-background-color: #f9fafb;");

        // Header Section (Title and Subtitle)
        Label headerTitle = new Label("Verification Requests");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("Review and approve psychologist credentials and user account requests");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0)); // Internal padding for this header section
        content.getChildren().add(headerBox);

        // Container for dynamically added verification request cards
        requestCardsListContainer = new VBox(15); // Vertically stacks the individual request cards
        // The cards are populated by refreshRequestListDisplay(), which is called by updateVerificationRequests()
        content.getChildren().add(requestCardsListContainer);

        // "View More" Button Logic
        viewMoreButton = new Button("View More Requests");
        viewMoreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreButton.setTextFill(Color.web("#D32F2F")); // Admin red
        viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreButton.setOnMouseEntered(e -> viewMoreButton.setStyle("-fx-background-color: #FFCDD2; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setOnMouseExited(e -> viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));

        viewMoreButton.setOnAction(e -> {
            currentMaxDisplayCount = currentDisplayedRequests.size(); // Set to display all available requests
            refreshRequestListDisplay(); // Re-render the list with all items
            viewMoreButton.setVisible(false); // Hide the button after all are shown
            viewMoreButton.setManaged(false); // Remove it from layout considerations
        });
        content.getChildren().add(viewMoreButton);

        // Main ScrollPane to make the content scrollable
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true); // Ensures content fits width of the scrollpane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scrollbar
        scrollPane.setStyle("-fx-background-color: #f9fafb; -fx-border-color: transparent;");

        return scrollPane;
    }

    /**
     * MVC: This public method is called by the VerificationController to update the UI.
     * It receives a list of VerificationRequest objects (the view's inner class).
     * @param requests The list of VerificationRequest objects to display.
     */
    public void updateVerificationRequests(List<VerificationRequest> requests) {
        this.currentDisplayedRequests = requests; // Update the internal data source
        refreshRequestListDisplay(); // Trigger the UI refresh based on the new data
    }
   

    public void showLoading(boolean isLoading) {
        Platform.runLater(() -> {
            if (requestCardsListContainer == null) {
                // Initialize if for some reason it's not yet (shouldn't happen with current flow)
                requestCardsListContainer = new VBox(15);
                // Potentially add it to the content if it's not already
                // Example: content.getChildren().add(requestCardsListContainer);
            }
            requestCardsListContainer.getChildren().clear(); // Clear current content

            if (isLoading) {
                Label loadingLabel = new Label("Loading verification requests...");
                loadingLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
                loadingLabel.setTextFill(Color.web("#777777"));
                requestCardsListContainer.setAlignment(Pos.CENTER); // Center loading text
                requestCardsListContainer.getChildren().add(loadingLabel);
            } else {
                // If not loading, and the list is still empty after update, show 'no requests' message
                if (currentDisplayedRequests.isEmpty()) {
                    Label noRequestsLabel = new Label("No pending verification requests at this time.");
                    noRequestsLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 14));
                    noRequestsLabel.setTextFill(Color.web("#777777"));
                    requestCardsListContainer.setAlignment(Pos.CENTER);
                    requestCardsListContainer.getChildren().add(noRequestsLabel);
                }
                requestCardsListContainer.setAlignment(Pos.TOP_LEFT); // Reset alignment for requests
                refreshRequestListDisplay(); // Re-populate with actual data if available
            }
            // Hide/show "View More" button correctly based on loaded data
            if (viewMoreButton != null) {
                boolean showViewMore = !isLoading && currentDisplayedRequests.size() > INITIAL_DISPLAY_COUNT &&
                                       currentMaxDisplayCount < currentDisplayedRequests.size();
                viewMoreButton.setVisible(showViewMore);
                viewMoreButton.setManaged(showViewMore);
            }
        });
    }
    /**
     * Refreshes the display of verification request cards in the requestCardsListContainer.
     * It clears existing cards and adds new ones based on currentDisplayedRequests and currentMaxDisplayCount.
     */
    private void refreshRequestListDisplay() {
        if (requestCardsListContainer == null) { // Defensive check, initialize if null
            requestCardsListContainer = new VBox(15);
        }
        requestCardsListContainer.getChildren().clear(); // Clear all existing cards

        // Determine how many requests to display (initial count or all)
        int count = Math.min(currentMaxDisplayCount, currentDisplayedRequests.size());
        for (int i = 0; i < count; i++) {
            // Create and add individual card for each request
            requestCardsListContainer.getChildren().add(createRequestCard(currentDisplayedRequests.get(i)));
        }

        // Manage visibility of the "View More" button
        if (viewMoreButton != null) {
            // Show "View More" only if there are more requests than currently displayed
            boolean showViewMore = currentDisplayedRequests.size() > INITIAL_DISPLAY_COUNT &&
                                   currentMaxDisplayCount < currentDisplayedRequests.size();
            viewMoreButton.setVisible(showViewMore);
            viewMoreButton.setManaged(showViewMore); // Ensures it takes/doesn't take up layout space
        }
    }

    /**
     * Creates an individual UI card for a single verification request.
     * @param request The VerificationRequest data object for this card.
     * @return An HBox representing the UI card.
     */
    private HBox createRequestCard(VerificationRequest request) {
        HBox card = new HBox(20); // Main container for a single request card
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Initials/Avatar Circle on the left
        Label initialsLabel = new Label(getInitials(request.name()));
        initialsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setPrefSize(50, 50);
        initialsLabel.setAlignment(Pos.CENTER);
        initialsLabel.setStyle("-fx-background-color: #D32F2F; -fx-background-radius: 25;"); // Admin red circle

        // Request Details (Name, Role/Type, Status)
        VBox detailsBox = new VBox(2);
        Label nameLabel = new Label(request.name());
        nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.web("#333333"));

        Label roleTypeLabel = new Label(request.role() + " \u2022 " + request.requestType());
        roleTypeLabel.setFont(Font.font("Inter", 12));
        roleTypeLabel.setTextFill(Color.web("#777777"));

        Label statusLabel = new Label("Submitted: " + request.submissionDate() + " \u2022 Status: " + request.status());
        statusLabel.setFont(Font.font("Inter", 12));
        statusLabel.setTextFill(Color.web("#555555"));

        detailsBox.getChildren().addAll(nameLabel, roleTypeLabel, statusLabel);

        Region spacer = new Region(); // Spacer to push action buttons to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action Buttons (Review, Approve, Reject)
        Button reviewDocumentsButton = new Button("Review Documents");
        reviewDocumentsButton.setFont(Font.font("Inter", 12));
        reviewDocumentsButton.setTextFill(Color.web("#555555"));
        reviewDocumentsButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;");
        reviewDocumentsButton.setOnMouseEntered(e -> reviewDocumentsButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        reviewDocumentsButton.setOnMouseExited(e -> reviewDocumentsButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        reviewDocumentsButton.setOnAction(e -> openDocumentReviewDialog(request)); // This calls the view's method now

        Button approveButton = new Button("Approve");
        approveButton.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        approveButton.setTextFill(Color.WHITE);
        approveButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8; -fx-padding: 8 12;"); // Green for Approve
        approveButton.setOnMouseEntered(e -> approveButton.setStyle("-fx-background-color: #66BB6A; -fx-background-radius: 8; -fx-padding: 8 12;"));
        approveButton.setOnMouseExited(e -> approveButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8; -fx-padding: 8 12;"));
        approveButton.setOnAction(e -> controller.handleApprove(request)); // This calls controller.handleApprove

        Button rejectButton = new Button("Reject");
        rejectButton.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        rejectButton.setTextFill(Color.WHITE);
        rejectButton.setStyle("-fx-background-color: #F44336; -fx-background-radius: 8; -fx-padding: 8 12;"); // Red for Reject
        rejectButton.setOnMouseEntered(e -> rejectButton.setStyle("-fx-background-color: #EF5350; -fx-background-radius: 8; -fx-padding: 8 12;"));
        rejectButton.setOnMouseExited(e -> rejectButton.setStyle("-fx-background-color: #F44336; -fx-background-radius: 8; -fx-padding: 8 12;"));
        rejectButton.setOnAction(e -> controller.handleReject(request)); // This calls controller.handleReject

        HBox actionButtonsBox = new HBox(10, reviewDocumentsButton, approveButton, rejectButton);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(initialsLabel, detailsBox, spacer, actionButtonsBox);
        return card;
    }

    /**
     * Extracts initials from a full name.
     */
    private String getInitials(String name) {
        StringBuilder initials = new StringBuilder();
        String[] parts = name.split(" ");
        if (parts.length > 0) {
            initials.append(parts[0].charAt(0));
            if (parts.length > 1) {
                initials.append(parts[parts.length - 1].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    /**
     * Opens a dialog to review uploaded documents.
     * IMPORTANT: This method now dynamically creates document links based on request.getDocumentUrls().
     * @param request The verification request data (view's inner class).
     */
    private void openDocumentReviewDialog(VerificationRequest request) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(primaryStage); // Use the stored primaryStage reference
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Review Documents for " + request.name());
        dialog.setHeaderText("Documents for: " + request.name() + " (" + request.role() + ")");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setMinWidth(400);

        Label instructions = new Label("Click 'View' to open each document in your browser.");
        instructions.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        content.getChildren().add(instructions);

        List<String> documentUrls = request.getDocumentUrls(); // Get document URLs from the request object
        if (documentUrls != null && !documentUrls.isEmpty()) {
            for (String docUrl : documentUrls) {
                HBox docRow = new HBox(10);
                docRow.setAlignment(Pos.CENTER_LEFT);
                docRow.setPadding(new Insets(5, 0, 5, 0));
                docRow.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");

                String fileName = extractFileNameFromUrl(docUrl); // Helper method to extract file name
                Label docLabel = new Label(fileName);
                docLabel.setFont(Font.font("Inter", 14));
                docLabel.setTextFill(Color.web("#333333"));
                HBox.setHgrow(docLabel, Priority.ALWAYS);

                Button viewButton = new Button("View");
                viewButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5; -fx-padding: 5 10;");
                viewButton.setOnAction(e -> {
                    System.out.println("Attempting to open URL: " + docUrl);
                    // In a real application, you would use java.awt.Desktop to open the URL
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(docUrl));
                    } catch (Exception ex) {
                        System.err.println("Failed to open document URL: " + docUrl + ". Error: " + ex.getMessage());
                        showMessage("Error Opening Document", "Could not open document: " + fileName + ". " + ex.getMessage(), true);
                    }
                });
                docRow.getChildren().addAll(docLabel, viewButton);
                content.getChildren().add(docRow);
            }
        } else {
            content.getChildren().add(new Label("No documents uploaded for this request."));
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Helper method to extract a plausible file name from a URL.
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "N/A";
        }
        try {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                String fileName = url.substring(lastSlashIndex + 1);
                int queryIndex = fileName.indexOf('?');
                if (queryIndex != -1) {
                    fileName = fileName.substring(0, queryIndex);
                }
                return java.net.URLDecoder.decode(fileName, "UTF-8");
            }
        } catch (Exception e) {
            System.err.println("Error extracting filename from URL: " + url + " - " + e.getMessage());
        }
        return "Document (Cannot parse name)"; // Fallback if parsing fails
    }

    /**
     * Helper to show a generic information/success alert dialog.
     * This method is called by the controller to display messages to the user.
     * @param title The title of the alert.
     * @param message The message content of the alert.
     * @param isError If true, shows an ERROR alert; otherwise, shows an an INFORMATION alert.
     */
    public void showMessage(String title, String message, boolean isError) {
        Platform.runLater(() -> {
            Alert alert = new Alert(isError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(primaryStage);
            alert.showAndWait();
        });
    }

    // Deprecated legacy alert methods, replaced by the unified showMessage for consistency
    @Deprecated
    public void showErrorAlert(String title, String message) {
        showMessage(title, message, true);
    }

    @Deprecated
    public void showAlert(String title, String message) {
        showMessage(title, message, false);
    }


    /**
     * A class to hold verification request data for the UI.
     * This inner class acts as the view's representation of a verification request,
     * containing data ready for display and the necessary IDs for backend operations.
     * IMPORTANT: Added a documentUrls field to match the VerificationRequestModel
     * so it can be passed to the openDocumentReviewDialog.
     */
    public static class VerificationRequest {
        private final String id;
        private final String entityId;
        private final String name;
        private final String role;
        private final String requestType;
        private final String submissionDate;
        private String status;
        private final List<String> documentUrls; // Added field for document URLs

        public VerificationRequest(String id, String entityId, String name, String role, String requestType, String submissionDate, String status, List<String> documentUrls) {
            this.id = id;
            this.entityId = entityId;
            this.name = name;
            this.role = role;
            this.requestType = requestType;
            this.submissionDate = submissionDate;
            this.status = status;
            this.documentUrls = documentUrls;
        }

        // Getters for all fields (Java record-style getters: fieldName())
        public String id() { return id; }
        public String entityId() { return entityId; }
        public String name() { return name; }
        public String role() { return role; }
        public String requestType() { return requestType; }
        public String submissionDate() { return submissionDate; }
        public String status() { return status; }

        // Setter for mutable status (used for immediate UI update after action)
        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getDocumentUrls() { // Getter for document URLs
            return documentUrls;
        }
    }
}