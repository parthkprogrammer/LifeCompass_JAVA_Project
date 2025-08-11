package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.Patient;
import javafx.application.Platform; // <--- MAKE SURE THIS LINE IS PRESENT
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientsView {

    private static final Logger logger = LoggerFactory.getLogger(PatientsView.class);

    private VBox patientCardsListContainer;
    private Label loadingLabel;
    private List<Patient> patientsList = new ArrayList<>();
    private int displayedPatientCount = 5;
    private Button viewMoreButton;
    private Consumer<Node> contentSwitcher;

    private ScrollPane viewScrollPane; 

    public PatientsView(Consumer<Node> contentSwitcher) {
        this.contentSwitcher = contentSwitcher;
        logger.info("PatientsView initialized.");
        this.viewScrollPane = createPatientManagementContent();
    }

    public ScrollPane getView() {
        return this.viewScrollPane;
    }

    private ScrollPane createPatientManagementContent() {
        logger.debug("Creating patient management content UI.");
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));

        Label headerTitle = new Label("Patient Management");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("Monitor and manage your patient caseload");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        patientCardsListContainer = new VBox(15); 

        content.getChildren().addAll(headerBox, patientCardsListContainer);

        viewMoreButton = new Button("View More Patients");
        viewMoreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreButton.setTextFill(Color.web("#6a1b9a"));
        viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreButton.setOnMouseEntered(e -> viewMoreButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setOnMouseExited(e -> viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));

        viewMoreButton.setOnAction(e -> {
            displayedPatientCount = patientsList.size();
            refreshPatientListDisplay();
        });
        
        content.getChildren().add(viewMoreButton);

        loadingLabel = new Label("Loading patients...");
        loadingLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 14));
        loadingLabel.setTextFill(Color.web("#777777"));
        loadingLabel.setVisible(false);
        loadingLabel.setManaged(false);
        content.getChildren().add(loadingLabel);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #f0f2f5;");

        refreshPatientListDisplay();

        return scrollPane;
    }

    private void refreshPatientListDisplay() {
        Platform.runLater(() -> {
            logger.debug("Refreshing patient list display. Current patientsList size: {}", patientsList.size());
            patientCardsListContainer.getChildren().clear();

            if (patientsList.isEmpty()) {
                patientCardsListContainer.getChildren().add(new Label("No patients found."));
                logger.warn("Patient list is empty. Displaying 'No patients found'.");
            } else {
                int count = Math.min(displayedPatientCount, patientsList.size());
                for (int i = 0; i < count; i++) {
                    patientCardsListContainer.getChildren().add(createPatientCard(patientsList.get(i)));
                }
                if (patientsList.size() > displayedPatientCount) {
                    viewMoreButton.setVisible(true);
                    viewMoreButton.setManaged(true);
                } else {
                    viewMoreButton.setVisible(false);
                    viewMoreButton.setManaged(false);
                }
            }
             logger.debug("View More button visibility: {}", viewMoreButton.isVisible());
        });
    }

    private HBox createPatientCard(Patient patient) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label initialsLabel = new Label(patient.getInitials());
        initialsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setPrefSize(50, 50);
        initialsLabel.setAlignment(Pos.CENTER);
        initialsLabel.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 25;");

        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(patient.getName());
        nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.web("#333333"));

        Label contactLabel = new Label("Last contact: " + patient.getLastContact());
        contactLabel.setFont(Font.font("Inter", 12));
        contactLabel.setTextFill(Color.web("#777777"));

        HBox statusRiskBox = new HBox(10);
        Label statusLabel = new Label(patient.getStatus());
        statusLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web("#333333"));
        statusLabel.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5; -fx-padding: 3 8;");

        Label riskLabel = new Label(patient.getRisk());
        riskLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        riskLabel.setTextFill(Color.WHITE);
        riskLabel.setStyle("-fx-background-color: " + getRiskColor(patient.getRisk()) + "; -fx-background-radius: 5; -fx-padding: 3 8;");

        statusRiskBox.getChildren().addAll(statusLabel, riskLabel);
        detailsBox.getChildren().addAll(nameLabel, contactLabel, statusRiskBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button chatButton = createActionButton("Chat", "\uD83D\uDCAC");
        chatButton.setOnAction(e -> openChatRoom(patient));

        Button analyticsButton = createActionButton("Analytics", "\uD83D\uDCCA");
        analyticsButton.setOnAction(e -> logger.info("View analytics for {} clicked.", patient.getName()));

        HBox actionButtonsBox = new HBox(10, chatButton, analyticsButton);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(initialsLabel, detailsBox, spacer, actionButtonsBox);
        return card;
    }

    private Button createActionButton(String text, String icon) {
        Button button = new Button(icon + " " + text);
        button.setFont(Font.font("Inter", 12));
        button.setTextFill(Color.web("#555555"));
        button.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        return button;
    }

    private String getRiskColor(String risk) {
        return switch (risk.toLowerCase()) {
            case "low risk" -> "#4CAF50";
            case "medium risk" -> "#FFC107";
            case "high risk" -> "#F44336";
            default -> "#777777";
        };
    }

    private void openChatRoom(Patient patient) {
        logger.info("Attempting to open chat room for patient: {}", patient.getName());
        contentSwitcher.accept(createChatRoomUI(patient));
    }

    private VBox createChatRoomUI(Patient patient) {
        logger.debug("Creating chat room UI for patient: {}", patient.getName());
        VBox chatRoom = new VBox(10);
        chatRoom.setPadding(new Insets(20, 40, 20, 40));
        chatRoom.setAlignment(Pos.TOP_CENTER);
        chatRoom.setStyle("-fx-background-color: #f0f2f5;");

        HBox chatHeader = new HBox(15);
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setPadding(new Insets(0, 0, 15, 0));

        Button backButton = new Button("\u2190 Back to Patients");
        backButton.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 14));
        backButton.setTextFill(Color.web("#6a1b9a"));
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 5; -fx-padding: 8 15;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 5; -fx-padding: 8 15;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 5; -fx-padding: 8 15;"));
        backButton.setOnAction(e -> contentSwitcher.accept(createPatientManagementContent()));

        Label chatTitle = new Label("Chat with " + patient.getName());
        chatTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        chatTitle.setTextFill(Color.web("#333333"));
        HBox.setHgrow(chatTitle, Priority.ALWAYS);

        chatHeader.getChildren().addAll(backButton, chatTitle);

        VBox messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(15));
        messagesBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        messagesBox.setMinHeight(400);
        messagesBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(messagesBox, Priority.ALWAYS);

        ScrollPane messagesScrollPane = new ScrollPane(messagesBox);
        messagesScrollPane.setFitToWidth(true);
        messagesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagesScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        messagesBox.getChildren().addAll(
                createChatMessage("Hello " + patient.getName() + "!", true),
                createChatMessage("Hi Dr. Sarah! I'm feeling a bit better today.", false),
                createChatMessage("That's great to hear!", true),
                createChatMessage("What specifically has improved?", true),
                createChatMessage("I've been sleeping more consistently and my anxiety levels have decreased.", false),
                createChatMessage("That's fantastic progress! Keep up the good work.", true),
                createChatMessage("Thank Thank you! I'm trying my best.", false)
        );

        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(15, 0, 0, 0));
        inputArea.setAlignment(Pos.CENTER);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setPrefHeight(40);
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        messageInput.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 8 15;");

        Button sendButton = new Button("Send");
        sendButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        sendButton.setTextFill(Color.WHITE);
        sendButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 10 25;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 8; -fx-padding: 10 25;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 10 25;"));

        Runnable sendMessageAction = () -> {
            String message = messageInput.getText();
            if (!message.trim().isEmpty()) {
                messagesBox.getChildren().add(createChatMessage(message, true));
                messageInput.clear();
                messagesScrollPane.setVvalue(1.0);
            }
        };

        sendButton.setOnAction(e -> sendMessageAction.run());
        messageInput.setOnAction(e -> sendMessageAction.run());

        inputArea.getChildren().addAll(messageInput, sendButton);

        chatRoom.getChildren().addAll(chatHeader, messagesScrollPane, inputArea);
        return chatRoom;
    }

    private HBox createChatMessage(String message, boolean isPsychologist) {
        HBox messageBubbleContainer = new HBox();
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(450);

        if (isPsychologist) {
            msgLabel.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 15;");
            messageBubbleContainer.setAlignment(Pos.CENTER_RIGHT);
        } else {
            msgLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-background-radius: 15; -fx-padding: 10 15;");
            messageBubbleContainer.setAlignment(Pos.CENTER_LEFT);
        }
        messageBubbleContainer.getChildren().add(msgLabel);
        return messageBubbleContainer;
    }

    // --- METHODS FOR THE CONTROLLER ---

    public void populatePatientList(List<Patient> patients) {
        Platform.runLater(() -> {
            this.patientsList = patients;
            logger.info("Populating patient list with {} items.", patients.size());
            refreshPatientListDisplay();
        });
    }
    
    public void showLoading(boolean isLoading) {
        Platform.runLater(() -> {
            logger.info("Showing loading state for PatientsView: {}", isLoading);
            if (loadingLabel != null) {
                loadingLabel.setVisible(isLoading);
                loadingLabel.setManaged(isLoading);
            }
            if (patientCardsListContainer != null) {
                 patientCardsListContainer.setVisible(!isLoading); // Hide when loading
                 patientCardsListContainer.setManaged(!isLoading); // And manage space
            }
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(!isLoading && patientsList.size() > displayedPatientCount);
                viewMoreButton.setManaged(!isLoading && patientsList.size() > displayedPatientCount);
            }
        });
    }
    
    public void showError(String message) {
        Platform.runLater(() -> {
            logger.error("Displaying error in PatientsView: {}", message);
            if (patientCardsListContainer != null) {
                patientCardsListContainer.getChildren().clear();
                patientCardsListContainer.getChildren().add(new Label("Error loading patients: " + message));
            }
            if (loadingLabel != null) { loadingLabel.setVisible(false); loadingLabel.setManaged(false); }
            if (viewMoreButton != null) { viewMoreButton.setVisible(false); viewMoreButton.setManaged(false); }

            new Alert(Alert.AlertType.ERROR, message).showAndWait();
        });
    } 
}