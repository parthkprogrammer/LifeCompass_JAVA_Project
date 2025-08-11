
package com.lifecompass.view;

import com.lifecompass.controller.PsychologistRegistrationController;
import com.lifecompass.util.SceneManager;
import com.lifecompass.util.ColumnConstraintsHelper;
// import com.lifecompass.util.FirebaseStorageUtil; // REMOVED as per instruction
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
// import java.io.IOException; // Not needed if not performing IO for uploads
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.UUID; // Not needed for generating temporary IDs for Storage
import java.util.stream.Collectors;

/**
 * This class creates the UI for the Psychologist Registration page.
 * It includes sections for Personal Details, Professional Details,
 * Clinic Information (optional), Account Information, and Document Uploads.
 */
public class PsychologistRegistrationView extends Application {

    private VBox rootLayout;
    private Map<String, Label> uploadedFileLabels = new HashMap<>();
    private Map<String, String> selectedDocumentLocalPaths = new HashMap<>(); // Store local paths for documents


    // References to UI input fields
    private TextField fullNameField;
    private ComboBox<String> genderComboBox;
    private DatePicker dobPicker;
    private TextField phoneField;
    private TextField emailField;
    private TextField profilePicLocalPathField; // Holds local path to profile picture
    private TextField qualificationField;
    private TextArea specializationsArea;
    private TextField yearsOfExperienceField;
    private TextField licenseField;
    private TextField authorityField;
    private TextArea languagesKnownArea;
    private ComboBox<String> workModeComboBox;
    private TextField availabilityField;
    private TextField feeField;
    private TextField clinicNameField;
    private TextField clinicAddressField;
    private TextField clinicCityField;
    private TextField clinicStateField;
    private TextField clinicPinCodeField;
    private TextField googleMapsLinkField;


    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    private Button registerButton;
    private final PsychologistRegistrationController registrationController = new PsychologistRegistrationController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(30, 50, 30, 50));
        rootLayout.setStyle("-fx-background-color: #f0f2f5;");

        Label pageTitle = new Label("Psychologist Registration");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));

        rootLayout.getChildren().add(pageTitle);

        rootLayout.getChildren().add(createPersonalDetailsSection(primaryStage));
        rootLayout.getChildren().add(createProfessionalDetailsSection());
        rootLayout.getChildren().add(createClinicInfoSection());
        rootLayout.getChildren().add(createAccountInfoSection());
        rootLayout.getChildren().add(createDocumentsUploadSection(primaryStage)); // Documents upload section

        registerButton = new Button("Register Now");
        registerButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 10; -fx-padding: 12 30;"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;"));
        registerButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(registerButton, new Insets(30, 0, 0, 0));

        registerButton.setOnAction(e -> handleRegistration(primaryStage));
        rootLayout.getChildren().add(registerButton);

        Hyperlink backToLoginLink = new Hyperlink("Back to Psychologist Login");
        backToLoginLink.setOnAction(e -> handleBackToLogin(primaryStage));
        rootLayout.getChildren().add(backToLoginLink);

        ScrollPane scrollPane = new ScrollPane(rootLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(scrollPane, 700, 800);
        primaryStage.setTitle("Psychologist Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSection(String titleText, Node content) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#333333"));
        section.getChildren().addAll(titleLabel, content);
        return section;
    }

    private VBox createPersonalDetailsSection(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("Full Name:"), 0, 0);
        fullNameField = new TextField();
        fullNameField.setPromptText("Enter full name");
        fullNameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(fullNameField, 1, 0);

        grid.add(new Label("Gender:"), 0, 1);
        genderComboBox = new ComboBox<>();
        genderComboBox.setPromptText("Select Gender");
        genderComboBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        genderComboBox.setMaxWidth(Double.MAX_VALUE);
        genderComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(genderComboBox, 1, 1);

        grid.add(new Label("Date of Birth:"), 0, 2);
        dobPicker = new DatePicker();
        dobPicker.setPromptText("Select Date");
        dobPicker.setMaxWidth(Double.MAX_VALUE);
        dobPicker.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(dobPicker, 1, 2);

        grid.add(new Label("Phone Number:"), 0, 3);
        phoneField = new TextField();
        phoneField.setPromptText("e.g., 9876543210");
        phoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(phoneField, 1, 3);

        grid.add(new Label("Email Address:"), 0, 4);
        emailField = new TextField();
        emailField.setPromptText("e.g., your.email@example.com");
        emailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(emailField, 1, 4);

        grid.add(new Label("Profile Picture:"), 0, 5);
        HBox profilePicUploadBox = new HBox(10);
        Button uploadProfilePicButton = new Button("Upload Image");
        uploadProfilePicButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 15;");
        uploadProfilePicButton.setOnMouseEntered(e -> uploadProfilePicButton.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 8; -fx-padding: 8 15;"));
        uploadProfilePicButton.setOnMouseExited(e -> uploadProfilePicButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 15;"));

        Label profilePicFileNameDisplayLabel = new Label("No file selected");
        profilePicFileNameDisplayLabel.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");
        profilePicLocalPathField = new TextField(); // Holds local path
        profilePicLocalPathField.setVisible(false);
        profilePicLocalPathField.setManaged(false);

        uploadProfilePicButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profilePicFileNameDisplayLabel.setText(selectedFile.getName());
                profilePicLocalPathField.setText(selectedFile.getAbsolutePath()); // Store local path
                System.out.println("Selected Profile Pic: " + selectedFile.getAbsolutePath());
            } else {
                profilePicFileNameDisplayLabel.setText("No file selected");
                profilePicLocalPathField.setText("");
            }
        });
        profilePicUploadBox.getChildren().addAll(uploadProfilePicButton, profilePicFileNameDisplayLabel);
        grid.add(profilePicUploadBox, 1, 5);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        return createSection("Personal Details", grid);
    }

    private VBox createProfessionalDetailsSection() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("Qualification(s):"), 0, 0);
        qualificationField = new TextField();
        qualificationField.setPromptText("e.g., M.A. in Clinical Psychology");
        qualificationField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(qualificationField, 1, 0);

        grid.add(new Label("Specializations (comma-separated):"), 0, 1);
        specializationsArea = new TextArea();
        specializationsArea.setPromptText("e.g., CBT, PTSD (comma-separated)");
        specializationsArea.setPrefRowCount(2);
        specializationsArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(specializationsArea, 1, 1);

        grid.add(new Label("Years of Experience:"), 0, 2);
        yearsOfExperienceField = new TextField();
        yearsOfExperienceField.setPromptText("e.g., 5");
        yearsOfExperienceField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        yearsOfExperienceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearsOfExperienceField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(yearsOfExperienceField, 1, 2);

        grid.add(new Label("License Number:"), 0, 3);
        licenseField = new TextField();
        licenseField.setPromptText("Enter license/registration number");
        licenseField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(licenseField, 1, 3);

        grid.add(new Label("Issuing Authority:"), 0, 4);
        authorityField = new TextField();
        authorityField.setPromptText("e.g., Rehabilitation Council of India");
        authorityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(authorityField, 1, 4);

        grid.add(new Label("Languages Known (comma-separated):"), 0, 5);
        languagesKnownArea = new TextArea();
        languagesKnownArea.setPromptText("e.g., English, Hindi (comma-separated)");
        languagesKnownArea.setPrefRowCount(2);
        languagesKnownArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(languagesKnownArea, 1, 5);

        grid.add(new Label("Work Mode:"), 0, 6);
        workModeComboBox = new ComboBox<>();
        workModeComboBox.setPromptText("Select Mode");
        workModeComboBox.getItems().addAll("Online", "Offline", "Both");
        workModeComboBox.setMaxWidth(Double.MAX_VALUE);
        workModeComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(workModeComboBox, 1, 6);

        grid.add(new Label("Available Days/Time:"), 0, 7);
        availabilityField = new TextField();
        availabilityField.setPromptText("e.g., Mon-Fri 9AM-5PM");
        availabilityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(availabilityField, 1, 7);

        grid.add(new Label("Consultation Fee:"), 0, 8);
        feeField = new TextField();
        feeField.setPromptText("e.g., 1000 INR");
        feeField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        feeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                feeField.setText(oldValue);
            }
        });
        grid.add(feeField, 1, 8);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        return createSection("Professional Details", grid);
    }

    private VBox createClinicInfoSection() {
        VBox clinicSection = new VBox(10);
        clinicSection.getChildren().add(new Label("Note: This section is optional if your work mode is 'Online Only'."));
        clinicSection.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: #777777;");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("Clinic Name:"), 0, 0);
        clinicNameField = new TextField();
        clinicNameField.setPromptText("Enter clinic name");
        clinicNameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(clinicNameField, 1, 0);

        grid.add(new Label("Clinic Address:"), 0, 1);
        clinicAddressField = new TextField();
        clinicAddressField.setPromptText("Enter full address");
        clinicAddressField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(clinicAddressField, 1, 1);

        grid.add(new Label("City / State / PIN:"), 0, 2);
        HBox cityStatePinBox = new HBox(10);
        clinicCityField = new TextField();
        clinicCityField.setPromptText("City");
        clinicCityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        clinicStateField = new TextField();
        clinicStateField.setPromptText("State");
        clinicStateField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        clinicPinCodeField = new TextField();
        clinicPinCodeField.setPromptText("PIN Code");
        clinicPinCodeField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        clinicPinCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                clinicPinCodeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        cityStatePinBox.getChildren().addAll(clinicCityField, clinicStateField, clinicPinCodeField);
        grid.add(cityStatePinBox, 1, 2);

        grid.add(new Label("Google Maps Link:"), 0, 3);
        googleMapsLinkField = new TextField();
        googleMapsLinkField.setPromptText("Paste Google Maps link");
        googleMapsLinkField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(googleMapsLinkField, 1, 3);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        clinicSection.getChildren().add(grid);
        return createSection("Clinic / Practice Information", clinicSection);
    }

    private VBox createAccountInfoSection() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("Username:"), 0, 0);
        usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(usernameField, 1, 0);

        grid.add(new Label("Password:"), 0, 1);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(passwordField, 1, 1);

        grid.add(new Label("Confirm Password:"), 0, 2);
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");
        confirmPasswordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(confirmPasswordField, 1, 2);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        return createSection("Account Information", grid);
    }

    private VBox createDocumentsUploadSection(Stage primaryStage) {
        VBox documentsSection = new VBox(10);
        documentsSection.getChildren().add(new Label("Please upload the following documents:"));

        List<String> documentTypes = List.of(
                "Government-issued ID Proof (e.g., Aadhar, PAN, Passport)",
                "Degree Certificates (Bachelor’s and Master’s)",
                "Professional License/Registration Certificate",
                "Experience Certificate(s)",
                "Passport Size Photograph",
                "Any relevant workshop/training certificates (optional)"
        );

        for (String docType : documentTypes) {
            HBox docRow = new HBox(10);
            docRow.setAlignment(Pos.CENTER_LEFT);
            Label docLabel = new Label(docType);
            docLabel.setPrefWidth(400);
            docLabel.setWrapText(true);

            Button uploadButton = new Button("Upload");
            uploadButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10;");
            uploadButton.setOnMouseEntered(e -> uploadButton.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 8; -fx-padding: 5 10;"));
            uploadButton.setOnMouseExited(e -> uploadButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10;"));

            Label fileNameLabel = new Label("No file selected");
            fileNameLabel.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");
            uploadedFileLabels.put(docType, fileNameLabel);

            selectedDocumentLocalPaths.putIfAbsent(docType, ""); // Initialize with empty string

            uploadButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select " + docType);
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Document Files", "*.pdf", "*.doc", "*.docx", "*.txt", "*.png", "*.jpg", "*.jpeg"),
                        new FileChooser.ExtensionFilter("All Files", "*.*")
                );
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    uploadedFileLabels.get(docType).setText(selectedFile.getName());
                    selectedDocumentLocalPaths.put(docType, selectedFile.getAbsolutePath()); // Store the local path
                    System.out.println("Selected file for " + docType + ": " + selectedFile.getAbsolutePath());
                } else {
                    uploadedFileLabels.get(docType).setText("No file selected");
                    selectedDocumentLocalPaths.put(docType, ""); // Clear path if no file selected
                }
            });

            docRow.getChildren().addAll(docLabel, uploadButton, fileNameLabel);
            documentsSection.getChildren().add(docRow);
        }

        return createSection("Documents to Upload", documentsSection);
    }

    private void handleRegistration(Stage currentStage) {
        // Retrieve values from UI fields, trimming whitespace
        String fullName = fullNameField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dateOfBirth = dobPicker.getValue();
        String phoneNumber = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String profilePictureLocalPath = profilePicLocalPathField.getText().trim(); // Local path
        String qualification = qualificationField.getText().trim();
        String specializationsText = specializationsArea.getText().trim();
        List<String> specializations = Arrays.asList(specializationsText.split(",")).stream().map(String::trim).collect(Collectors.toList());
        
        int yearsOfExperience = 0;
        try {
            if (!yearsOfExperienceField.getText().trim().isEmpty()) {
                yearsOfExperience = Integer.parseInt(yearsOfExperienceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Input Error", "Years of Experience must be a valid number.");
            return;
        }
        String licenseNumber = licenseField.getText().trim();
        String issuingAuthority = authorityField.getText().trim();
        String languagesKnownText = languagesKnownArea.getText().trim();
        List<String> languagesKnown = Arrays.asList(languagesKnownText.split(",")).stream().map(String::trim).collect(Collectors.toList());
        String workMode = workModeComboBox.getValue();
        String availability = availabilityField.getText().trim();
        double consultationFee = 0.0;
        try {
            if (!feeField.getText().trim().isEmpty()) {
                consultationFee = Double.parseDouble(feeField.getText().trim());
            }
        } catch (NumberFormatException e) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Input Error", "Consultation Fee must be a valid number.");
            return;
        }
        String clinicName = clinicNameField.getText().trim();
        String clinicAddress = clinicAddressField.getText().trim();
        String clinicCity = clinicCityField.getText().trim();
        String clinicState = clinicStateField.getText().trim();
        String clinicPinCode = clinicPinCodeField.getText().trim();
        String googleMapsLink = googleMapsLinkField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();


        // Robust validation for required fields
        if (fullName.isEmpty() || gender == null || dateOfBirth == null || phoneNumber.isEmpty() || email.isEmpty() ||
            qualification.isEmpty() || specializationsText.isEmpty() || yearsOfExperienceField.getText().trim().isEmpty() ||
            licenseNumber.isEmpty() || issuingAuthority.isEmpty() || languagesKnownText.isEmpty() || workMode == null ||
            availability.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all required fields.");
            return;
        }

        // Password validation
        if (password.length() < 6) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Password must be at least 6 characters long.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        // --- Constructing Placeholder URLs (as per "do not use storage") ---
        String profilePictureFinalUrl = "";
        if (!profilePictureLocalPath.isEmpty()) {
            // For demonstration, creating a local file URL or a dummy URL
            // In a real application, if you need actual hosted URLs,
            // this is where you would call an external service or perform actual upload.
            profilePictureFinalUrl = "file:///" + profilePictureLocalPath.replace("\\", "/"); 
            System.out.println("Using placeholder URL for profile pic: " + profilePictureFinalUrl);
        }

        Map<String, String> documentUrlsToPass = new HashMap<>();
        for (Map.Entry<String, String> entry : selectedDocumentLocalPaths.entrySet()) {
            String docType = entry.getKey();
            String localPath = entry.getValue();
            if (!localPath.isEmpty()) {
                // For demonstration, creating local file URLs or dummy URLs
                // In a real application, these would be actual hosted URLs (e.g., Firebase Storage URLs).
                documentUrlsToPass.put(docType, "file:///" + localPath.replace("\\", "/"));
                System.out.println("Using placeholder URL for " + docType + ": " + documentUrlsToPass.get(docType));
            }
        }
        // --- End Constructing Placeholder URLs ---

        // Call controller with the constructed URLs (or empty strings if no file selected)
        boolean success = registrationController.registerPsychologist(
                fullName, gender, dateOfBirth, phoneNumber, email, profilePictureFinalUrl, // Pass the constructed URL
                qualification, specializations, yearsOfExperience, licenseNumber,
                issuingAuthority, languagesKnown, workMode, availability, consultationFee,
                clinicName, clinicAddress, clinicCity, clinicState, clinicPinCode,
                googleMapsLink, username, password, documentUrlsToPass // Pass the map of constructed URLs
        );

        if (success) {
            SceneManager.showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Psychologist registered successfully!");
            SceneManager.switchScreen(new PsychologistLoginScreen(), "Psychologist Login");
            currentStage.close();
        } else {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to register psychologist. Please check inputs or try again.");
        }
    }

    private void handleBackToLogin(Stage currentStage) {
        SceneManager.switchScreen(new PsychologistLoginScreen(), "Psychologist Login");
        currentStage.close();
    }
}