// package com.lifecompass.view;
// import com.lifecompass.LoginScreen; // Corrected to com.lifecompass.view.LoginScreen if it's in view package
// import com.lifecompass.controller.AuthController;
// import com.lifecompass.util.SceneManager;
// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert; // FIX: Added Alert import
// import javafx.scene.control.Button;
// import javafx.scene.control.Hyperlink;
// import javafx.scene.control.Label;
// import javafx.scene.control.PasswordField;
// import javafx.scene.control.TextField;
// import javafx.scene.effect.DropShadow;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.stage.Stage;

// import com.lifecompass.view.UserDashboardScreen; // FIX: Added UserDashboardScreen import
// import com.lifecompass.view.UserRegistrationView; // Assuming this class exists for handleRegisterButton

// public class UserLoginScreen extends Application {

//     private TextField emailField;
//     private PasswordField passwordField;
//     private final AuthController authController = new AuthController();

//     @Override
//     public void start(Stage stage) {
//         // Ensure SceneManager has a reference to the primary stage
//         SceneManager.setPrimaryStage(stage);

//         Label icon = new Label("💜");
//         icon.setFont(Font.font(32));
//         icon.setTextFill(Color.WHITE);

//         StackPane iconCircle = new StackPane(icon);
//         iconCircle.setPrefSize(80, 80);
//         iconCircle.setMaxSize(80, 80);
//         iconCircle.setMinSize(80, 80);
//         iconCircle.setStyle("-fx-background-color: linear-gradient(to right, #6a5acd, #8a2be2); " +
//                 "-fx-background-radius: 50%; -fx-alignment: center;");

//         Label title = new Label("Login for User");
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

//         Label subtitle = new Label("Sign in to access your user dashboard");
//         subtitle.setTextFill(Color.GRAY);
//         subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

//         Label emailLabel = new Label("Email");
//         emailField = new TextField();
//         emailField.setPromptText("Enter your email");
//         emailField.setPrefWidth(300);
//         emailField.setMaxWidth(300);

//         Label passwordLabel = new Label("Password");
//         passwordField = new PasswordField();
//         passwordField.setPromptText("Enter password");
//         passwordField.setPrefWidth(300);
//         passwordField.setMaxWidth(300);

//         Button loginBtn = new Button("Login →");
//         loginBtn.setPrefWidth(300);
//         loginBtn.setMaxWidth(300);
//         loginBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
//         loginBtn.setOnAction(e -> handleLoginButton(stage)); // Pass the stage

//         Button registerBtn = new Button("Register Now");
//         registerBtn.setPrefWidth(300);
//         registerBtn.setMaxWidth(300);
//         registerBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-size: 14px;");
//         registerBtn.setOnAction(e -> handleRegisterButton(stage)); // Pass the stage

//         Hyperlink backBtn = new Hyperlink("Back to Role Selection");
//         backBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
//         backBtn.setOnAction(e -> handleBackToRoleSelection(stage)); // Pass the stage

//         VBox form = new VBox(10,
//                 iconCircle,
//                 title,
//                 subtitle,
//                 emailLabel,
//                 emailField,
//                 passwordLabel,
//                 passwordField,
//                 loginBtn,
//                 registerBtn,
//                 backBtn);
//         form.setAlignment(Pos.CENTER);
//         form.setPadding(new Insets(30));
//         form.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
//         form.setEffect(new DropShadow(10, Color.gray(0.6)));

//         form.setOnMouseEntered(e -> form.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 0); -fx-scale-y: 1.02; -fx-scale-x: 1.02;"));
//         form.setOnMouseExited(e -> form.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 0); -fx-scale-y: 1.0; -fx-scale-x: 1.0;"));


//         StackPane root = new StackPane(form);
//         root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eef2ff, #dbe7ff);");

//         Scene scene = new Scene(root, 1540, 860);
//         stage.setScene(scene);
//         stage.setTitle("User Login");
//         stage.show();
//     }

//     private void handleLoginButton(Stage currentStage) {
//         String email = emailField.getText();
//         String password = passwordField.getText();

//         if (email.isEmpty() || password.isEmpty()) {
//             SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both email and password.");
//             return;
//         }

//         boolean success = authController.login(email, password, "user");

//         if (success) {
//             // FIX: Pass currentStage to UserDashboardScreen constructor
//             // UserDashboardScreen's start method will then be called, which sets its content on this stage.
//             try {
//                 new UserDashboardScreen(currentStage).start(currentStage);
//             } catch (Exception ex) {
//                 System.err.println("Error launching UserDashboardScreen: " + ex.getMessage());
//                 ex.printStackTrace();
//                 SceneManager.showAlert(Alert.AlertType.ERROR, "Login Error", "Failed to launch user dashboard.");
//             }
//             // currentStage.close(); // UserDashboardScreen.start() will replace the scene, so closing is optional
//         } else {
//             // AuthController.login() already shows an alert on failure
//         }
//     }

//     private void handleRegisterButton(Stage currentStage) {
//         // Assuming UserRegistrationView is an Application subclass like UserLoginScreen
//         // FIX: Pass currentStage to UserRegistrationView constructor if it takes one, or ensure its start method handles it.
//         // For now, assuming default constructor and that its start() method handles stage correctly.
//         try {
//             new UserRegistrationView().start(currentStage); // Calling start on a new instance
//         } catch (Exception e) {
//             System.err.println("Error launching UserRegistrationView: " + e.getMessage());
//             e.printStackTrace();
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Failed to launch registration screen.");
//         }
//         // currentStage.close(); // Keep this if you want to close current stage
//     }

