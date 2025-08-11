package com.lifecompass.view; // Corrected package name

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints; // Inline ColumnConstraints
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.net.URL; // For image loading
import java.time.LocalDate;
import java.util.Optional;

// Controller and DAO imports for Firebase functionality
import com.lifecompass.controller.AuthController;
import com.lifecompass.dao.impl.UserDaoFirestoreImpl;
import com.lifecompass.model.User; // Import the User model
import com.lifecompass.util.SceneManager; // For alerts and scene switching

import java.util.concurrent.ExecutionException;


// Refactored: No longer extends Application, now extends VBox for embedding in UserDashboardScreen
public class ProfileScreen extends VBox {

    private BorderPane rootLayout;
    private ImageView profileImageView; // To update profile picture
    private User currentUserData; // Now directly holds the com.lifecompass.model.User object

    // References to editable fields in the edit mode (for data retrieval)
    private TextField editFullNameField;
    private TextField editEmailField; // Email is displayed but not typically directly editable for Firebase Auth
    private TextField editPhoneField;
    private DatePicker editDobPicker;
    private ComboBox<String> editGenderComboBox;
    private TextField editCityField;
    private TextField editStateField;
    private TextField editCountryField;
    private TextField editPinZipField;

    // New fields for related information
    private TextField editEmergencyContactNameField;
    private TextField editEmergencyContactPhoneField;
    private TextField editFamilyPhoneField;
    private TextField editFriendsPhoneField;
    private TextArea editTherapyGoalsTextArea;
    private CheckBox editEmailNotificationsCheckbox;
    private CheckBox editSmsNotificationsCheckbox;

    // References to the primary stage and dashboard needed for interactions
    private Stage primaryStage; // This is the dashboard's primary stage
    private UserDashboardScreen dashboardInstance; // Reference to the main dashboard instance

    private final AuthController authController = new AuthController();
    private final UserDaoFirestoreImpl userDao = new UserDaoFirestoreImpl();


    // Constructor for integration with dashboard
    public ProfileScreen(Stage primaryStage, UserDashboardScreen dashboardInstance) {
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;
        initializeProfileScreen(); // Call a new method to set up the content
    }

    /**
     * Provides access to the current User model object.
     * This is useful for the parent dashboard to get the latest profile picture URL or username.
     * @return The current User model object.
     */
    public User getCurrentUserData() { // Return type changed from UserData to User
        return currentUserData;
    }


    // New method to encapsulate the initialization logic for the ProfileScreen component
    private void initializeProfileScreen() {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #e8ebf0;"); // Overall background

        loadUserProfileData(); // This method populates currentUserData from Firebase

        rootLayout.setCenter(createDisplayProfileContent());

        this.getChildren().add(rootLayout);
        VBox.setVgrow(rootLayout, Priority.ALWAYS);
    }

