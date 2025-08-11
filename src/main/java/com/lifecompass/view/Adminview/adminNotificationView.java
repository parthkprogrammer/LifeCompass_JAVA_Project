package com.lifecompass.view.Adminview; // Changed package to match dashboard's package

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Important: for returning UI elements
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image; // Added for ImageView
import javafx.scene.image.ImageView; // Added for ImageView
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
// No longer needs Scene, Application, Stage from javafx.stage here
// as it's a component, not the main app.

import java.net.URL; // For loading images from resources

public class adminNotificationView { // No longer extends Application

    private AdminDashboardView parentDashboard; // Reference to the main dashboard

    public adminNotificationView(AdminDashboardView parentDashboard) {
        this.parentDashboard = parentDashboard;
    }

    /**
     * Returns the root Node of the Admin Notifications UI.
     * @return A Node representing the complete Admin Notifications UI.
     */
    public Node getContent() {
        VBox contentLayout = new VBox(15);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        contentLayout.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentLayout, Priority.ALWAYS);

        // Header
        Label title = new Label("Admin Notifications");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label("System-wide alerts and verification requests.");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.GRAY);

        Button markAllReadButton = new Button("Mark All as Read");
        markAllReadButton.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        markAllReadButton.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(markAllReadButton, "#c5e4ff", "#e3f2fd");
        markAllReadButton.setOnAction(e -> System.out.println("Marking all admin notifications as read."));

        Button backToDashboardButton = new Button("Back to Dashboard");
        backToDashboardButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        backToDashboardButton.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(backToDashboardButton, "#dcdcdc", "#f0f0f0");
        backToDashboardButton.setOnAction(e -> {
            System.out.println("Navigating back to Admin Overview dashboard.");
            if (parentDashboard != null) {
                parentDashboard.switchContent("Overview");
            }
        });

        HBox headerActions = new HBox(10, backToDashboardButton, new Region(), markAllReadButton);
        HBox.setHgrow(headerActions.getChildren().get(1), Priority.ALWAYS);
        headerActions.setAlignment(Pos.CENTER_LEFT);

        contentLayout.getChildren().addAll(headerActions, title, subtitle);

        VBox notificationsList = new VBox(10);
        notificationsList.setPadding(new Insets(10, 0, 10, 0));
        notificationsList.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(notificationsList, Priority.ALWAYS);

        // Sample Admin Notifications (Image paths updated)
        notificationsList.getChildren().add(createNotificationItem("New Therapist Application", "Dr. Jane Doe submitted credentials for verification.", "10 min ago", AdminNotificationType.VERIFICATION));
        notificationsList.getChildren().add(createNotificationItem("Content Flagged", "User_5678 reported offensive content in public chat.", "30 min ago", AdminNotificationType.FLAGGED_CONTENT));
        notificationsList.getChildren().add(createNotificationItem("Critical Mood Alert", "User_7834 requires immediate attention (suicide ideation).", "1 hour ago", AdminNotificationType.CRISIS));
        notificationsList.getChildren().add(createNotificationItem("System Error Log", "Database connection issue detected. High severity.", "2 hours ago", AdminNotificationType.SYSTEM_ALERT));
        notificationsList.getChildren().add(createNotificationItem("User Account Suspended", "User_9012 suspended due to repeated policy violations.", "1 day ago", AdminNotificationType.USER_ACTION));

        contentLayout.getChildren().add(notificationsList);

        return contentLayout;
    }

    private HBox createNotificationItem(String title, String message, String time, AdminNotificationType type) {
        VBox textContent = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        titleLbl.setTextFill(Color.web("#333333"));

        Label msgLbl = new Label(message);
        msgLbl.setFont(Font.font("System", FontWeight.NORMAL, 12));
        msgLbl.setTextFill(Color.web("#555555"));
        msgLbl.setWrapText(true);

        Label timeLbl = new Label(time);
        timeLbl.setFont(Font.font("System", FontWeight.NORMAL, 10));
        timeLbl.setTextFill(Color.GRAY);

        textContent.getChildren().addAll(titleLbl, msgLbl, timeLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dismissButton = new Button("Dismiss");
        dismissButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-font-weight: normal; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 3 8;");
        dismissButton.setFont(Font.font("System", FontWeight.NORMAL, 11));
        applyHoverEffect(dismissButton, "#f0f0f0", "transparent");
        dismissButton.setOnAction(e -> {
            System.out.println("Dismissing: " + title);
            ((HBox) dismissButton.getParent()).setVisible(false);
            ((HBox) dismissButton.getParent()).setManaged(false);
        });

        HBox itemLayout = new HBox(10, textContent, spacer, dismissButton);
        itemLayout.setAlignment(Pos.CENTER_LEFT);
        itemLayout.setPadding(new Insets(10));
        itemLayout.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8; -fx-border-color: #eeeeee; -fx-border-width: 1; -fx-border-radius: 8;");
        applyHoverEffect(itemLayout, "#f0f0f0", "#f9f9f9");

        ImageView icon = null;
        try {
            // ALL IMAGE PATHS CHANGED TO /assets/admin_images/
            switch (type) {
                case VERIFICATION: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/verification.png"))); break;
                case FLAGGED_CONTENT: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/flag.png"))); break; // Need flag.png
                case CRISIS: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/psyalert.png"))); break;
                case SYSTEM_ALERT: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/system_health.png"))); break;
                case USER_ACTION: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/total_user.png"))); break; // Using total_user.png
            }
            if (icon != null) {
                icon.setFitWidth(20);
                icon.setFitHeight(20);
                icon.setPreserveRatio(true);
                itemLayout.getChildren().add(0, icon);
                HBox.setMargin(textContent, new Insets(0,0,0,5));
            }
        } catch (Exception e) {
            System.err.println("Error loading notification icon for type " + type + ": " + e.getMessage());
            // Fallback to placeholder if icon fails to load
            ImageView fallbackIcon = createImageView("/assets/admin_images/placeholder.png", 20, 20);
            if (fallbackIcon != null) {
                itemLayout.getChildren().add(0, fallbackIcon);
                HBox.setMargin(textContent, new Insets(0,0,0,5));
            }
        }

        return itemLayout;
    }

    private void applyHoverEffect(Region node, String hoverColor, String originalColor) {
        // Ensure that originalColor is actually part of the node's initial style,
        // otherwise this will not work as expected.
        // For buttons/regions created with specific styles, it's better to store original style string.
        final String initialStyle = node.getStyle(); // Capture initial style at creation

        node.setOnMouseEntered(e -> {
            // Apply hover style
            if (!initialStyle.contains(hoverColor)) { // Prevent re-applying if already in hover state
                node.setStyle(initialStyle.replace(originalColor, hoverColor));
            }
        });
        node.setOnMouseExited(e -> {
            // Revert to original style
            node.setStyle(initialStyle); // Always revert to the stored initial style
        });
    }

    private enum AdminNotificationType {
        VERIFICATION, FLAGGED_CONTENT, CRISIS, SYSTEM_ALERT, USER_ACTION
    }

    // Helper method for ImageView (copied for self-containment of component)
    private ImageView createImageView(String imagePath, int fitWidth, int fitHeight) {
        ImageView iconView = new ImageView();
        try {
            // Using getClass().getResource for component-level resource loading
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("Failed to load image: " + imagePath);
                // Fallback to a placeholder if primary fails
                URL fallbackUrl = getClass().getResource("/assets/admin_images/placeholder.png"); // Assuming placeholder is here
                if (fallbackUrl != null) {
                    iconView.setImage(new Image(fallbackUrl.toExternalForm()));
                } else {
                    System.err.println("Also failed to load placeholder.png!");
                }
            } else {
                iconView.setImage(new Image(imageUrl.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Exception while loading image: " + imagePath + " - " + e.getMessage());
            try {
                URL fallbackUrl = getClass().getResource("/assets/admin_images/placeholder.png");
                if (fallbackUrl != null) {
                    iconView.setImage(new Image(fallbackUrl.toExternalForm()));
                }
            } catch (Exception pe) {
                System.err.println("Also failed to load placeholder.png during exception: " + pe.getMessage());
            }
        }
        iconView.setFitWidth(fitWidth);
        iconView.setFitHeight(fitHeight);
        iconView.setPreserveRatio(true);
        return iconView;
    }
}