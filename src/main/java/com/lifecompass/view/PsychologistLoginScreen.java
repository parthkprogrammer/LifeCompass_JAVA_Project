// package com.lifecompass.view;

// import com.lifecompass.LoginScreen;
// import com.lifecompass.controller.AuthController;
// import com.lifecompass.controller.psychologist.PsychologistDashboardController;
// import com.lifecompass.util.SceneManager;
// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
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

// import com.lifecompass.view.Psycologiestview.PsychologistDashboard;

// public class PsychologistLoginScreen extends Application {

//     private TextField emailField;
//     private PasswordField passwordField;
//     private final AuthController authController = new AuthController();

//     @Override
//     public void start(Stage stage) {

//         Label icon = new Label("💜");
//         icon.setFont(Font.font(32));
//         icon.setTextFill(Color.WHITE);

//         StackPane iconCircle = new StackPane(icon);
//         iconCircle.setPrefSize(80, 80);
//         iconCircle.setMaxSize(80, 80);
//         iconCircle.setMinSize(80, 80);
//         iconCircle.setStyle("-fx-background-color: linear-gradient(to right, #6a5acd, #8a2be2); " +
//                 "-fx-background-radius: 50%; " +
//                 "-fx-alignment: center;");

//         Label title = new Label("Login for Psychologist");
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//         Label subtitle = new Label("Sign in to access your dashboard");
//         subtitle.setTextFill(Color.GRAY);
//         subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

//         Label emailLabel = new Label("Email");
//         emailField = new TextField();
//         emailField.setPromptText("Enter your email");
//         emailField.setPrefWidth(300);
//         emailField.setMaxWidth(300);

//         Label passwordLabel = new Label("Password");
//         passwordField = new PasswordField();
//         passwordField.setPromptText("Enter your password");
//         passwordField.setPrefWidth(300);
//         passwordField.setMaxWidth(300);

//         Button signInBtn = new Button("Sign In →");
//         signInBtn.setPrefWidth(300);
//         signInBtn.setMaxWidth(300);
//         signInBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
//         signInBtn.setOnAction(e -> handleLoginButton(stage));

//         Button registerBtn = new Button("Register Now");
//         registerBtn.setPrefWidth(300);
//         registerBtn.setMaxWidth(300);
//         registerBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-size: 14px;");
//         registerBtn.setOnAction(e -> handleRegisterButton(stage));

//         Hyperlink backLink = new Hyperlink("Back to Role Selection");
//         backLink.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
//         backLink.setOnAction(e -> handleBackToRoleSelection(stage));

//         VBox form = new VBox(10,
//                 iconCircle,
//                 title,
//                 subtitle,
//                 emailLabel,
//                 emailField,
//                 passwordLabel,
//                 passwordField,
//                 signInBtn,
//                 registerBtn,
//                 backLink);

//         form.setAlignment(Pos.CENTER);
//         form.setPadding(new Insets(30));
//         form.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
//         form.setEffect(new DropShadow(10, Color.gray(0.6)));

//         form.setOnMouseEntered(e -> form.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 0); -fx-scale-y: 1.02; -fx-scale-x: 1.02;"));
//         form.setOnMouseExited(e -> form.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 0); -fx-scale-y: 1.0; -fx-scale-x: 1.0;"));

//         StackPane root = new StackPane(form);
//         root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eef2ff, #dbe7ff);");

//         Scene scene = new Scene(root, 1200, 800);
//         stage.setScene(scene);
//         stage.setTitle("Psychologist Login");
//         stage.show();
//     }

//     private void handleLoginButton(Stage currentStage) {
//         String email = emailField.getText();
//         String password = passwordField.getText();

//         if (email.isEmpty() || password.isEmpty()) {
//             SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both email and password.");
//             return;
//         }

//         boolean success = authController.login(email, password, "psychologist");

//         if (success) {
            
//             SceneManager.showAlert(Alert.AlertType.INFORMATION, "Login Success", "Psychologist login successful!");
//            // SceneManager.switchScreen(new PsychologistDashboard(currentStage), "Psychologist Dashboard");
//            initialize(currentStage);
            
//         } else {
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or not authorized as Psychologist.");
//         }
//     }

