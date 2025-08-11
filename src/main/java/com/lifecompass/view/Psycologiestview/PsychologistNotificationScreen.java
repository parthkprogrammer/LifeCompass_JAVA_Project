package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.PsychologistNotification;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.net.URL; // For image loading
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lifecompass.model.psychologist.PsychologistNotification;

public class PsychologistNotificationScreen {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistNotificationScreen.class);

    private Runnable backToDashboardAction;
    
    private VBox notificationsListContainer;
    private Runnable markAllReadHandler;
    private Consumer<PsychologistNotification> dismissHandler;

    public PsychologistNotificationScreen(Runnable backToDashboardAction) {
        this.backToDashboardAction = backToDashboardAction;
        logger.info("PsychologistNotificationScreen initialized.");

        notificationsListContainer = new VBox(10); // Initialize it here
        notificationsListContainer.setPadding(new Insets(10, 0, 10, 0));
        notificationsListContainer.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(notificationsListContainer, Priority.ALWAYS);
        // You might want to add a temporary loading label to it here too if showLoading() is called before getContent() adds content.
        notificationsListContainer.getChildren().add(new Label("Loading notifications..."));
    }

    public Node getContent() {
        VBox contentLayout = new VBox(15);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        contentLayout.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentLayout, Priority.ALWAYS);

        // Header and action buttons
        Label title = new Label("Notifications");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label("Manage your alerts and messages.");
        subtitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        subtitle.setTextFill(Color.GRAY);

        Button markAllReadButton = new Button("Mark All as Read");
        markAllReadButton.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        markAllReadButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        applyHoverEffect(markAllReadButton, "#c5e4ff", "#e3f2fd");
        markAllReadButton.setOnAction(e -> {
            logger.info("Mark All as Read button clicked.");
            if (markAllReadHandler != null) {
                markAllReadHandler.run();
            } else {
                logger.warn("Mark All as Read handler is null.");
            }
        });

        Button backToDashboardButton = new Button("Back to Dashboard");
        backToDashboardButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        backToDashboardButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        applyHoverEffect(backToDashboardButton, "#dcdcdc", "#f0f0f0");
        backToDashboardButton.setOnAction(e -> {
            logger.info("Back to Dashboard button clicked from notifications.");
            if (backToDashboardAction != null) {
                backToDashboardAction.run();
            } else {
                logger.warn("Back to Dashboard action is null.");
            }
        });

        HBox headerActions = new HBox(10, backToDashboardButton, new Region(), markAllReadButton);
        HBox.setHgrow(headerActions.getChildren().get(1), Priority.ALWAYS);
        headerActions.setAlignment(Pos.CENTER_LEFT);

        contentLayout.getChildren().addAll(headerActions, title, subtitle);

        notificationsListContainer = new VBox(10);
        notificationsListContainer.setPadding(new Insets(10, 0, 10, 0));
        notificationsListContainer.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(notificationsListContainer, Priority.ALWAYS);

        contentLayout.getChildren().add(notificationsListContainer);
        logger.debug("Notifications screen content built.");
        return contentLayout;
    }

    private HBox createNotificationItem(PsychologistNotification notification) {
        VBox textContent = new VBox(2);
        Label titleLbl = new Label(notification.getTitle());
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        titleLbl.setTextFill(Color.web("#333333"));

        Label msgLbl = new Label(notification.getMessage());
        msgLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        msgLbl.setTextFill(Color.web("#555555"));
        msgLbl.setWrapText(true);

        Label timeLbl = new Label(notification.getTime());
        timeLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        timeLbl.setTextFill(Color.GRAY);

        textContent.getChildren().addAll(titleLbl, msgLbl, timeLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dismissButton = new Button("Dismiss");
        dismissButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-font-weight: normal; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 3 8;");
        dismissButton.setFont(Font.font("System", FontWeight.NORMAL, 11));
        applyHoverEffect(dismissButton, "#f0f0f0", "transparent");
        dismissButton.setOnAction(e -> {
            logger.info("Dismiss button clicked for notification ID: {}", notification.getId());
            if (dismissHandler != null) {
                dismissHandler.accept(notification);
            } else {
                logger.warn("Dismiss handler is null for notification ID: {}", notification.getId());
            }
        });

        HBox itemLayout = new HBox(10, textContent, spacer, dismissButton);
        itemLayout.setAlignment(Pos.CENTER_LEFT);
        itemLayout.setPadding(new Insets(10));
        
        if (notification.isRead()) {
             itemLayout.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8; -fx-border-color: #eeeeee; -fx-border-width: 1; -fx-border-radius: 8;");
        } else {
             itemLayout.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 8; -fx-border-color: #bbdefb; -fx-border-width: 1; -fx-border-radius: 8;");
        }
        applyHoverEffect(itemLayout, "#f0f0f0", notification.isRead() ? "#f9f9f9" : "#e3f2fd");

        ImageView icon = null;
        try {
            switch (NotificationType.valueOf(notification.getType())) {
                case APPOINTMENT: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/psyappointment.png"))); break;
                case CRISIS: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/psyalert.png"))); break;
                case INFO: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/info_icon.png"))); break;
                default: icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/placeholder.png"))); break;
            }
            if (icon != null && icon.getImage() != null) {
                icon.setFitWidth(20);
                icon.setFitHeight(20);
                icon.setPreserveRatio(true);
                itemLayout.getChildren().add(0, icon);
                HBox.setMargin(textContent, new Insets(0,0,0,5));
            } else {
                logger.warn("Icon not found or failed to load for notification type {}. Using text fallback.", notification.getType());
                Label fallbackIcon = new Label("?");
                fallbackIcon.setFont(Font.font("System", FontWeight.BOLD, 14));
                fallbackIcon.setTextFill(Color.GRAY);
                itemLayout.getChildren().add(0, fallbackIcon);
                HBox.setMargin(textContent, new Insets(0,0,0,5));
            }
        } catch (Exception e) {
            logger.error("Error loading notification icon for type {}: {}. Using text fallback.", notification.getType(), e.getMessage(), e);
            Label fallbackIcon = new Label("!");
            fallbackIcon.setFont(Font.font("System", FontWeight.BOLD, 14));
            fallbackIcon.setTextFill(Color.RED);
            itemLayout.getChildren().add(0, fallbackIcon);
            HBox.setMargin(textContent, new Insets(0,0,0,5));
        }

        return itemLayout;
    }

    private void applyHoverEffect(Region node, String hoverColor, String originalColor) {
        String initialStyle = node.getStyle(); 
        node.setOnMouseEntered(e -> {
            if (!node.getStyle().contains(hoverColor)) {
                String newStyle = initialStyle.replace(originalColor, hoverColor);
                if (!newStyle.contains("background-color") && hoverColor.contains("background-color")) {
                    newStyle += "; -fx-background-color: " + hoverColor.substring(hoverColor.indexOf("#"));
                }
                node.setStyle(newStyle);
            }
        });
        node.setOnMouseExited(e -> {
            if (node.getStyle().contains(hoverColor)) {
                node.setStyle(initialStyle);
            }
        });
    }

    private enum NotificationType {
        APPOINTMENT, CRISIS, INFO
    }

    // --- METHODS FOR THE CONTROLLER ---
    public void populateNotifications(List<PsychologistNotification> notifications) {
        logger.info("Populating notifications in view with {} items.", notifications.size());
        notificationsListContainer.getChildren().clear();
        if (notifications == null || notifications.isEmpty()) {
            notificationsListContainer.getChildren().add(new Label("No new notifications."));
            logger.info("No notifications to display. Showing 'No notifications' message.");
        } else {
            notifications.forEach(n -> notificationsListContainer.getChildren().add(createNotificationItem(n)));
            logger.debug("Notifications population complete. Displayed {} items.", notifications.size());
        }
    }

    public void setMarkAllReadHandler(Runnable handler) {
        this.markAllReadHandler = handler;
        logger.debug("Mark All Read handler set for notifications view.");
    }

    public void setDismissHandler(Consumer<PsychologistNotification> handler) {
        this.dismissHandler = handler;
        logger.debug("Dismiss notification handler set for notifications view.");
    }

    // public void showLoading(boolean isLoading) {
    //     logger.debug("Setting loading state for notifications view to: {}", isLoading);
    //     notificationsListContainer.getChildren().clear();
    //     if(isLoading) {
    //         notificationsListContainer.getChildren().add(new Label("Loading notifications..."));
    //     } else {
    //         if (notificationsListContainer.getChildren().isEmpty()) { 
    //             notificationsListContainer.getChildren().add(new Label("No new notifications."));
    //             logger.info("Loading finished. No new notifications found.");
    //         }
    //     }
    // }

     public void showLoading(boolean isLoading) {
        logger.debug("Setting loading state for notifications view to: {}", isLoading);
        if (notificationsListContainer == null) {
             logger.warn("notificationsListContainer is null. Cannot show loading indicator effectively.");
             return;
        }

        if (isLoading) {
            notificationsListContainer.getChildren().clear();
            notificationsListContainer.getChildren().add(new Label("Loading notifications..."));
        } else {
            // This part should be handled by populateNotifications() after data is fetched.
            // If populateNotifications() is not called or returns empty, the "Loading notifications..." will remain.
            // Ensure populateNotifications is always called when loading finishes.
            if (notificationsListContainer.getChildren().size() == 1 && ((Label)notificationsListContainer.getChildren().get(0)).getText().equals("Loading notifications...")) {
                 notificationsListContainer.getChildren().clear(); // Clear loading message if no notifications
            }
            if (notificationsListContainer.getChildren().isEmpty()) { 
                notificationsListContainer.getChildren().add(new Label("No new notifications."));
                logger.info("Loading finished. No new notifications found.");
            }
        }
    }

    public void showError(String message) {
        logger.error("Displaying error in PsychologistNotificationScreen: {}", message);
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    public void showSuccess(String message) {
        logger.info("Displaying success in PsychologistNotificationScreen: {}", message);
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}