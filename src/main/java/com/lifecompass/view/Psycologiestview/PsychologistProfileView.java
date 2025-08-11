package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.PsychologistProfile; // Import the new model
import javafx.application.Platform; // For Platform.runLater in showError/showSuccess
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer; // For contentSwitcher, saveHandler

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistProfileView {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistProfileView.class);

    private ImageView profileImageView;
    private PsychologistProfile currentPsychologistData; // Stores the profile currently displayed/edited
    private Stage parentStage;
    private Consumer<Node> contentSwitcher; // For switching dashboard content, e.g., back to display mode
    private Consumer<PsychologistProfile> saveHandler; // Callback for saving changes

    private StackPane rootContentPane; // Main content holder, for switching between display/edit/loading
    private Label loadingLabel;

    // References to editable fields
    private TextField editFullNameField;
    private TextField editQualificationsField;
    private TextField editSpecializationField; // Needs splitting/joining
    private TextField editExperienceField; // Stored as String in model
    private TextArea editBioTextArea;
    private TextField editFeeField; // Needs double conversion
    private ComboBox<String> editDurationComboBox;
    private ComboBox<String> editWorkModeComboBox;
    private TextField editAvailabilityField;
    private TextField editLanguagesField; // Needs splitting/joining
    private TextArea editDegreesTextArea;
    private TextArea editCertsTextArea;
    private TextField editClinicNameField;
    private TextField editClinicAddressField;
    private TextField editCityField;
    private TextField editStateField;
    private TextField editPinField;
    private TextField editGoogleMapsLinkField;

    // File upload related (for profile pic update) - no actual upload logic here
    private Label profilePicFileNameLabel;


    public PsychologistProfileView(Stage parentStage, Consumer<Node> contentSwitcher) {
        this.parentStage = parentStage;
        this.contentSwitcher = contentSwitcher;
        logger.info("PsychologistProfileView initialized.");

        // Initialize rootContentPane and loadingLabel
        rootContentPane = new StackPane();
        loadingLabel = new Label("Loading profile data...");
        loadingLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        rootContentPane.getChildren().add(loadingLabel); // Show loading initially
        rootContentPane.setAlignment(Pos.CENTER);
    }

    public ScrollPane getView() {
        // This is the overall ScrollPane that the DashboardController will display.
        // It wraps the rootContentPane which handles internal content switching (loading vs profile display/edit).
        ScrollPane wrapperScrollPane = new ScrollPane(rootContentPane);
        wrapperScrollPane.setFitToWidth(true);
        wrapperScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        wrapperScrollPane.setStyle("-fx-background-color: #e8ebf0;");
        return wrapperScrollPane;
    }

    private VBox createSection(String titleText, Node content) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        section.getChildren().addAll(titleLabel, new Separator(), content);
        return section;
    }

    /**
     * Creates the VBox content for displaying the psychologist's profile (read-only mode).
     * This method is called when data is loaded or after saving.
     */
    private VBox createDisplayProfileContentVBox() {
        logger.debug("Creating display profile content VBox.");
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: #e8ebf0;");

        Label pageTitle = new Label("Psychologist Profile");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));
        content.getChildren().add(pageTitle);

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #f5f0f9; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");

        profileImageView = createImageView(currentPsychologistData.getProfilePicUrl(), 120, 120, "Profile Picture");
        Circle clip = new Circle(60, 60, 60);
        profileImageView.setClip(clip);
        profileImageView.setStyle("-fx-border-color: #6a1b9a; -fx-border-width: 3; -fx-border-radius: 60;");

        Label fullNameLabel = new Label(currentPsychologistData.getFullName());
        fullNameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        fullNameLabel.setTextFill(Color.web("#333333"));

        Label qualificationsLabel = new Label(currentPsychologistData.getQualifications());
        qualificationsLabel.setFont(Font.font("Inter", 16));
        qualificationsLabel.setTextFill(Color.web("#555555"));

        Label specializationsLabel = new Label("Specializations: " + String.join(", ", currentPsychologistData.getSpecializations()));
        specializationsLabel.setFont(Font.font("Inter", 14));
        specializationsLabel.setTextFill(Color.web("#777777"));

        Label experienceLabel = new Label("Experience: " + currentPsychologistData.getYearsOfExperience() + " years");
        experienceLabel.setFont(Font.font("Inter", 14));
        experienceLabel.setTextFill(Color.web("#777777"));

        headerBox.getChildren().addAll(profileImageView, fullNameLabel, qualificationsLabel, specializationsLabel, experienceLabel);
        content.getChildren().add(createSection("Profile Summary", headerBox));

        VBox summaryBox = new VBox(5);
        Label bioTitle = new Label("Short Bio / Introduction");
        bioTitle.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        bioTitle.setTextFill(Color.web("#333333"));
        Label bioContent = new Label(currentPsychologistData.getShortBio());
        bioContent.setWrapText(true);
        bioContent.setFont(Font.font("Inter", 12));
        bioContent.setTextFill(Color.web("#555555"));
        summaryBox.getChildren().addAll(bioTitle, bioContent);
        content.getChildren().add(createSection("Professional Summary", summaryBox));

        GridPane consultGrid = new GridPane();
        consultGrid.setVgap(10); consultGrid.setHgap(20);
        consultGrid.add(new Label("Consultation Fee:"), 0, 0); consultGrid.add(new Label("INR " + String.format("%.2f", currentPsychologistData.getConsultationFee())), 1, 0);
        consultGrid.add(new Label("Session Duration:"), 0, 1); consultGrid.add(new Label(currentPsychologistData.getSessionDuration()), 1, 1);
        consultGrid.add(new Label("Mode of Therapy:"), 0, 2); consultGrid.add(new Label(currentPsychologistData.getModeOfTherapy()), 1, 2);
        consultGrid.add(new Label("Available Days/Time:"), 0, 3); consultGrid.add(new Label(currentPsychologistData.getAvailableDaysTime()), 1, 3);
        consultGrid.add(new Label("Languages Spoken:"), 0, 4); consultGrid.add(new Label(String.join(", ", currentPsychologistData.getLanguagesSpoken())), 1, 4);
        ColumnConstraintsHelper.setColumnConstraints(consultGrid);
        content.getChildren().add(createSection("Consultation Details", consultGrid));

        GridPane credGrid = new GridPane();
        credGrid.setVgap(10); credGrid.setHgap(20);
        credGrid.add(new Label("Degrees & Institutions:"), 0, 0); credGrid.add(new Label(currentPsychologistData.getDegreesInstitutions()), 1, 0);
        credGrid.add(new Label("License Number:"), 0, 1); credGrid.add(new Label(currentPsychologistData.getLicenseNumber()), 1, 1);
        credGrid.add(new Label("Issuing Body:"), 0, 2); credGrid.add(new Label(currentPsychologistData.getLicenseIssuingBody()), 1, 2);
        credGrid.add(new Label("Certifications/Workshops:"), 0, 3); credGrid.add(new Label(currentPsychologistData.getCertificationsWorkshops()), 1, 3);
        credGrid.add(new Label("Verification Status:"), 0, 4);
         Label verifiedBadge = new Label(currentPsychologistData.isVerified() ? "✅ Verified Profile" : "❌ Not Verified");
       // Label verifiedBadge = new Label(currentPsychologistData.isVerified() ? "✅ Verified Profile" : "✅ Verified Profile");
        verifiedBadge.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        verifiedBadge.setTextFill(currentPsychologistData.isVerified() ? Color.web("#4CAF50") : Color.web("#F44336"));
        credGrid.add(verifiedBadge, 1, 4);
        ColumnConstraintsHelper.setColumnConstraints(credGrid);
        content.getChildren().add(createSection("Credentials", credGrid));

        if (currentPsychologistData.getModeOfTherapy() != null && (currentPsychologistData.getModeOfTherapy().contains("Offline") || currentPsychologistData.getModeOfTherapy().contains("Both"))) {
            GridPane locGrid = new GridPane();
            locGrid.setVgap(15); locGrid.setHgap(20);

            locGrid.add(new Label("Clinic Name:"), 0, 0); locGrid.add(new Label(currentPsychologistData.getClinicInfo().getOrDefault("clinicName", "N/A")), 1, 0);
            locGrid.add(new Label("Clinic Address:"), 0, 1); locGrid.add(new Label(currentPsychologistData.getClinicInfo().getOrDefault("clinicAddress", "N/A")), 1, 1);
            locGrid.add(new Label("City / State / PIN:"), 0, 2);
            locGrid.add(new Label(currentPsychologistData.getClinicInfo().getOrDefault("city", "N/A") + ", " + 
                                 currentPsychologistData.getClinicInfo().getOrDefault("state", "N/A") + " - " + 
                                 currentPsychologistData.getClinicInfo().getOrDefault("pinCode", "N/A")), 1, 2);
            locGrid.add(new Label("Google Maps Link:"), 0, 3);
            Hyperlink mapLink = new Hyperlink(currentPsychologistData.getClinicInfo().getOrDefault("googleMapsLink", "N/A"));
            mapLink.setOnAction(e -> {
                logger.info("Attempting to open map link: {}", currentPsychologistData.getClinicInfo().getOrDefault("googleMapsLink", "N/A"));
                // Add code here to open URL in browser (e.g., using Desktop.getDesktop().browse(new URI(url)))
            });
            locGrid.add(mapLink, 1, 3);
            ColumnConstraintsHelper.setColumnConstraints(locGrid);
            content.getChildren().add(createSection("Location", locGrid));
        }

        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        editProfileButton.setTextFill(Color.WHITE);
        editProfileButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;");
        editProfileButton.setOnMouseEntered(e -> editProfileButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 10; -fx-padding: 12 30;"));
        editProfileButton.setOnMouseExited(e -> editProfileButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;"));
        editProfileButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(editProfileButton, new Insets(30, 0, 0, 0));
        // When Edit Profile is clicked, switch to the edit view
        editProfileButton.setOnAction(e -> rootContentPane.getChildren().setAll(createEditProfileContentVBox()));

        content.getChildren().add(editProfileButton);

        return content;
    }

    /**
     * Creates the VBox content for editing the psychologist's profile.
     * Fields are pre-filled with `currentPsychologistData`.
     */
    private VBox createEditProfileContentVBox() {
        logger.debug("Creating edit profile content VBox.");
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: #e8ebf0;");

        Label pageTitle = new Label("Edit Profile");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));
        content.getChildren().add(pageTitle);

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #f5f0f9; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");

        profileImageView = createImageView(currentPsychologistData.getProfilePicUrl(), 120, 120, "Profile Picture");
        Circle clip = new Circle(60, 60, 60);
        profileImageView.setClip(clip);
        profileImageView.setStyle("-fx-border-color: #6a1b9a; -fx-border-width: 3; -fx-border-radius: 60;");

        Button changePicButton = new Button("Change Picture");
        changePicButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;");
        changePicButton.setOnMouseEntered(e -> changePicButton.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;"));
        changePicButton.setOnMouseExited(e -> changePicButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;"));
        changePicButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(parentStage);
            if (selectedFile != null) {
                try {
                    Image newImage = new Image(selectedFile.toURI().toString());
                    profileImageView.setImage(newImage);
                    // Update the URL in currentPsychologistData. This will be saved when "Save Changes" is clicked.
                    currentPsychologistData.setProfilePicUrl(selectedFile.toURI().toString()); 
                    logger.info("New Profile Pic selected: {}", selectedFile.getAbsolutePath());
                } catch (Exception ex) {
                    logger.error("Error loading image from selected file: {}", ex.getMessage(), ex);
                }
            }
        });

        editFullNameField = new TextField(currentPsychologistData.getFullName());
        editFullNameField.setPromptText("Enter full name");
        editFullNameField.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        editFullNameField.setAlignment(Pos.CENTER);
        editFullNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 5; -fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: #333333;");

        editQualificationsField = new TextField(currentPsychologistData.getQualifications());
        editQualificationsField.setPromptText("e.g., M.A. Clinical Psychology");
        editQualificationsField.setFont(Font.font("Inter", 16));
        editQualificationsField.setAlignment(Pos.CENTER);
        editQualificationsField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 5; -fx-font-size: 16px; -fx-text-fill: #555555;");

        editSpecializationField = new TextField(String.join(", ", currentPsychologistData.getSpecializations()));
        editSpecializationField.setPromptText("e.g., CBT, Trauma (comma-separated)");
        editSpecializationField.setFont(Font.font("Inter", 14));
        editSpecializationField.setAlignment(Pos.CENTER);
        editSpecializationField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 5; -fx-font-size: 14px; -fx-text-fill: #777777;");

        //editExperienceField = new TextField(currentPsychologistData.getYearsOfExperience());
        editExperienceField = new TextField(String.valueOf(currentPsychologistData.getYearsOfExperience()));
        editExperienceField.setPromptText("e.g., 5+ years");
        editExperienceField.setFont(Font.font("Inter", 14));
        editExperienceField.setAlignment(Pos.CENTER);
        editExperienceField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 5; -fx-font-size: 14px; -fx-text-fill: #777777;");

        headerBox.getChildren().addAll(profileImageView, changePicButton, editFullNameField, editQualificationsField, editSpecializationField, editExperienceField);
        content.getChildren().add(createSection("Edit Profile Summary", headerBox));

        VBox summaryBox = new VBox(5);
        Label bioTitle = new Label("Short Bio / Introduction");
        bioTitle.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        bioTitle.setTextFill(Color.web("#333333"));
        
        editBioTextArea = new TextArea(currentPsychologistData.getShortBio());
        editBioTextArea.setPromptText("Write a short professional biography...");
        editBioTextArea.setWrapText(true);
        editBioTextArea.setPrefRowCount(8);
        editBioTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        VBox.setVgrow(editBioTextArea, Priority.ALWAYS);
        summaryBox.getChildren().addAll(bioTitle, editBioTextArea);
        content.getChildren().add(createSection("Edit Professional Summary", summaryBox));

        GridPane consultGrid = new GridPane();
        consultGrid.setVgap(15); consultGrid.setHgap(20);

        consultGrid.add(new Label("Consultation Fee:"), 0, 0);
        editFeeField = new TextField(String.valueOf(currentPsychologistData.getConsultationFee())); // Convert double to String
        editFeeField.setPromptText("e.g., 1000 INR");
        editFeeField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        // Input validation for fee: only allow digits and optionally a single decimal point
        editFeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                editFeeField.setText(oldValue); // Revert to old value if invalid
            }
        });
        consultGrid.add(editFeeField, 1, 0);

        consultGrid.add(new Label("Session Duration:"), 0, 1);
        editDurationComboBox = new ComboBox<>();
        editDurationComboBox.setPromptText("Select Duration");
        editDurationComboBox.getItems().addAll("30 min", "45 min", "60 min", "90 min");
        editDurationComboBox.setValue(currentPsychologistData.getSessionDuration());
        editDurationComboBox.setMaxWidth(Double.MAX_VALUE);
        editDurationComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        consultGrid.add(editDurationComboBox, 1, 1);

        consultGrid.add(new Label("Mode of Therapy:"), 0, 2);
        editWorkModeComboBox = new ComboBox<>();
        editWorkModeComboBox.setPromptText("Select Mode");
        editWorkModeComboBox.getItems().addAll("Online", "Offline", "Both");
        editWorkModeComboBox.setValue(currentPsychologistData.getModeOfTherapy());
        editWorkModeComboBox.setMaxWidth(Double.MAX_VALUE);
        editWorkModeComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        consultGrid.add(editWorkModeComboBox, 1, 2);

        consultGrid.add(new Label("Available Days/Time:"), 0, 3);
        editAvailabilityField = new TextField(currentPsychologistData.getAvailableDaysTime());
        editAvailabilityField.setPromptText("e.g., Mon-Fri 9AM-5PM");
        editAvailabilityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        consultGrid.add(editAvailabilityField, 1, 3);

        consultGrid.add(new Label("Languages Spoken:"), 0, 4);
        editLanguagesField = new TextField(String.join(", ", currentPsychologistData.getLanguagesSpoken()));
        editLanguagesField.setPromptText("e.g., English, Hindi (comma-separated)");
        editLanguagesField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        consultGrid.add(editLanguagesField, 1, 4);

        ColumnConstraintsHelper.setColumnConstraints(consultGrid);
        content.getChildren().add(createSection("Edit Consultation Details", consultGrid));

        GridPane credGrid = new GridPane();
        credGrid.setVgap(15); credGrid.setHgap(20);

        credGrid.add(new Label("Degrees & Institutions:"), 0, 0);
        editDegreesTextArea = new TextArea(currentPsychologistData.getDegreesInstitutions());
        editDegreesTextArea.setPromptText("List degrees and institutions (one per line)");
        editDegreesTextArea.setWrapText(true);
        editDegreesTextArea.setPrefRowCount(3);
        editDegreesTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        credGrid.add(editDegreesTextArea, 1, 0);

        credGrid.add(new Label("License Number:"), 0, 1);
        TextField licenseFieldDisplay = new TextField(currentPsychologistData.getLicenseNumber()); // Display-only or disabled
        licenseFieldDisplay.setEditable(false); // Make it non-editable
        licenseFieldDisplay.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8; -fx-control-inner-background: #f8f8f8;");
        credGrid.add(licenseFieldDisplay, 1, 1);

        credGrid.add(new Label("Issuing Body:"), 0, 2);
        TextField issuingBodyFieldDisplay = new TextField(currentPsychologistData.getLicenseIssuingBody()); // Display-only or disabled
        issuingBodyFieldDisplay.setEditable(false); // Make it non-editable
        issuingBodyFieldDisplay.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8; -fx-control-inner-background: #f8f8f8;");
        credGrid.add(issuingBodyFieldDisplay, 1, 2);

        credGrid.add(new Label("Certifications/Workshops:"), 0, 3);
        editCertsTextArea = new TextArea(currentPsychologistData.getCertificationsWorkshops());
        editCertsTextArea.setPromptText("List certifications/workshops (one per line)");
        editCertsTextArea.setWrapText(true);
        editCertsTextArea.setPrefRowCount(3);
        editCertsTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        credGrid.add(editCertsTextArea, 1, 3);

        credGrid.add(new Label("Verification Status:"), 0, 4);
        Label verifiedBadge = new Label(currentPsychologistData.isVerified() ? "✅ Verified Profile" : "❌ Not Verified");
        verifiedBadge.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        verifiedBadge.setTextFill(currentPsychologistData.isVerified() ? Color.web("#4CAF50") : Color.web("#F44336"));
        credGrid.add(verifiedBadge, 1, 4);

        ColumnConstraintsHelper.setColumnConstraints(credGrid);
        content.getChildren().add(createSection("Edit Credentials", credGrid));

        if (currentPsychologistData.getModeOfTherapy() != null && (currentPsychologistData.getModeOfTherapy().contains("Offline") || currentPsychologistData.getModeOfTherapy().contains("Both"))) {
            GridPane locGrid = new GridPane();
            locGrid.setVgap(15); locGrid.setHgap(20);

            locGrid.add(new Label("Clinic Name:"), 0, 0);
            editClinicNameField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("clinicName", ""));
            locGrid.add(editClinicNameField, 1, 0);

            locGrid.add(new Label("Clinic Address:"), 0, 1);
            editClinicAddressField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("clinicAddress", ""));
            locGrid.add(editClinicAddressField, 1, 1);

            locGrid.add(new Label("City / State / PIN:"), 0, 2);
            HBox cityStatePinBox = new HBox(10);
            editCityField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("city", ""));
            editCityField.setPromptText("City");
            editStateField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("state", ""));
            editStateField.setPromptText("State");
            editPinField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("pinCode", ""));
            editPinField.setPromptText("PIN Code");
            cityStatePinBox.getChildren().addAll(editCityField, editStateField, editPinField);
            locGrid.add(cityStatePinBox, 1, 2);

            locGrid.add(new Label("Google Maps Link:"), 0, 3);
            editGoogleMapsLinkField = new TextField(currentPsychologistData.getClinicInfo().getOrDefault("googleMapsLink", ""));
            locGrid.add(editGoogleMapsLinkField, 1, 3);

            ColumnConstraintsHelper.setColumnConstraints(locGrid);
            content.getChildren().add(createSection("Edit Location", locGrid));
        }

        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        saveChangesButton.setTextFill(Color.WHITE);
        saveChangesButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 10; -fx-padding: 12 30;");
        saveChangesButton.setOnMouseEntered(e -> saveChangesButton.setStyle("-fx-background-color: #66BB6A; -fx-background-radius: 10; -fx-padding: 12 30;"));
        saveChangesButton.setOnMouseExited(e -> saveChangesButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 10; -fx-padding: 12 30;"));
        saveChangesButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(saveChangesButton, new Insets(30, 0, 0, 0));
        
        // This is where the save handler is called
        saveChangesButton.setOnAction(e -> {
            logger.info("Save Changes button clicked in view.");
            if (saveHandler != null) {
                // Collect data and pass to the handler in the controller
                saveHandler.accept(getProfileDataFromEditFields()); 
            } else {
                logger.warn("Save handler is null. Cannot save changes.");
                showError("Save functionality not available. Please contact support.");
            }
        });

        content.getChildren().add(saveChangesButton);

        return content;
    }

    /**
     * Helper method to create ImageView.
     */
    private ImageView createImageView(String imagePathOrUrl, double fitWidth, double fitHeight, String debugName) {
        ImageView imageView = new ImageView();
        Image image = null;

        try {
            if (imagePathOrUrl != null && !imagePathOrUrl.isEmpty()) {
                // Try as URL first (for web URLs from Firebase Storage)
                if (imagePathOrUrl.startsWith("http://") || imagePathOrUrl.startsWith("https://") || imagePathOrUrl.startsWith("file:/")) {
                    image = new Image(imagePathOrUrl, true);
                } else {
                    // Then try as resource path
                    URL imageUrl = getClass().getResource(imagePathOrUrl);
                    if (imageUrl != null) {
                        image = new Image(imageUrl.toExternalForm());
                    } else {
                        // Fallback to direct path if not resource (e.g., local file path)
                        File file = new File(imagePathOrUrl);
                        if (file.exists()) {
                            image = new Image(file.toURI().toString(), true);
                        }
                    }
                }
            }
            
            if (image == null || image.isError()) {
                logger.warn("Could not load {} from path/URL: {}. Attempting fallback.", debugName, imagePathOrUrl);
                URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png"); 
                if (fallbackUrl != null) {
                    image = new Image(fallbackUrl.toExternalForm());
                } else {
                    logger.error("Fallback image not found either for {}.", debugName);
                }
            }
        } catch (Exception e) {
            logger.error("Exception during image loading for {} from {}: {}", debugName, imagePathOrUrl, e.getMessage(), e);
        }

        if (image != null) {
            imageView.setImage(image);
            imageView.setFitWidth(fitWidth);
            imageView.setFitHeight(fitHeight);
            imageView.setPreserveRatio(true);
        }
        return imageView;
    }


    private static class ColumnConstraintsHelper {
        public static void setColumnConstraints(GridPane grid) {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.NEVER);
            col1.setHalignment(HPos.LEFT);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(col1, col2);
        }
    }

    // --- Methods for Controller to Populate and Get Data ---

    /**
     * Populates the view's display mode fields with existing psychologist profile data.
     * @param profile The PsychologistProfile object to display.
     */
    public void populateProfileData(PsychologistProfile profile) {
        if (profile == null) {
            logger.warn("populateProfileData called with null profile. Cannot populate view.");
            // Optionally clear fields or show a message
            currentPsychologistData = new PsychologistProfile(); // Ensure current data is not null
            rootContentPane.getChildren().setAll(new Label("No profile data found. Please fill out the form."));
            return;
        }

        logger.info("Populating profile view for: {}", profile.getFullName());
        this.currentPsychologistData = profile; // Store the data for display and editing

        // Set the content of the rootContentPane to the display mode
        rootContentPane.getChildren().setAll(createDisplayProfileContentVBox());
    }

    /**
     * Collects data from the edit mode form fields and returns a PsychologistProfile object.
     * This method is called by the view's save button handler, which then passes it to the controller.
     * It relies on the edit fields being initialized via createEditProfileContentVBox().
     */
    private PsychologistProfile getProfileDataFromEditFields() {
        PsychologistProfile profile = new PsychologistProfile(); // Start with a new profile object
        // The ID will be set by the controller before saving to Firestore
        
        profile.setFullName(editFullNameField.getText());
        profile.setQualifications(editQualificationsField.getText());
        
        // Convert comma-separated string to List<String>
        if (editSpecializationField != null && !editSpecializationField.getText().isEmpty()) {
            profile.setSpecializations(Arrays.asList(editSpecializationField.getText().split("\\s*,\\s*")));
        } else {
            profile.setSpecializations(List.of()); // Use List.of() for immutable empty list
        }

        //profile.setYearsOfExperience(editExperienceField.getText()); // Model expects String

        try {
            if (editExperienceField != null && !editExperienceField.getText().isEmpty()) {
                profile.setYearsOfExperience(Integer.parseInt(editExperienceField.getText().trim())); // <--- UPDATED
            } else {
                profile.setYearsOfExperience(0); // Default if empty
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format for years of experience: {}", editExperienceField.getText(), e);
            profile.setYearsOfExperience(0); // Set default and handle error (e.g., show alert)
            showError("Years of Experience must be a valid number."); // Inform user
        }

        profile.setShortBio(editBioTextArea.getText());
        
        // Convert String to double for consultationFee
        try {
            if (editFeeField != null && !editFeeField.getText().isEmpty()) {
                profile.setConsultationFee(Double.parseDouble(editFeeField.getText().replaceAll("[^\\d.]", ""))); // Remove non-digit/dot chars
            } else {
                profile.setConsultationFee(0.0);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format for consultation fee: {}", editFeeField.getText(), e);
            profile.setConsultationFee(0.0); // Default to 0.0 or handle error
        }

        profile.setSessionDuration(editDurationComboBox.getValue());
        profile.setModeOfTherapy(editWorkModeComboBox.getValue());
        profile.setAvailableDaysTime(editAvailabilityField.getText());
        
        // Convert comma-separated string to List<String>
        if (editLanguagesField != null && !editLanguagesField.getText().isEmpty()) {
            profile.setLanguagesSpoken(Arrays.asList(editLanguagesField.getText().split("\\s*,\\s*")));
        } else {
            profile.setLanguagesSpoken(List.of());
        }

        profile.setDegreesInstitutions(editDegreesTextArea.getText());
        profile.setLicenseNumber(currentPsychologistData.getLicenseNumber()); // License number not editable in this view
        profile.setLicenseIssuingBody(currentPsychologistData.getLicenseIssuingBody()); // Issuing body not editable
        profile.setCertificationsWorkshops(editCertsTextArea.getText());
        profile.setVerified(currentPsychologistData.isVerified()); // Verification status not editable
        profile.setProfilePicUrl(currentPsychologistData.getProfilePicUrl()); // Profile pic URL from current data

        Map<String, String> clinicInfo = new HashMap<>();
        if (editClinicNameField != null) clinicInfo.put("clinicName", editClinicNameField.getText());
        if (editClinicAddressField != null) clinicInfo.put("clinicAddress", editClinicAddressField.getText());
        if (editCityField != null) clinicInfo.put("city", editCityField.getText());
        if (editStateField != null) clinicInfo.put("state", editStateField.getText());
        if (editPinField != null) clinicInfo.put("pinCode", editPinField.getText());
        if (editGoogleMapsLinkField != null) clinicInfo.put("googleMapsLink", editGoogleMapsLinkField.getText());
        profile.setClinicInfo(clinicInfo);
        
        return profile;
    }

    /**
     * Sets the action handler for the "Save Changes" button.
     * This handler is typically provided by the controller.
     * @param handler The Consumer to execute when the button is clicked, receiving the collected profile data.
     */
    public void setSaveHandler(Consumer<PsychologistProfile> handler) {
        this.saveHandler = handler;
        logger.debug("Save handler set for profile view.");
    }
    
    /**
     * Shows or hides a loading indicator.
     * @param show true to show, false to hide.
     */
    public void showLoading(boolean show) {
        logger.debug("Setting loading state for PsychologistProfileView to: {}", show);
        if (loadingLabel != null && rootContentPane != null) {
            if (show) {
                rootContentPane.getChildren().setAll(loadingLabel); // Replace content with loading indicator
                rootContentPane.setDisable(true); // Disable interaction
            } else {
                rootContentPane.setDisable(false); // Re-enable interaction
                // The populateProfileData or createEditProfileContentVBox should set the actual content after loading completes.
                // If loading finishes and no data is populated, this won't change back from loadingLabel automatically.
            }
        }
    }

    /**
     * Displays an error message to the user.
     * @param message The error message.
     */
    public void showError(String message) {
        logger.error("Displaying error in PsychologistProfileView: {}", message);
        Platform.runLater(() -> { // Ensure alert is shown on FX Application Thread
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(parentStage);
            alert.setTitle("Error");
            alert.setHeaderText("Profile Error"); // Changed header
            alert.setContentText(message);
            alert.showAndWait();
        });
        showLoading(false); // Hide loading on error
        // On error, perhaps switch back to display mode or keep edit mode if needed for correction
        // rootContentPane.getChildren().setAll(createDisplayProfileContentVBox()); // Example
    }

    /**
     * Displays a success message to the user.
     * @param message The success message.
     */
    public void showSuccess(String message) {
        logger.info("Displaying success in PsychologistProfileView: {}", message);
        Platform.runLater(() -> { // Ensure alert is shown on FX Application Thread
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initOwner(parentStage);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
        showLoading(false); // Hide loading on success
        // After successful save, switch back to display mode
        rootContentPane.getChildren().setAll(createDisplayProfileContentVBox());
    }
}