    /**
     * Loads the user's profile data from Firestore and populates `currentUserData`.
     * This method now uses userDao.getUserById(userId) which is the correct and efficient approach.
     */
    private void loadUserProfileData() {
        String userId = AuthController.loggedInUserId;
        if (userId == null) {
            System.err.println("ProfileScreen: No user logged in to load profile.");
            // Fallback to a default User object if no user ID
            currentUserData = new User();
            currentUserData.setId("guest_id");
            currentUserData.setFullName("Guest User");
            currentUserData.setUsername("guest");
            currentUserData.setEmail("guest@example.com");
            currentUserData.setPhoneNumber("N/A");
            currentUserData.setDateOfBirth(LocalDate.of(2000, 1, 1));
            currentUserData.setGender("Prefer not to say");
            currentUserData.setCity("N/A");
            currentUserData.setState("N/A");
            currentUserData.setCountry("N/A");
            currentUserData.setPinZipCode("N/A");
            // currentUserData.setProfilePictureUrl("/assets/images/profile.png"); // Default path
            currentUserData.setTermsAgreed(false);
            currentUserData.setPrivacyAgreed(false);
            currentUserData.setFamilyPhoneNumber("N/A"); // Default for new fields
            currentUserData.setFriendsPhoneNumber("N/A"); // Default for new fields
            return;
        }
        try {
            Optional<User> userOptional = userDao.getUserById(userId);
            if (userOptional.isPresent()) {
                currentUserData = userOptional.get(); // Directly assign the fetched User object
                // Ensure profilePictureUrl is never null or empty, provide a default if needed
                // if (currentUserData.getProfilePictureUrl() == null || currentUserData.getProfilePictureUrl().isEmpty()) {
                //     currentUserData.setProfilePictureUrl("/assets/images/profile.png");
                // }
                System.out.println("User profile data loaded for: " + currentUserData.getFullName());
            } else {
                System.err.println("ProfileScreen: User profile not found in database for ID: " + userId);
                SceneManager.showAlert(Alert.AlertType.ERROR, "Data Error", "User profile not found in database. Setting default data.");
                currentUserData = new User();
                currentUserData.setId(userId);
                currentUserData.setFullName("Default User");
                currentUserData.setUsername("defaultuser");
                currentUserData.setEmail("default@example.com");
                currentUserData.setPhoneNumber("N/A");
                currentUserData.setDateOfBirth(LocalDate.of(2000, 1, 1));
                currentUserData.setGender("Prefer not to say");
                currentUserData.setCity("N/A");
                currentUserData.setState("N/A");
                currentUserData.setCountry("N/A");
                currentUserData.setPinZipCode("N/A");
                // currentUserData.setProfilePictureUrl("/assets/images/placeholder.png"); // Fallback to placeholder
                currentUserData.setTermsAgreed(false);
                currentUserData.setPrivacyAgreed(false);
                currentUserData.setFamilyPhoneNumber("N/A");
                currentUserData.setFriendsPhoneNumber("N/A");
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("ProfileScreen: Error loading user profile: " + e.getMessage());
            SceneManager.showAlert(Alert.AlertType.ERROR, "Load Failed", "Failed to load profile. Please check internet and try again.");
            currentUserData = new User();
            currentUserData.setId(userId);
            currentUserData.setFullName("Error User");
            currentUserData.setUsername("erroruser");
            currentUserData.setEmail("error@example.com");
            currentUserData.setPhoneNumber("N/A");
            currentUserData.setDateOfBirth(LocalDate.of(2000, 1, 1));
            currentUserData.setGender("Prefer not to say");
            currentUserData.setCity("N/A");
            currentUserData.setState("N/A");
            currentUserData.setCountry("N/A");
            currentUserData.setPinZipCode("N/A");
            // currentUserData.setProfilePictureUrl("/assets/images/placeholder.png"); // Fallback to placeholder
            currentUserData.setTermsAgreed(false);
            currentUserData.setPrivacyAgreed(false);
            currentUserData.setFamilyPhoneNumber("N/A");
            currentUserData.setFriendsPhoneNumber("N/A");
        }
        // Ensure therapyGoals and emergencyContact info is also initialized.
        if (currentUserData.getFamilyPhoneNumber() == null) {
             currentUserData.setFamilyPhoneNumber("N/A");
        }
        if (currentUserData.getFriendsPhoneNumber() == null) {
            currentUserData.setFriendsPhoneNumber("N/A");
        }
        // Ensure that TherapyGoals is initialized as it's not directly in the User model
        // You might consider adding it to the User model if it's persistent data.
        // For display in UI, if it's not coming from the model, it needs a default here.
        // This is a placeholder as the User model doesn't explicitly have a 'therapyGoals' field.
        // If your User model *does* have this field, update the User.fromMap() in UserDao.
        // For now, let's just make sure the TextAreas/Labels are populated safely.
    }


    /**
     * Creates a titled section for the profile form.
     */
    private VBox createSection(String titleText, Node content) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#333333"));
        section.getChildren().addAll(titleLabel, content);
        return section;
    }

    /**
     * Creates the read-only display version of the user's profile.
     * This is the default view.
     */
    public ScrollPane createDisplayProfileContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: #e8ebf0;");

