package com.lifecompass.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javafx.scene.Node;

public class AboutUsScreen extends VBox {

    private Stage ownerStage;

    // --- Fixed styling constants for better visibility ---
    private static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
    private static final String CARD_BACKGROUND_COLOR_DARK = "-fx-background-color: #2c3e50; -fx-background-radius: 10;";
    private static final String CARD_BACKGROUND_COLOR_LIGHT = "-fx-background-color: white; -fx-background-radius: 10;";
    private static final String BORDER_COLOR_LIGHT = "-fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 10px;";
    private static final String CARD_SHADOW_STYLE = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";
    private static final Color TEXT_COLOR_DARK = Color.web("#2c3e50"); // Dark blue-gray for better contrast
    private static final Color TEXT_COLOR_WHITE = Color.WHITE;
    private static final Color TEXT_COLOR_LIGHT_GRAY = Color.web("#7f8c8d");

    public AboutUsScreen(Stage ownerStage) {
        this.ownerStage = ownerStage;
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(0));
        this.setSpacing(0);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle(BACKGROUND_COLOR_LIGHT_GREY);

        // --- Back Button at the very top ---
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15; -fx-font-weight: bold;");
        backButton.setOnAction(e -> ((Stage) this.getScene().getWindow()).close());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(15, 0, 0, 15));

        VBox contentHolder = new VBox(40);
        contentHolder.setPadding(new Insets(0));
        contentHolder.setAlignment(Pos.TOP_CENTER);
        contentHolder.setMaxWidth(Double.MAX_VALUE);

        // --- Top Welcome Section ---
        VBox topWelcomeWrapper = new VBox();
        topWelcomeWrapper.setAlignment(Pos.CENTER);
        topWelcomeWrapper.setPadding(new Insets(50, 20, 20, 20));
        topWelcomeWrapper.setStyle(BACKGROUND_COLOR_LIGHT_GREY);

        VBox topWelcomeCard = new VBox(15);
        topWelcomeCard.setAlignment(Pos.CENTER);
        topWelcomeCard.setPadding(new Insets(30, 25, 30, 25));
        topWelcomeCard.setStyle(CARD_BACKGROUND_COLOR_DARK + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        topWelcomeCard.setMaxWidth(800);
        topWelcomeCard.setPrefWidth(800);

        ImageView appLogoTop = createImageView("/assets/images/lifecompass_logo.png", 100, 100);
        appLogoTop.setClip(new Circle(50, 50, 50));
        appLogoTop.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 3; -fx-border-radius: 50;");

        Label welcomeTitle = new Label("Welcome to LifeCompass");
        welcomeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcomeTitle.setTextFill(TEXT_COLOR_WHITE);

        Label tagline = new Label("Holistic Mental Wellness Platform");
        tagline.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        tagline.setTextFill(Color.web("#bdc3c7")); // Light gray for better contrast

        topWelcomeCard.getChildren().addAll(appLogoTop, welcomeTitle, tagline);
        topWelcomeWrapper.getChildren().add(topWelcomeCard);
        contentHolder.getChildren().add(topWelcomeWrapper);

        // --- Our Journey Section (Dark Card) ---
        contentHolder.getChildren().add(createSectionWithHeaderAndBody(
                "Our Journey",
                "LifeCompass bridges the gap between traditional mental wellness practices and modern technology, " +
                        "offering reliable, personalized experiences, and a holistic solution for emotional growth.",
                Arrays.asList(
                        "Easy mood logging with intuitive emoji and slider inputs.",
                        "Saves time with real-time mood trend analysis and visualization.",
                        "Personalized journaling history for self-reflection and progress tracking.",
                        "Integrated CBT tools for immediate support and reframing negative thoughts.",
                        "Access to a curated 'Explore' section with diverse wellness content."
                )
        ));

        // --- Our Mission Section (Dark Card) ---
        contentHolder.getChildren().add(createSectionWithHeaderAndBody(
                "Our Mission",
                "To empower individuals to understand, track, and improve their mental health through intuitive tools and compassionate support.",
                null
        ));

        // --- What Makes Us Different? Section (Feature Grid) ---
        VBox whatMakesUsDifferentSection = new VBox(20);
        whatMakesUsDifferentSection.setAlignment(Pos.TOP_CENTER);
        whatMakesUsDifferentSection.setPadding(new Insets(20));
        whatMakesUsDifferentSection.setStyle(CARD_BACKGROUND_COLOR_DARK + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        whatMakesUsDifferentSection.setMaxWidth(Double.MAX_VALUE);

        Label whatMakesUsDifferentHeader = new Label("What Makes Us Different?");
        whatMakesUsDifferentHeader.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        whatMakesUsDifferentHeader.setTextFill(TEXT_COLOR_DARK);
        whatMakesUsDifferentSection.getChildren().addAll(whatMakesUsDifferentHeader, createFeatureGrid());
        contentHolder.getChildren().add(whatMakesUsDifferentSection);

        // --- Special Thanks & Gratitude Section ---
        VBox specialThanksSection = new VBox(20);
        specialThanksSection.setAlignment(Pos.TOP_CENTER);
        specialThanksSection.setPadding(new Insets(20));
        specialThanksSection.setStyle(CARD_BACKGROUND_COLOR_DARK + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        specialThanksSection.setMaxWidth(Double.MAX_VALUE);
        specialThanksSection.setSpacing(30);

        Label specialThanksHeader = new Label("Special Thanks & Gratitude");
        specialThanksHeader.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        specialThanksHeader.setTextFill(TEXT_COLOR_DARK);
        specialThanksSection.getChildren().add(specialThanksHeader);

        HBox mentorLogos = new HBox(50);
        mentorLogos.setAlignment(Pos.CENTER);
        mentorLogos.setPadding(new Insets(10, 0, 20, 0));

        VBox shashiSirCard = createMentorCard("Mr. Shashi Bagal Sir", "/assets/images/shashisir.png", "Head & Founder, Core2Web");
        VBox core2WebCard = createMentorCard("Core2Web (SuperX)", "/assets/images/core2web.png", "Platform & Inspiration");

        mentorLogos.getChildren().addAll(shashiSirCard, core2WebCard);
        specialThanksSection.getChildren().add(mentorLogos);

        Label gratitudeText1 = new Label(
                "We extend our heartfelt gratitude to everyone who played a role in shaping LifeCompass. " +
                        "To our mentor Shashi Sir, thank you for your unwavering guidance, insightful feedback, and constant encouragement. " +
                        "Your wisdom challenged us to think critically, work diligently, and push beyond boundaries."
        );
        gratitudeText1.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gratitudeText1.setTextFill(Color.BLACK); // Force black color
        gratitudeText1.setWrapText(true);
        gratitudeText1.setMaxWidth(700);
        gratitudeText1.setAlignment(Pos.CENTER);
        gratitudeText1.setPadding(new Insets(20));
        gratitudeText1.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8; -fx-text-fill: black;");

        Label gratitudeText2 = new Label(
                "A special thank you to Core2Web, a whole educational platform and community that inspired us to dream bigger and reach higher. " +
                        "Your support provided not just a foundation, but the spark that ignited this entire journey."
        );
        gratitudeText2.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gratitudeText2.setTextFill(Color.BLACK); // Force black color
        gratitudeText2.setWrapText(true);
        gratitudeText2.setMaxWidth(700);
        gratitudeText2.setAlignment(Pos.CENTER);
        gratitudeText2.setPadding(new Insets(20));
        gratitudeText2.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8; -fx-text-fill: black;");

        specialThanksSection.getChildren().addAll(gratitudeText1, gratitudeText2);
        contentHolder.getChildren().add(specialThanksSection);

        // --- Our Developers Section ---
        VBox developersSection = new VBox(20);
        developersSection.setAlignment(Pos.TOP_CENTER);
        developersSection.setPadding(new Insets(20));
        developersSection.setStyle(CARD_BACKGROUND_COLOR_DARK + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        developersSection.setMaxWidth(Double.MAX_VALUE);

        Label developersHeader = new Label("Our Dedicated Developers 🧑‍💻");
        developersHeader.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        developersHeader.setTextFill(TEXT_COLOR_DARK);
        developersSection.getChildren().add(developersHeader);

        HBox developersContainer = new HBox(40);
        developersContainer.setAlignment(Pos.CENTER);
        developersContainer.setPadding(new Insets(10, 0, 10, 0));
        developersContainer.setMaxWidth(800);
        developersContainer.getChildren().addAll(
                createDeveloperCard("Akash Shinde", "/assets/images/akash.jpg"),
                createDeveloperCard("Parth Kedar", "/assets/images/parth.jpg"),
                createDeveloperCard("Shrinidhi Naik", "/assets/images/shrinidhi.jpg"),
                createDeveloperCard("Mohan Jadhav", "/assets/images/mohon.jpg")
        );
        developersSection.getChildren().add(developersContainer);
        contentHolder.getChildren().add(developersSection);

        // --- Add the main content holder to a ScrollPane ---
        ScrollPane scrollPane = new ScrollPane(contentHolder);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-insets: 0;");

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(backButtonContainer, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.getChildren().addAll(mainLayout);
    }

    private VBox createSectionWithHeaderAndBody(String headerText, String bodyText, List<String> bulletPoints) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(25));
        section.setStyle(CARD_BACKGROUND_COLOR_DARK + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        section.setMaxWidth(700);
        section.setAlignment(Pos.TOP_CENTER);

        Label header = new Label(headerText);
        header.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        header.setTextFill(TEXT_COLOR_WHITE);
        section.getChildren().add(header);

        Label body = new Label(bodyText);
        body.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        body.setTextFill(Color.web("#ecf0f1")); // Light color for better contrast on dark background
        body.setWrapText(true);
        body.setMaxWidth(650);
        body.setAlignment(Pos.CENTER);
        section.getChildren().add(body);

        if (bulletPoints != null && !bulletPoints.isEmpty()) {
            VBox bulletList = new VBox(8);
            bulletList.setPadding(new Insets(15, 0, 0, 20));
            bulletList.setMaxWidth(630);
            for (String point : bulletPoints) {
                Label bulletLabel = new Label("• " + point);
                bulletLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                bulletLabel.setTextFill(Color.web("#bdc3c7")); // Light gray for bullets
                bulletLabel.setWrapText(true);
                bulletList.getChildren().add(bulletLabel);
            }
            section.getChildren().add(bulletList);
        }
        return section;
    }

    private GridPane createFeatureGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Node[] cards = new Node[]{
                createFeatureGridCard("Mood Tracking", "Track your emotional state daily and gain insights into your mental patterns.", "\uD83D\uDE0A"),
                createFeatureGridCard("CBT Exercises", "Engage in proven exercises to reshape negative thinking and build resilience.", "\uD83D\uDCD8"),
                createFeatureGridCard("Journaling Art", "Express your thoughts and feelings freely in a personalized digital journal.", "\uD83D\uDCD6"),
                createFeatureGridCard("User Portfolio", "Keep track of your mood trends, journal entries, and progress over time.", "\uD83D\uDCC8"),
                createFeatureGridCard("Live Notifications", "Receive timely alerts for appointments, challenges, and mood logging.", "\uD83D\uDD14"),
                createFeatureGridCard("Therapist Portfolio", "Connect with verified professionals and view their specializations.", "\uD83D\uDC68\u200D\u2695\uFE0F")
        };

        for (int i = 0; i < cards.length; i++) {
            grid.add(cards[i], i % 2, i / 2);
        }
        grid.setMaxWidth(800);
        return grid;
    }

    private VBox createFeatureGridCard(String title, String description, String emoji) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.setPrefSize(280, 140);
        card.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font("Arial", 32));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(TEXT_COLOR_WHITE);
        titleLabel.setWrapText(true);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.web("#ecf0f1"));
        descLabel.setWrapText(true);

        card.getChildren().addAll(emojiLabel, titleLabel, descLabel);
        return card;
    }

    private VBox createMentorCard(String name, String imagePath, String roleOrDescription) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(CARD_BACKGROUND_COLOR_LIGHT + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        card.setPrefWidth(220);

        ImageView mentorImage = createImageView(imagePath, 120, 120);
        if (imagePath.toLowerCase().contains("shashisir")) {
            mentorImage.setClip(new Circle(60, 60, 60));
            mentorImage.setStyle("-fx-border-color: #3498db; -fx-border-width: 3; -fx-border-radius: 60;");
        }

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.BLACK); // Force black color
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-text-fill: black;"); // Additional CSS override

        Label roleLabel = new Label(roleOrDescription);
        roleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        roleLabel.setTextFill(Color.DARKGRAY); // Force dark gray
        roleLabel.setWrapText(true);
        roleLabel.setAlignment(Pos.CENTER);
        roleLabel.setStyle("-fx-text-fill: #666666;"); // Additional CSS override

        card.getChildren().addAll(mentorImage, nameLabel, roleLabel);
        return card;
    }

    private VBox createDeveloperCard(String name, String imagePath) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle(CARD_BACKGROUND_COLOR_LIGHT + " " + BORDER_COLOR_LIGHT + " " + CARD_SHADOW_STYLE);
        card.setPrefSize(150, 180);

        ImageView devImage = createImageView(imagePath, 90, 90);
        Circle clip = new Circle(45, 45, 45);
        devImage.setClip(clip);
        devImage.setStyle("-fx-border-color: #3498db; -fx-border-width: 3; -fx-border-radius: 45;");

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.BLACK); // Force black color
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-text-fill: black;"); // Additional CSS override

        card.getChildren().addAll(devImage, nameLabel);
        return card;
    }

    private ImageView createImageView(String imagePath, int fitWidth, int fitHeight) {
        ImageView iconView = new ImageView();
        Image loadedImage = null;

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    try (InputStream is = imageUrl.openStream()) {
                        loadedImage = new Image(is);
                    } catch (Exception e) {
                        System.err.println("Exception while opening stream for local image '" + imagePath + "': " + e.getMessage());
                    }
                } else {
                    System.err.println("LOCAL IMAGE RESOURCE NOT FOUND: '" + imagePath + "'");
                }
            } catch (Exception e) {
                System.err.println("General exception during local image loading for '" + imagePath + "': " + e.getMessage());
            }
        }

        if (loadedImage == null || loadedImage.isError()) {
            System.err.println("Falling back to placeholder image for path: " + imagePath);
            loadFallbackImage(iconView);
        } else {
            iconView.setImage(loadedImage);
        }

        iconView.setFitWidth(fitWidth);
        iconView.setFitHeight(fitHeight);
        iconView.setPreserveRatio(true);
        return iconView;
    }

    private void loadFallbackImage(ImageView imageView) {
        try {
            URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
            if (fallbackUrl != null) {
                Platform.runLater(() -> {
                    try (InputStream is = fallbackUrl.openStream()) {
                        imageView.setImage(new Image(is));
                    } catch (Exception e) {
                        System.err.println("Exception loading fallback image via stream: " + e.getMessage());
                    }
                });
            } else {
                System.err.println("CRITICAL ERROR: placeholder.png NOT FOUND at /assets/images/placeholder.png!");
            }
        } catch (Exception pe) {
            System.err.println("Exception preparing fallback image URL: " + pe.getMessage());
        }
    }

    public void show() {
        Stage aboutStage = new Stage();
        aboutStage.initModality(Modality.WINDOW_MODAL);
        aboutStage.initOwner(ownerStage);
        aboutStage.setTitle("About LifeCompass");

        Scene scene = new Scene(this, 850, 750);
        aboutStage.setScene(scene);
        aboutStage.showAndWait();
    }
}