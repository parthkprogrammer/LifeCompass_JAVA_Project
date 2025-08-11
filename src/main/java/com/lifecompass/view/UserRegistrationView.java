package com.lifecompass.view;

import com.lifecompass.controller.UserRegistrationController;
import com.lifecompass.util.SceneManager;
import com.lifecompass.util.ColumnConstraintsHelper;
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
import javafx.stage.Stage;
import javafx.scene.Node;

import java.time.LocalDate;

/**
 * This class creates the UI for the User Registration page.
 * It includes sections for Personal Information, Account Credentials, optional Location,
 * and Security & Agreement. Family and Friends phone numbers are now compulsory.
 */
public class UserRegistrationView extends Application {

    private VBox rootLayout;

    private TextField fullNameField;
    private ComboBox<String> genderComboBox;
    private DatePicker dobPicker;
    private TextField phoneField;
    private TextField familyPhoneField;
    private TextField friendsPhoneField;
    private TextField emailField;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField cityField;
    private TextField stateField;
    private TextField countryField;
    private TextField pinZipField;
    private CheckBox termsCheckbox;
    private CheckBox privacyCheckbox;

    private Button registerButton;
    private final UserRegistrationController registrationController = new UserRegistrationController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(30, 50, 30, 50));
        rootLayout.setStyle("-fx-background-color: #f0f2f5;");

        Label pageTitle = new Label("User Registration");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        pageTitle.setTextFill(Color.web("#333333"));
        pageTitle.setAlignment(Pos.CENTER);
        VBox.setMargin(pageTitle, new Insets(0, 0, 20, 0));

        rootLayout.getChildren().add(pageTitle);

        rootLayout.getChildren().add(createPersonalInformationSection());
        rootLayout.getChildren().add(createAccountCredentialsSection());
        rootLayout.getChildren().add(createLocationSection());
        rootLayout.getChildren().add(createSecurityAgreementSection());

        registerButton = new Button("Register Now");
        registerButton.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 10; -fx-padding: 12 30;"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 10; -fx-padding: 12 30;"));
        registerButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(registerButton, new Insets(30, 0, 0, 0));

        updateRegisterButtonState();

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());
        familyPhoneField.textProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());
        friendsPhoneField.textProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());
        termsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());
        privacyCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());


        registerButton.setOnAction(e -> handleRegistration(primaryStage));
        rootLayout.getChildren().add(registerButton);

        Hyperlink backToLoginLink = new Hyperlink("Back to User Login");
        backToLoginLink.setOnAction(e -> handleBackToLogin(primaryStage));
        rootLayout.getChildren().add(backToLoginLink);

        ScrollPane scrollPane = new ScrollPane(rootLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(scrollPane, 700, 800);
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateRegisterButtonState() {
        boolean isTermsAgreed = (termsCheckbox != null && termsCheckbox.isSelected());
        boolean isPrivacyAgreed = (privacyCheckbox != null && privacyCheckbox.isSelected());
        boolean isPersonalPhoneFilled = (phoneField != null && !phoneField.getText().trim().isEmpty());
        boolean isFamilyPhoneFilled = (familyPhoneField != null && !familyPhoneField.getText().trim().isEmpty());
        boolean isFriendsPhoneFilled = (friendsPhoneField != null && !friendsPhoneField.getText().trim().isEmpty());

        boolean isFormValid = isTermsAgreed && isPrivacyAgreed && isPersonalPhoneFilled && isFamilyPhoneFilled && isFriendsPhoneFilled;
        if (registerButton != null) {
            registerButton.setDisable(!isFormValid);
        }
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

    private VBox createPersonalInformationSection() {
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
        phoneField.setPromptText("Enter your phone number");
        phoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(phoneField, 1, 3);

        grid.add(new Label("Family Phone Number:"), 0, 4);
        familyPhoneField = new TextField();
        familyPhoneField.setPromptText("Enter family phone number");
        familyPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        familyPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                familyPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(familyPhoneField, 1, 4);

        grid.add(new Label("Friends Phone Number:"), 0, 5);
        friendsPhoneField = new TextField();
        friendsPhoneField.setPromptText("Enter friends phone number");
        friendsPhoneField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        friendsPhoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                friendsPhoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(friendsPhoneField, 1, 5);

        grid.add(new Label("Email Address:"), 0, 6);
        emailField = new TextField();
        emailField.setPromptText("e.g., your.email@example.com");
        emailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(emailField, 1, 6);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        return createSection("Personal Information", grid);
    }

    private VBox createAccountCredentialsSection() {
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
        return createSection("Account Credentials", grid);
    }

    private VBox createLocationSection() {
        VBox locationSection = new VBox(10);
        locationSection.getChildren().add(new Label("Note: This section is optional."));
        locationSection.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: #777777;");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("City:"), 0, 0);
        cityField = new TextField();
        cityField.setPromptText("Enter city");
        cityField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(cityField, 1, 0);

        grid.add(new Label("State:"), 0, 1);
        stateField = new TextField();
        stateField.setPromptText("Enter state");
        stateField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(stateField, 1, 1);

        grid.add(new Label("Country:"), 0, 2);
        countryField = new TextField();
        countryField.setPromptText("Enter country");
        countryField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        grid.add(countryField, 1, 2);

        grid.add(new Label("PIN/ZIP Code:"), 0, 3);
        pinZipField = new TextField();
        pinZipField.setPromptText("Enter PIN/ZIP code");
        pinZipField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;");
        pinZipField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pinZipField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        grid.add(pinZipField, 1, 3);

        ColumnConstraintsHelper.setColumnConstraints(grid);
        locationSection.getChildren().add(grid);
        return createSection("Location (Optional)", locationSection);
    }

    private VBox createSecurityAgreementSection() {
        VBox securitySection = new VBox(10);
        securitySection.setPadding(new Insets(10, 0, 0, 0));

        termsCheckbox = new CheckBox("I agree to the Terms and Conditions");
        termsCheckbox.setFont(Font.font("Inter", 13));
        termsCheckbox.setTextFill(Color.web("#333333"));
        termsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());


        privacyCheckbox = new CheckBox("I agree to the Privacy Policy");
        privacyCheckbox.setFont(Font.font("Inter", 13));
        privacyCheckbox.setTextFill(Color.web("#333333"));
        privacyCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());

        securitySection.getChildren().addAll(termsCheckbox, privacyCheckbox);
        return createSection("Security & Agreement", securitySection);
    }

    private void handleRegistration(Stage currentStage) {
        String fullName = fullNameField.getText();
        String gender = genderComboBox.getValue();
        LocalDate dateOfBirth = dobPicker.getValue();
        String phoneNumber = phoneField.getText();
        String familyPhoneNumber = familyPhoneField.getText();
        String friendsPhoneNumber = friendsPhoneField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String country = countryField.getText();
        String pinZipCode = pinZipField.getText();
        boolean termsAgreed = termsCheckbox.isSelected();
        boolean privacyAgreed = privacyCheckbox.isSelected();

        if (fullName.isEmpty() || gender == null || dateOfBirth == null || phoneNumber.isEmpty() ||
            familyPhoneNumber.isEmpty() || friendsPhoneNumber.isEmpty() || email.isEmpty() || username.isEmpty() ||
            password.isEmpty() || confirmPasswordField.getText().isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all required personal and account fields.");
            return;
        }

        if (password.length() < 6) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Password must be at least 6 characters long.");
            return;
        }
        if (!password.equals(confirmPasswordField.getText())) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        if (!termsAgreed) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Agreement Required", "Please agree to the Terms and Conditions.");
            return;
        }
        if (!privacyAgreed) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Agreement Required", "Please agree to the Privacy Policy.");
            return;
        }

        boolean success = registrationController.registerUser(
                fullName, gender, dateOfBirth, phoneNumber, familyPhoneNumber,
                friendsPhoneNumber, email, username, password, city, state,
                country, pinZipCode, termsAgreed, privacyAgreed
        );

        if (success) {
            SceneManager.showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User registered successfully!");
            SceneManager.switchScreen(new UserLoginScreen(), "User Login");
            currentStage.close();
        } else {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to register user. Please check your inputs or try again.");
        }
    }

    private void handleBackToLogin(Stage currentStage) {
        SceneManager.switchScreen(new UserLoginScreen(), "User Login");
        currentStage.close();
    }

   
}