//     private void handleRegisterButton(Stage currentStage) {
//         SceneManager.switchScreen(new PsychologistRegistrationView(), "Psychologist Registration");
       
//     }

//     private void handleBackToRoleSelection(Stage currentStage) {
//         SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
        
//     }

//     public void initialize(Stage myStage) {
//            PsychologistDashboard dashboard = new PsychologistDashboard();
//            dashboard.setDashStage(myStage);
//            PsychologistDashboardController controller = new PsychologistDashboardController(dashboard, AuthController.loggedInUserId);
//            controller.initialize();
//            dashboard.show();
//     }
// }


package com.lifecompass.view;

import com.lifecompass.LoginScreen;
import com.lifecompass.controller.AuthController;
import com.lifecompass.controller.psychologist.PsychologistDashboardController;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import com.lifecompass.view.Psycologiestview.PsychologistDashboard;

public class PsychologistLoginScreen extends Application {

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
        iconCircle.setStyle("-fx-background-color: linear-gradient(to right, #6a5acd, #8a2be2); " +
                "-fx-background-radius: 50%; " +
                "-fx-alignment: center;");

        Label title = new Label("Login for Psychologist");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label subtitle = new Label("Sign in to access your dashboard");
        subtitle.setTextFill(Color.GRAY);
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Label emailLabel = new Label("Email");
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(300);
        emailField.setMaxWidth(300);

        Label passwordLabel = new Label("Password");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(300);
        passwordField.setMaxWidth(300);

        Button signInBtn = new Button("Sign In →");
        signInBtn.setPrefWidth(300);
        signInBtn.setMaxWidth(300);
        signInBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
        signInBtn.setOnAction(e -> handleLoginButton(stage));

        Button registerBtn = new Button("Register Now");
        registerBtn.setPrefWidth(300);
        registerBtn.setMaxWidth(300);
        registerBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-size: 14px;");
        registerBtn.setOnAction(e -> handleRegisterButton(stage));

        Hyperlink backLink = new Hyperlink("Back to Role Selection");
        backLink.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        backLink.setOnAction(e -> handleBackToRoleSelection(stage));

        // Create a VBox to hold the form's content (no style applied here)
        VBox formContent = new VBox(10,
                iconCircle,
                title,
                subtitle,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                signInBtn,
                registerBtn,
                backLink);

        formContent.setAlignment(Pos.CENTER);
        formContent.setPadding(new Insets(30));

        // Create a separate container (VBox) for the visual style
        VBox formContainer = new VBox();
        formContainer.getChildren().add(formContent);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPrefSize(400, 600);
        formContainer.setMaxSize(400, 600);

        // Initial style with the prominent black border
        String initialStyle = "-fx-background-color: white; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 12px;";
        formContainer.setStyle(initialStyle);
        formContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));

        // Hover style
        String hoverStyle = "-fx-background-color: white; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: #8a2be2; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12px;";
        formContainer.setOnMouseEntered(e -> {
            formContainer.setStyle(hoverStyle);
            formContainer.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.3)));
        });

        // Exit style
        formContainer.setOnMouseExited(e -> {
            formContainer.setStyle(initialStyle);
            formContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));
        });

        StackPane root = new StackPane(formContainer);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eef2ff, #dbe7ff);");

        Scene scene = new Scene(root, 1540, 860);
        stage.setScene(scene);
        stage.setTitle("Psychologist Login");
        stage.show();
    }

    private void handleLoginButton(Stage currentStage) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both email and password.");
            return;
        }

        boolean success = authController.login(email, password, "psychologist");

        if (success) {
            SceneManager.showAlert(Alert.AlertType.INFORMATION, "Login Success", "Psychologist login successful!");
            initialize(currentStage);
        } else {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or not authorized as Psychologist.");
        }
    }

    private void handleRegisterButton(Stage currentStage) {
        SceneManager.switchScreen(new PsychologistRegistrationView(), "Psychologist Registration");
    }

    private void handleBackToRoleSelection(Stage currentStage) {
        SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
    }

    public void initialize(Stage myStage) {
        PsychologistDashboard dashboard = new PsychologistDashboard();
        dashboard.setDashStage(myStage);
        PsychologistDashboardController controller = new PsychologistDashboardController(dashboard, AuthController.loggedInUserId);
        controller.initialize();
        dashboard.show();
    }
}