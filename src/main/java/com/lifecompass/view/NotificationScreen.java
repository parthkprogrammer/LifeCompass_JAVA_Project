package com.lifecompass.view; // Corrected package name

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL; // Added for createImageView helper

// Refactored: No longer extends Application
public class NotificationScreen {

    private Stage primaryStage; // This is the parent stage (dashboard's stage)
    private UserDashboardScreen dashboardInstance; // Reference to the main dashboard to go back to
    private Stage notificationStage; // The new stage for the notification window

    public NotificationScreen(Stage primaryStage, UserDashboardScreen dashboardInstance) { // Corrected type
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;
        this.notificationStage = new Stage(); // Initialize the new stage

        this.notificationStage.initOwner(primaryStage);
        this.notificationStage.initModality(Modality.NONE);
        this.notificationStage.setTitle("Notifications");
    }

    public void show() {
        System.out.println("Opening Notification Window...");

        // If the window is already open, bring it to the front instead of opening a new one
        if (notificationStage.isShowing()) {
            notificationStage.toFront();
            return;
        }

        notificationStage.centerOnScreen();

        // --- Header Section for the small window ---
        Label screenTitle = new Label("Notifications");
        screenTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        screenTitle.setTextFill(Color.web("#333333"));

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        closeBtn.setOnAction(e -> notificationStage.close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));


        HBox header = new HBox(10, screenTitle, closeBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(screenTitle, Priority.ALWAYS);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setStyle("-fx-background-color: #e0f2f7; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        // --- Main Content for the notification screen ---
        VBox notificationsList = new VBox(8);
        notificationsList.setAlignment(Pos.TOP_LEFT);
        notificationsList.setPadding(new Insets(10));
        notificationsList.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 5, 0, 0, 2);"
        );

        notificationsList.getChildren().addAll(
                createNotificationItem("Today", "Your request to connect with Dr. Sharma has been accepted!", "#e6ffe6", "/assets/images/check.png"),
                createNotificationItem("Today", "Your request for Dr. Patel was rejected due to unavailability.", "#ffe6e6", "/assets/images/cross.png"),
                createNotificationItem("Yesterday", "Time to log your daily mood! How are you feeling?", "#e6f2ff", "/assets/images/bell.png"),
                createNotificationItem("Yesterday", "Suggestion: Try 10 minutes of deep breathing today.", "#f0e6ff", "/assets/images/idea.png"),
                createNotificationItem("July 15", "Congratulations! You've achieved a 14-day journaling streak!", "#fff0e6", "/assets/images/trophy.png"),
                createNotificationItem("July 14", "New message from Dr. Miller: 'Let's schedule next session.'", "#ffffe6", "/assets/images/chat_bubble.png"),
                createNotificationItem("Urgent", "Emergency Alert: Crisis support resources available immediately.", "#ffcdd2", "/assets/images/warning.png")
        );

        Label noNotificationsLabel = new Label("You have no new notifications.");
        noNotificationsLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        noNotificationsLabel.setTextFill(Color.GRAY);
        if (notificationsList.getChildren().isEmpty()) {
            notificationsList.getChildren().add(noNotificationsLabel);
        }

        ScrollPane scrollPane = new ScrollPane(notificationsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f9fafb; -fx-border-insets: 0;");
        scrollPane.setPadding(new Insets(10));

        BorderPane notificationLayout = new BorderPane();
        notificationLayout.setTop(header);
        notificationLayout.setCenter(scrollPane);
        notificationLayout.setStyle("-fx-background-color: #f9fafb;");

        Scene notificationScene = new Scene(notificationLayout, 400, 500);
        notificationStage.setScene(notificationScene);
        notificationStage.show();
    }

    /**
     * Helper method to create a single notification item for the list with an optional icon.
     * @param date The date/time of the notification.
     * @param message The notification message.
     * @param bgColor Background color for this specific item.
     * @param iconPath Path to an optional icon for the notification.
     * @return An HBox representing a notification item.
     */
    private HBox createNotificationItem(String date, String message, String bgColor, String iconPath) {
        ImageView notificationIcon = createImageView(iconPath, 18, 18);
        
        Label dateLabel = new Label(date);
        dateLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        dateLabel.setTextFill(Color.web("#888888"));

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#333333"));

        VBox textContent = new VBox(2, dateLabel, messageLabel);
        VBox.setVgrow(textContent, Priority.ALWAYS);

        HBox item = new HBox(10);
        item.getChildren().addAll(notificationIcon, textContent);
        
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setStyle(
                "-fx-background-color: " + bgColor + "; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-color: #dddddd; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 6;"
        );
        item.setPrefWidth(Double.MAX_VALUE);

        // Add hover effect
        String originalStyle = item.getStyle();
        item.setOnMouseEntered(e -> item.setStyle(originalStyle.replace(bgColor, Color.web("#e0f0f0").toString().replace("0x", "#")) + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 5, 0, 0, 1);"));
        item.setOnMouseExited(e -> item.setStyle(originalStyle));

        return item;
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods (Copied from UserDashboardScreen.java for this component)
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Helper to create an ImageView with error handling for missing image resources.
     */
    private ImageView createImageView(String imagePath, int fitWidth, int fitHeight) {
        ImageView iconView = new ImageView();
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("Failed to load image: " + imagePath);
                URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
                if (fallbackUrl != null) {
                    iconView.setImage(new Image(fallbackUrl.toExternalForm()));
                } else {
                    System.err.println("Also failed to load placeholder.png: Input stream must not be null for placeholder!");
                }
            } else {
                iconView.setImage(new Image(imageUrl.toExternalForm()));
            }
        }
        catch (Exception e) {
            System.err.println("Exception while loading image: " + imagePath + " - " + e.getMessage());
            try {
                URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
                if (fallbackUrl != null) {
                    iconView.setImage(new Image(fallbackUrl.toExternalForm()));
                }
            } catch (Exception pe) {
                System.err.println("Also failed to load placeholder.png: Input stream must not be null for placeholder!");
            }
        }
        iconView.setFitWidth(fitWidth);
        iconView.setFitHeight(fitHeight);
        iconView.setPreserveRatio(true);
        return iconView;
    }

    /**
     * Helper method to apply generic hover styles to any Region.
     */
    private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
        region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
        region.setOnMouseExited(e -> region.setStyle(normalStyle));
    }
}