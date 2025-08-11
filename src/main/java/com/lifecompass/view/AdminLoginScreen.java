// package com.lifecompass.view;

// import com.lifecompass.LoginScreen;
// import com.lifecompass.controller.AuthController;
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
// import com.lifecompass.view.Adminview.AdminDashboardView;
// import com.lifecompass.view.Psycologiestview.PsychologistDashboard; // <-- Example path, adjust to your actual package


// public class AdminLoginScreen extends Application {

//     private TextField usernameField;
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
//                 "-fx-background-radius: 50%; -fx-alignment: center;");

//         Label title = new Label("Login as Admin");
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//         Label subtitle = new Label("Sign in to access the admin dashboard");
//         subtitle.setTextFill(Color.GRAY);
//         subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

//         Label usernameLabel = new Label("Username");
//         usernameField = new TextField();
//         usernameField.setPromptText("Enter admin username");
//         usernameField.setPrefWidth(300);
//         usernameField.setMaxWidth(300);

//         Label passwordLabel = new Label("Password");
//         passwordField = new PasswordField();
//         passwordField.setPromptText("Enter password");
//         passwordField.setPrefWidth(300);
//         passwordField.setMaxWidth(300);

//         Button loginBtn = new Button("Login →");
//         loginBtn.setPrefWidth(300);
//         loginBtn.setMaxWidth(300);
//         loginBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
//         loginBtn.setOnAction(e -> handleLoginButton(stage));

//         Button registerBtn = new Button("Register Now");
//         registerBtn.setPrefWidth(300);
//         registerBtn.setMaxWidth(300);
//         registerBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-size: 14px;");
//         registerBtn.setOnAction(e -> handleRegisterButton(stage));

//         Hyperlink backBtn = new Hyperlink("Back to Role Selection");
//         backBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
//         backBtn.setOnAction(e -> handleBackToRoleSelection(stage));

//         VBox form = new VBox(10,
//                 iconCircle,
//                 title,
//                 subtitle,
//                 usernameLabel,
//                 usernameField,
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
//         stage.setTitle("Admin Login");
//         stage.show();
//     }

//     private void handleLoginButton(Stage currentStage) {
//         String emailOrUsername = usernameField.getText();
//         String password = passwordField.getText();

//         if (emailOrUsername.isEmpty() || password.isEmpty()) {
//             SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both username/email and password.");
//             return;
//         }

//         boolean success = authController.login(emailOrUsername, password, "admin");

//         if (success) {
//             SceneManager.showAlert(Alert.AlertType.INFORMATION, "Login Success", "Admin login successful!");
//             SceneManager.switchScreen(new AdminDashboardView(), "Admin Dashboard");
//             currentStage.close();
//         } else {
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or not authorized as Admin.");
//         }
//     }

//     private void handleRegisterButton(Stage currentStage) {
//         SceneManager.showAlert(Alert.AlertType.INFORMATION, "Admin Registration", "Admin registration is usually managed internally. Please contact support.");
//     }

//     private void handleBackToRoleSelection(Stage currentStage) {
//         SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
//         currentStage.close();
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.lifecompass.view.Adminview.AdminDashboardView;

public class AdminLoginScreen extends Application {

    private TextField usernameField;
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
                "-fx-background-radius: 50%; -fx-alignment: center;");

        Label title = new Label("Login as Admin");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label subtitle = new Label("Sign in to access the admin dashboard");
        subtitle.setTextFill(Color.GRAY);
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Label usernameLabel = new Label("Username");
        usernameField = new TextField();
        usernameField.setPromptText("Enter admin username");
        usernameField.setPrefWidth(300);
        usernameField.setMaxWidth(300);

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

        // This VBox now only holds the content, no styling
        VBox formContent = new VBox(10,
                iconCircle,
                title,
                subtitle,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                loginBtn,
                registerBtn,
                backBtn);

        formContent.setAlignment(Pos.CENTER);
        formContent.setPadding(new Insets(30));

        // We create a new StackPane to act as the styled box wrapper
        StackPane formBox = new StackPane(formContent);
        formBox.setMaxSize(400, 600); // Set a fixed size for the box

        // Initial style with the prominent black border
        String initialStyle = "-fx-background-color: white; " +
                              "-fx-background-radius: 12px; " +
                              "-fx-border-color: black; " +
                              "-fx-border-width: 3; " +
                              "-fx-border-radius: 12px;";
        formBox.setStyle(initialStyle);
        formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));

        // Hover style
        String hoverStyle = "-fx-background-color: white; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: #8a2be2; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 12px;";
        formBox.setOnMouseEntered(e -> {
            formBox.setStyle(hoverStyle);
            formBox.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.3)));
        });

        // Exit style
        formBox.setOnMouseExited(e -> {
            formBox.setStyle(initialStyle);
            formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));
        });

        StackPane root = new StackPane(formBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eef2ff, #dbe7ff);");

        Scene scene = new Scene(root, 1540, 860);
        stage.setScene(scene);
        stage.setTitle("Admin Login");
        stage.show();
    }

    private void handleLoginButton(Stage currentStage) {
        String emailOrUsername = usernameField.getText();
        String password = passwordField.getText();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both username/email and password.");
            return;
        }

        boolean success = authController.login(emailOrUsername, password, "admin");

        if (success) {
            SceneManager.showAlert(Alert.AlertType.INFORMATION, "Login Success", "Admin login successful!");
            SceneManager.switchScreen(new AdminDashboardView(), "Admin Dashboard");
        } else {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or not authorized as Admin.");
        }
    }

    private void handleRegisterButton(Stage currentStage) {
        SceneManager.showAlert(Alert.AlertType.INFORMATION, "Admin Registration", "Admin registration is usually managed internally. Please contact support.");
    }

    private void handleBackToRoleSelection(Stage currentStage) {
        SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
    }
}