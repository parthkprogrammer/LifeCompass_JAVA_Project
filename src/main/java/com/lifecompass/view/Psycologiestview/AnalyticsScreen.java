package com.lifecompass.view.Psycologiestview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle; // For progress bars
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL; // For loading images
import java.util.logging.Logger; // Retained for image loading error logging

/**
 * This class builds the UI content for the Patient Analytics Dashboard.
 * It is designed to be a reusable component that returns a Node.
 * All data displayed in this version is static (hardcoded).
 */
public class AnalyticsScreen {

    private final Logger logger = Logger.getLogger(AnalyticsScreen.class.getName());

    // No instance variables for dynamic UI elements, as all content is static.

    public AnalyticsScreen() {
        // No dependencies needed for simple UI construction
        logger.info("AnalyticsScreen initialized (static version).");
    }

    /**
     * Builds and returns the complete Patient Analytics Dashboard UI.
     * All data is hardcoded within this method.
     * @return A VBox representing the entire Analytics dashboard UI.
     */
    public VBox getView() {
        VBox analyticsContentLayout = new VBox(20); // space-y-6 implies ~24px, using 20 for visual fit
        // No padding here, as the parent mainContentArea will provide it.
        analyticsContentLayout.setStyle("-fx-background-color: #f9fafb;"); // Matches main background
        VBox.setVgrow(analyticsContentLayout, Priority.ALWAYS); // Allows this content to fill vertical space

        // Patient Analytics Dashboard Header Section (Title and Subtitle)
        HBox headerTitleSection = new HBox(10); // space-x-2 in JSX is usually 8px, using 10 for visual fit
        headerTitleSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerTitleSection, Priority.ALWAYS); // Allow header to grow horizontally

        // Icon for "Patient Analytics Dashboard" (BarChart3 in JSX)
        // You need 'analytics_icon_dark.png' in your assets/images folder.
        ImageView barChartIcon = createImageView("/assets/images/analytics.png", 20, 20, "Bar Chart Icon"); // w-5 h-5 in Tailwind is 20px
        if (barChartIcon == null) {
            Label fallbackIcon = new Label("📊"); // Fallback Unicode if image not found
            fallbackIcon.setFont(Font.font("System", FontWeight.NORMAL, 20));
            fallbackIcon.setTextFill(Color.web("#3b82f6")); // text-blue-600 from JSX
            headerTitleSection.getChildren().add(fallbackIcon);
        } else {
            headerTitleSection.getChildren().add(barChartIcon);
        }

        Label dashboardTitle = new Label("Patient Analytics Dashboard");
        dashboardTitle.setFont(Font.font("System", FontWeight.BOLD, 20)); // Matches screenshot and implies font-semibold
        dashboardTitle.setTextFill(Color.BLACK); // Default black text

