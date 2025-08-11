package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.PsychologistSettings;
import javafx.application.Platform;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistSettingsScreen {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistSettingsScreen.class);

    private Runnable backToDashboardAction;
    private Stage parentStage;
    
    private StackPane rootContentPane;
    private VBox mainLayoutVBox;
    private StackPane loadingOverlayRegion;
    private ProgressIndicator progressIndicator;

    private Consumer<PsychologistSettings> saveHandler;
    private TriConsumer<String, String, String> changePasswordHandler;

    private ChoiceBox<String> sessionDurationChoice;
    private CheckBox autoApproveAppointmentsCheckBox;
    private ChoiceBox<String> preferredModeChoice;
    private CheckBox allowReschedulingCheckBox;

    private CheckBox receiveCrisisAlertsCheckBox;
    private ChoiceBox<String> alertModeChoice;
    private CheckBox crisisProtocolShortcutCheckBox;

    private ChoiceBox<String> accessUserJournalsChoice;
    private ChoiceBox<String> moodGraphVisibilityChoice;
    private ChoiceBox<String> autoDeleteDataChoice;

    private CheckBox appointmentRemindersCheckBox;
    private CheckBox sessionFeedbackRequestsCheckBox;
    private CheckBox adminAnnouncementsCheckBox;

    private Button manageCbtPromptsBtn;
    private Button saveNoteTemplatesBtn;
    private Button customizeReflectionQuestionsBtn;

    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button logoutAllDevicesBtn;
    private Button viewLoginHistoryBtn;
    private Button updatePasswordBtn;

    private ChoiceBox<String> languageChoice;
    private ChoiceBox<String> themeChoice;
    private ChoiceBox<String> fontSizeChoice;

    private ScrollPane settingsContentScrollPane; 


    public PsychologistSettingsScreen(Runnable backToDashboardAction, Stage parentStage) {
        this.backToDashboardAction = backToDashboardAction;
        this.parentStage = parentStage;
        logger.info("PsychologistSettingsScreen initialized. Received parentStage: {}", parentStage);
    }

    public Node getContent() {
        if (rootContentPane == null) {
            mainLayoutVBox = new VBox(20);
            mainLayoutVBox.setPadding(new Insets(20));
            mainLayoutVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
            VBox.setVgrow(mainLayoutVBox, Priority.ALWAYS);

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label title = new Label("Settings");
            title.setFont(Font.font("System", FontWeight.BOLD, 20));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button backToDashboardButton = new Button("Back to Dashboard");
            backToDashboardButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 5; -fx-padding: 8 15;");
            backToDashboardButton.setOnAction(e -> {
                if (backToDashboardAction != null) {
                    backToDashboardAction.run();
                }
            });
            header.getChildren().addAll(title, spacer, backToDashboardButton);
            mainLayoutVBox.getChildren().add(header);

            HBox navButtons = new HBox(10);
            navButtons.setPadding(new Insets(10, 0, 10, 0));
            navButtons.setAlignment(Pos.CENTER);
            
            Button sessionBtn = createSettingNavButton("Session Preferences");
            sessionBtn.setOnAction(e -> showSettingsSection(createSessionPreferencesContent()));
            
            Button crisisNotifBtn = createSettingNavButton("Crisis Notifications");
            crisisNotifBtn.setOnAction(e -> showSettingsSection(createCrisisNotificationSettingsContent()));
            
            Button privacyBtn = createSettingNavButton("Privacy & Data");
            privacyBtn.setOnAction(e -> showSettingsSection(createPrivacyAndDataControlContent()));
            
            Button notificationPrefBtn = createSettingNavButton("Notification Preferences");
            notificationPrefBtn.setOnAction(e -> showSettingsSection(createNotificationPreferencesContent()));
            
            Button customToolsBtn = createSettingNavButton("Custom Therapy Tools");
            customToolsBtn.setOnAction(e -> showSettingsSection(createCustomTherapyToolsContent()));
            
            Button securityBtn = createSettingNavButton("Security & Access");
            securityBtn.setOnAction(e -> showSettingsSection(createSecurityAndAccessContent()));
            
            Button appPrefBtn = createSettingNavButton("App Preferences");
            appPrefBtn.setOnAction(e -> showSettingsSection(createAppPreferencesContent()));

            navButtons.getChildren().addAll(
                sessionBtn, crisisNotifBtn, privacyBtn, notificationPrefBtn,
                customToolsBtn, securityBtn, appPrefBtn
            );
            mainLayoutVBox.getChildren().add(navButtons);

            settingsContentScrollPane = new ScrollPane();
            settingsContentScrollPane.setFitToWidth(true);
            settingsContentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            settingsContentScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            VBox.setVgrow(settingsContentScrollPane, Priority.ALWAYS);
            
            showSettingsSection(createSessionPreferencesContent());

            mainLayoutVBox.getChildren().add(settingsContentScrollPane);

            Button saveButton = new Button("Save Changes");
            saveButton.setFont(Font.font("System", FontWeight.BOLD, 14));
            saveButton.setTextFill(Color.WHITE);
            saveButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8; -fx-padding: 10 20;");
            saveButton.setOnAction(e -> handleSaveChanges());

            HBox actionButtons = new HBox(10, saveButton);
            actionButtons.setAlignment(Pos.CENTER_RIGHT);
            actionButtons.setPadding(new Insets(10, 0, 0, 0));
            mainLayoutVBox.getChildren().add(actionButtons);
            
            progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxSize(50,50);
            
            loadingOverlayRegion = new StackPane(progressIndicator);
            loadingOverlayRegion.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);"); 
            loadingOverlayRegion.setAlignment(Pos.CENTER);
            loadingOverlayRegion.setManaged(false); 
            loadingOverlayRegion.setVisible(false); 

            rootContentPane = new StackPane(mainLayoutVBox, loadingOverlayRegion);
        }
        return rootContentPane;
    }
    
    private Node createSessionPreferencesContent() {
        GridPane grid = createSettingsGrid();

        sessionDurationChoice = new ChoiceBox<>();
        sessionDurationChoice.getItems().addAll("30 min", "45 min", "60 min", "90 min");
        sessionDurationChoice.setValue("60 min");
        sessionDurationChoice.setMaxWidth(Double.MAX_VALUE);

        autoApproveAppointmentsCheckBox = new CheckBox("Auto-approve New Appointments");
        preferredModeChoice = new ChoiceBox<>();
        preferredModeChoice.getItems().addAll("Online", "Offline", "Both");
        preferredModeChoice.setValue("Both");
        preferredModeChoice.setMaxWidth(Double.MAX_VALUE);
        allowReschedulingCheckBox = new CheckBox("Allow Patients to Reschedule");

        grid.add(new Label("Default Session Duration:"), 0, 0);
        grid.add(sessionDurationChoice, 1, 0);
        grid.add(autoApproveAppointmentsCheckBox, 0, 1, 2, 1);
        grid.add(new Label("Preferred Mode of Therapy:"), 0, 2);
        grid.add(preferredModeChoice, 1, 2);
        grid.add(allowReschedulingCheckBox, 0, 3, 2, 1);

        return wrapInSection("Session Preferences", grid);
    }

    private Node createCrisisNotificationSettingsContent() {
        GridPane grid = createSettingsGrid();

        receiveCrisisAlertsCheckBox = new CheckBox("Receive Crisis Alerts");
        alertModeChoice = new ChoiceBox<>();
        alertModeChoice.getItems().addAll("Email", "SMS", "App Notification", "All");
        alertModeChoice.setValue("App Notification");
        alertModeChoice.setMaxWidth(Double.MAX_VALUE);
        crisisProtocolShortcutCheckBox = new CheckBox("Enable Crisis Protocol Shortcut");

        grid.add(receiveCrisisAlertsCheckBox, 0, 0, 2, 1);
        grid.add(new Label("Alert Mode:"), 0, 1);
        grid.add(alertModeChoice, 1, 1);
        grid.add(crisisProtocolShortcutCheckBox, 0, 2, 2, 1);

        return wrapInSection("Crisis Notification Settings", grid);
    }
    
    private Node createPrivacyAndDataControlContent() {
        GridPane grid = createSettingsGrid();

        accessUserJournalsChoice = new ChoiceBox<>();
        accessUserJournalsChoice.getItems().addAll("Full Access", "Read-Only", "No Access");
        accessUserJournalsChoice.setValue("Read-Only");
        accessUserJournalsChoice.setMaxWidth(Double.MAX_VALUE);

        moodGraphVisibilityChoice = new ChoiceBox<>();
        moodGraphVisibilityChoice.getItems().addAll("Visible to All Patients", "Visible to Assigned Patients Only", "Private");
        moodGraphVisibilityChoice.setValue("Visible to Assigned Patients Only");
        moodGraphVisibilityChoice.setMaxWidth(Double.MAX_VALUE);

        autoDeleteDataChoice = new ChoiceBox<>();
        autoDeleteDataChoice.getItems().addAll("Never", "After 1 Year", "After 3 Years", "After 5 Years");
        autoDeleteDataChoice.setValue("Never");
        autoDeleteDataChoice.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Access User Journals:"), 0, 0);
        grid.add(accessUserJournalsChoice, 1, 0);
        grid.add(new Label("Mood Graph Visibility:"), 0, 1);
        grid.add(moodGraphVisibilityChoice, 1, 1);
        grid.add(new Label("Auto-Delete Patient Data:"), 0, 2);
        grid.add(autoDeleteDataChoice, 1, 2);

        return wrapInSection("Privacy & Data Control", grid);
    }
    
    private Node createNotificationPreferencesContent() {
        VBox preferencesBox = new VBox(15);
        preferencesBox.setPadding(new Insets(10));
        preferencesBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8;");
        
        Label sectionTitle = new Label("Notification Preferences");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        preferencesBox.getChildren().add(sectionTitle);

        appointmentRemindersCheckBox = new CheckBox("Appointment Reminders");
        sessionFeedbackRequestsCheckBox = new CheckBox("Session Feedback Requests");
        adminAnnouncementsCheckBox = new CheckBox("Admin Announcements");
        
        preferencesBox.getChildren().addAll(appointmentRemindersCheckBox, sessionFeedbackRequestsCheckBox, adminAnnouncementsCheckBox);
        return wrapInSection("Notification Preferences", preferencesBox);
    }

    private Node createCustomTherapyToolsContent() {
        VBox toolsBox = new VBox(15);
        toolsBox.setPadding(new Insets(10));
        toolsBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8;");

        Label sectionTitle = new Label("Custom Therapy Tools");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        toolsBox.getChildren().add(sectionTitle);

        manageCbtPromptsBtn = new Button("Manage CBT Prompts");
        manageCbtPromptsBtn.setMaxWidth(Double.MAX_VALUE);
        manageCbtPromptsBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        saveNoteTemplatesBtn = new Button("Save Note Templates");
        saveNoteTemplatesBtn.setMaxWidth(Double.MAX_VALUE);
        saveNoteTemplatesBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        customizeReflectionQuestionsBtn = new Button("Customize Reflection Questions");
        customizeReflectionQuestionsBtn.setMaxWidth(Double.MAX_VALUE);
        customizeReflectionQuestionsBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        toolsBox.getChildren().addAll(manageCbtPromptsBtn, saveNoteTemplatesBtn, customizeReflectionQuestionsBtn);
        return wrapInSection("Custom Therapy Tools", toolsBox);
    }

    private Node createSecurityAndAccessContent() {
        GridPane securityGrid = createSettingsGrid();

        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current password");
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        
        updatePasswordBtn = new Button("Update Password");
        updatePasswordBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        updatePasswordBtn.setOnAction(e -> handleChangePassword());

        logoutAllDevicesBtn = new Button("Logout All Devices");
        logoutAllDevicesBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        viewLoginHistoryBtn = new Button("View Login History");
        viewLoginHistoryBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");


        securityGrid.add(new Label("Change Password:"), 0, 0);
        securityGrid.add(currentPasswordField, 1, 0);
        securityGrid.add(new Label("New Password:"), 0, 1);
        securityGrid.add(newPasswordField, 1, 1);
        securityGrid.add(new Label("Confirm New Password:"), 0, 2);
        securityGrid.add(confirmPasswordField, 1, 2);
        securityGrid.add(updatePasswordBtn, 0, 3, 2, 1);

        securityGrid.add(new Separator(), 0, 4, 2, 1);

        securityGrid.add(logoutAllDevicesBtn, 0, 5, 2, 1);
        securityGrid.add(viewLoginHistoryBtn, 0, 6, 2, 1);

        return wrapInSection("Security & Access", securityGrid);
    }

    private Node createAppPreferencesContent() {
        GridPane grid = createSettingsGrid();

        languageChoice = new ChoiceBox<>();
        languageChoice.getItems().addAll("English (US)", "English (UK)", "Hindi", "Marathi");
        languageChoice.setValue("English (US)");
        languageChoice.setMaxWidth(Double.MAX_VALUE);

        themeChoice = new ChoiceBox<>();
        themeChoice.getItems().addAll("Light", "Dark");
        themeChoice.setValue("Light");
        themeChoice.setMaxWidth(Double.MAX_VALUE);

        fontSizeChoice = new ChoiceBox<>();
        fontSizeChoice.getItems().addAll("Small", "Medium", "Large");
        fontSizeChoice.setValue("Medium");
        fontSizeChoice.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Language:"), 0, 0);
        grid.add(languageChoice, 1, 0);
        grid.add(new Label("Theme:"), 0, 1);
        grid.add(themeChoice, 1, 1);
        grid.add(new Label("Interface Font Size:"), 0, 2);
        grid.add(fontSizeChoice, 1, 2);

        return wrapInSection("App Preferences", grid);
    }

    private void handleSaveChanges() {
        logger.info("Attempting to save settings changes.");
        if (saveHandler != null) {
            PsychologistSettings updatedSettings = new PsychologistSettings();
            
            if (sessionDurationChoice != null) updatedSettings.setSessionDuration(sessionDurationChoice.getValue());
            if (autoApproveAppointmentsCheckBox != null) updatedSettings.setAutoApproveAppointments(autoApproveAppointmentsCheckBox.isSelected());
            if (preferredModeChoice != null) updatedSettings.setPreferredMode(preferredModeChoice.getValue());
            if (allowReschedulingCheckBox != null) updatedSettings.setAllowRescheduling(allowReschedulingCheckBox.isSelected());
            if (receiveCrisisAlertsCheckBox != null) updatedSettings.setReceiveCrisisAlerts(receiveCrisisAlertsCheckBox.isSelected());
            if (alertModeChoice != null) updatedSettings.setAlertMode(alertModeChoice.getValue());
            if (crisisProtocolShortcutCheckBox != null) updatedSettings.setCrisisProtocolShortcut(crisisProtocolShortcutCheckBox.isSelected());
            if (accessUserJournalsChoice != null) updatedSettings.setAccessUserJournals(accessUserJournalsChoice.getValue());
            if (moodGraphVisibilityChoice != null) updatedSettings.setMoodGraphVisibility(moodGraphVisibilityChoice.getValue());
            if (autoDeleteDataChoice != null) updatedSettings.setAutoDeleteData(autoDeleteDataChoice.getValue());
            if (appointmentRemindersCheckBox != null) updatedSettings.setAppointmentReminders(appointmentRemindersCheckBox.isSelected());
            if (sessionFeedbackRequestsCheckBox != null) updatedSettings.setSessionFeedbackRequests(sessionFeedbackRequestsCheckBox.isSelected());
            if (adminAnnouncementsCheckBox != null) updatedSettings.setAdminAnnouncements(adminAnnouncementsCheckBox.isSelected());
            if (languageChoice != null) updatedSettings.setLanguage(languageChoice.getValue());
            if (themeChoice != null) updatedSettings.setTheme(themeChoice.getValue());
            if (fontSizeChoice != null) updatedSettings.setFontSize(fontSizeChoice.getValue());
            
            saveHandler.accept(updatedSettings);
            logger.info("Settings changes collected and passed to save handler.");
        } else {
            logger.warn("Save handler is null. Settings not saved.");
            showError("Save functionality not available. Please contact support.");
        }
    }

    private void handleChangePassword() {
        logger.info("Attempting password change.");
        if (changePasswordHandler != null) {
            String currentPass = (currentPasswordField != null) ? currentPasswordField.getText() : "";
            String newPass = (newPasswordField != null) ? newPasswordField.getText() : "";
            String confirmPass = (confirmPasswordField != null) ? confirmPasswordField.getText() : "";
            
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showError("All password fields must be filled.");
                logger.warn("Password change failed: fields are empty.");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showError("New passwords do not match.");
                logger.warn("Password change failed: new passwords do not match.");
                return;
            }
            if (newPass.length() < 6) {
                showError("New password must be at least 6 characters long.");
                logger.warn("Password change failed: new password too short.");
                return;
            }

            changePasswordHandler.accept(currentPass, newPass, confirmPass);

            if (currentPasswordField != null) currentPasswordField.clear();
            if (newPasswordField != null) newPasswordField.clear();
            if (confirmPasswordField != null) confirmPasswordField.clear();
        } else {
            logger.warn("Change password handler is null. Password change not processed.");
            showError("Password change functionality not available. Please contact support.");
        }
    }
    
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public void populateSettings(PsychologistSettings settings) {
        logger.info("Populating settings view with data.");
        if (settings == null) {
            logger.warn("Provided settings object is null. Cannot populate view.");
            return;
        }
        
        if (sessionDurationChoice != null) sessionDurationChoice.setValue(settings.getSessionDuration());
        if (autoApproveAppointmentsCheckBox != null) autoApproveAppointmentsCheckBox.setSelected(settings.isAutoApproveAppointments());
        if (preferredModeChoice != null) preferredModeChoice.setValue(settings.getPreferredMode());
        if (allowReschedulingCheckBox != null) allowReschedulingCheckBox.setSelected(settings.isAllowRescheduling());
        if (receiveCrisisAlertsCheckBox != null) receiveCrisisAlertsCheckBox.setSelected(settings.isReceiveCrisisAlerts());
        if (alertModeChoice != null) alertModeChoice.setValue(settings.getAlertMode());
        if (crisisProtocolShortcutCheckBox != null) crisisProtocolShortcutCheckBox.setSelected(settings.isCrisisProtocolShortcut());
        if (accessUserJournalsChoice != null) accessUserJournalsChoice.setValue(settings.getAccessUserJournals());
        if (moodGraphVisibilityChoice != null) moodGraphVisibilityChoice.setValue(settings.getMoodGraphVisibility());
        if (autoDeleteDataChoice != null) autoDeleteDataChoice.setValue(settings.getAutoDeleteData());
        if (appointmentRemindersCheckBox != null) appointmentRemindersCheckBox.setSelected(settings.isAppointmentReminders());
        if (sessionFeedbackRequestsCheckBox != null) sessionFeedbackRequestsCheckBox.setSelected(settings.isSessionFeedbackRequests());
        if (adminAnnouncementsCheckBox != null) adminAnnouncementsCheckBox.setSelected(settings.isAdminAnnouncements());
        if (languageChoice != null) languageChoice.setValue(settings.getLanguage());
        if (themeChoice != null) settings.setTheme(themeChoice.getValue());
        if (fontSizeChoice != null) fontSizeChoice.setValue(settings.getFontSize());
    }

    public void setSaveHandler(Consumer<PsychologistSettings> handler) {
        this.saveHandler = handler;
        logger.debug("Save handler set for settings screen.");
    }

    public void setChangePasswordHandler(TriConsumer<String, String, String> handler) {
        this.changePasswordHandler = handler;
        logger.debug("Change password handler set for settings screen.");
    }
    
    public void showLoading(boolean isLoading) {
        logger.info("Showing loading state: {}", isLoading);
        if (rootContentPane == null) {
             logger.warn("rootContentPane is null. Cannot show loading indicator effectively.");
             return;
        }

        if (isLoading) {
            progressIndicator.setVisible(true); 
            loadingOverlayRegion.setVisible(true); 
            loadingOverlayRegion.setManaged(true); 
            mainLayoutVBox.setDisable(true); 
        } else {
            progressIndicator.setVisible(false); 
            loadingOverlayRegion.setVisible(false); 
            loadingOverlayRegion.setManaged(false); 
            mainLayoutVBox.setDisable(false); 
        }
    }

    public void showError(String message) {
        logger.error("Displaying error in PsychologistSettingsScreen: {}", message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            if (parentStage != null) {
                alert.initOwner(parentStage);
            } else {
                logger.warn("parentStage is null. Alert will not have an owner and may appear in the background.");
            }
            alert.setTitle("Error");
            alert.setHeaderText("Settings Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
        showLoading(false);
    }

    public void showSuccess(String message) {
        logger.info("Displaying success in PsychologistSettingsScreen: {}", message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            if (parentStage != null) {
                alert.initOwner(parentStage);
            } else {
                logger.warn("parentStage is null. Alert will not have an owner and may appear in the background.");
            }
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
        showLoading(false);
    }

    private Node wrapInSection(String title, Node content) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        section.getChildren().addAll(sectionTitle, new Separator(), content);
        return section;
    }

    private void showSettingsSection(Node sectionContent) {
        if (settingsContentScrollPane != null) {
            settingsContentScrollPane.setContent(sectionContent);
            settingsContentScrollPane.setVvalue(0.0);
        }
    }
    
    private Button createSettingNavButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: normal; -fx-padding: 8 15; -fx-border-width: 0 0 2 0; -fx-border-color: transparent;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: normal; -fx-padding: 8 15; -fx-border-width: 0 0 2 0; -fx-border-color: transparent;"));
        return button;
    }

    private GridPane createSettingsGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setHalignment(HPos.LEFT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }
}