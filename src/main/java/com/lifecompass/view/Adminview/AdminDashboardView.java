package com.lifecompass.view.Adminview;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;

// Import all Admin-specific component screens (ensure these paths are correct)
import com.lifecompass.view.Adminview.UserManagementView;
import com.lifecompass.view.Adminview.SystemAnalyticsView;
import com.lifecompass.view.Adminview.CrisisResponseView;
import com.lifecompass.view.Adminview.AdminVerification;
import com.lifecompass.view.Adminview.adminNotificationView;
import com.lifecompass.view.Adminview.adminSettingView;

// MVC Imports
import com.lifecompass.controller.AdminDashboardController;
import com.lifecompass.model.CrisisSituation; // Import the model class

import java.util.List;

public class AdminDashboardView extends Application {

    String adminName = "Admin User"; // Placeholder for logged-in admin's name

    private Button activeTabButton;
    private Stage primaryStage;

    // A VBox to hold the dynamically changing content of the dashboard
    private VBox mainContentArea;

    // Instances of component screens
    private UserManagementView userManagementScreen;
    private SystemAnalyticsView systemAnalyticsScreen;
    private CrisisResponseView crisisResponseScreen;
    private AdminVerification adminVerificationScreen;
    private adminNotificationView adminNotificationScreen;
    private adminSettingView adminSettingsScreen;

    // MVC: Controller instance
    private AdminDashboardController controller;

    // References to the MetricCard instances to allow controller to update them
    private MetricCard totalUsersCard;
    private MetricCard therapistsCard; // This still represents the "Psychologist" card internally
    private MetricCard pendingReviewsCard;
    private MetricCard activeCrisesCard;

    // VBox to hold dynamic crisis items
    private VBox activeCrisisItemsContainer;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("LifeCompass - Admin Portal");

        // MVC: Initialize the controller
        this.controller = new AdminDashboardController(this);

        // Initialize all component screens here, passing primaryStage and a reference to this dashboard
        this.userManagementScreen = new UserManagementView(primaryStage);
        this.systemAnalyticsScreen = new SystemAnalyticsView(primaryStage);
        this.crisisResponseScreen = new CrisisResponseView(primaryStage);
        this.adminVerificationScreen = new AdminVerification(primaryStage);
        this.adminNotificationScreen = new adminNotificationView(this);
        this.adminSettingsScreen = new adminSettingView(primaryStage, this);

        StackPane rootLayout = createRootLayout();
        Scene scene = new Scene(rootLayout, 1540, 860); // Consistent size

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize with the Overview content when the app starts
        switchContent("Overview");

