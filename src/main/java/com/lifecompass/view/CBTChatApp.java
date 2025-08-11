package com.lifecompass.view; // Corrected package name

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL; // Added for createImageView
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Refactored: No longer extends Application
public class CBTChatApp {

    // --- Styling Constants ---
    private static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f5f5f5;";
    private static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
    private static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
    private static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
    private static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
    private static final String COLOR_BLUE_ACCENT = "#3b82f6";
    private static final String COLOR_GREEN_LIGHT = "#f0fdf4";
    private static final String COLOR_GREEN_BORDER = "#dcfce7";
    private static final String COLOR_BLUE_TEXT_LIGHT = "#93c5fd";
    private static final String COLOR_RED_ACCENT = "#ef4444";
    private static final String COLOR_YELLOW_ACCENT = "#fbbf24";

    // --- Data Models ---
    static class Message {
        String id;
        String content;
        SenderType sender;
        LocalTime timestamp;
        MessageType type;

        public Message(String id, String content, SenderType sender, LocalTime timestamp, MessageType type) {
            this.id = id;
            this.content = content;
            this.sender = sender;
            this.timestamp = timestamp;
            this.type = type;
        }

        public enum SenderType {
            USER, BOT
        }

        public enum MessageType {
            SUGGESTION, EXERCISE, NORMAL
        }

        public String getFormattedTime() {
            return timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
        }
    }

    // --- Chat Data and Controls ---
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private TextField inputMessageField;
    private ScrollPane chatScrollPane;
    private VBox messageContainer; // This will be dynamically updated
    private ScheduledExecutorService botTypingExecutor = Executors.newSingleThreadScheduledExecutor();
    private BooleanProperty isBotTyping = new SimpleBooleanProperty(false);

    // --- Sidebar Data ---
    private static final List<String> CBT_TECHNIQUES = Arrays.asList(
            "Thought challenging",
            "Cognitive restructuring",
            "Behavioral activation",
            "Mindfulness exercises",
            "Gratitude practice"
    );

    private static final List<String> QUICK_RESPONSES = Arrays.asList(
            "I'm feeling anxious",
            "I had a bad day",
            "I'm feeling overwhelmed",
            "I want to practice gratitude",
            "Help me reframe my thoughts"
    );

    // Session Summary Data
    private Label messagesExchangedLabel;
    private Label techniquesUsedLabel;
    private int techniquesUsedCount = 0;

    // --- References from Dashboard ---
    private Stage primaryStage; // The primary stage of the main application (dashboard).
    private UserDashboardScreen dashboardInstance; // Corrected dashboard reference type