        Label pageTitle = new Label("User Profile");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));
        content.getChildren().add(pageTitle);

        // Header Section (Display Only)
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #f0f5f9; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");

        // CORRECTED: Ensure getProfilePictureUrl() doesn't return null to createImageView
        // String displayProfilePicUrl = (currentUserData != null && currentUserData.getProfilePictureUrl() != null && !currentUserData.getProfilePictureUrl().isEmpty())
        //                              ? currentUserData.getProfilePictureUrl()
         String displayProfilePicUrl = "/assets/images/profile.png"; // Fallback to your default resource
        profileImageView = createImageView(displayProfilePicUrl, 120, 120);
        Circle clip = new Circle(60, 60, 60);
        profileImageView.setClip(clip); // This line will now always have a non-null profileImageView
        profileImageView.setStyle("-fx-border-color: #2196F3; -fx-border-width: 3; -fx-border-radius: 60;");

        Label fullNameLabel = new Label(currentUserData.getFullName());
        fullNameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        fullNameLabel.setTextFill(Color.web("#333333"));

        Label usernameLabel = new Label("@" + currentUserData.getUsername());
        usernameLabel.setFont(Font.font("Inter", 16));
        usernameLabel.setTextFill(Color.web("#555555"));
        usernameLabel.setAlignment(Pos.CENTER);


        headerBox.getChildren().addAll(profileImageView, fullNameLabel, usernameLabel);
        content.getChildren().add(createSection("Basic Details", headerBox));

        // Personal Information (Display Only)
        GridPane personalGrid = new GridPane();
        personalGrid.setVgap(10); personalGrid.setHgap(20);

        // Inline ColumnConstraints for personalGrid
        ColumnConstraints col1Personal = new ColumnConstraints();
        col1Personal.setHgrow(Priority.NEVER);
        ColumnConstraints col2Personal = new ColumnConstraints();
        col2Personal.setHgrow(Priority.ALWAYS);
        personalGrid.getColumnConstraints().addAll(col1Personal, col2Personal);

        personalGrid.add(new Label("Email Address:"), 0, 0); personalGrid.add(new Label(currentUserData.getEmail()), 1, 0);
        personalGrid.add(new Label("Phone Number:"), 0, 1); personalGrid.add(new Label(currentUserData.getPhoneNumber()), 1, 1);
        personalGrid.add(new Label("Family Phone:"), 0, 2); personalGrid.add(new Label(currentUserData.getFamilyPhoneNumber()), 1, 2);
        personalGrid.add(new Label("Friends Phone:"), 0, 3); personalGrid.add(new Label(currentUserData.getFriendsPhoneNumber()), 1, 3);
        personalGrid.add(new Label("Date of Birth:"), 0, 4); personalGrid.add(new Label(currentUserData.getDateOfBirth().toString()), 1, 4);
        personalGrid.add(new Label("Gender:"), 0, 5); personalGrid.add(new Label(currentUserData.getGender()), 1, 5);
        personalGrid.add(new Label("Location:"), 0, 6); personalGrid.add(new Label(currentUserData.getCity() + ", " + currentUserData.getState() + ", " + currentUserData.getCountry() + " - " + currentUserData.getPinZipCode()), 1, 6);

        content.getChildren().add(createSection("Personal Information", personalGrid));

        // New: Preferences & Emergency Contact Section (Display Only)
        GridPane relatedGrid = new GridPane();
        relatedGrid.setVgap(10); relatedGrid.setHgap(20);

        // Inline ColumnConstraints for relatedGrid
        ColumnConstraints col1Related = new ColumnConstraints();
        col1Related.setHgrow(Priority.NEVER);
        ColumnConstraints col2Related = new ColumnConstraints();
        col2Related.setHgrow(Priority.ALWAYS);
        relatedGrid.getColumnConstraints().addAll(col1Related, col2Related);

        relatedGrid.add(new Label("Emergency Contact:"), 0, 0);
        relatedGrid.add(new Label(currentUserData.getFamilyPhoneNumber() + " (Family Phone) / " + currentUserData.getFriendsPhoneNumber() + " (Friends Phone)"), 1, 0);

        relatedGrid.add(new Label("Therapy Goals:"), 0, 1);
        Label therapyGoalsLabel = new Label("My main goal is to manage stress."); // Default placeholder
        // If you had a therapy goals field in your User model, you'd use currentUserData.getTherapyGoals() here.
        therapyGoalsLabel.setWrapText(true);
        relatedGrid.add(therapyGoalsLabel, 1, 1);

        relatedGrid.add(new Label("Notifications:"), 0, 2);
        String notifications = "";
        if (currentUserData.isTermsAgreed()) notifications += "Email";
        if (currentUserData.isPrivacyAgreed()) notifications += (notifications.isEmpty() ? "" : ", ") + "SMS";
        if (notifications.isEmpty()) notifications = "None";
        relatedGrid.add(new Label(notifications), 1, 2);

        content.getChildren().add(createSection("Preferences & Emergency Contact", relatedGrid));


        // Edit Profile Button
        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        editProfileButton.setTextFill(Color.WHITE);
        editProfileButton.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 10; -fx-padding: 12 30;");
        setHoverStyle(editProfileButton, "-fx-background-color: #42A5F5; -fx-background-radius: 10; -fx-padding: 12 30;", "-fx-background-color: #2196F3; -fx-background-radius: 10; -fx-padding: 12 30;");
        editProfileButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(editProfileButton, new Insets(30, 0, 0, 0));
        editProfileButton.setOnAction(e -> rootLayout.setCenter(createEditProfileContent()));

        content.getChildren().add(editProfileButton);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #e8ebf0;");
        return scrollPane;
    }

    /**
     * Creates the editable version of the user's profile.
     */
    private ScrollPane createEditProfileContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: #e8ebf0;");

        Label pageTitle = new Label("Edit Profile");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));
        content.getChildren().add(pageTitle);

        // Header Section (Editable)
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #f0f5f9; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);");

        // CORRECTED: Ensure getProfilePictureUrl() doesn't return null to createImageView
        // String editProfilePicUrl = (currentUserData != null && currentUserData.getProfilePictureUrl() != null && !currentUserData.getProfilePictureUrl().isEmpty())
        //                              ? currentUserData.getProfilePictureUrl()
          String editProfilePicUrl =  "/assets/images/profile.png"; // Fallback to your default resource
        profileImageView = createImageView(editProfilePicUrl, 120, 120);
        Circle clip = new Circle(60, 60, 60);
        profileImageView.setClip(clip);
        profileImageView.setStyle("-fx-border-color: #2196F3; -fx-border-width: 3; -fx-border-radius: 60;");

        Button changePicButton = new Button("Change Picture");
        changePicButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;");
        setHoverStyle(changePicButton, "-fx-background-color: #d0d0d0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;", "-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 5 10; -fx-font-size: 10px;");
        changePicButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    String newImageUrl = selectedFile.toURI().toURL().toExternalForm();
                    profileImageView.setImage(new Image(newImageUrl));
                    // currentUserData.setProfilePictureUrl(newImageUrl); // Update the model directly
                    System.out.println("New Profile Pic: " + selectedFile.getAbsolutePath());
                } catch (Exception ex) {
                    System.err.println("Error loading image: " + ex.getMessage());
                }
            }
        });

        editFullNameField = new TextField(currentUserData.getFullName());
        editFullNameField.setPromptText("Enter full name");
        editFullNameField.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        editFullNameField.setAlignment(Pos.CENTER);
        editFullNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 5; -fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: #333333;");

        Label usernameDisplayLabel = new Label("@" + currentUserData.getUsername());
        usernameDisplayLabel.setFont(Font.font("Inter", 16));
        usernameDisplayLabel.setTextFill(Color.web("#555555"));
        usernameDisplayLabel.setAlignment(Pos.CENTER);


        headerBox.getChildren().addAll(profileImageView, changePicButton, editFullNameField, usernameDisplayLabel);
        content.getChildren().add(createSection("Edit Basic Details", headerBox));

        // Personal Information (Editable)
        GridPane personalGrid = new GridPane();
        personalGrid.setVgap(15); personalGrid.setHgap(20);

        // Inline ColumnConstraints for personalGrid
        ColumnConstraints col1Personal = new ColumnConstraints();
        col1Personal.setHgrow(Priority.NEVER);
        ColumnConstraints col2Personal = new ColumnConstraints();
        col2Personal.setHgrow(Priority.ALWAYS);
        personalGrid.getColumnConstraints().addAll(col1Personal, col2Personal);

        personalGrid.add(new Label("Email Address:"), 0, 0);
        editEmailField = new TextField(currentUserData.getEmail());
        editEmailField.setPromptText("Enter email address");
        editEmailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editEmailField.setEditable(false);
        editEmailField.setDisable(true);

        personalGrid.add(editEmailField, 1, 0);

        personalGrid.add(new Label("Phone Number:"), 0, 1);
        editPhoneField = new TextField(currentUserData.getPhoneNumber());
        editPhoneField.setPromptText("e.g., 9876543210");
        editPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        personalGrid.add(editPhoneField, 1, 1);

        personalGrid.add(new Label("Family Phone:"), 0, 2);
        editFamilyPhoneField = new TextField(currentUserData.getFamilyPhoneNumber());
        editFamilyPhoneField.setPromptText("e.g., 1234567890");
        editFamilyPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editFamilyPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editFamilyPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        personalGrid.add(editFamilyPhoneField, 1, 2);

        personalGrid.add(new Label("Friends Phone:"), 0, 3);
        editFriendsPhoneField = new TextField(currentUserData.getFriendsPhoneNumber());
        editFriendsPhoneField.setPromptText("e.g., 0987654321");
        editFriendsPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editFriendsPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editFriendsPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        personalGrid.add(editFriendsPhoneField, 1, 3);


        personalGrid.add(new Label("Date of Birth:"), 0, 4);
        editDobPicker = new DatePicker(currentUserData.getDateOfBirth());
        editDobPicker.setPromptText("Select Date");
        editDobPicker.setMaxWidth(Double.MAX_VALUE);
        editDobPicker.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        personalGrid.add(editDobPicker, 1, 4);

        personalGrid.add(new Label("Gender:"), 0, 5);
        editGenderComboBox = new ComboBox<>();
        editGenderComboBox.setPromptText("Select Gender");
        editGenderComboBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        editGenderComboBox.setValue(currentUserData.getGender());
        editGenderComboBox.setMaxWidth(Double.MAX_VALUE);
        editGenderComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        personalGrid.add(editGenderComboBox, 1, 5);

        personalGrid.add(new Label("City:"), 0, 6);
        editCityField = new TextField(currentUserData.getCity());
        editCityField.setPromptText("Enter city");
        editCityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        personalGrid.add(editCityField, 1, 6);

        personalGrid.add(new Label("State:"), 0, 7);
        editStateField = new TextField(currentUserData.getState());
        editStateField.setPromptText("Enter state");
        editStateField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        personalGrid.add(editStateField, 1, 7);

        personalGrid.add(new Label("Country:"), 0, 8);
        editCountryField = new TextField(currentUserData.getCountry());
        editCountryField.setPromptText("Enter country");
        editCountryField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        personalGrid.add(editCountryField, 1, 8);

        personalGrid.add(new Label("ZIP/PIN Code:"), 0, 9);
        editPinZipField = new TextField(currentUserData.getPinZipCode());
        editPinZipField.setPromptText("Enter ZIP/PIN code");
        editPinZipField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editPinZipField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editPinZipField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        personalGrid.add(editPinZipField, 1, 9);

        content.getChildren().add(createSection("Edit Personal Information", personalGrid));

        // New: Edit Preferences & Emergency Contact Section
        GridPane relatedGrid = new GridPane();
        relatedGrid.setVgap(15); relatedGrid.setHgap(20);

        // Inline ColumnConstraints for relatedGrid
        ColumnConstraints col1Related = new ColumnConstraints();
        col1Related.setHgrow(Priority.NEVER);
        ColumnConstraints col2Related = new ColumnConstraints();
        col2Related.setHgrow(Priority.ALWAYS);
        relatedGrid.getColumnConstraints().addAll(col1Related, col2Related);

        relatedGrid.add(new Label("Emergency Contact Name:"), 0, 0);
        editEmergencyContactNameField = new TextField(currentUserData.getFamilyPhoneNumber()); // Using FamilyPhone as dummy
        editEmergencyContactNameField.setPromptText("Name of emergency contact");
        editEmergencyContactNameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        relatedGrid.add(editEmergencyContactNameField, 1, 0);

        relatedGrid.add(new Label("Emergency Contact Phone:"), 0, 1);
        editEmergencyContactPhoneField = new TextField(currentUserData.getFriendsPhoneNumber()); // Using FriendsPhone as dummy
        editEmergencyContactPhoneField.setPromptText("Phone of emergency contact");
        editEmergencyContactPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        editEmergencyContactPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editEmergencyContactPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        relatedGrid.add(editEmergencyContactPhoneField, 1, 1);

        relatedGrid.add(new Label("Therapy Goals:"), 0, 2);
        editTherapyGoalsTextArea = new TextArea("My main goal is to manage stress."); // Default value
        editTherapyGoalsTextArea.setPromptText("Describe your therapy goals (optional)");
        editTherapyGoalsTextArea.setWrapText(true);
        editTherapyGoalsTextArea.setPrefRowCount(4);
        editTherapyGoalsTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        relatedGrid.add(editTherapyGoalsTextArea, 1, 2);

        relatedGrid.add(new Label("Email Notifications:"), 0, 3);
        editEmailNotificationsCheckbox = new CheckBox();
        editEmailNotificationsCheckbox.setSelected(currentUserData.isTermsAgreed()); // Using terms agreed as proxy
        relatedGrid.add(editEmailNotificationsCheckbox, 1, 3);

        relatedGrid.add(new Label("SMS Notifications:"), 0, 4);
        editSmsNotificationsCheckbox = new CheckBox();
        editSmsNotificationsCheckbox.setSelected(currentUserData.isPrivacyAgreed()); // Using privacy agreed as proxy
        relatedGrid.add(editSmsNotificationsCheckbox, 1, 4);

        content.getChildren().add(createSection("Edit Preferences & Emergency Contact", relatedGrid));


        // Change Password Button
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        changePasswordButton.setTextFill(Color.WHITE);
        changePasswordButton.setStyle("-fx-background-color: #FFC107; -fx-background-radius: 8; -fx-padding: 10 20;");
        setHoverStyle(changePasswordButton, "-fx-background-color: #FFD54F; -fx-background-radius: 8; -fx-padding: 10 20;", "-fx-background-color: #FFC107; -fx-background-radius: 8; -fx-padding: 10 20;");
        changePasswordButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(changePasswordButton, new Insets(10, 0, 0, 0));
        changePasswordButton.setOnAction(e -> openChangePasswordDialog(primaryStage));

        content.getChildren().add(changePasswordButton);


        // Save Changes Button
        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        saveChangesButton.setTextFill(Color.WHITE);
        saveChangesButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 10; -fx-padding: 12 30;");
        setHoverStyle(saveChangesButton, "-fx-background-color: #66BB6A; -fx-background-radius: 10; -fx-padding: 12 30;", "-fx-background-color: #4CAF50; -fx-background-radius: 10; -fx-padding: 12 30;");
        saveChangesButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(saveChangesButton, new Insets(30, 0, 0, 0));
        saveChangesButton.setOnAction(e -> handleSaveChanges());

        content.getChildren().add(saveChangesButton);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #e8ebf0;");
        return scrollPane;
    }

    /**
     * Opens a dialog for changing the user's password.
     */
    private void openChangePasswordDialog(Stage ownerStage) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update your account password");

        ButtonType changeButtonType = new ButtonType("Change Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Inline ColumnConstraints for dialog grid
        ColumnConstraints col1Dialog = new ColumnConstraints();
        col1Dialog.setHgrow(Priority.NEVER);
        ColumnConstraints col2Dialog = new ColumnConstraints();
        col2Dialog.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1Dialog, col2Dialog);

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmNewPasswordField = new PasswordField();
        confirmNewPasswordField.setPromptText("Confirm New Password");

        grid.add(new Label("Current Password:"), 0, 0); grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1); grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm New Password:"), 0, 2); grid.add(confirmNewPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);

        newPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            changeButton.setDisable(newText.isEmpty() || !newText.equals(confirmNewPasswordField.getText()));
        });
        confirmNewPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            changeButton.setDisable(newText.isEmpty() || !newText.equals(newPasswordField.getText()));
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                System.out.println("Change Password initiated.");
                System.out.println("Old: " + oldPasswordField.getText());
                System.out.println("New: " + newPasswordField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Password Change", "Password change initiated. (Validation and update in backend)");
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Handles the "Save Changes" button click in edit mode.
     * Collects data from editable fields, updates the data model, and switches back to display mode.
     */
    private void handleSaveChanges() {
        // Collect data from all editable fields, trimming whitespace
        String newFullName = editFullNameField.getText().trim();
        String newEmail = editEmailField.getText().trim(); // Email is read-only from Firebase Auth, but we take it from the field for consistency if it were editable
        String newPhone = editPhoneField.getText().trim();
        LocalDate newDob = editDobPicker.getValue();
        String newGender = editGenderComboBox.getValue();
        String newCity = editCityField.getText().trim();
        String newState = editStateField.getText().trim();
        String newCountry = editCountryField.getText().trim();
        String newPinZip = editPinZipField.getText().trim();

        // Collect new related fields data
        String newEmergencyContactName = editEmergencyContactNameField.getText().trim();
        String newEmergencyContactPhone = editEmergencyContactPhoneField.getText().trim();
        String newFamilyPhone = editFamilyPhoneField.getText().trim();
        String newFriendsPhone = editFriendsPhoneField.getText().trim();
        String newTherapyGoals = editTherapyGoalsTextArea.getText().trim();
        boolean newEmailNotifications = editEmailNotificationsCheckbox.isSelected();
        boolean newSmsNotifications = editSmsNotificationsCheckbox.isSelected();

        // Basic validation for required fields
        if (newFullName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || newDob == null || newGender == null ||
            newFamilyPhone.isEmpty() || newFriendsPhone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all required fields for profile update.");
            return;
        }

        String currentProfilePicUrl = (profileImageView != null && profileImageView.getImage() != null) ? profileImageView.getImage().getUrl() : null;

        // Update the currentUserData object (com.lifecompass.model.User directly)
        // Setters are used to update the properties of the existing model object
        currentUserData.setFullName(newFullName);
        // currentUserData.setUsername(currentUserData.getUsername()); // Username not changed via this screen
        currentUserData.setEmail(newEmail);
        currentUserData.setPhoneNumber(newPhone);
        currentUserData.setDateOfBirth(newDob);
        currentUserData.setGender(newGender);
        currentUserData.setCity(newCity);
        currentUserData.setState(newState);
        currentUserData.setCountry(newCountry);
        currentUserData.setPinZipCode(newPinZip);
        currentUserData.setFamilyPhoneNumber(newFamilyPhone);
        currentUserData.setFriendsPhoneNumber(newFriendsPhone);
        // Assuming TherapyGoals are not directly in User model, might need separate DAO for this
        currentUserData.setTermsAgreed(newEmailNotifications); // Mapping to User model field
        currentUserData.setPrivacyAgreed(newSmsNotifications); // Mapping to User model field
        // currentUserData.setProfilePictureUrl(currentProfilePicUrl);

        // --- Send updated data to Firestore ---
        String userId = AuthController.loggedInUserId;
        if (userId != null) {
            try {
                // Pass the directly modified currentUserData object to the DAO
                userDao.updateUser(currentUserData);

                System.out.println("Profile changes saved for: " + currentUserData.getFullName());
                dashboardInstance.updateUsername(currentUserData.getFullName());
                // dashboardInstance.updateProfilePicture(currentUserData.getProfilePictureUrl()); // Use getter from User model

                showAlert(Alert.AlertType.INFORMATION, "Profile Update", "Profile changes saved successfully!");
                rootLayout.setCenter(createDisplayProfileContent());
            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Error updating user profile in Firestore: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Profile Update Failed", "Failed to save changes to database. Please try again.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "No user ID found for update.");
        }
    }

    /**
     * Helper method to show an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
                    System.err.println("Also failed to load placeholder.png!");
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

    /**
     * Helper method to apply generic hover styles to any Region.
     */
    private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
        region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
        region.setOnMouseExited(e -> region.setStyle(normalStyle));
    }

    // Removed the UserData record as we are now directly using com.lifecompass.model.User
}