        // MVC: After the UI is built, let the controller load the initial data
        controller.loadDashboardMetrics();
    }

    public StackPane createRootLayout() {
        VBox baseDashboardLayout = new VBox();
        baseDashboardLayout.setSpacing(20);
        baseDashboardLayout.setPadding(new Insets(20, 20, 0, 20));
        baseDashboardLayout.setStyle("-fx-background-color: #f9fafb;");

        baseDashboardLayout.getChildren().addAll(
                buildHeader(),
                buildTabBar()
        );

        mainContentArea = new VBox();
        mainContentArea.setSpacing(20);
        mainContentArea.setPadding(new Insets(20, 20, 20, 20));
        VBox.setVgrow(mainContentArea, Priority.ALWAYS);

        VBox scrollContent = new VBox();
        scrollContent.getChildren().addAll(baseDashboardLayout, mainContentArea);
        scrollContent.setSpacing(0);

        ScrollPane scrollPane = new ScrollPane(scrollContent); // Use the correctly defined variable
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f9fafb; -fx-hbar-policy: never; -fx-vbar-policy: as-needed;");

        StackPane rootStack = new StackPane();
        rootStack.getChildren().addAll(scrollPane);
        return rootStack;
    }

    private HBox buildHeader() {
        ImageView profileImage = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/admin_profile.jpg")));
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        Circle clip = new Circle(20, 20, 20);
        profileImage.setClip(clip);

        Button profileButton = new Button();
        profileButton.setGraphic(profileImage);
        profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        profileButton.setOnAction(event -> {
            System.out.println("View Admin Profile");
        });
        Tooltip.install(profileButton, new Tooltip("View Profile"));
        profileButton.setOnMouseEntered(e -> profileButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 0; -fx-background-radius: 25;"));
        profileButton.setOnMouseExited(e -> profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;"));

        Label title = new Label("LifeCompass - Admin Portal");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: black;");

        Label welcome = new Label("System Administration Dashboard");
        welcome.setFont(Font.font("System", FontWeight.NORMAL, 14));
        welcome.setTextFill(Color.GRAY);

        VBox left = new VBox(2, title, welcome);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox leftSection = new HBox(10, profileButton, left);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView notification = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/notification.png")));
        notification.setFitWidth(20);
        notification.setFitHeight(20);
        Button notificationButton = new Button();
        notificationButton.setGraphic(notification);
        notificationButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;");
        notificationButton.setOnAction(event -> {
            System.out.println("Switching to Admin Notifications screen.");
            switchContent("Notifications");
            if (activeTabButton != null) {
                String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-padding: 8 20;";
                activeTabButton.setStyle(inactiveStyle);
                activeTabButton.setFont(Font.font("System", FontWeight.BOLD, 15));
                activeTabButton = null;
            }
        });
        Tooltip.install(notificationButton, new Tooltip("Notifications"));
        notificationButton.setOnMouseEntered(e -> notificationButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-background-radius: 5;"));
        notificationButton.setOnMouseExited(e -> notificationButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;"));

        ImageView setting = new ImageView(new Image(getClass().getResourceAsStream("/assets/admin_images/settings.png")));
        setting.setFitWidth(20);
        setting.setFitHeight(20);
        Button settingButton = new Button();
        settingButton.setGraphic(setting);
        settingButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;");
        settingButton.setOnAction(event -> {
            System.out.println("Switching to Admin Settings screen.");
            switchContent("Settings");
            if (activeTabButton != null) {
                String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-padding: 8 20;";
                activeTabButton.setStyle(inactiveStyle);
                activeTabButton.setFont(Font.font("System", FontWeight.BOLD, 15));
                activeTabButton = null;
            }
        });
        Tooltip.install(settingButton, new Tooltip("Settings"));
        settingButton.setOnMouseEntered(e -> settingButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-background-radius: 5;"));
        settingButton.setOnMouseExited(e -> settingButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;"));

        HBox icons = new HBox(15, notificationButton, settingButton);
        icons.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(leftSection, spacer, icons);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox buildTabBar() {
        String[] tabs = {"Overview", "User Management", "Verifications", "Crisis Response", "System Analytics"};
        String[] imagePaths = {
                "/assets/admin_images/home.png", // Overview
                "/assets/admin_images/total_user.png", // User Management
                "/assets/admin_images/verification.png", // Verifications
                "/assets/admin_images/psyalert.png", // Crisis Response
                "/assets/admin_images/analytics.png" // System Analytics
        };

        HBox container = new HBox();
        container.setPadding(new Insets(8));
        container.setStyle("-fx-background-color: #f3eeeee1; -fx-background-radius: 10;");
        container.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(container, Priority.ALWAYS);

        HBox hbox = new HBox();
        hbox.setSpacing(8);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hbox, Priority.ALWAYS);

        for (int i = 0; i < tabs.length; i++) {
            String tabName = tabs[i];

            Button btn = new Button(tabName);
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);

            final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-padding: 8 20;";
            final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;" +
                    " -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 20;";
            final String hoverInactiveStyle = "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-background-radius: 5; -fx-padding: 8 20;";

            btn.setStyle(inactiveStyle);
            btn.setFont(Font.font("System", FontWeight.BOLD, 15));

            if (tabName.equals("Overview")) {
                btn.setStyle(activeStyle);
                btn.setFont(Font.font("System", FontWeight.BOLD, 15));
                activeTabButton = btn;
            }

            btn.setOnAction(event -> {
                if (activeTabButton != null) {
                    activeTabButton.setStyle(inactiveStyle);
                    activeTabButton.setFont(Font.font("System", FontWeight.BOLD, 15));
                }
                btn.setStyle(activeStyle);
                btn.setFont(Font.font("System", FontWeight.BOLD, 15));
                activeTabButton = btn;

                switchContent(tabName);
                System.out.println("Clicked Tab: " + tabName);
            });

            btn.setOnMouseEntered(e -> {
                if (btn != activeTabButton) {
                    btn.setStyle(hoverInactiveStyle);
                }
            });
            btn.setOnMouseExited(e -> {
                if (btn != activeTabButton) {
                    btn.setStyle(inactiveStyle);
                }
            });

            hbox.getChildren().add(btn);
        }

        container.getChildren().add(hbox);
        return container;
    }

    public void switchContent(String tabName) {
        mainContentArea.getChildren().clear();

        switch (tabName) {
            case "Overview":
                mainContentArea.getChildren().addAll(
                        buildOverviewMetricsRow(),
                        buildActiveCrisisSituations(),
                        buildSystemPerformanceAndRecentActivity()
                );
                // When switching back to Overview, reload the metrics
                if (controller != null) {
                    controller.loadDashboardMetrics();
                }
                break;
            case "User Management":
                if (userManagementScreen != null) {
                    mainContentArea.getChildren().add(userManagementScreen.getView());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: User Management Screen not initialized."));
                }
                break;
            case "Verifications":
                if (adminVerificationScreen != null) {
                    mainContentArea.getChildren().add(adminVerificationScreen.getView());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Verifications Screen not initialized."));
                }
                break;
            case "Crisis Response":
                if (crisisResponseScreen != null) {
                    mainContentArea.getChildren().add(crisisResponseScreen.getView());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Crisis Response Screen not initialized."));
                }
                break;
            case "System Analytics":
                if (systemAnalyticsScreen != null) {
                    mainContentArea.getChildren().add(systemAnalyticsScreen.getView());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: System Analytics Screen not initialized."));
                }
                break;
            case "Notifications":
                if (adminNotificationScreen != null) {
                    mainContentArea.getChildren().add(adminNotificationScreen.getContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Admin Notifications Screen not initialized."));
                }
                break;
            case "Settings":
                if (adminSettingsScreen != null) {
                    mainContentArea.getChildren().add(adminSettingsScreen.getContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Admin Settings Screen not initialized."));
                }
                break;
            default:
                mainContentArea.getChildren().add(new Label("Content for " + tabName + " is not yet implemented."));
                break;
        }
    }

    // --- Admin Overview Tab Content ---

    private HBox buildOverviewMetricsRow() {
        HBox hbox = new HBox(20); // Maintain spacing between cards
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(0));

        // Define data for the cards. Initial values are "Fetching..."
        // CHANGE: "Therapists" to "Psychologist" here for display label
        String[] titles = {"Total Users", "Psychologist", "Pending Reviews", "Active Crises", "System Health"};
        String[] initialValues = {"Fetching...", "Fetching...", "Fetching...", "Fetching...", "98%"};
        String[] subtitles = {"+12% from last month", "Verified professionals", "Awaiting verification", "Requires attention", "All systems operational"};
        String[] images = {
                "/assets/admin_images/total_user.png",
                "/assets/admin_images/verification.png",
                "/assets/admin_images/journal.jpg",
                "/assets/admin_images/psyalert.png",
                "/assets/admin_images/system_health.png"
        };
        String[] valueColors = {"black", "black", "black", "#ef5350", "#4CAF50"};

        // Create MetricCard instances and store references
        totalUsersCard = new MetricCard(titles[0], initialValues[0], subtitles[0], images[0], valueColors[0]);
        // Note: The 'therapistsCard' variable still holds the card for "Psychologist" metric,
        // so its name isn't changed, just the display title.
        therapistsCard = new MetricCard(titles[1], initialValues[1], subtitles[1], images[1], valueColors[1]);
        pendingReviewsCard = new MetricCard(titles[2], initialValues[2], subtitles[2], images[2], valueColors[2]);
        activeCrisesCard = new MetricCard(titles[3], initialValues[3], subtitles[3], images[3], valueColors[3]);
        MetricCard systemHealthCard = new MetricCard(titles[4], initialValues[4], subtitles[4], images[4], valueColors[4]);

        // Add them to the HBox
        hbox.getChildren().addAll(
                totalUsersCard.getCardNode(),
                therapistsCard.getCardNode(),
                pendingReviewsCard.getCardNode(),
                activeCrisesCard.getCardNode(),
                systemHealthCard.getCardNode()
        );

        // MVC: Pass the references to the controller for updates
        controller.setMetricCards(totalUsersCard, therapistsCard, pendingReviewsCard, activeCrisesCard);

        return hbox;
    }

    /**
     * Inner class to represent a single metric card on the dashboard.
     * Encapsulates the UI elements and provides a method to update the value.
     */
    public class MetricCard {
        private Label valueLabel; // The label that displays the dynamic value
        private HBox cardNode;    // The entire HBox node representing the card

        public MetricCard(String title, String value, String subtitle, String imagePath, String valueColor) {
            ImageView imageView = createImageView(imagePath, 28, 28);

            HBox iconWrapper = new HBox(imageView);
            iconWrapper.setAlignment(Pos.CENTER);
            iconWrapper.setPadding(new Insets(0));

            Label titleLbl = new Label(title);
            titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
            titleLbl.setTextFill(Color.GRAY);

            this.valueLabel = new Label(value); // Initialize with placeholder, controller updates this
            this.valueLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
            this.valueLabel.setStyle("-fx-text-fill: " + valueColor + ";");

            Label subLbl = new Label(subtitle);
            subLbl.setFont(Font.font("System", FontWeight.NORMAL, 11));
            subLbl.setTextFill(Color.GRAY);

            VBox textContent = new VBox(4, titleLbl, valueLabel, subLbl);
            textContent.setAlignment(Pos.TOP_LEFT);
            VBox.setVgrow(textContent, Priority.ALWAYS);

            HBox box = new HBox(10);
            box.setPadding(new Insets(15, 20, 15, 20));
            String originalStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);";
            box.setStyle(originalStyle);

            // Crucial changes for layout: Set preferred width and remove HGrow
            // A common width for these cards could be around 250-280 depending on total screen width and spacing
            box.setPrefWidth(260); // Set a fixed preferred width for each card
            box.setMaxWidth(Region.USE_PREF_SIZE); // Ensure it doesn't try to grow beyond preferred size
            // HBox.setHgrow(box, Priority.ALWAYS); // REMOVE this line from where the card is added to the HBox

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            box.getChildren().addAll(textContent, spacer, iconWrapper);
            box.setAlignment(Pos.CENTER_LEFT);

            setHoverStyle(box,
                    "-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);",
                    originalStyle
            );

            this.cardNode = box; // Store the created HBox
        }

        /**
         * Updates the value displayed on this metric card.
         * @param newValue The new string value to display.
         */
        public void updateValue(String newValue) {
            this.valueLabel.setText(newValue);
        }

        /**
         * Returns the JavaFX Node representing this metric card.
         * @return The HBox containing the card's UI.
         */
        public HBox getCardNode() {
            return cardNode;
        }
    }


    private VBox buildActiveCrisisSituations() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(section, Priority.ALWAYS);

        Label heading = new Label("Active Crisis Situations");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        heading.setTextFill(Color.web("#ef5350"));

        Label sub = new Label("Critical situations requiring immediate administrative oversight");
        sub.setFont(Font.font("System", FontWeight.NORMAL, 12));
        sub.setTextFill(Color.GRAY);

        ImageView crisisIcon = createImageView("/assets/admin_images/admin_alert_red.png", 18, 18);

        HBox headerBox = new HBox(5, crisisIcon, heading);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 3 10;");
        closeButton.setFont(Font.font("System", FontWeight.BOLD, 10));

        HBox sectionHeader = new HBox(headerBox, new Region(), closeButton);
        HBox.setHgrow(sectionHeader.getChildren().get(1), Priority.ALWAYS);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        section.getChildren().addAll(sectionHeader, sub);

        // MVC: Create a VBox to hold dynamic crisis items, which will be populated by the controller
        activeCrisisItemsContainer = new VBox(10); // Spacing for crisis items
        section.getChildren().add(activeCrisisItemsContainer); // Add this container to the section

        // The crisis items will be added dynamically by the controller via updateActiveCrisisSituations
        return section;
    }

    /**
     * MVC: Method to update the displayed list of active crisis situations.
     * Called by the AdminDashboardController.
     * @param crises A list of CrisisSituation objects to display.
     */
    public void updateActiveCrisisSituations(List<CrisisSituation> crises) {
        if (activeCrisisItemsContainer != null) {
            activeCrisisItemsContainer.getChildren().clear(); // Clear existing items

            if (crises != null && !crises.isEmpty()) {
                for (CrisisSituation crisis : crises) {
                    String timeAndAssignment = (crisis.getTimestamp() != null ?
                                                new java.text.SimpleDateFormat("hh:mm a").format(crisis.getTimestamp()) + " - " :
                                                "") +
                                                "Assigned to " + (crisis.getAssignedToPsychologistId() != null ? crisis.getAssignedToPsychologistId() : "N/A");
                    activeCrisisItemsContainer.getChildren().add(
                        createCrisisItem(
                            crisis.getUserId(),
                            crisis.getDescription(),
                            timeAndAssignment,
                            crisis.getSeverity()
                        )
                    );
                }
            } else {
                activeCrisisItemsContainer.getChildren().add(new Label("No active crisis situations."));
            }
        }
    }


    private HBox createCrisisItem(String userId, String description, String timeAndAssignment, String severity) {
        VBox userInfo = new VBox(3);
        Label userLabel = new Label(userId);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        userLabel.setTextFill(Color.BLACK);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.GRAY);

        Label timeLabel = new Label(timeAndAssignment);
        timeLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
        timeLabel.setTextFill(Color.DARKGRAY);

        userInfo.getChildren().addAll(userLabel, descLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label severityLabel = new Label(severity);
        String severityStyle = "";
        if (severity.equalsIgnoreCase("High")) {
            severityStyle = "-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 2 7;";
        } else if (severity.equalsIgnoreCase("Medium")) {
            severityStyle = "-fx-background-color: #ffb300; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 2 7;";
        }
        severityLabel.setStyle(severityStyle);
        severityLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        Button coordinateButton = new Button("Coordinate Response");
        coordinateButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;");
        coordinateButton.setFont(Font.font("System", FontWeight.BOLD, 12));
        coordinateButton.setOnAction(e -> System.out.println("Coordinating response for " + userId));
        coordinateButton.setOnMouseEntered(e -> coordinateButton.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;"));
        coordinateButton.setOnMouseExited(e -> coordinateButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;"));

        HBox itemLayout = new HBox(15, userInfo, spacer, severityLabel, coordinateButton);
        itemLayout.setAlignment(Pos.CENTER_LEFT);
        itemLayout.setPadding(new Insets(10, 15, 10, 15));
        String originalItemStyle = "-fx-background-color: #fffafa; -fx-background-radius: 8; -fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 8;";
        itemLayout.setStyle(originalItemStyle);
        setHoverStyle(itemLayout,
                "-fx-background-color: #ffe0e0; -fx-background-radius: 8; -fx-border-color: #ef9a9a; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);",
                originalItemStyle
        );

        return itemLayout;
    }

    private HBox buildSystemPerformanceAndRecentActivity() {
        HBox hbox = new HBox(20);
        hbox.setHgrow(hbox, Priority.ALWAYS);
        hbox.setHgrow(hbox, Priority.ALWAYS);

        VBox systemPerformance = buildSystemPerformance();
        VBox recentActivity = buildRecentActivity();

        HBox.setHgrow(systemPerformance, Priority.ALWAYS);
        HBox.setHgrow(recentActivity, Priority.ALWAYS);

        hbox.getChildren().addAll(systemPerformance, recentActivity);
        return hbox;
    }

    private VBox buildSystemPerformance() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(box, Priority.ALWAYS);

        Label heading = new Label("System Performance");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label sub = new Label("Real-time system health metrics");
        sub.setFont(Font.font("System", FontWeight.NORMAL, 12));
        sub.setTextFill(Color.GRAY);

        box.getChildren().addAll(heading, sub);

        // Performance metrics (using dummy data for progress/bars)
        box.getChildren().add(createPerformanceMetric("Server Uptime", "99.9%"));
        box.getChildren().add(createPerformanceMetric("Response Time", "120ms"));
        box.getChildren().add(createPerformanceMetric("Active Sessions", "342"));
        box.getChildren().add(createPerformanceMetric("Database Health", "98%"));

        return box;
    }

    private HBox createPerformanceMetric(String metricName, String value) {
        Label nameLabel = new Label(metricName);
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nameLabel.setTextFill(Color.BLACK);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        valueLabel.setTextFill(Color.web("#333333")); // Darker color for value

        Region bar = new Region();
        bar.setPrefSize(value.contains("%") ? Double.parseDouble(value.replace("%", "")) : 50, 8);
        bar.setStyle("-fx-background-color: #1976d2; -fx-background-radius: 4;");
        if (metricName.equals("Response Time")) {
            bar.setPrefSize(50, 8);
            bar.setStyle("-fx-background-color: #ffc107; -fx-background-radius: 4;");
        } else if (metricName.equals("Database Health")) {
            bar.setPrefSize(Double.parseDouble(value.replace("%", "")) * 0.5, 8);
            bar.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 4;");
        }

        HBox row = new HBox(10, nameLabel, spacer, valueLabel, bar);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));

        return row;
    }

    private VBox buildRecentActivity() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(box, Priority.ALWAYS);

        Label heading = new Label("Recent Activity");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label sub = new Label("Latest system and user activities");
        sub.setFont(Font.font("System", FontWeight.NORMAL, 12));
        sub.setTextFill(Color.GRAY);

        box.getChildren().addAll(heading, sub);

        String[][] activities = {
                {"New therapist verification", "Dr. Emily Rodriguez - 5 minutes ago", "#1976d2"},
                {"Crisis alert resolved", "User_5643 - 12 minutes ago", "#4CAF50"},
                {"Content flagged for review", "User_8901 - 18 minutes ago", "#ffb300"},
                {"System backup completed", "System - 1 hour ago", "#607d8b"}
        };

        for (String[] activity : activities) {
            box.getChildren().add(createActivityRow(activity[0], activity[1], activity[2]));
        }
        return box;
    }

    private HBox createActivityRow(String activityText, String details, String colorHex) {
        Circle dot = new Circle(4);
        dot.setFill(Color.web(colorHex));

        VBox textContent = new VBox(2);
        Label activityLabel = new Label(activityText);
        activityLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        activityLabel.setTextFill(Color.BLACK);

        Label detailsLabel = new Label(details);
        detailsLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        detailsLabel.setTextFill(Color.GRAY);

        textContent.getChildren().addAll(activityLabel, detailsLabel);

        HBox row = new HBox(10, dot, textContent);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));

        return row;
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods (for image loading and hover effects)
    //------------------------------------------------------------------------------------------------------------------

    private ImageView createImageView(String imagePath, int fitWidth, int intHeight) { // Renamed fitHeight to intHeight
        ImageView iconView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            if (image.isError()) {
                System.err.println("Failed to load image: " + imagePath + " - Error: " + image.exceptionProperty().get().getMessage());
                try {
                    Image placeholder = new Image(getClass().getResourceAsStream("/assets/admin_images/placeholder.png"));
                    iconView.setImage(placeholder);
                } catch (Exception pe) {
                    System.err.println("Also failed to load placeholder.png: " + pe.getMessage());
                }
            } else {
                iconView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Exception while loading image: " + imagePath + " - " + e.getMessage());
            try {
                Image placeholder = new Image(getClass().getResourceAsStream("/assets/admin_images/placeholder.png"));
                iconView.setImage(placeholder);
            } catch (Exception pe) {
                System.err.println("Also failed to load placeholder.png: " + pe.getMessage());
            }
        }
        iconView.setFitWidth(fitWidth);
        iconView.setFitHeight(intHeight); // Use intHeight here
        iconView.setPreserveRatio(true);
        return iconView;
    }

    private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
        region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
        region.setOnMouseExited(e -> region.setStyle(normalStyle));
    }

    public static void main(String[] args) {
        launch(args);
    }
}