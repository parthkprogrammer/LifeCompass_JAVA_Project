package com.lifecompass.view;

import com.lifecompass.util.SceneManager;
import javafx.application.Platform; // Import Platform for running UI updates on FX thread
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene; // Not directly used in VBox extending class, but good to keep if needed
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

// NEW IMPORTS FOR FIREBASE INTEGRATION
import com.lifecompass.controller.AuthController; // To get logged-in user info
import com.lifecompass.model.Feedback; // The new Feedback model
import com.lifecompass.dao.impl.FeedbackDaoFirestoreImpl; // The new Feedback DAO

import java.util.concurrent.CompletableFuture; // For asynchronous database operations
import java.util.concurrent.ExecutionException;


public class FeedbackScreen extends VBox {

    private Stage ownerStage;
    private TextField subjectField;
    private TextArea feedbackContentArea;

    // NEW: Instance of the Feedback DAO
    private final FeedbackDaoFirestoreImpl feedbackDao = new FeedbackDaoFirestoreImpl();

    public FeedbackScreen(Stage ownerStage) {
        this.ownerStage = ownerStage;
        initializeUI();
    }

    private void initializeUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #f9fafb;");

        Label headerLabel = new Label("Submit Your Feedback");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-text-fill: #333333;");

        Label instructionsLabel = new Label("We'd love to hear your thoughts, suggestions, or any issues you've encountered with LifeCompass.");
        instructionsLabel.setFont(Font.font("Arial", 14));
        instructionsLabel.setStyle("-fx-text-fill: #606060;");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setAlignment(Pos.CENTER);

        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label subjectLabel = new Label("Subject:");
        subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        subjectField = new TextField();
        subjectField.setPromptText("e.g., Bug Report, Feature Suggestion, General Inquiry");
        subjectField.setPrefHeight(35);

        Label contentLabel = new Label("Your Feedback:");
        contentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        feedbackContentArea = new TextArea();
        feedbackContentArea.setPromptText("Please provide detailed feedback here (max 500 characters)...");
        feedbackContentArea.setPrefRowCount(8);
        feedbackContentArea.setWrapText(true);
        feedbackContentArea.setMaxHeight(200);
        VBox.setVgrow(feedbackContentArea, Priority.ALWAYS);

        Button submitButton = new Button("Submit Feedback");
        submitButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20; -fx-background-radius: 8;");
        submitButton.setOnAction(e -> handleSubmitFeedback());
        submitButton.setMaxWidth(Double.MAX_VALUE);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f0f2f5; -fx-text-fill: #555555; -fx-font-size: 16px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #cccccc; -fx-border-width: 1;");
        cancelButton.setOnAction(e -> ((Stage) this.getScene().getWindow()).close());
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(submitButton, Priority.ALWAYS);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        buttonBox.getChildren().addAll(submitButton, cancelButton);

        formContainer.getChildren().addAll(subjectLabel, subjectField, contentLabel, feedbackContentArea, buttonBox);

        this.getChildren().addAll(headerLabel, instructionsLabel, formContainer);
    }

    private void handleSubmitFeedback() {
        String subject = subjectField.getText().trim();
        String content = feedbackContentArea.getText().trim();

        if (subject.isEmpty() || content.isEmpty()) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in both subject and feedback content.");
            return;
        }

        if (content.length() > 500) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Feedback Too Long", "Feedback content is limited to 500 characters. Please shorten your message.");
            return;
        }

        String userId = AuthController.loggedInUserId;
        String userEmail = AuthController.loggedInUserEmail;

        if (userId == null) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "You must be logged in to submit feedback.");
            return;
        }

        Feedback newFeedback = new Feedback(userId, userEmail, subject, content);

        // Run database operation asynchronously to keep UI responsive
        CompletableFuture.runAsync(() -> {
            try {
                feedbackDao.addFeedback(newFeedback);
                Platform.runLater(() -> {
                    SceneManager.showAlert(Alert.AlertType.INFORMATION, "Thank You!", "Your feedback has been submitted successfully. We appreciate it!");
                    ((Stage) this.getScene().getWindow()).close(); // Close the feedback window
                });
            } catch (ExecutionException | InterruptedException e) {
                Platform.runLater(() -> {
                    System.err.println("Error submitting feedback to Firebase: " + e.getMessage());
                    SceneManager.showAlert(Alert.AlertType.ERROR, "Submission Failed", "Failed to submit feedback. Please try again.");
                });
                e.printStackTrace();
            }
        });
    }

    /**
     * Shows the Feedback screen as a modal dialog.
     */
    public void show() {
        Stage feedbackStage = new Stage();
        feedbackStage.initModality(Modality.WINDOW_MODAL);
        feedbackStage.initOwner(ownerStage);
        feedbackStage.setTitle("LifeCompass: Submit Feedback");

        Scene scene = new Scene(this, 600, 500);
        feedbackStage.setScene(scene);
        feedbackStage.showAndWait();
    }
}