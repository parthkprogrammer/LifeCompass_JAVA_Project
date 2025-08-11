package com.lifecompass.view.Adminview; // Changed package to match dashboard's package

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Important: for returning UI elements
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane; // Retained if needed internally
import javafx.scene.control.Separator;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea; // For longer text inputs like protocols
import javafx.stage.Stage; // Needed for showAlert, dialogs

public class adminSettingView { // No longer extends Application

    private AdminDashboardView parentDashboard; // Reference to the main dashboard
    private StackPane settingsContentArea; // Where the different settings sections are displayed
    private Stage primaryStage; // To correctly show Alerts and Dialogs

    // --- UI Controls for various sections ---
    // 1. Account Management
    private TextField adminNameField;
    private TextField adminEmailField;

    // 2. Platform Moderation
    private CheckBox autoFilterKeywordsCheckBox;
    private TextArea flaggedKeywordsArea;
    private CheckBox enableManualReviewCheckBox;

    // 3. Emergency Protocols
    private TextArea emergencyProtocolArea;
    private CheckBox autoEscalateCrisisCheckBox;
    private TextField emergencyContactField;

    // 4. Data & Backup
    private Button runBackupNowBtn;
    private Button exportAllUserDataBtn;
    private Button purgeOldLogsBtn;

    // 5. System Preferences
    private ChoiceBox<String> adminLanguageChoice;
    private ChoiceBox<String> adminThemeChoice;
    private ChoiceBox<String> adminFontSizeChoice;

    // 6. Security & Access
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button logoutAllAdminDevicesBtn;
    private Button viewAdminLoginHistoryBtn;


    public adminSettingView(AdminDashboardView parentDashboard) {
        this.parentDashboard = parentDashboard;
        // The primaryStage is needed for alerts/dialogs, it should be passed from the dashboard
        // A common pattern is to pass Stage to the constructor if the component will show dialogs.
        // Assuming parentDashboard has a way to provide its primaryStage, or it's directly passed in.
        // For simplicity, let's assume parentDashboard can provide its primaryStage if needed.
        // Alternatively, if this component is initialized in the dashboard's start method, pass primaryStage:
        // admindashaboardView (admindashaboardView dashboardInstance, Stage stage) { ... this.primaryStage = stage; }
        // For now, let's assume parentDashboard has a getter for its primary stage if needed for sub-components
        // or ensure primaryStage is passed through here from its creation in admindashaboardView.
        // Let's modify the constructor to explicitly take Stage:
        // This constructor is designed to be called from admindashaboardView:
        // this.adminSettingsScreen = new adminSettingView(this.primaryStage, this);
    }

    // New constructor to take primaryStage directly (better practice for components showing dialogs)
    public adminSettingView(Stage primaryStage, AdminDashboardView parentDashboard) {
        this.primaryStage = primaryStage;
        this.parentDashboard = parentDashboard;
    }