//     private void handleBackToRoleSelection(Stage currentStage) {
//         // Assuming LoginScreen is an Application subclass like UserLoginScreen
//         // FIX: Pass currentStage to LoginScreen constructor if it takes one, or ensure its start method handles it.
//         // For now, assuming default constructor and that its start() method handles stage correctly.
//         try {
//             new LoginScreen().start(currentStage); // Calling start on a new instance
//         } catch (Exception e) {
//             System.err.println("Error returning to LoginScreen: " + e.getMessage());
//             e.printStackTrace();
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to role selection.");
//         }
//         // currentStage.close(); // Keep this if you want to close current stage
//     }
// }

package com.lifecompass.view;
import com.lifecompass.LoginScreen;
import com.lifecompass.controller.AuthController;
import com.lifecompass.util.SceneManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane; // New Import
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import com.lifecompass.view.UserDashboardScreen;
import com.lifecompass.view.UserRegistrationView;

public class UserLoginScreen extends Application {

    private TextField emailField;
    private PasswordField passwordField;
    private final AuthController authController = new AuthController();

    @Override
    public void start(Stage stage) {
        // Ensure SceneManager has a reference to the primary stage
        SceneManager.setPrimaryStage(stage);

        Label icon = new Label("💜");
        icon.setFont(Font.font(32));
        icon.setTextFill(Color.WHITE);

        StackPane iconCircle = new StackPane(icon);
        iconCircle.setPrefSize(80, 80);
        iconCircle.setMaxSize(80, 80);
        iconCircle.setMinSize(80, 80);
        iconCircle.setStyle("-fx-background-color: linear-gradient(to right, #6a5acd, #8a2be2); -fx-background-radius: 50%; -fx-alignment: center;");

        Label title = new Label("Login for User");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label subtitle = new Label("Sign in to access your user dashboard");
        subtitle.setTextFill(Color.GRAY);
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Label emailLabel = new Label("Email");
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(300);
        emailField.setMaxWidth(300);

        Label passwordLabel = new Label("Password");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(300);
        passwordField.setMaxWidth(300);

        Button loginBtn = new Button("Login →");
        loginBtn.setPrefWidth(300);
        loginBtn.setMaxWidth(300);
        loginBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
        loginBtn.setOnAction(e -> handleLoginButton(stage));

        Button registerBtn = new Button("Register Now");
        registerBtn.setPrefWidth(300);
        registerBtn.setMaxWidth(300);
        registerBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-size: 14px;");
        registerBtn.setOnAction(e -> handleRegisterButton(stage));

        Hyperlink backBtn = new Hyperlink("Back to Role Selection");
        backBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        backBtn.setOnAction(e -> handleBackToRoleSelection(stage));

        VBox formContent = new VBox(10,
                iconCircle,
                title,
                subtitle,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                loginBtn,
                registerBtn,
                backBtn);
        formContent.setAlignment(Pos.CENTER);
        formContent.setPadding(new Insets(30));
        formContent.setStyle("-fx-background-color: transparent;"); // Ensure the VBox has no background

        // Use a BorderPane to create the "box" around the form content
        BorderPane formBox = new BorderPane();
        formBox.setCenter(formContent);
        formBox.setMaxSize(400, 550); // Set a max size for the entire box
        
        // Initial style with a prominent black border and dropshadow
        formBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 12px; " +
                         "-fx-border-color: black; " +
                         "-fx-border-width: 3; " +
                         "-fx-border-radius: 12px;");
        formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));

        // Highlight on hover
        formBox.setOnMouseEntered(e -> {
            formBox.setStyle("-fx-background-color: white; " +
                             "-fx-background-radius: 12px; " +
                             "-fx-border-color: #8a2be2; " +
                             "-fx-border-width: 2; " +
                             "-fx-border-radius: 12px;");
            formBox.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.3)));
        });

        // Revert style on exit
        formBox.setOnMouseExited(e -> {
            formBox.setStyle("-fx-background-color: white; " +
                             "-fx-background-radius: 12px; " +
                             "-fx-border-color: black; " +
                             "-fx-border-width: 3; " +
                             "-fx-border-radius: 12px;");
            formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));
        });

        StackPane root = new StackPane(formBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eef2ff, #dbe7ff);");

        Scene scene = new Scene(root, 1540, 860);
        stage.setScene(scene);
        stage.setTitle("User Login");
        stage.show();
    }

    private void handleLoginButton(Stage currentStage) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both email and password.");
            return;
        }

        boolean success = authController.login(email, password, "user");

        if (success) {
            try {
                new UserDashboardScreen(currentStage).start(currentStage);
            } catch (Exception ex) {
                System.err.println("Error launching UserDashboardScreen: " + ex.getMessage());
                ex.printStackTrace();
                SceneManager.showAlert(Alert.AlertType.ERROR, "Login Error", "Failed to launch user dashboard.");
            }
        } else {
            // AuthController.login() already shows an alert on failure
        }
    }

    private void handleRegisterButton(Stage currentStage) {
        try {
            new UserRegistrationView().start(currentStage);
        } catch (Exception e) {
            System.err.println("Error launching UserRegistrationView: " + e.getMessage());
            e.printStackTrace();
            SceneManager.showAlert(Alert.AlertType.ERROR, "Registration Error", "Failed to launch registration screen.");
        }
    }

    private void handleBackToRoleSelection(Stage currentStage) {
        try {
            new LoginScreen().start(currentStage);
        } catch (Exception e) {
            System.err.println("Error returning to LoginScreen: " + e.getMessage());
            e.printStackTrace();
            SceneManager.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to role selection.");
        }
    }
}