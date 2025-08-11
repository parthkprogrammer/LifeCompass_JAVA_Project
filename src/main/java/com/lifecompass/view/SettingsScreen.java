package com.lifecompass.view; // Corrected package name

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
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

import java.net.URL; // For createImageView helper

import com.lifecompass.controller.AuthController;
import com.lifecompass.view.AboutUsScreen; // Corrected import for UserDashboardScreen
// Refactored: No longer extends Application
public class SettingsScreen {

    private Stage primaryStage; // Needed for resource loading context (dashboard's stage)
    private UserDashboardScreen dashboardInstance; // Corrected dashboard reference type
    private Runnable onCloseCallback; // Callback to hide the panel in dashboard

    public SettingsScreen(Stage primaryStage, UserDashboardScreen dashboardInstance, Runnable onCloseCallback) { // Corrected type
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;
        this.onCloseCallback = onCloseCallback;
    }

    /**
     * Creates the UI for the settings panel. This method returns a VBox
     * that can be added to another layout (like a StackPane in dashboard).
     * @return The VBox representing the settings panel UI.
     */
    public VBox createSettingsPanel() {
        System.out.println("Creating Settings Panel UI...");

        // --- Header for the Sliding Panel ---
        Button backBtn = createBackButton();
        Label screenTitle = new Label("Settings");
        screenTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        screenTitle.setTextFill(Color.web("#333333"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15, backBtn, spacer, screenTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #e0f2f7; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);"); 

        // --- Main Content for the Settings Panel ---
        VBox settingsOptions = new VBox(5);
        settingsOptions.setAlignment(Pos.TOP_LEFT);
        settingsOptions.setPadding(new Insets(10));
        settingsOptions.setStyle("-fx-background-color: white;");

        // --- Add New Setting Menu Items ---
        settingsOptions.getChildren().addAll(
            createSettingMenuItem("Language", () -> System.out.println("Language settings clicked!")),
            createSettingMenuItem("Theme & Personalization", () -> System.out.println("Theme settings clicked!")),
            createSettingMenuItem("About Us", () -> {
                System.out.println("About Us clicked!");
                // Create and show the AboutUsScreen
                AboutUsScreen aboutUsScreen = new AboutUsScreen(primaryStage); // Pass the primary stage
                aboutUsScreen.show(); // Show the modal About Us screen
            }),
            createSettingMenuItem("Terms & Privacy", () -> {System.out.println("Terms & Privacy clicked!");
            TermsAndPolicyScreen termsAndPolicyScreen = new TermsAndPolicyScreen(primaryStage); // Pass the primary stage
                termsAndPolicyScreen.show(); // Show the modal About Us screen
                }),
            createSettingMenuItem("Feedback & Support", () -> {System.out.println("Feedback & Support clicked!");
    
            FeedbackScreen feedbackScreen = new FeedbackScreen(primaryStage); // Pass the primary stage
                feedbackScreen.show(); // Show the modal Feedback screen
            }),
            createSettingMenuItem("Logout", () -> {
        // This is the code that runs when "Logout" is clicked
                              System.out.println("Logout clicked!");
        // Call the logout method from your authentication controller
                            AuthController.logout();
            }, true) // true indicates this is a destructive action
        );

        ScrollPane settingsScrollPane = new ScrollPane(settingsOptions);
        settingsScrollPane.setFitToWidth(true);
        settingsScrollPane.setFitToHeight(true);
        settingsScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-insets: 0;"); 

        VBox fullPanel = new VBox(header, settingsScrollPane);
        VBox.setVgrow(settingsScrollPane, Priority.ALWAYS);
        fullPanel.setStyle(
            "-fx-background-color: #f5fafd; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 0 0 0 1px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, -2, 0);"
        );
        fullPanel.setPrefHeight(VBox.USE_COMPUTED_SIZE);

        return fullPanel;
    }

    /**
     * Helper method to create a back button for this panel.
     */
    private Button createBackButton() {
        ImageView backArrow = createImageView("/assets/images/back_arrow.png", 20, 20);

        Button backButton = new Button();
        backButton.setGraphic(backArrow);
        backButton.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 5;");
        Tooltip.install(backButton, new Tooltip("Close Settings"));

        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 8; -fx-background-radius: 5;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 5;"));

        backButton.setOnAction(e -> {
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        });
        return backButton;
    }

    /**
     * Helper method to create a single clickable menu item for settings.
     */
    private Button createSettingMenuItem(String title, Runnable action) {
        return createSettingMenuItem(title, action, false);
    }

    /**
     * Helper method to create a single clickable menu item for settings.
     * Overloaded to allow for destructive actions.
     */
    private Button createSettingMenuItem(String title, Runnable action, boolean isDestructive) {
        Button menuItem = new Button(title);
        menuItem.setPrefWidth(Double.MAX_VALUE);
        menuItem.setAlignment(Pos.CENTER_LEFT);
        menuItem.setPadding(new Insets(12, 15, 12, 15));
        menuItem.setFont(Font.font("System", FontWeight.NORMAL, 15));

        String normalTextColor = isDestructive ? "#dc3545" : "#333333";
        String hoverBgColor = isDestructive ? "#ffe6e6" : "#e6f7ff";
        String hoverTextColor = isDestructive ? "#800000" : "#007bff";

        menuItem.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + normalTextColor + "; " +
            "-fx-border-color: #f0f0f0; " +
            "-fx-border-width: 0 0 1 0;"
        );

        menuItem.setOnMouseEntered(e -> menuItem.setStyle(
            "-fx-background-color: " + hoverBgColor + "; " +
            "-fx-text-fill: " + hoverTextColor + "; " +
            "-fx-border-color: #bbdffd; " +
            "-fx-border-width: 0 0 1 0;"
        ));
        menuItem.setOnMouseExited(e -> menuItem.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + normalTextColor + "; " +
            "-fx-border-color: #f0f0f0; " +
            "-fx-border-width: 0 0 1 0;"
        ));

        menuItem.setOnAction(e -> {
            action.run();
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        });
        
        return menuItem;
    }

    // Helper method to create an ImageView with error handling for missing image resources.
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

    // Helper method to apply generic hover styles to any Region.
    private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
        region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
        region.setOnMouseExited(e -> region.setStyle(normalStyle));
    }
}