    /**
     * Returns the root Node of the Admin Settings UI.
     * @return A Node representing the complete Admin Settings UI.
     */
    public Node getContent() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        mainLayout.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        // Header
        Label title = new Label("Admin Settings");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label("Configure platform-wide settings.");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.GRAY);

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

        HBox headerActions = new HBox(10, backToDashboardButton, new Region());
        HBox.setHgrow(headerActions.getChildren().get(1), Priority.ALWAYS);
        headerActions.setAlignment(Pos.CENTER_LEFT);

        mainLayout.getChildren().addAll(headerActions, title, subtitle);

        // Settings navigation buttons
        HBox navButtons = new HBox(10);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(10, 0, 10, 0));

        Button accountMgtBtn = createSettingNavButton("Account Management");
        Button moderationBtn = createSettingNavButton("Platform Moderation");
        Button emergencyBtn = createSettingNavButton("Emergency Protocols");
        Button dataBackupBtn = createSettingNavButton("Data & Backup");
        Button systemPrefBtn = createSettingNavButton("System Preferences");
        Button securityBtn = createSettingNavButton("Security & Access");

        accountMgtBtn.setOnAction(e -> showAccountManagement());
        moderationBtn.setOnAction(e -> showPlatformModeration());
        emergencyBtn.setOnAction(e -> showEmergencyProtocols());
        dataBackupBtn.setOnAction(e -> showDataAndBackup());
        systemPrefBtn.setOnAction(e -> showSystemPreferences());
        securityBtn.setOnAction(e -> showSecurityAndAccess());


        navButtons.getChildren().addAll(accountMgtBtn, moderationBtn, emergencyBtn, dataBackupBtn, systemPrefBtn, securityBtn);

        ScrollPane navScrollPane = new ScrollPane(navButtons);
        navScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        navScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        navScrollPane.setFitToHeight(true);
        navScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        mainLayout.getChildren().add(navScrollPane);


        // Content area for specific settings
        settingsContentArea = new StackPane();
        settingsContentArea.setPrefSize(Region.USE_COMPUTED_SIZE, 450); // Set a preferred height
        settingsContentArea.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 10; -fx-border-color: #eeeeee; -fx-border-width: 1; -fx-border-radius: 10;");
        VBox.setVgrow(settingsContentArea, Priority.ALWAYS);
        mainLayout.getChildren().add(settingsContentArea);

        showAccountManagement(); // Display first section by default

        // Universal Save Changes button for consistency
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        saveButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        applyHoverEffect(saveButton, "#388E3C", "#4CAF50");
        saveButton.setOnAction(e -> handleSaveChanges());

        actionButtons.getChildren().add(saveButton);
        mainLayout.getChildren().add(actionButtons);

        return mainLayout;
    }

    private Button createSettingNavButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-padding: 8 15; -fx-background-radius: 5;");
        applyHoverEffect(btn, "#e0e0e0", "transparent");
        return btn;
    }

    // --- SECTION 1: Account Management ---
    private void showAccountManagement() {
        settingsContentArea.getChildren().clear();

        GridPane grid = createSettingsGrid();

        Label nameLabel = new Label("Admin Name:");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        adminNameField = new TextField("Main Admin"); // Placeholder data
        adminNameField.setPromptText("Enter admin name");
        adminNameField.setPrefWidth(250);
        grid.add(nameLabel, 0, 0);
        grid.add(adminNameField, 1, 0);

        Label emailLabel = new Label("Admin Email:");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        adminEmailField = new TextField("admin@lifecompass.com"); // Placeholder data
        adminEmailField.setPromptText("Enter admin email");
        adminEmailField.setPrefWidth(250);
        adminEmailField.setDisable(true); // Email usually not editable directly
        grid.add(emailLabel, 0, 1);
        grid.add(adminEmailField, 1, 1);

        settingsContentArea.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
    }

    // --- SECTION 2: Platform Moderation ---
    private void showPlatformModeration() {
        settingsContentArea.getChildren().clear();

        GridPane grid = createSettingsGrid();

        Label autoFilterLabel = new Label("Auto-filter harmful keywords:");
        autoFilterLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        autoFilterKeywordsCheckBox = new CheckBox();
        autoFilterKeywordsCheckBox.setSelected(true);
        grid.add(autoFilterLabel, 0, 0);
        grid.add(autoFilterKeywordsCheckBox, 1, 0);

        Label keywordsLabel = new Label("Flagged Keywords:");
        keywordsLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        flaggedKeywordsArea = new TextArea("suicide, kill, harm, abusive, hate"); // Example keywords
        flaggedKeywordsArea.setPromptText("Enter keywords separated by commas");
        flaggedKeywordsArea.setWrapText(true);
        flaggedKeywordsArea.setPrefRowCount(3);
        flaggedKeywordsArea.setPrefWidth(300);
        grid.add(keywordsLabel, 0, 1);
        grid.add(flaggedKeywordsArea, 1, 1);

        Label manualReviewLabel = new Label("Enable Manual Review of Flagged Content:");
        manualReviewLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        enableManualReviewCheckBox = new CheckBox();
        enableManualReviewCheckBox.setSelected(true);
        grid.add(manualReviewLabel, 0, 2);
        grid.add(enableManualReviewCheckBox, 1, 2);

        settingsContentArea.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
    }

    // --- SECTION 3: Emergency Protocols ---
    private void showEmergencyProtocols() {
        settingsContentArea.getChildren().clear();

        GridPane grid = createSettingsGrid();

        Label protocolLabel = new Label("Emergency Protocol:");
        protocolLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        emergencyProtocolArea = new TextArea("1. Contact local authorities (Police/Ambulance).\n2. Attempt direct user contact.\n3. Notify assigned therapist.");
        emergencyProtocolArea.setPromptText("Define steps for crisis response");
        emergencyProtocolArea.setWrapText(true);
        emergencyProtocolArea.setPrefRowCount(5);
        emergencyProtocolArea.setPrefWidth(350);
        grid.add(protocolLabel, 0, 0);
        grid.add(emergencyProtocolArea, 1, 0);

        Label autoEscalateLabel = new Label("Auto-escalate Crisis to Emergency Services:");
        autoEscalateLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        autoEscalateCrisisCheckBox = new CheckBox();
        autoEscalateCrisisCheckBox.setSelected(false);
        grid.add(autoEscalateLabel, 0, 1);
        grid.add(autoEscalateCrisisCheckBox, 1, 1);

        Label contactLabel = new Label("Emergency Contact (Internal):");
        contactLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        emergencyContactField = new TextField("support@lifecompass.com, +91-9988776655");
        emergencyContactField.setPromptText("Emails or Phone numbers");
        emergencyContactField.setPrefWidth(350);
        grid.add(contactLabel, 0, 2);
        grid.add(emergencyContactField, 1, 2);

        settingsContentArea.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
    }

    // --- SECTION 4: Data & Backup ---
    private void showDataAndBackup() {
        settingsContentArea.getChildren().clear();

        VBox dataBox = new VBox(15);
        dataBox.setPadding(new Insets(20));
        dataBox.setAlignment(Pos.TOP_LEFT);

        Label header = new Label("Platform Data Management");
        header.setFont(Font.font("System", FontWeight.BOLD, 16));
        dataBox.getChildren().addAll(header, new Separator());

        runBackupNowBtn = new Button("Run Manual Database Backup Now");
        runBackupNowBtn.setStyle("-fx-background-color: #42a5f5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
        runBackupNowBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(runBackupNowBtn, "#2196F3", "#42a5f5");
        runBackupNowBtn.setOnAction(e -> System.out.println("Initiating full database backup... (Not implemented)"));
        dataBox.getChildren().add(runBackupNowBtn);

        exportAllUserDataBtn = new Button("Export All User Data (GDPR/Compliance)");
        exportAllUserDataBtn.setStyle("-fx-background-color: #66bb6a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
        exportAllUserDataBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(exportAllUserDataBtn, "#43a047", "#66bb6a");
        exportAllUserDataBtn.setOnAction(e -> System.out.println("Exporting all user data... (Not implemented)"));
        dataBox.getChildren().add(exportAllUserDataBtn);

        purgeOldLogsBtn = new Button("Purge Old System Logs (Older than 1 year)");
        purgeOldLogsBtn.setStyle("-fx-background-color: #ff7043; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
        purgeOldLogsBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(purgeOldLogsBtn, "#f4511e", "#ff7043");
        purgeOldLogsBtn.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Purge Logs");
            alert.setHeaderText("Are you sure you want to purge old system logs?");
            alert.setContentText("This action is irreversible.");
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    System.out.println("Purging old system logs... (Not implemented)");
                }
            });
        });
        dataBox.getChildren().add(purgeOldLogsBtn);

        settingsContentArea.getChildren().add(dataBox);
        StackPane.setAlignment(dataBox, Pos.TOP_LEFT);
    }

    // --- SECTION 5: System Preferences ---
    private void showSystemPreferences() {
        settingsContentArea.getChildren().clear();

        GridPane grid = createSettingsGrid();

        Label languageLabel = new Label("Default Language:");
        languageLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        adminLanguageChoice = new ChoiceBox<>();
        adminLanguageChoice.getItems().addAll("English (US)", "English (UK)", "Hindi", "Marathi");
        adminLanguageChoice.setValue("English (US)");
        adminLanguageChoice.setStyle("-fx-font-size: 13px;");
        grid.add(languageLabel, 0, 0);
        grid.add(adminLanguageChoice, 1, 0);

        Label themeLabel = new Label("Default Theme:");
        themeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        adminThemeChoice = new ChoiceBox<>();
        adminThemeChoice.getItems().addAll("Light", "Dark");
        adminThemeChoice.setValue("Light");
        adminThemeChoice.setStyle("-fx-font-size: 13px;");
        grid.add(themeLabel, 0, 1);
        grid.add(adminThemeChoice, 1, 1);

        Label fontSizeLabel = new Label("Global Interface Font Size:");
        fontSizeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        adminFontSizeChoice = new ChoiceBox<>();
        adminFontSizeChoice.getItems().addAll("Small", "Medium", "Large");
        adminFontSizeChoice.setValue("Medium");
        adminFontSizeChoice.setStyle("-fx-font-size: 13px;");
        grid.add(fontSizeLabel, 0, 2);
        grid.add(adminFontSizeChoice, 1, 2);

        settingsContentArea.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
    }

    // --- SECTION 6: Security & Access (Admin-specific) ---
    private void showSecurityAndAccess() {
        settingsContentArea.getChildren().clear();

        GridPane securityGrid = createSettingsGrid();

        Label changePasswordHeader = new Label("Change Admin Password");
        changePasswordHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
        GridPane.setColumnSpan(changePasswordHeader, 2);
        securityGrid.add(changePasswordHeader, 0, 0);

        Label currentPassLabel = new Label("Current Password:");
        currentPassLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        currentPasswordField.setPrefWidth(250);
        securityGrid.add(currentPassLabel, 0, 1);
        securityGrid.add(currentPasswordField, 1, 1);

        Label newPassLabel = new Label("New Password:");
        newPassLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setPrefWidth(250);
        securityGrid.add(newPassLabel, 0, 2);
        securityGrid.add(newPasswordField, 1, 2);

        Label confirmPassLabel = new Label("Confirm New Password:");
        confirmPassLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.setPrefWidth(250);
        securityGrid.add(confirmPassLabel, 0, 3);
        securityGrid.add(confirmPasswordField, 1, 3);

        Button updatePasswordBtn = new Button("Update Password");
        updatePasswordBtn.setStyle("-fx-background-color: #f9a825; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        updatePasswordBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(updatePasswordBtn, "#ef6c00", "#f9a825");
        updatePasswordBtn.setOnAction(e -> handleChangePassword());
        GridPane.setColumnSpan(updatePasswordBtn, 2);
        GridPane.setHalignment(updatePasswordBtn, HPos.RIGHT);
        securityGrid.add(updatePasswordBtn, 0, 4);

        // Separator
        Node separator1 = new Separator();
        GridPane.setColumnSpan(separator1, 2);
        GridPane.setMargin(separator1, new Insets(10, 0, 10, 0));
        securityGrid.add(separator1, 0, 5);

        Label twoFactorHeader = new Label("Two-Factor Authentication (2FA)");
        twoFactorHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
        GridPane.setColumnSpan(twoFactorHeader, 2);
        securityGrid.add(twoFactorHeader, 0, 6);

        Label twoFactorStatus = new Label("Status: Disabled");
        twoFactorStatus.setFont(Font.font("System", FontWeight.NORMAL, 13));
        securityGrid.add(twoFactorStatus, 0, 7);

        Button setup2FABtn = new Button("Setup 2FA");
        setup2FABtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        setup2FABtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(setup2FABtn, "#1565C0", "#1976d2");
        setup2FABtn.setOnAction(e -> System.out.println("Setup 2FA clicked"));
        GridPane.setColumnSpan(setup2FABtn, 2);
        GridPane.setHalignment(setup2FABtn, HPos.RIGHT);
        securityGrid.add(setup2FABtn, 0, 8);

        // Separator
        Node separator2 = new Separator();
        GridPane.setColumnSpan(separator2, 2);
        GridPane.setMargin(separator2, new Insets(10, 0, 10, 0));
        securityGrid.add(separator2, 0, 9);

        logoutAllAdminDevicesBtn = new Button("Logout from All Devices");
        logoutAllAdminDevicesBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        logoutAllAdminDevicesBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(logoutAllAdminDevicesBtn, "#d32f2f", "#ef5350");
        logoutAllAdminDevicesBtn.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Logout All Devices");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This will log you out from all active sessions on other devices.");
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    System.out.println("Logging out from all devices (Not implemented)");
                }
            });
        });
        GridPane.setColumnSpan(logoutAllAdminDevicesBtn, 2);
        GridPane.setHalignment(logoutAllAdminDevicesBtn, HPos.LEFT);
        securityGrid.add(logoutAllAdminDevicesBtn, 0, 10);

        viewAdminLoginHistoryBtn = new Button("View Login History");
        viewAdminLoginHistoryBtn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15;");
        viewAdminLoginHistoryBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        applyHoverEffect(viewAdminLoginHistoryBtn, "#b0b0b0", "#cccccc");
        viewAdminLoginHistoryBtn.setOnAction(e -> System.out.println("Viewing login history (Not implemented)"));
        GridPane.setColumnSpan(viewAdminLoginHistoryBtn, 2);
        GridPane.setHalignment(viewAdminLoginHistoryBtn, HPos.LEFT);
        securityGrid.add(viewAdminLoginHistoryBtn, 0, 11);

        settingsContentArea.getChildren().add(securityGrid);
        StackPane.setAlignment(securityGrid, Pos.TOP_LEFT);
    }

    // --- Utility Methods ---

    private GridPane createSettingsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.TOP_LEFT);
        return grid;
    }

    // --- Functionality Handlers ---

    private void handleSaveChanges() {
        System.out.println("--- Attempting to Save All Admin Settings ---");

        // Account Management
        System.out.println("\nAccount Management:");
        System.out.println("Admin Name: " + (adminNameField != null ? adminNameField.getText() : "N/A"));
        System.out.println("Admin Email: " + (adminEmailField != null ? adminEmailField.getText() : "N/A"));

        // Platform Moderation
        System.out.println("\nPlatform Moderation:");
        System.out.println("Auto-filter Keywords: " + (autoFilterKeywordsCheckBox != null ? autoFilterKeywordsCheckBox.isSelected() : "N/A"));
        System.out.println("Flagged Keywords: " + (flaggedKeywordsArea != null ? flaggedKeywordsArea.getText() : "N/A"));
        System.out.println("Enable Manual Review: " + (enableManualReviewCheckBox != null ? enableManualReviewCheckBox.isSelected() : "N/A"));

        // Emergency Protocols
        System.out.println("\nEmergency Protocols:");
        System.out.println("Emergency Protocol: " + (emergencyProtocolArea != null ? emergencyProtocolArea.getText() : "N/A"));
        System.out.println("Auto-escalate Crisis: " + (autoEscalateCrisisCheckBox != null ? autoEscalateCrisisCheckBox.isSelected() : "N/A"));
        System.out.println("Emergency Contact: " + (emergencyContactField != null ? emergencyContactField.getText() : "N/A"));

        // System Preferences
        System.out.println("\nSystem Preferences:");
        System.out.println("Language: " + (adminLanguageChoice != null ? adminLanguageChoice.getValue() : "N/A"));
        System.out.println("Theme: " + (adminThemeChoice != null ? adminThemeChoice.getValue() : "N/A"));
        System.out.println("Font Size: " + (adminFontSizeChoice != null ? adminFontSizeChoice.getValue() : "N/A"));

        showAlert(AlertType.INFORMATION, "Admin Settings Saved", "Admin settings have been saved successfully!");
    }

    private void handleChangePassword() {
        String currentPass = (currentPasswordField != null ? currentPasswordField.getText() : "");
        String newPass = (newPasswordField != null ? newPasswordField.getText() : "");
        String confirmPass = (confirmPasswordField != null ? confirmPasswordField.getText() : "");

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(AlertType.WARNING, "Missing Fields", "Please fill in all password fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(AlertType.ERROR, "Password Mismatch", "New password and confirm password do not match.");
            return;
        }

        if (newPass.length() < 8) { // Admin passwords should be strong
            showAlert(AlertType.WARNING, "Weak Password", "New password must be at least 8 characters long.");
            return;
        }

        System.out.println("Attempting to change admin password from '" + currentPass + "' to '" + newPass + "'");
        showAlert(AlertType.INFORMATION, "Password Change", "Admin password change request sent. (Not validated/persisted in this demo)");

        if (currentPasswordField != null) currentPasswordField.clear();
        if (newPasswordField != null) newPasswordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Use the primary stage as the owner for the alert
        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }
        alert.showAndWait();
    }

    private void applyHoverEffect(Region node, String hoverColor, String originalColor) {
        String currentStyle = node.getStyle();
        node.setOnMouseEntered(e -> {
            if (!node.getStyle().contains(hoverColor)) {
                 node.setStyle(currentStyle.replace(originalColor, hoverColor));
            }
        });
        node.setOnMouseExited(e -> {
            if (node.getStyle().contains(hoverColor)) {
                node.setStyle(currentStyle);
            }
        });
    }
}