    /**
     * Constructor for the CBTChatApp component.
     * @param primaryStage The primary stage of the main application (dashboard).
     * @param dashboardInstance A reference to the dashboard instance for communication.
     */
    public CBTChatApp(Stage primaryStage, UserDashboardScreen dashboardInstance) { // Corrected type
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;

        // Initialize the first bot message here when the component is created.
        messages.add(new Message("1",
                "Hello! I'm your CBT companion. I'm here to help you explore your thoughts and feelings using cognitive behavioral therapy techniques. How are you feeling today?",
                Message.SenderType.BOT, LocalTime.now(), Message.MessageType.NORMAL));

        // Add a listener to the messages list to update the UI dynamically
        messages.addListener((javafx.collections.ListChangeListener.Change<? extends Message> change) -> {
            Platform.runLater(() -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (Message msg : change.getAddedSubList()) {
                            if (messageContainer != null) { // Check if messageContainer is initialized
                                messageContainer.getChildren().add(createMessageBubble(msg));
                            }
                        }
                    }
                }
                // Always scroll to bottom after any change
                if (chatScrollPane != null) {
                    chatScrollPane.layout(); // Force layout pass
                    chatScrollPane.setVvalue(1.0); // Scroll to bottom
                }
                // Update message count whenever messages change
                if (messagesExchangedLabel != null) {
                    messagesExchangedLabel.setText(String.valueOf(messages.size()));
                }
            });
        });
    }

    /**
     * Creates and returns the entire UI content for the CBT Chat screen.
     * This method is designed to be called by the `UserDashboardScreen` class when the "Chat" tab is selected.
     * @return A GridPane containing all UI elements for the CBT Chat.
     */
    public GridPane createChatScreenContent() {
        GridPane mainContentGrid = new GridPane();
        mainContentGrid.setHgap(20);
        mainContentGrid.setVgap(20);
        mainContentGrid.setPadding(new Insets(20));
        mainContentGrid.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        GridPane.setHgrow(mainContentGrid, Priority.ALWAYS);
        GridPane.setVgrow(mainContentGrid, Priority.ALWAYS);

        // Column Constraints for chat (65%) and sidebar (35%)
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(65);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(35);
        mainContentGrid.getColumnConstraints().addAll(col1, col2);

        // Chat Interface (Left Column)
        VBox chatInterface = createChatInterface();
        GridPane.setConstraints(chatInterface, 0, 0, 1, 1);
        GridPane.setHgrow(chatInterface, Priority.ALWAYS);
        GridPane.setVgrow(chatInterface, Priority.ALWAYS);

        // Sidebar (Right Column)
        VBox sidebar = createSidebar();
        GridPane.setConstraints(sidebar, 1, 0, 1, 1);
        GridPane.setHgrow(sidebar, Priority.ALWAYS);
        GridPane.setVgrow(sidebar, Priority.ALWAYS);

        mainContentGrid.getChildren().addAll(chatInterface, sidebar);

        // Ensure current state of labels is reflected when content is created/recreated
        Platform.runLater(() -> {
            // Re-populate the messageContainer with all current messages every time content is created
            messageContainer.getChildren().clear();
            for (Message msg : messages) {
                messageContainer.getChildren().add(createMessageBubble(msg));
            }
            if (chatScrollPane != null) {
                chatScrollPane.layout(); // Force layout pass
                chatScrollPane.setVvalue(1.0); // Scroll to bottom
            }
            if (messagesExchangedLabel != null) {
                messagesExchangedLabel.setText(String.valueOf(messages.size()));
            }
            if (techniquesUsedLabel != null) {
                techniquesUsedLabel.setText(String.valueOf(techniquesUsedCount));
            }
        });

        return mainContentGrid;
    }

    /**
     * Public method to stop background services (like ScheduledExecutorService).
     * This should be called by the parent (UserDashboardScreen) when navigating away from this component.
     */
    public void stopChatServices() {
        if (botTypingExecutor != null && !botTypingExecutor.isShutdown()) {
            botTypingExecutor.shutdownNow();
            System.out.println("CBT Chat bot typing executor shut down.");
        }
    }

    // --- Chat Interface UI ---
    private VBox createChatInterface() {
        VBox chatCard = new VBox();
        chatCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        chatCard.setPrefHeight(600);
        VBox.setVgrow(chatCard, Priority.ALWAYS);
        HBox.setHgrow(chatCard, Priority.ALWAYS);

        // Header
        HBox chatHeader = new HBox(10);
        chatHeader.setPadding(new Insets(15, 20, 15, 20));
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1px 0;");

        StackPane botIconCircle = new StackPane();
        botIconCircle.setPrefSize(32, 32);
        botIconCircle.setStyle("-fx-background-color: #e0f2fe; -fx-background-radius: 16px;");
        Label botIcon = new Label("\uD83E\uDD16");
        botIcon.setFont(new Font("Arial", 16));
        botIcon.setTextFill(Color.web(COLOR_BLUE_ACCENT));
        botIconCircle.getChildren().add(botIcon);

        Label assistantName = new Label("CBT Assistant");
        assistantName.setFont(new Font("Arial Bold", 14));
        assistantName.setStyle(TEXT_COLOR_DARK_GREY);

        Label onlineBadge = new Label("Online");
        onlineBadge.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 2px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; " + TEXT_COLOR_GREY);

        chatHeader.getChildren().addAll(botIconCircle, assistantName, onlineBadge);

        // Messages Scroll Area - initialize messageContainer here
        messageContainer = new VBox(10); // Ensure this is the VBox that the listener updates
        messageContainer.setPadding(new Insets(10, 15, 10, 15));
        messageContainer.setAlignment(Pos.TOP_LEFT);

        chatScrollPane = new ScrollPane(messageContainer);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        // Quick Responses
        FlowPane quickResponsesPane = new FlowPane(8, 8);
        quickResponsesPane.setPadding(new Insets(0, 15, 10, 15));
        for (String response : QUICK_RESPONSES) {
            Button quickButton = new Button(response);
            quickButton.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 6px 12px; -fx-font-size: 12px; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
            quickButton.setOnAction(e -> handleSendMessage(response));
            quickResponsesPane.getChildren().add(quickButton);
        }

        // Input Area
        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(0, 15, 15, 15));
        inputArea.setAlignment(Pos.CENTER_LEFT);

        inputMessageField = new TextField();
        inputMessageField.setPromptText("Share your thoughts or feelings...");
        inputMessageField.setStyle("-fx-control-inner-background: #ffffff; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px;");
        HBox.setHgrow(inputMessageField, Priority.ALWAYS);
        inputMessageField.setOnAction(e -> handleSendMessage(inputMessageField.getText()));

        Button sendButton = new Button();
        sendButton.setGraphic(new Label("\u27A1\uFE0F"));
        sendButton.setStyle("-fx-background-color: " + COLOR_BLUE_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 14px; -fx-cursor: hand;");
        sendButton.setOnAction(e -> handleSendMessage(inputMessageField.getText()));
        sendButton.disableProperty().bind(inputMessageField.textProperty().isEmpty().or(isBotTyping));

        inputArea.getChildren().addAll(inputMessageField, sendButton);

        chatCard.getChildren().addAll(chatHeader, chatScrollPane, quickResponsesPane, inputArea);

        return chatCard;
    }

    private VBox createSidebar() {
        VBox sidebarContent = new VBox(20);
        sidebarContent.setAlignment(Pos.TOP_CENTER);

        // CBT Techniques Card
        VBox cbtTechCard = new VBox(10);
        cbtTechCard.setPadding(new Insets(20));
        cbtTechCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        cbtTechCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(cbtTechCard, Priority.NEVER);

        Label cbtTechTitle = new Label("CBT Techniques");
        cbtTechTitle.setFont(new Font("Arial Bold", 16));
        cbtTechTitle.setStyle(TEXT_COLOR_DARK_GREY);
        Label lightbulbIcon = new Label("\uD83D\uDCA1");
        lightbulbIcon.setFont(new Font("Arial", 18));
        lightbulbIcon.setTextFill(Color.web(COLOR_YELLOW_ACCENT));
        cbtTechTitle.setGraphic(lightbulbIcon);
        cbtTechTitle.setContentDisplay(ContentDisplay.LEFT);

        Label cbtTechDescription = new Label("Evidence-based methods we can explore together");
        cbtTechDescription.setFont(new Font("Arial", 12));
        cbtTechDescription.setStyle(TEXT_COLOR_GREY);
        cbtTechDescription.setWrapText(true);

        VBox techniquesList = new VBox(3);
        for (String technique : CBT_TECHNIQUES) {
            VBox techniqueItem = new VBox();
            techniqueItem.setPadding(new Insets(8, 12, 8, 12));
            techniqueItem.setStyle("-fx-background-color: " + COLOR_BLUE_TEXT_LIGHT + "; -fx-background-radius: 5px;");
            Label techText = new Label(technique);
            techText.setFont(new Font("Arial Bold", 13));
            techText.setTextFill(Color.web("#1e40af"));
            techniqueItem.getChildren().add(techText);
            techniqueItem.setOnMouseClicked(e -> {
                handleSendMessage("Tell me more about " + technique);
                System.out.println("Technique clicked: " + technique);
            });
            techniquesList.getChildren().add(techniqueItem);
        }
        cbtTechCard.getChildren().addAll(cbtTechTitle, cbtTechDescription, techniquesList);


        // Crisis Support Card
        VBox crisisSupportCard = new VBox(10);
        crisisSupportCard.setPadding(new Insets(20));
        crisisSupportCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        crisisSupportCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(crisisSupportCard, Priority.NEVER);

        Label crisisTitle = new Label("Crisis Support");
        crisisTitle.setFont(new Font("Arial Bold", 16));
        crisisTitle.setStyle(TEXT_COLOR_DARK_GREY);
        Label crisisHeartIcon = new Label("\u2764\uFE0F");
        crisisHeartIcon.setFont(new Font("Arial", 18));
        crisisHeartIcon.setTextFill(Color.web(COLOR_RED_ACCENT));
        crisisTitle.setGraphic(crisisHeartIcon);
        crisisTitle.setContentDisplay(ContentDisplay.LEFT);

        Label crisisDescription = new Label("If you're experiencing a mental health crisis, please reach out for immediate help:");
        crisisDescription.setFont(new Font("Arial", 12));
        crisisDescription.setStyle(TEXT_COLOR_GREY);
        crisisDescription.setWrapText(true);

        VBox crisisButtons = new VBox(5);
        Button hotlineButton = new Button("Crisis Hotline: 988");
        styleCrisisButton(hotlineButton);
        Button emergencyButton = new Button("Emergency: 911");
        styleCrisisButton(emergencyButton);
        Button textLineButton = new Button("Text Crisis Line: Text HOME to 741741");
        styleCrisisButton(textLineButton);
        crisisButtons.getChildren().addAll(hotlineButton, emergencyButton, textLineButton);
        crisisSupportCard.getChildren().addAll(crisisTitle, crisisDescription, crisisButtons);


        // Session Summary Card
        VBox sessionSummaryCard = new VBox(10);
        sessionSummaryCard.setPadding(new Insets(20));
        sessionSummaryCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        sessionSummaryCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(sessionSummaryCard, Priority.NEVER);

        Label summaryTitle = new Label("Session Summary");
        summaryTitle.setFont(new Font("Arial Bold", 16));
        summaryTitle.setStyle(TEXT_COLOR_DARK_GREY);

        GridPane summaryDetails = new GridPane();
        summaryDetails.setHgap(10);
        summaryDetails.setVgap(5);

        Label msgExchangedKey = new Label("Messages exchanged:");
        msgExchangedKey.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 12px;");
        messagesExchangedLabel = new Label("0"); // Initialize the label for messages exchanged
        messagesExchangedLabel.setStyle(TEXT_COLOR_DARK_GREY + "-fx-font-weight: bold; -fx-font-size: 12px;");
        GridPane.setConstraints(msgExchangedKey, 0, 0);
        GridPane.setConstraints(messagesExchangedLabel, 1, 0);
        GridPane.setHgrow(messagesExchangedLabel, Priority.ALWAYS);

        Label sessionDurationKey = new Label("Session duration:");
        sessionDurationKey.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 12px;");
        Label sessionDurationValue = new Label("0 minutes");
        sessionDurationValue.setStyle(TEXT_COLOR_DARK_GREY + "-fx-font-weight: bold; -fx-font-size: 12px;");
        GridPane.setConstraints(sessionDurationKey, 0, 1);
        GridPane.setConstraints(sessionDurationValue, 1, 1);
        GridPane.setHgrow(sessionDurationValue, Priority.ALWAYS);

        Label techUsedKey = new Label("Techniques used:");
        techUsedKey.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 12px;");
        techniquesUsedLabel = new Label("0"); // Initialize the label for techniques used
        techniquesUsedLabel.setStyle(TEXT_COLOR_DARK_GREY + "-fx-font-weight: bold; -fx-font-size: 12px;");
        GridPane.setConstraints(techUsedKey, 0, 2);
        GridPane.setConstraints(techniquesUsedLabel, 1, 2);
        GridPane.setHgrow(techniquesUsedLabel, Priority.ALWAYS);

        summaryDetails.getChildren().addAll(msgExchangedKey, messagesExchangedLabel,
                                             sessionDurationKey, sessionDurationValue,
                                             techUsedKey, techniquesUsedLabel);

        sessionSummaryCard.getChildren().addAll(summaryTitle, summaryDetails);

        sidebarContent.getChildren().addAll(cbtTechCard, crisisSupportCard, sessionSummaryCard);
        return sidebarContent;
    }

    private void styleCrisisButton(Button button) {
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 12px; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
    }

    // --- Chat Logic ---
    private void handleSendMessage(String messageContent) {
        if (isBotTyping.get() || messageContent == null || messageContent.trim().isEmpty()) {
            return;
        }

        Message userMessage = new Message(
                String.valueOf(System.currentTimeMillis()),
                messageContent.trim(),
                Message.SenderType.USER,
                LocalTime.now(),
                Message.MessageType.NORMAL
        );
        messages.add(userMessage);
        inputMessageField.clear();

        isBotTyping.set(true);
        Platform.runLater(() -> inputMessageField.setDisable(true));

        botTypingExecutor.schedule(() -> {
            String botResponseContent = generateBotResponse(userMessage.content);
            Message.MessageType responseType = Message.MessageType.NORMAL;

            if (botResponseContent.toLowerCase().contains("grounding technique") ||
                botResponseContent.toLowerCase().contains("reframing thoughts") ||
                botResponseContent.toLowerCase().contains("gratitude practice")) {
                responseType = Message.MessageType.SUGGESTION;
                techniquesUsedCount++;
                Platform.runLater(() -> techniquesUsedLabel.setText(String.valueOf(techniquesUsedCount)));
            }

            Message botMessage = new Message(
                    String.valueOf(System.currentTimeMillis() + 1),
                    botResponseContent,
                    Message.SenderType.BOT,
                    LocalTime.now(),
                    responseType
            );
            Platform.runLater(() -> {
                messages.add(botMessage);
                isBotTyping.set(false);
                inputMessageField.setDisable(false);
            });
        }, new Random().nextInt(1500) + 500, TimeUnit.MILLISECONDS);
    }

    private String generateBotResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("anxious") || lowerMessage.contains("anxiety")) {
            return "I understand you're feeling anxious. Let's try a grounding technique. Can you name 5 things you can see, 4 things you can touch, 3 things you can hear, 2 things you can smell, and 1 thing you can taste?";
        }
        if (lowerMessage.contains("bad day") || lowerMessage.contains("terrible")) {
            return "I'm sorry you're having a difficult day. Sometimes our thoughts can make situations feel worse than they are. What specific thoughts are going through your mind right now?";
        }
        if (lowerMessage.contains("overwhelmed")) {
            return "Feeling overwhelmed is challenging. Let's break this down. What are the main things on your mind right now? We can tackle them one by one.";
        }
        if (lowerMessage.contains("grateful") || lowerMessage.contains("gratitude")) {
            return "Gratitude practice is wonderful for mental health! Can you share three things you're grateful for today, no matter how small they might seem?";
        }
        if (lowerMessage.contains("reframe") || lowerMessage.contains("thoughts")) {
            return "Great that you want to work on reframing thoughts! What's a specific negative thought you've been having? Let's examine the evidence for and against it.";
        }
        if (lowerMessage.contains("thought challenging") || lowerMessage.contains("cognitive restructuring") ||
            lowerMessage.contains("behavioral activation") || lowerMessage.contains("mindfulness exercises") ||
            lowerMessage.contains("gratitude practice")) {
            return "That's a great technique to focus on! How would you like to start with that?";
        }

        return "Thank you for sharing that with me. Can you tell me more about what you're experiencing? What thoughts or feelings are most prominent right now?";
    }

    // --- Message Bubble UI ---
    private Node createMessageBubble(Message message) {
        HBox bubbleWrapper = new HBox();
        bubbleWrapper.setMaxWidth(Double.MAX_VALUE);

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(24, 24);
        iconCircle.setStyle("-fx-background-radius: 12px;");
        Label iconLabel = new Label();
        iconLabel.setFont(new Font("Arial", 12));
        iconCircle.getChildren().add(iconLabel);

        VBox bubbleContent = new VBox(2);
        Label messageText = new Label(message.content);
        messageText.setWrapText(true);
        messageText.setMaxWidth(400);

        Label timestampLabel = new Label(message.getFormattedTime());
        timestampLabel.setFont(new Font("Arial", 9));


        if (message.sender == Message.SenderType.USER) {
            bubbleWrapper.setAlignment(Pos.CENTER_RIGHT);
            iconCircle.setStyle(iconCircle.getStyle() + "-fx-background-color: " + COLOR_BLUE_ACCENT + ";");
            iconLabel.setText("\uD83D\uDC64"); // User emoji
            iconLabel.setTextFill(Color.WHITE);
            messageText.setStyle("-fx-text-fill: white;");
            timestampLabel.setStyle("-fx-text-fill: " + COLOR_BLUE_TEXT_LIGHT + ";");
            bubbleContent.setStyle("-fx-background-color: " + COLOR_BLUE_ACCENT + "; -fx-background-radius: 8px; -fx-padding: 8px 12px;");
            bubbleContent.getChildren().addAll(messageText, timestampLabel);
            bubbleWrapper.getChildren().addAll(bubbleContent, iconCircle);
            HBox.setMargin(bubbleContent, new Insets(0, 5, 0, 0));
        } else { // BOT
            bubbleWrapper.setAlignment(Pos.CENTER_LEFT);
            if (message.type == Message.MessageType.SUGGESTION) {
                bubbleContent.setStyle("-fx-background-color: " + COLOR_GREEN_LIGHT + "; -fx-border-color: " + COLOR_GREEN_BORDER + "; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-padding: 8px 12px;");
                messageText.setStyle("-fx-text-fill: " + TEXT_COLOR_DARK_GREY + ";");
                timestampLabel.setStyle("-fx-text-fill: " + TEXT_COLOR_GREY + ";");
                iconCircle.setStyle(iconCircle.getStyle() + "-fx-background-color: " + COLOR_GREEN_LIGHT + ";");
                iconLabel.setText("\uD83E\uDD16"); // Bot emoji
                iconLabel.setTextFill(Color.web(COLOR_BLUE_ACCENT));
            } else { // NORMAL bot message
                bubbleContent.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8px; -fx-padding: 8px 12px;");
                messageText.setStyle("-fx-text-fill: " + TEXT_COLOR_DARK_GREY + ";");
                timestampLabel.setStyle("-fx-text-fill: " + TEXT_COLOR_GREY + ";");
                iconCircle.setStyle(iconCircle.getStyle() + "-fx-background-color: #e0e0e0;");
                iconLabel.setText("\uD83E\uDD16"); // Bot emoji
                iconLabel.setTextFill(Color.web(COLOR_BLUE_ACCENT));
            }
            bubbleContent.getChildren().addAll(messageText, timestampLabel);
            bubbleWrapper.getChildren().addAll(iconCircle, bubbleContent);
            HBox.setMargin(bubbleContent, new Insets(0, 0, 0, 5));
        }
        return bubbleWrapper;
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods (Copied from UserDashboardScreen.java for this component)
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Helper to create an ImageView with error handling for missing image resources.
     */
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
        } catch (Exception e) {
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
}
