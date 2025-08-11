package com.lifecompass.view.Adminview; // Changed package to match dashboard's package

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Important: for returning UI elements
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane; // Still used internally for layout if needed
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage; // Used for primary stage reference if needed (e.g., for alerts)

import java.net.URL; // For loading images (if any local images were used)

/**
 * This class creates the UI for the Admin Portal's "System Analytics" section.
 * It is now a UI component that returns a Node for integration into a larger dashboard.
 */
public class SystemAnalyticsView { // No longer extends Application

    private Stage primaryStage; // Reference to the main application's primary stage (if needed for alerts etc.)

    /**
     * Constructor for the SystemAnalyticsView component.
     * @param primaryStage The primary stage of the main application (admindashaboardView).
     * This is useful if this component needs to display dialogs or new windows.
     */
    public SystemAnalyticsView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Any initialization specific to this component can go here
    }

    /**
     * Returns the root Node of the System Analytics UI.
     * This method replaces the functionality previously in `start()`.
     * @return A Node representing the complete System Analytics UI.
     */
    public Node getView() {
        ScrollPane scrollPane = createSystemAnalyticsContent();
        scrollPane.setStyle("-fx-background-color: #f9fafb;"); // Ensure background matches dashboard's main content area
        return scrollPane;
    }

    // The top navigation bar from the original SystemAnalyticsView Application
    // is now assumed to be part of the main admindashaboardView.
    // So, this component only provides its core content below that.

    private ScrollPane createSystemAnalyticsContent() {
        VBox content = new VBox(20);
        // Padding controlled by parent mainContentArea in dashboard, so 0 here.
        content.setPadding(new Insets(0, 0, 0, 0));
        content.setStyle("-fx-background-color: #f9fafb;"); // Match dashboard background

        Label headerTitle = new Label("System Analytics");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("Platform usage statistics and performance metrics");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0)); // Internal padding for this header

        content.getChildren().add(headerBox);

        // Top Row of Metric Cards
        HBox metricCardsRow = new HBox(20);
        metricCardsRow.getChildren().addAll(
                createMetricCard("Daily Active Users", "892", "+5.2% from yesterday", "#2196F3"), // Blue
                createMetricCard("Session Duration", "24m", "Average per session", "#4CAF50"), // Green
                createMetricCard("Crisis Resolution", "96%", "Successfully resolved", "#9C27B0") // Purple
        );
        content.getChildren().add(metricCardsRow);

        // Platform Health Overview Section
        content.getChildren().add(createPlatformHealthSection());


        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f9fafb; -fx-border-color: transparent;"); // Match background, remove border
        return scrollPane;
    }

    /**
     * Creates a single metric display card.
     * @param title The title of the metric.
     * @param value The main value of the metric.
     * @param subtitle The descriptive subtitle.
     * @param colorHex The color for the value.
     * @return A VBox representing the metric card.
     */
    private VBox createMetricCard(String title, String value, String subtitle, String colorHex) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 5);");
        HBox.setHgrow(card, Priority.ALWAYS); // Allow cards to grow horizontally

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#777777"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.web(colorHex));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Inter", 12));
        subtitleLabel.setTextFill(Color.web("#555555"));

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    /**
     * Creates the "Platform Health Overview" section.
     */
    private VBox createPlatformHealthSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(30, 0, 0, 0)); // Padding from above sections
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        Label sectionTitle = new Label("Platform Health Overview");
        sectionTitle.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#333333"));
        section.getChildren().add(sectionTitle);

        Label sectionSubtitle = new Label("Key performance indicators and system status");
        sectionSubtitle.setFont(Font.font("Inter", 14));
        sectionSubtitle.setTextFill(Color.web("#777777"));
        section.getChildren().add(sectionSubtitle);

        section.getChildren().add(createHealthIndicator("User Satisfaction", "4.7/5", 0.94)); // 94% filled
        section.getChildren().add(createHealthIndicator("Psychologist Response Time", "85%", 0.85));
        section.getChildren().add(createHealthIndicator("Crisis Response Time", "92%", 0.92));
        section.getChildren().add(createHealthIndicator("System Reliability", "99.8%", 0.998));

        return section;
    }

    /**
     * Creates a single platform health indicator row with a progress-like bar.
     * @param labelText The text label for the indicator.
     * @param valueText The value to display.
     * @param progressValue The progress as a double (0.0 to 1.0).
     * @return An HBox representing the indicator row.
     */
    private HBox createHealthIndicator(String labelText, String valueText, double progressValue) {
        HBox indicatorRow = new HBox(15);
        indicatorRow.setAlignment(Pos.CENTER_LEFT);
        indicatorRow.setPadding(new Insets(10, 0, 10, 0));
        indicatorRow.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;"); // Bottom border

        Label label = new Label(labelText);
        label.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#333333"));
        label.setPrefWidth(200); // Fixed width for labels for alignment

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Progress Bar (simulated with HBox and background colors)
        HBox progressBarContainer = new HBox();
        progressBarContainer.setPrefWidth(250); // Fixed width for the bar
        progressBarContainer.setPrefHeight(8);
        progressBarContainer.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 4;"); // Gray background

        Region filledProgress = new Region();
        filledProgress.setPrefWidth(250 * progressValue); // Calculate filled width
        filledProgress.setPrefHeight(8);
        filledProgress.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 4;"); // Purple filled color

        progressBarContainer.getChildren().add(filledProgress);

        Label valueLabel = new Label(valueText);
        valueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        valueLabel.setTextFill(Color.web("#333333"));
        valueLabel.setPrefWidth(60); // Fixed width for value text
        valueLabel.setAlignment(Pos.CENTER_RIGHT);


        indicatorRow.getChildren().addAll(label, spacer, progressBarContainer, valueLabel);
        return indicatorRow;
    }

    // Helper method for ImageView (copied from admindashaboardView for self-containment of component)
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