        Label dashboardSubtitle = new Label("Track patient progress and treatment effectiveness");
        dashboardSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 13)); // Matches screenshot and text-xs, muted-foreground
        dashboardSubtitle.setTextFill(Color.GRAY); // text-muted-foreground

        VBox titleAndSubtitleBox = new VBox(2, dashboardTitle, dashboardSubtitle); // Small spacing
        headerTitleSection.getChildren().add(titleAndSubtitleBox);


        // Top Metrics Row: "Average Improvement", "Session Completion", "Patient Satisfaction"
        HBox metricsRow = new HBox(24); // grid-cols-3 gap-6 means 24px gap between columns
        metricsRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(metricsRow, Priority.ALWAYS); // CRITICAL: This allows the HBox itself to expand to fill available space

        // Create and add individual metric cards with HARDCODED values
        metricsRow.getChildren().addAll(
                createAnalyticsMetricCard("Average Improvement", "+23%", "Mood scores this month", Color.web("#28a745")), // text-green-600
                createAnalyticsMetricCard("Session Completion", "94%", "Attendance rate", Color.web("#3b82f6")), // text-blue-600
                createAnalyticsMetricCard("Patient Satisfaction", "4.8/5", "Average rating", Color.web("#8b5cf6")) // text-purple-600
        );

        // Treatment Effectiveness Section
        VBox treatmentEffectivenessSection = new VBox(15); // space-y-4 is typically 16px. Using 15 for visual fit
        treatmentEffectivenessSection.setPadding(new Insets(20)); // Padding of the card
        treatmentEffectivenessSection.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: #e0e0e0; " + // Light gray border matching implied Card border
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);" // Subtle shadow
        );
        VBox.setVgrow(treatmentEffectivenessSection, Priority.ALWAYS); // Allow section to grow

        Label treatmentTitle = new Label("Treatment Effectiveness");
        treatmentTitle.setFont(Font.font("System", FontWeight.BOLD, 18)); // Matches screenshot
        treatmentTitle.setTextFill(Color.BLACK); // Default black text

        Label treatmentSubtitle = new Label("Overview of patient progress across different treatment approaches");
        treatmentSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 14)); // Matches screenshot
        treatmentSubtitle.setTextFill(Color.GRAY); // Default gray text

        treatmentEffectivenessSection.getChildren().addAll(treatmentTitle, treatmentSubtitle);

        // Container for individual therapy items (space-y-4)
        VBox therapyItemsContainer = new VBox(16); // space-y-4 in JSX translates to 16px vertical spacing
        // Add HARDCODED treatment effectiveness items
        therapyItemsContainer.getChildren().addAll(
                createTreatmentEffectivenessItem("Cognitive Behavioral Therapy", 15, 78),
                createTreatmentEffectivenessItem("Mindfulness-Based Therapy", 8, 65),
                createTreatmentEffectivenessItem("Dialectical Behavior Therapy", 6, 82)
        );
        treatmentEffectivenessSection.getChildren().add(therapyItemsContainer);

        // No loading overlays or error labels are part of a static view

        analyticsContentLayout.getChildren().addAll(headerTitleSection, metricsRow, treatmentEffectivenessSection);
        return analyticsContentLayout;
    }

    /**
     * Helper method to create a single metric card for the Analytics Dashboard's top row.
     * Replicates the <Card> structure within the grid.
     * @param title The main title of the metric (e.g., "Average Improvement").
     * @param value The key value of the metric (e.g., "+23%").
     * @param description A descriptive subtitle (e.g., "Mood scores this month").
     * @param valueColor The JavaFX Color object for the value label.
     * @return A VBox representing a single metric card.
     */
    private VBox createAnalyticsMetricCard(String title, String value, String description, Color valueColor) {
        VBox card = new VBox(5); // Internal spacing, "pb-2" usually smaller vertical spacing
        card.setPadding(new Insets(15)); // Padding inside the card, matches screenshot's perceived padding
        card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: #e0e0e0; " + // Light gray border
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);" // Subtle shadow
        );
        HBox.setHgrow(card, Priority.ALWAYS); // CRITICAL: Allows this card to grow horizontally to distribute space
        card.setMaxWidth(Double.MAX_VALUE); // Ensures card expands within its HBox container

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14)); // text-sm font-medium
        titleLabel.setTextFill(Color.web("#555555")); // text-muted-foreground implied

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24)); // text-2xl font-bold
        valueLabel.setTextFill(valueColor); // Dynamic color (green, blue, purple)

        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("System", FontWeight.NORMAL, 10)); // text-xs
        descriptionLabel.setTextFill(Color.GRAY); // text-muted-foreground

        card.getChildren().addAll(titleLabel, valueLabel, descriptionLabel);
        return card;
    }

    /**
     * Helper method to create a single row item for the "Treatment Effectiveness" section.
     * Replicates the div structure for each item.
     * @param therapyName The name of the therapy.
     * @param patientCount The number of patients.
     * @param improvementPercentage The percentage of improvement.
     * @return An HBox representing a single treatment effectiveness entry.
     */
    private HBox createTreatmentEffectivenessItem(String therapyName, int patientCount, int improvementPercentage) {
        HBox item = new HBox(15); // space-x-4
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 15, 10, 15)); // p-4 in JSX, used for the inner card background

        // Mimics the div for left content (treatment name, patients count)
        VBox leftContent = new VBox(2); // Spacing for h4 and p
        Label nameLabel = new Label(therapyName);
        nameLabel.setFont(Font.font("System", FontWeight.MEDIUM, 15)); // font-medium
        nameLabel.setTextFill(Color.BLACK); // h4 color

        Label countLabel = new Label(patientCount + " patients");
        countLabel.setFont(Font.font("System", FontWeight.NORMAL, 13)); // text-sm
        countLabel.setTextFill(Color.web("#666666")); // text-gray-600
        leftContent.getChildren().addAll(nameLabel, countLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Pushes subsequent elements to the right

        // Mimics the div for right content (improvement percentage, progress bar, icon)
        HBox rightContentArea = new HBox(16); // space-x-4 between text-right div and icon
        rightContentArea.setAlignment(Pos.CENTER_RIGHT); // Align right part to center-right

        VBox percentageAndProgressBar = new VBox(4); // space-y-4 for vertical elements
        percentageAndProgressBar.setAlignment(Pos.CENTER_RIGHT); // Align text right for "improvement"

        Label percentageLabel = new Label(improvementPercentage + "% improvement");
        percentageLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14)); // text-sm font-medium
        percentageLabel.setTextFill(Color.BLACK); // Default text color

        // Progress Bar (Progress component in JSX)
        StackPane progressBarContainer = new StackPane();
        progressBarContainer.setPrefSize(96, 8); // w-24 is 96px, h-2 is 8px
        progressBarContainer.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 4px;"); // Light gray background, rounded

        Rectangle filledBar = new Rectangle((improvementPercentage / 100.0) * 96, 8); // Calculate width based on percentage, max 96
        filledBar.setFill(Color.BLACK); // Progress bar fill is black in screenshot
        filledBar.setArcWidth(8); // Rounded corners for the filled bar
        filledBar.setArcHeight(8);
        StackPane.setAlignment(filledBar, Pos.CENTER_LEFT); // Align filled part to left

        progressBarContainer.getChildren().add(filledBar);
        percentageAndProgressBar.getChildren().addAll(percentageLabel, progressBarContainer);

        // Trend Up Icon (TrendingUp in JSX)
        // You need 'trend_up_green.png' in your assets/images folder for this.
        ImageView trendingUpIcon = createImageView("/assets/images/trend_up_green.png", 20, 20, "Trending Up Icon"); // w-5 h-5 means 20px
        if (trendingUpIcon == null) {
            Label fallbackArrow = new Label("↗");
            fallbackArrow.setFont(Font.font("System", FontWeight.BOLD, 20));
            fallbackArrow.setTextFill(Color.web("#28a745")); // text-green-600
            rightContentArea.getChildren().addAll(percentageAndProgressBar, fallbackArrow);
        } else {
            rightContentArea.getChildren().addAll(percentageAndProgressBar, trendingUpIcon);
        }

        item.getChildren().addAll(leftContent, spacer, rightContentArea);

        // Apply background and border to the entire item, mimicking JSX div with bg-gray-50 and rounded-lg
        item.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8px;"); // bg-gray-50 from Tailwind usually #f9fafb or similar very light gray.
        return item;
    }

    /**
     * Helper method to safely create an ImageView from a resource path.
     * Includes error handling for missing images.
     */
    private ImageView createImageView(String imagePath, double fitWidth, double fitHeight, String debugName) {
        ImageView imageView = new ImageView();
        URL imageUrl = getClass().getResource(imagePath); // Assumes imagePath is relative to the class path
        if (imageUrl == null) {
            System.err.println("ERROR: Image resource not found for " + debugName + " at path: " + imagePath);
            URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png"); // Ensure this placeholder exists
            if (fallbackUrl != null) {
                imageView.setImage(new Image(fallbackUrl.toExternalForm()));
            } else {
                System.err.println("ERROR: Fallback image not found either!");
            }
        } else {
            try {
                Image image = new Image(imageUrl.toExternalForm());
                imageView.setImage(image);
                imageView.setFitWidth(fitWidth);
                imageView.setFitHeight(fitHeight);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading " + debugName + " from " + imagePath + ": " + e.getMessage());
            }
        }
        return imageView;
    }
}