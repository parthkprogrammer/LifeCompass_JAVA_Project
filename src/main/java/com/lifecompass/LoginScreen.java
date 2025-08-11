package com.lifecompass;
import com.lifecompass.util.SceneManager;
import com.lifecompass.view.AdminLoginScreen;
import com.lifecompass.view.PsychologistLoginScreen;
import com.lifecompass.view.UserLoginScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.InputStream;

public class LoginScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Logo & Title
        String logoPath = "/assets/images/lifecompass_logo.png";
        InputStream logoIs = getClass().getResourceAsStream(logoPath);

        if (logoIs == null) {
            System.err.println("CRITICAL ERROR: Logo image not found at path: " + logoPath + ". Please check file existence and build configuration.");
            throw new RuntimeException("Failed to load logo image.");
        }
        ImageView logoImageView = new ImageView(new Image(logoIs));
        logoImageView.setFitWidth(200);
        logoImageView.setPreserveRatio(true);

        Label title = new Label("Life");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        Label compass = new Label("Compass");
        compass.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        compass.setTextFill(Color.DEEPSKYBLUE);

        HBox titleBox = new HBox(title, compass);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(5);

        VBox logoTitle = new VBox(logoImageView, titleBox);
        logoTitle.setAlignment(Pos.CENTER);
        logoTitle.setSpacing(10);

        Label subTitle = new Label(
                "Your holistic personal growth companion for mental wellness, therapy access, and emotional growth");
        subTitle.setWrapText(true);
        subTitle.setMaxWidth(700);
        subTitle.setFont(Font.font(14));
        subTitle.setTextFill(Color.DIMGRAY);
        subTitle.setAlignment(Pos.CENTER);

        Label chooseRole = new Label("Choose Your Role");
        chooseRole.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        chooseRole.setTextFill(Color.BLACK);

        // User Card
        VBox userCard = createRoleCard("/assets/images/user_icon.jpg", "User", new String[] {
                "                          ",
                "Mood tracking & journaling",
                "CBT-based chatbot support",
                "Visual mood analytics",
                "Expressive art board"
        }, "Continue as User", () -> SceneManager.switchScreen(new UserLoginScreen(), "User Login"));

        // Psychologist Card
        VBox psychologistCard = createRoleCard("/assets/images/psychologist_icon.png", "Psychologist", new String[] {
                "Client mood monitoring",
                "Secure client chat",
                "Crisis alerts & monitoring",
                "Analytics dashboard"
        }, "Continue as Psychologist", () -> SceneManager.switchScreen(new PsychologistLoginScreen(), "Psychologist Login"));

        // Admin Card
        VBox adminCard = createRoleCard("/assets/images/protected_icon.png", "Admin", new String[] {
                "                                ",
                "User & psychologist verification",
                "Crisis alerts & monitoring",
                "Platform analytics",
                "Content moderation"
        }, "Continue as Admin", () -> SceneManager.switchScreen(new AdminLoginScreen(), "Admin Login"));

        // Roles Layout
        HBox roles = new HBox(30, userCard, psychologistCard, adminCard);
        roles.setAlignment(Pos.CENTER);

        VBox topSection = new VBox(20, logoTitle, subTitle, chooseRole, roles);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20));

        // Platform Features Section
        Label featuresTitle = new Label("Platform Features");
        featuresTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Modify createFeature to accept an image path
        VBox analytics = createFeature("/assets/images/analytics_icon.png", "Advanced Analytics",
                "Visualize mood patterns and emotional trends with interactive charts and insights");
        VBox cbt = createFeature("/assets/images/cbt_chat.png", "CBT Support",
                "AI-powered chatbot provides cognitive behavioral therapy techniques and support");
        VBox community = createFeature("/assets/images/community_icon.png", "Community Support",
                "Connect with others through anonymous group chats and community features");

        HBox featureRow = new HBox(60, analytics, cbt, community);
        featureRow.setAlignment(Pos.CENTER);

        VBox bottomSection = new VBox(20, featuresTitle, featureRow);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(40, 10, 40, 10));

        // Final Layout
        VBox root = new VBox(40, topSection, bottomSection);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4ff, #dbe7ff);");

        // Wrap the root VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true); // Ensures the VBox resizes to the width of the ScrollPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hides the horizontal scroll bar

        Scene scene = new Scene(scrollPane, 1540, 860);
        primaryStage.setTitle("LifeCompass - Role Selection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Modified createRoleCard to take an imagePath instead of an icon string
    private VBox createRoleCard(String imagePath, String title, String[] points, String buttonText, Runnable onClick) {
        InputStream is = getClass().getResourceAsStream(imagePath);
        if (is == null) {
            System.err.println("CRITICAL ERROR: Role card image not found for " + title + " at path: " + imagePath);
            Label errorLabel = new Label("Image not found");
            return new VBox(new ImageView(), new Label(title), errorLabel);
        }
        ImageView iconImageView = new ImageView(new Image(is));
        iconImageView.setFitWidth(36);
        iconImageView.setPreserveRatio(true);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label subtitle = new Label(getSubtitle(title));
        subtitle.setFont(Font.font(13));
        subtitle.setTextFill(Color.DARKGRAY);
        subtitle.setWrapText(true);

        VBox pointList = new VBox(5);
        for (String p : points) {
            Label point = new Label("• " + p);
            point.setFont(Font.font(12));
            point.setTextFill(Color.DIMGRAY);
            pointList.getChildren().add(point);
        }

        Button button = new Button(buttonText);
        button.setStyle(getButtonStyle(title));
        button.setOnAction(e -> onClick.run());
        button.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(10, iconImageView, titleLabel, subtitle, pointList, button);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));
        box.setStyle(
                "-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.setPrefSize(260, 300);

        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: white; -fx-border-color: #a0a0a0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(149, 200, 230, 0.2), 10, 0, 0, 0);"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;"));

        return box;
    }

    private String getSubtitle(String role) {
        String subtitleText;
        switch (role) {
            case "User":
                subtitleText = "Track your mental wellness journey";
                break;
            case "Psychologist":
                subtitleText = "Provide expert guidance to your clients";
                break;
            case "Admin":
                subtitleText = "Manage platform safety & operations";
                break;
            default:
                subtitleText = "";
                break;
        }
        return subtitleText;
    }

    private String getButtonStyle(String role) {
        String style;
        switch (role) {
            case "User":
                style = "-fx-background-color: black; -fx-text-fill: white;";
                break;
            case "Psychologist":
                style = "-fx-background-color: purple; -fx-text-fill: white;";
                break;
            case "Admin":
                style = "-fx-background-color: red; -fx-text-fill: white;";
                break;
            default:
                style = "";
                break;
        }
        return style;
    }

    // Modified createFeature to take an imagePath instead of an icon string
    private VBox createFeature(String imagePath, String title, String description) {
        InputStream is = getClass().getResourceAsStream(imagePath);
        if (is == null) {
            System.err.println("CRITICAL ERROR: Feature image not found for " + title + " at path: " + imagePath);
            Label errorLabel = new Label("Image not found");
            return new VBox(new ImageView(), new Label(title), errorLabel);
        }
        ImageView iconImageView = new ImageView(new Image(is));
        iconImageView.setFitWidth(28);
        iconImageView.setPreserveRatio(true);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label desc = new Label(description);
        desc.setFont(Font.font(12));
        desc.setWrapText(true);
        desc.setMaxWidth(220);
        desc.setTextFill(Color.DARKGRAY);

        VBox featureBox = new VBox(8, iconImageView, titleLabel, desc);
        featureBox.setAlignment(Pos.TOP_CENTER);
        return featureBox;
    }

    public Object createLoginContent() {
        return null;
    }
}