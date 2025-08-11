package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.Appointment;
import com.lifecompass.model.psychologist.CrisisAlert;
import com.lifecompass.model.psychologist.Patient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos; // <--- ENSURE THIS IMPORT IS PRESENT
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos; // <--- ENSURE THIS IMPORT IS PRESENT
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle; // Still used for profile picture clipping
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistDashboard {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistDashboard.class);

    private Stage primaryStage;
    private String therapistName = "Psychologist";
    private Button activeTabButton;
    private VBox mainContentArea;
    private HBox tabBar;
    private Label welcomeLabel;

    private Consumer<String> navigationHandler;
    private Consumer<CrisisAlert> crisisRespondHandler;

    // --- Dynamic UI Elements References ---
    private Label totalPatientsValueLbl;
    private Label activeChatsValueLbl;
    private Label appointmentsTodayValueLbl;
    private Label crisisAlertsValueLbl;

    private VBox crisisAlertsContainer;
    private VBox recentPatientActivityContainer;
    private VBox todaysScheduleContainer;

    public Stage myStage;
    public Scene myScene;
    

    // public PsychologistDashboard(Stage primaryStage) {
    //     System.out.println("========"+primaryStage);
    //     start(primaryStage);
    // }

    // @Override
    // public void start(Stage primaryStage) {
    //     this.primaryStage = primaryStage;
    //     initializeStaticUI();
    //     logger.info("PsychologistDashboard view initialized.");
    // }

    
    public void setDashScene(Scene myScene) {
        this.myScene = myScene;
    }

    public void setDashStage(Stage myStage) {
        primaryStage = myStage;
        initializeStaticUI();
    }

    private void initializeStaticUI() {
        tabBar = buildTabBar();
        welcomeLabel = new Label("Welcome back, " + therapistName);
    }

    public void show() {
        if (primaryStage == null) {
            logger.error("Primary Stage is null in PsychologistDashboard.show(). Cannot display.");
            return;
        }
        primaryStage.setTitle("LifeCompass - Psychologist Portal");
        StackPane rootLayout = createRootLayout();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        logger.info("Psychologist Dashboard window displayed.");
    }

    public StackPane createRootLayout() {
        VBox baseDashboardLayout = new VBox();
        baseDashboardLayout.setSpacing(20);
        baseDashboardLayout.setPadding(new Insets(20, 20, 0, 20)); // Padding around header and tabs
        baseDashboardLayout.setStyle("-fx-background-color: #f9fafb;");

        baseDashboardLayout.getChildren().addAll(
                buildHeader(),
                tabBar
        );

        mainContentArea = new VBox();
        mainContentArea.setSpacing(20);
        mainContentArea.setPadding(new Insets(20)); // Padding around the main content (overview, patients, etc.)
        VBox.setVgrow(mainContentArea, Priority.ALWAYS);
        // Removed temporary debug background color from mainContentArea
        mainContentArea.setStyle("-fx-background-color: transparent;"); // Reset to original style or transparent if no specific background is desired here


        VBox scrollContentWrapper = new VBox();
        scrollContentWrapper.getChildren().addAll(baseDashboardLayout, mainContentArea);
        scrollContentWrapper.setSpacing(0);

        ScrollPane scrollPane = new ScrollPane(scrollContentWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f9fafb; -fx-hbar-policy: never; -fx-vbar-policy: as-needed;");

        StackPane rootStack = new StackPane(scrollPane);
        logger.debug("Root layout created.");
        return rootStack;
    }

    private HBox buildHeader() {
        // Replaced ImageView with text-based icons
        Label profileIconLabel = new Label("👤"); // Unicode Profile Icon
        profileIconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Adjust size/font as needed
        profileIconLabel.setTextFill(Color.BLACK); // Set color to make it visible
        Circle clip = new Circle(20, 20, 20); // Keep for clipping if desired, or remove
        profileIconLabel.setClip(clip); // Apply clip to the label

        Button profileButton = new Button();
        profileButton.setGraphic(profileIconLabel);
        profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        profileButton.setOnAction(event -> {
            logger.debug("Profile button clicked.");
            if (navigationHandler != null) {
                deactivateActiveTab();
                navigationHandler.accept("Profile");
            }
        });
        Tooltip.install(profileButton, new Tooltip("View Profile"));
        profileButton.setOnMouseEntered(e -> profileButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 0; -fx-background-radius: 25;"));
        profileButton.setOnMouseExited(e -> profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;"));

        Label title = new Label("LifeCompass - Psychologist Portal");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: black;");

        welcomeLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        welcomeLabel.setTextFill(Color.GRAY);

        VBox left = new VBox(2, title, welcomeLabel);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox leftSection = new HBox(10, profileButton, left);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Replaced ImageView with text-based icon
        Label notificationIconLabel = new Label("🔔"); // Unicode Bell Icon
        notificationIconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        notificationIconLabel.setTextFill(Color.BLACK); // Set color to make it visible
        Button notificationButton = new Button();
        notificationButton.setGraphic(notificationIconLabel);
        notificationButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;");
        notificationButton.setOnAction(event -> {
            logger.debug("Notifications button clicked.");
            if (navigationHandler != null) {
                deactivateActiveTab();
                navigationHandler.accept("Notifications");
            }
        });
        Tooltip.install(notificationButton, new Tooltip("Notifications"));
        notificationButton.setOnMouseEntered(e -> notificationButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-background-radius: 5;"));
        notificationButton.setOnMouseExited(e -> notificationButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;"));

        // Replaced ImageView with text-based icon
        Label settingIconLabel = new Label("⚙"); // Unicode Gear Icon
        settingIconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        settingIconLabel.setTextFill(Color.BLACK); // Set color to make it visible
        Button settingButton = new Button();
        settingButton.setGraphic(settingIconLabel);
        settingButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;");
        settingButton.setOnAction(event -> {
            logger.debug("Settings button clicked.");
            if (navigationHandler != null) {
                deactivateActiveTab();
                navigationHandler.accept("Settings");
            }
        });
        Tooltip.install(settingButton, new Tooltip("Settings"));
        settingButton.setOnMouseEntered(e -> settingButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-background-radius: 5;"));
        settingButton.setOnMouseExited(e -> settingButton.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 5;"));

        HBox icons = new HBox(15, notificationButton, settingButton);
        icons.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(leftSection, spacer, icons);
        header.setAlignment(Pos.CENTER_LEFT);
        logger.debug("Header built.");
        return header;
    }

    private HBox buildTabBar() {
        String[] tabs = {"Overview", "Patients", "Appointments", "Crisis Monitor", "Analytics"};
        // Image paths are no longer used for tab icons in this file

        HBox container = new HBox();
        container.setPadding(new Insets(8));
        container.setStyle("-fx-background-color: #f3eeeee1; -fx-background-radius: 10;");
        container.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(container, Priority.ALWAYS);

        HBox hbox = new HBox();
        hbox.setSpacing(8);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hbox, Priority.ALWAYS);

        final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: normal; -fx-text-fill: #555555; -fx-padding: 8 20;";
        final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: 600;" +
                " -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 20;";
        final String hoverInactiveStyle = "-fx-background-color: #e0e0e0; -fx-font-weight: normal; -fx-text-fill: #555555; -fx-background-radius: 5; -fx-padding: 8 20;";

        for (int i = 0; i < tabs.length; i++) {
            String tabName = tabs[i];
            Button btn = new Button(tabName);
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);

            btn.setStyle(inactiveStyle);
            btn.setFont(Font.font("System", FontWeight.BOLD, 16));

            if (tabName.equals("Overview")) {
                btn.setStyle(activeStyle);
                btn.setFont(Font.font("System", FontWeight.BOLD, 16));
                activeTabButton = btn;
            }

            btn.setOnAction(event -> {
                logger.debug("Tab button clicked: {}", tabName);
                updateActiveTab(btn);
                if (navigationHandler != null) {
                    navigationHandler.accept(tabName);
                }
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
        logger.debug("Tab bar built.");
        container.getChildren().add(hbox);
        return container;
    }

    public VBox buildOverviewContent() {
        logger.debug("Building Overview content.");
        VBox overviewLayout = new VBox(20);
        overviewLayout.setPadding(new Insets(0));

        GridPane metricsGrid = buildOverviewMetricsGrid(); 
        overviewLayout.getChildren().add(metricsGrid);

        VBox crisisSection = buildCrisisAlertsSection();
        overviewLayout.getChildren().add(crisisSection);

        HBox activityAndScheduleRow = buildRecentPatientActivityAndSchedule();
        overviewLayout.getChildren().add(activityAndScheduleRow);

        logger.info("Overview content structure built.");
        return overviewLayout;
    }

    private GridPane buildOverviewMetricsGrid() { 
        GridPane grid = new GridPane();
        grid.setHgap(20); 
        grid.setVgap(20); 
        grid.setPadding(new Insets(0)); 
        grid.setAlignment(Pos.CENTER); 
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(50); 
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        row1.setPrefHeight(Region.USE_COMPUTED_SIZE); 
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        row2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.getRowConstraints().addAll(row1, row2);


        totalPatientsValueLbl = new Label("N/A");
        activeChatsValueLbl = new Label("N/A");
        appointmentsTodayValueLbl = new Label("N/A");
        crisisAlertsValueLbl = new Label("N/A");

        // Replaced ImageViews with text-based icons
        Node card1 = createMetricCard("Total Patients", totalPatientsValueLbl, "Active caseload", "👥", "black"); 
        Node card2 = createMetricCard("Active Chats", activeChatsValueLbl, "Ongoing conversations", "💬", "black"); 
        Node card3 = createMetricCard("Appointments Today", appointmentsTodayValueLbl, "Scheduled sessions", "📅", "black"); 
        Node card4 = createMetricCard("Crisis Alerts", crisisAlertsValueLbl, "Requires attention", "🚨", "#ef5350"); 

        grid.add(card1, 0, 0); 
        grid.add(card2, 1, 0); 
        grid.add(card3, 0, 1); 
        grid.add(card4, 1, 1); 

        for (Node node : grid.getChildren()) {
            GridPane.setValignment(node, VPos.CENTER); 
            GridPane.setHalignment(node, HPos.CENTER); 
        }

        logger.debug("Overview metrics grid built.");
        return grid;
    }

    private HBox createMetricCard(String title, Label valueLabel, String subtitle, String iconSymbol, String valueColor) {
        // Replaced ImageView with a Label for text-based icon
        Label iconLabel = new Label(iconSymbol); 
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20)); 
        iconLabel.setTextFill(Color.web(valueColor)); 
        
        HBox iconWrapper = new HBox(iconLabel);
        iconWrapper.setAlignment(Pos.CENTER);
        iconWrapper.setPadding(new Insets(0, 0, 0, 0));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        titleLbl.setTextFill(Color.GRAY);

        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        valueLabel.setStyle("-fx-text-fill: " + valueColor + ";");

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        subLbl.setTextFill(Color.GRAY);

        VBox textContent = new VBox(4, titleLbl, valueLabel, subLbl);
        textContent.setAlignment(Pos.CENTER_LEFT); 
        VBox.setVgrow(textContent, Priority.ALWAYS);

        HBox box = new HBox(10); 
        box.setPadding(new Insets(15, 20, 15, 20));
        String originalStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);";
        box.setStyle(originalStyle);
        
        HBox.setHgrow(box, Priority.ALWAYS); 
        box.setMaxWidth(Double.MAX_VALUE); 
        box.setPrefHeight(100); 
        box.setMinHeight(Region.USE_PREF_SIZE); 
        box.setMaxHeight(Region.USE_PREF_SIZE); 

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        box.getChildren().addAll(textContent, spacer, iconWrapper);
        box.setAlignment(Pos.CENTER_LEFT);

        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);"));
        box.setOnMouseExited(e -> box.setStyle(originalStyle));

        return box;
    }

    private VBox buildCrisisAlertsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox titleRow = new HBox(5);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label alertIcon = new Label("\u26A0"); // Text icon
        alertIcon.setFont(Font.font("System", FontWeight.BOLD, 18));
        alertIcon.setTextFill(Color.web("#B71C1C"));
        Label heading = new Label("Crisis Alerts");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        heading.setTextFill(Color.web("#ef5350"));
        titleRow.getChildren().addAll(alertIcon, heading);

        Label sub = new Label("Patients requiring immediate attention");
        sub.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        sub.setTextFill(Color.GRAY);

        section.getChildren().addAll(titleRow, sub);

        crisisAlertsContainer = new VBox(8);
        section.getChildren().add(crisisAlertsContainer);

        logger.debug("Crisis alerts section built for overview.");
        return section;
    }

    private HBox createOverviewCrisisItem(CrisisAlert alert) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 15, 10, 15));
        card.setStyle("-fx-background-color: #fffafa; -fx-background-radius: 8; -fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 8;");

        VBox patientInfo = new VBox(3);
        Label nameLabel = new Label(alert.getPatientName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.BLACK);

        Label descLabel = new Label(alert.getDescription());
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        descLabel.setTextFill(Color.GRAY);

        Label timeLabel = new Label(alert.getTimeAgo());
        timeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        timeLabel.setTextFill(Color.DARKGRAY);

        patientInfo.getChildren().addAll(nameLabel, descLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button highButton = new Button(alert.getSeverity());
        highButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;");
        highButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        highButton.setOnMouseEntered(e -> highButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;"));
        highButton.setOnMouseExited(e -> highButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 4 12;"));
        highButton.setMinWidth(60);

        Button respondButton = new Button("Respond");
        respondButton.setStyle("-fx-background-color: #c5d9f0; -fx-text-fill: #3f51b5; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 4 12; -fx-border-color: #3f51b5; -fx-border-width: 1;");
        respondButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        respondButton.setOnMouseEntered(e -> respondButton.setStyle("-fx-background-color: #b0c4de; -fx-text-fill: #3f51b5; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 4 12; -fx-border-color: #3f51b5; -fx-border-width: 1;"));
        respondButton.setOnMouseExited(e -> respondButton.setStyle("-fx-background-color: #c5d9f0; -fx-text-fill: #3f51b5; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 4 12; -fx-border-color: #3f51b5; -fx-border-width: 1;"));
        respondButton.setMinWidth(70);
        
        respondButton.setOnAction(e -> {
            logger.info("Respond button clicked for Crisis Alert: {}", alert.getAlertId());
            if (crisisRespondHandler != null) {
                crisisRespondHandler.accept(alert);
            }
        });

        HBox buttons = new HBox(8, highButton, respondButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(patientInfo, spacer, buttons);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #ffe0e0; -fx-background-radius: 8; -fx-border-color: #ef9a9a; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #fffafa; -fx-background-radius: 8; -fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 8;"));

        return card;
    }

    private HBox buildRecentPatientActivityAndSchedule() {
        HBox hbox = new HBox(20);
        HBox.setHgrow(hbox, Priority.ALWAYS);

        VBox patientActivity = buildRecentPatientActivity();
        VBox schedule = buildTodaySchedule();

        HBox.setHgrow(patientActivity, Priority.ALWAYS);
        HBox.setHgrow(schedule, Priority.ALWAYS);

        hbox.getChildren().addAll(patientActivity, schedule);
        logger.debug("Recent patient activity and schedule section built.");
        return hbox;
    }

    private VBox buildRecentPatientActivity() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(box, Priority.ALWAYS);

        Label heading = new Label("Recent Patient Activity");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label sub = new Label("Latest updates from your patients");
        sub.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        sub.setTextFill(Color.GRAY);

        box.getChildren().addAll(heading, sub);

        recentPatientActivityContainer = new VBox(8);
        box.getChildren().add(recentPatientActivityContainer);

        Button viewMoreButton = new Button("View More Patients");
        viewMoreButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        viewMoreButton.setTextFill(Color.web("#6a1b9a"));
        viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreButton.setOnMouseEntered(e -> viewMoreButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setOnMouseExited(e -> viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(viewMoreButton, new Insets(10, 0, 0, 0));
        viewMoreButton.setOnAction(e -> {
            logger.info("View More Patients clicked from Overview (navigating to Patients tab)");
            if (navigationHandler != null) {
                navigationHandler.accept("Patients");
            }
        });
        box.getChildren().add(viewMoreButton);
        logger.debug("Recent patient activity section built for overview.");
        return box;
    }

    private HBox createPatientActivityRow(Patient patient) {
        Circle profileCircle = new Circle(16);
        profileCircle.setFill(Color.LIGHTGRAY);
        Label initialLabel = new Label(patient.getInitials().toUpperCase());
        initialLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        initialLabel.setTextFill(Color.WHITE);
        StackPane patientIcon = new StackPane(profileCircle, initialLabel);
        patientIcon.setAlignment(Pos.CENTER);

        VBox info = new VBox(2);
        Label name = new Label(patient.getName());
        name.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label session = new Label("Last contact: " + patient.getLastContact());
        session.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        session.setTextFill(Color.GRAY);
        info.getChildren().addAll(name, session);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label moodTrendLabel = new Label(patient.getStatus());
        String moodTrendStyle = "";
        if (patient.getStatus() != null) {
            switch (patient.getStatus().toLowerCase()) {
                case "improving": moodTrendStyle = "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                case "stable": moodTrendStyle = "-fx-background-color: #fffde7; -fx-text-fill: #f9a825; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                case "declining": moodTrendStyle = "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                default: moodTrendStyle = "-fx-background-color: #e0e0e0; -fx-text-fill: #555555; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
            }
        }
        moodTrendLabel.setStyle(moodTrendStyle);
        moodTrendLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));

        Label riskLevelLabel = new Label(patient.getRisk());
        String riskLevelStyle = "";
        if (patient.getRisk() != null) {
            switch (patient.getRisk().toLowerCase()) {
                case "low risk": riskLevelStyle = "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                case "medium risk": riskLevelStyle = "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                case "high risk": riskLevelStyle = "-fx-background-color: #fbe9e7; -fx-text-fill: #d84315; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
                default: riskLevelStyle = "-fx-background-color: #e0e0e0; -fx-text-fill: #555555; -fx-background-radius: 5; -fx-padding: 2 7;"; break;
            }
        }
        riskLevelLabel.setStyle(riskLevelStyle);
        riskLevelLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 10));

        HBox actionButtons = new HBox(8);
        Label chatIcon = new Label("\uD83D\uDCAC"); // Text icon
        chatIcon.setFont(Font.font("System", 12));
        Button chatButton = new Button("Chat", chatIcon);
        chatButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;");
        chatButton.setFont(Font.font("System", 12));
        chatButton.setOnMouseEntered(e -> chatButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        chatButton.setOnMouseExited(e -> chatButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        chatButton.setOnAction(e -> {
            logger.info("Chat with {} clicked (dummy action)", patient.getName());
        });

        Label analyticsIcon = new Label("\uD83D\uDCCA"); // Text icon
        analyticsIcon.setFont(Font.font("System", 12));
        Button analyticsButton = new Button("Analytics", analyticsIcon);
        analyticsButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;");
        analyticsButton.setFont(Font.font("System", 12));
        analyticsButton.setOnMouseEntered(e -> analyticsButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        analyticsButton.setOnMouseExited(e -> analyticsButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        analyticsButton.setOnAction(e -> logger.info("View Analytics for {} clicked (dummy action)", patient.getName()));

        actionButtons.getChildren().addAll(chatButton, analyticsButton);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(12, patientIcon, info, moodTrendLabel, riskLevelLabel, spacer, actionButtons);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        String originalRowStyle = "-fx-background-color: transparent;";
        row.setStyle(originalRowStyle);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle(originalRowStyle));

        return row;
    }

    private VBox buildTodaySchedule() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        VBox.setVgrow(box, Priority.ALWAYS);

        Label heading = new Label("Today's Schedule");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label sub = new Label("Upcoming appointments and sessions");
        sub.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        sub.setTextFill(Color.GRAY);

        box.getChildren().addAll(heading, sub);

        todaysScheduleContainer = new VBox(8);
        box.getChildren().add(todaysScheduleContainer);

        logger.debug("Today's schedule section built for overview.");
        return box;
    }

    private HBox createAppointmentRow(Appointment appointment) {
        // Replaced ImageView with a text-based clock symbol
        Label clockSymbolLabel = new Label("⏰"); // Unicode Clock Symbol
        clockSymbolLabel.setFont(Font.font("Arial", 30));
        clockSymbolLabel.setTextFill(Color.web("#6a1b9a"));

        Circle iconBgCircle = new Circle(18);
        iconBgCircle.setFill(Color.web("#e3f2fd"));
        StackPane iconContainer = new StackPane(iconBgCircle, clockSymbolLabel); // Use clockSymbolLabel
        iconContainer.setAlignment(Pos.CENTER);

        VBox info = new VBox(2);
        Label name = new Label(appointment.getPatientName());
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#333333"));
        Label sessionType = new Label(appointment.getSessionType());
        sessionType.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        sessionType.setTextFill(Color.GRAY);
        info.getChildren().addAll(name, sessionType);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox timeDuration = new VBox(2);
        Label timeLabel = new Label(appointment.getTime());
        timeLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        timeLabel.setTextFill(Color.BLACK);
        Label durationLabel = new Label(appointment.getDuration());
        durationLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        durationLabel.setTextFill(Color.GRAY);
        timeDuration.getChildren().addAll(timeLabel, durationLabel);
        timeDuration.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(12, iconContainer, info, spacer, timeDuration);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        String originalRowStyle = "-fx-background-color: transparent;";
        row.setStyle(originalRowStyle);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle(originalRowStyle));

        return row;
    }

    private void updateActiveTab(Button clickedButton) {
        final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: normal; -fx-text-fill: #555555; -fx-padding: 8 20;";
        final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: 600;" +
                " -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 20;";
        final String hoverInactiveStyle = "-fx-background-color: #e0e0e0; -fx-font-weight: normal; -fx-text-fill: #555555; -fx-background-radius: 5; -fx-padding: 8 20;";
        if (activeTabButton != null) {
            activeTabButton.setStyle(inactiveStyle);
        }
        clickedButton.setStyle(activeStyle);
        activeTabButton = clickedButton;
        logger.debug("Active tab updated to: {}", clickedButton.getText());
    }

    private void deactivateActiveTab() {
        if (activeTabButton != null) {
            activeTabButton.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-text-fill: #555555; -fx-padding: 8 20;");
            activeTabButton = null;
            logger.debug("Active tab deactivated.");
        }
    }
    
    // createImageView method is completely removed from PsychologistDashboard as it's not used here anymore.

    // --- Public Setter Methods for Controller to Update UI ---

    public void setTotalPatientsCount(int count) {
        Platform.runLater(() -> {
            if (totalPatientsValueLbl != null) {
                totalPatientsValueLbl.setText(String.valueOf(count));
            } else {
                logger.warn("totalPatientsValueLbl is null when trying to set count. UI might not be fully initialized.");
            }
        });
    }

    public void setActiveChatsCount(int count) {
        Platform.runLater(() -> {
            if (activeChatsValueLbl != null) {
                activeChatsValueLbl.setText(String.valueOf(count));
            } else {
                logger.warn("activeChatsValueLbl is null when trying to set chat count. UI might not be fully initialized.");
            }
        });
    }

    public void setAppointmentsTodayCount(int count) {
        Platform.runLater(() -> {
            if (appointmentsTodayValueLbl != null) {
                appointmentsTodayValueLbl.setText(String.valueOf(count));
            } else {
                logger.warn("appointmentsTodayValueLbl is null when trying to set appointment count. UI might not be fully initialized.");
            }
        });
    }

    public void setCrisisAlertsCount(int count) {
        Platform.runLater(() -> {
            if (crisisAlertsValueLbl != null) {
                crisisAlertsValueLbl.setText(String.valueOf(count));
            } else {
                logger.warn("crisisAlertsValueLbl is null when trying to set crisis alert count. UI might not be fully initialized.");
            }
        });
    }

    public void setRecentPatientActivity(List<Patient> patients) {
        Platform.runLater(() -> {
            if (recentPatientActivityContainer != null) {
                recentPatientActivityContainer.getChildren().clear();
                if (patients.isEmpty()) {
                    recentPatientActivityContainer.getChildren().add(new Label("No recent patient activity."));
                } else {
                    patients.stream()
                            .limit(3)
                            .forEach(patient -> recentPatientActivityContainer.getChildren().add(createPatientActivityRow(patient)));
                }
            } else {
                logger.warn("recentPatientActivityContainer is null when trying to set recent patient activity. UI might not be fully initialized.");
            }
        });
    }

    public void setTodaysSchedule(List<Appointment> appointments) {
        Platform.runLater(() -> {
            if (todaysScheduleContainer != null) {
                todaysScheduleContainer.getChildren().clear();
                if (appointments.isEmpty()) {
                    todaysScheduleContainer.getChildren().add(new Label("No appointments today."));
                } else {
                    appointments.forEach(appointment -> todaysScheduleContainer.getChildren().add(createAppointmentRow(appointment)));
                }
            } else {
                logger.warn("todaysScheduleContainer is null when trying to set today's schedule. UI might not be fully initialized.");
            }
        });
    }

    public void setActiveCrisisAlerts(List<CrisisAlert> alerts) {
        Platform.runLater(() -> {
            if (crisisAlertsContainer != null) {
                crisisAlertsContainer.getChildren().clear();
                if (alerts.isEmpty()) {
                    crisisAlertsContainer.getChildren().add(new Label("No active crisis alerts."));
                } else {
                    alerts.forEach(alert -> crisisAlertsContainer.getChildren().add(createOverviewCrisisItem(alert)));
                }
            } else {
                logger.warn("crisisAlertsContainer is null when trying to set active crisis alerts. UI might not be fully initialized.");
            }
        });
    }

    // --- Methods for Controller Interaction ---

    public VBox getMainContentArea() {
        return mainContentArea;
    }

    public void setNavigationHandler(Consumer<String> handler) {
        this.navigationHandler = handler;
        logger.debug("Navigation handler set for dashboard.");
    }

    public void setCrisisRespondHandler(Consumer<CrisisAlert> handler) {
        this.crisisRespondHandler = handler;
        logger.debug("Crisis respond handler set for dashboard.");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setTherapistName(String name) {
        this.therapistName = name;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + name);
            logger.info("Welcome message updated to: {}", name);
        }
    }

    public void showSuccessMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            try { 
                alert.showAndWait();
            } catch (IllegalStateException e) {
                logger.error("Error showing success alert: Illegal state during showAndWait. Full error: {}", e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Unexpected error showing success alert: {}", e.getMessage(), e);
            }
        });
    }

    public void showErrorMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            try { 
                alert.showAndWait();
            } catch (IllegalStateException e) {
                logger.error("Error showing error alert: Illegal state during showAndWait. Full error: {}", e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Unexpected error showing error alert: {}", e.getMessage(), e);
            }
        });
    }
}