package com.lifecompass.view.Psycologiestview;

import com.lifecompass.model.psychologist.Appointment;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region; // Added for Region.USE_COMPUTED_SIZE
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppointmentsView {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentsView.class);

    private List<Appointment> appointmentsList = new ArrayList<>();
    private int displayedAppointmentCount = 5;
    private VBox appointmentCardsListContainer;
    private Button viewMoreButton;
    private Label loadingLabel;


    private Stage parentStage;
    private Consumer<Node> contentSwitcher; 
    private TriConsumer<Appointment, LocalDate, String> rescheduleHandler; 

    private ScrollPane viewScrollPane; 

    public AppointmentsView(Stage parentStage, Consumer<Node> contentSwitcher) {
        this.parentStage = parentStage;
        this.contentSwitcher = contentSwitcher;
        logger.info("AppointmentsView initialized.");
        this.viewScrollPane = createAppointmentManagementContent(); 
    }

    public ScrollPane getView() {
        return this.viewScrollPane;
    }

    private ScrollPane createAppointmentManagementContent() {
        logger.debug("Creating appointment management content UI.");
        VBox content = new VBox(20); 
        content.setPadding(new Insets(30, 40, 30, 40));
        VBox.setVgrow(content, Priority.ALWAYS); 
        // Reverted to original style, removed debug styles for content VBox
        content.setStyle("-fx-background-color: #f9fafb;"); 

        Label headerTitle = new Label("Appointment Management");
        headerTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        headerTitle.setTextFill(Color.web("#333333"));

        Label headerSubtitle = new Label("View and manage your appointment schedule");
        headerSubtitle.setFont(Font.font("Inter", 14));
        headerSubtitle.setTextFill(Color.web("#777777"));

        VBox headerBox = new VBox(5, headerTitle, headerSubtitle);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        appointmentCardsListContainer = new VBox(15); 
        // Reverted to original style, removed aggressive debugging styles for the card container
        appointmentCardsListContainer.setStyle("-fx-background-color: transparent;"); 
        VBox.setVgrow(appointmentCardsListContainer, Priority.ALWAYS); 

        content.getChildren().addAll(headerBox, appointmentCardsListContainer);

        viewMoreButton = new Button("View More Appointments");
        viewMoreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreButton.setTextFill(Color.web("#6a1b9a"));
        viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreButton.setOnMouseEntered(e -> viewMoreButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreButton.setOnMouseExited(e -> viewMoreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));

        viewMoreButton.setOnAction(e -> {
            displayedAppointmentCount = appointmentsList.size();
            refreshAppointmentListDisplay();
            viewMoreButton.setVisible(false);
            viewMoreButton.setManaged(false);
            logger.info("View More Appointments button clicked. Displaying all {} appointments.", appointmentsList.size());
        });
        
        content.getChildren().add(viewMoreButton);

        loadingLabel = new Label("Loading appointments...");
        loadingLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 14));
        loadingLabel.setTextFill(Color.web("#777777"));
        loadingLabel.setVisible(false); // Hidden by default
        loadingLabel.setManaged(false); // Does not take up space when hidden
        content.getChildren().add(loadingLabel);

        viewScrollPane = new ScrollPane(content); 
        viewScrollPane.setFitToWidth(true);
        viewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        viewScrollPane.setStyle("-fx-background-color: #f0f2f5;"); 
        viewScrollPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE); 

        return viewScrollPane;
    }

    private void refreshAppointmentListDisplay() {
        logger.debug("Refreshing appointment list display. Current displayed count: {}", displayedAppointmentCount);
        appointmentCardsListContainer.getChildren().clear(); // This clear is correct and needed here
        int count = Math.min(displayedAppointmentCount, appointmentsList.size());
        
        if (appointmentsList.isEmpty()) {
            appointmentCardsListContainer.getChildren().add(new Label("No appointments found."));
            logger.warn("Appointment list is empty. Displaying 'No appointments found'.");
        } else {
            logger.debug("Attempting to add {} appointment cards.", count);
            for (int i = 0; i < count; i++) {
                try { 
                    HBox card = createAppointmentCard(appointmentsList.get(i), parentStage);
                    appointmentCardsListContainer.getChildren().add(card);
                    logger.debug("Successfully added card to container for appointment: PatientName={}", appointmentsList.get(i).getPatientName());
                } catch (Exception e) {
                    logger.error("ERROR: Exception while creating appointment card for appointment ID {}. Details: {}", 
                                 (appointmentsList.get(i) != null ? appointmentsList.get(i).getId() : "null appointment"), 
                                 e.getMessage(), e);
                    System.err.println("CRITICAL ERROR IN APPOINTMENT CARD CREATION: " + e.getMessage()); 
                    e.printStackTrace(); 
                    appointmentCardsListContainer.getChildren().add(new Label("Error displaying appointment: " + 
                                                                    (appointmentsList.get(i) != null ? appointmentsList.get(i).getPatientName() : "Unknown Patient") + ". Check logs."));
                }
            }
        }
        logger.debug("Container after adding cards. Children count: {}. Container PrefSize: {}x{}. Container IsVisible: {}",
                     appointmentCardsListContainer.getChildren().size(),
                     appointmentCardsListContainer.getPrefWidth(), appointmentCardsListContainer.getPrefHeight(),
                     appointmentCardsListContainer.isVisible());
        if (!appointmentCardsListContainer.getChildren().isEmpty()) {
            System.out.println("DEBUG: appointmentCardsListContainer has children, they should be visible. Current bounds: " + appointmentCardsListContainer.getLayoutBounds());
        }
        appointmentCardsListContainer.layout(); 
        appointmentCardsListContainer.requestLayout();

        if (viewMoreButton != null) {
            boolean shouldShow = appointmentsList.size() > displayedAppointmentCount;
            viewMoreButton.setVisible(shouldShow);
            viewMoreButton.setManaged(shouldShow);
            logger.debug("View More button visibility: {}", shouldShow);
        }
    }

    private HBox createAppointmentCard(Appointment appointment, Stage ownerStage) {
        logger.debug("Inside createAppointmentCard for: PatientName={}, Time={}, Date={}", 
                     appointment.getPatientName(), appointment.getTime(), appointment.getLocalDate());
        System.out.println("DEBUG: createAppointmentCard started for " + appointment.getPatientName() + " (ID: " + appointment.getId() + ")"); 
        
        // --- Input Validation (for debugging) ---
        if (appointment.getPatientName() == null || appointment.getPatientName().isEmpty()) {
            System.err.println("CRITICAL DATA ERROR: Patient Name is null or empty for ID: " + appointment.getId());
            logger.error("CRITICAL DATA ERROR: Patient Name is null or empty for ID: {}", appointment.getId());
        }
        if (appointment.getSessionType() == null || appointment.getSessionType().isEmpty()) {
            System.err.println("CRITICAL DATA ERROR: Session Type is null or empty for ID: " + appointment.getId());
            logger.error("CRITICAL DATA ERROR: Appointment Session Type is null or empty for ID: {}", appointment.getId());
        }
        if (appointment.getTime() == null || appointment.getTime().isEmpty()) {
            System.err.println("CRITICAL DATA ERROR: Appointment Time is null or empty for ID: " + appointment.getId());
            logger.error("CRITICAL DATA ERROR: Appointment Time is null or empty for ID: {}", appointment.getId());
        }
        if (appointment.getDuration() == null || appointment.getDuration().isEmpty()) {
            System.err.println("CRITICAL DATA ERROR: Appointment Duration is null or empty for ID: " + appointment.getId());
            logger.error("CRITICAL DATA ERROR: Appointment Duration is null or empty for ID: {}", appointment.getId());
        }
        if (appointment.getLocalDate() == null) {
            System.err.println("CRITICAL DATA ERROR: Appointment LocalDate is null for ID: " + appointment.getId());
            logger.error("CRITICAL DATA ERROR: Appointment LocalDate is null for ID: {}", appointment.getId());
        }
        // --- END Input Validation ---

        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        // Reverted to original styling, removed aggressive debugging colors/border
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        // Ensure card has proper flexible sizing but with a consistent preferred height
        card.setPrefWidth(Region.USE_COMPUTED_SIZE); // Let width be computed by content (growable)
        card.setMinWidth(Region.USE_PREF_SIZE);
        card.setMaxWidth(Double.MAX_VALUE); // Maximize width
        HBox.setHgrow(card, Priority.ALWAYS); // Allow parent to distribute width
        
        card.setPrefHeight(100); // Consistent preferred height (adjust as needed)
        card.setMinHeight(Region.USE_PREF_SIZE); 
        card.setMaxHeight(Region.USE_PREF_SIZE); 

        // Replaced ImageView with a text-based icon (calendar symbol)
        Label calendarSymbolLabel = new Label("\uD83D\uDCC5"); // Unicode Calendar Symbol
        calendarSymbolLabel.setFont(Font.font("Arial", 30));
        calendarSymbolLabel.setTextFill(Color.web("#6a1b9a"));
        calendarSymbolLabel.setPadding(new Insets(0, 10, 0, 0));
        

        VBox detailsBox = new VBox(5);
        Label patientNameLabel = new Label(appointment.getPatientName());
        patientNameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        patientNameLabel.setTextFill(Color.web("#333333"));
        logger.debug("  - PatientNameLabel text: '{}'", patientNameLabel.getText());

        Label sessionTypeLabel = new Label(appointment.getSessionType());
        sessionTypeLabel.setFont(Font.font("Inter", 12));
        sessionTypeLabel.setTextFill(Color.web("#777777"));
        logger.debug("  - SessionTypeLabel text: '{}'", sessionTypeLabel.getText());

        String dateString = (appointment.getLocalDate() != null) ? 
                             appointment.getLocalDate().toString() : "N/A Date"; 
        String timeDisplay = (appointment.getTime() != null) ? appointment.getTime() : "N/A Time";
        String durationDisplay = (appointment.getDuration() != null) ? appointment.getDuration() : "N/A Duration";

        Label timeDurationLabel = new Label(timeDisplay + " \u2022 " + durationDisplay + " \u2022 " + dateString);
        timeDurationLabel.setFont(Font.font("Inter", 12));
        timeDurationLabel.setTextFill(Color.web("#555555"));
        logger.debug("  - TimeDurationLabel text: '{}'", timeDurationLabel.getText());

        detailsBox.getChildren().addAll(patientNameLabel, sessionTypeLabel, timeDurationLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button rescheduleButton = new Button("Reschedule");
        rescheduleButton.setFont(Font.font("Inter", 12));
        rescheduleButton.setTextFill(Color.web("#555555"));
        rescheduleButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;");
        rescheduleButton.setOnMouseEntered(e -> rescheduleButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #bbbbbb; -fx-border-width: 1;"));
        rescheduleButton.setOnMouseExited(e -> rescheduleButton.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        rescheduleButton.setOnAction(e -> openRescheduleDialog(appointment, ownerStage));

        Button startSessionButton = new Button("Start Session");
        startSessionButton.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        startSessionButton.setTextFill(Color.WHITE);
        startSessionButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 8 12;");
        startSessionButton.setOnMouseEntered(e -> startSessionButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 8; -fx-padding: 8 20;"));
        startSessionButton.setOnMouseExited(e -> startSessionButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 8 20;"));
        startSessionButton.setOnAction(e -> openChatRoom(appointment.getPatientName(), ownerStage));

        HBox actionButtonsBox = new HBox(10, rescheduleButton, startSessionButton);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(calendarSymbolLabel, detailsBox, spacer, actionButtonsBox); 
        logger.debug("Appointment card creation ENDED for {}. Card final children count: {}. Card PrefSize: {}x{}", 
                     appointment.getPatientName(), card.getChildren().size(), card.getPrefWidth(), card.getPrefHeight());
        System.out.println("DEBUG: createAppointmentCard finished for " + appointment.getPatientName() + ", returning card. Card visibility: " + card.isVisible()); 
        return card;
    }

    // createImageView is removed as it's no longer used in AppointmentsView

    private void openRescheduleDialog(Appointment appointment, Stage ownerStage) {
        logger.debug("Opening reschedule dialog for appointment ID: {}", appointment.getId());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Reschedule Appointment");
        dialog.setHeaderText("Reschedule " + appointment.getPatientName() + "'s Session");

        ButtonType rescheduleButtonType = new ButtonType("Reschedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(rescheduleButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        LocalDate initialDate = (appointment.getDate() != null) ? 
                                 appointment.getLocalDate() : 
                                 LocalDate.now();
        DatePicker datePicker = new DatePicker(initialDate);
        
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Select new time");
        timeComboBox.getItems().addAll("9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM");
        if (appointment.getTime() != null && timeComboBox.getItems().contains(appointment.getTime())) {
            timeComboBox.setValue(appointment.getTime());
        }

        grid.add(new Label("Current Appointment:"), 0, 0);
        grid.add(new Label(appointment.getTime() + " on " + initialDate.toString()), 1, 0);
        grid.add(new Label("New Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("New Time:"), 0, 2);
        grid.add(timeComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node rescheduleButtonNode = dialog.getDialogPane().lookupButton(rescheduleButtonType);
        rescheduleButtonNode.setDisable(true);

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            rescheduleButtonNode.setDisable(newDate == null || timeComboBox.getValue() == null);
        });
        timeComboBox.valueProperty().addListener((obs, oldTime, newTime) -> {
            rescheduleButtonNode.setDisable(newTime == null || datePicker.getValue() == null);
        });

        Optional<ButtonType> result = null;
        try {
            result = dialog.showAndWait();
        } catch (IllegalStateException e) {
            logger.error("Error showing reschedule dialog: Illegal state during showAndWait. Full error: {}", e.getMessage(), e);
            showError("Failed to open reschedule dialog due to internal UI issue. Please restart the app if it persists.");
            return;
        } catch (Exception e) {
            logger.error("Unexpected error showing reschedule dialog: {}", e.getMessage(), e);
            showError("An unexpected error occurred while opening reschedule dialog: " + e.getMessage());
            return;
        }

        if (result != null) {
            result.ifPresent(buttonType -> {
                if (buttonType == rescheduleButtonType && rescheduleHandler != null) {
                    LocalDate newDate = datePicker.getValue();
                    String newTime = timeComboBox.getValue();
                    if (newDate != null && newTime != null) {
                        logger.info("Reschedule dialog: Rescheduling appointment {} to {} at {}", appointment.getId(), newDate, newTime);
                        rescheduleHandler.accept(appointment, newDate, newTime);
                    } else {
                        logger.warn("Reschedule dialog: New date or time not selected when trying to reschedule.");
                        showError("Please select both a new date and time."); // Provide user feedback
                    }
                } else if (buttonType == ButtonType.CANCEL) {
                    logger.info("Reschedule dialog: Cancelled by user.");
                }
            });
        }
    }

    private void openChatRoom(String patientName, Stage ownerStage) {
        logger.info("Opening chat room for patient: {}", patientName);
        Stage chatStage = new Stage();
        chatStage.initModality(Modality.WINDOW_MODAL);
        chatStage.initOwner(ownerStage);
        chatStage.setTitle("Chat with " + patientName);

        VBox chatRoom = new VBox(10);
        chatRoom.setPadding(new Insets(20));
        chatRoom.setAlignment(Pos.TOP_CENTER);
        chatRoom.setStyle("-fx-background-color: #f0f2f5;");

        HBox chatHeader = new HBox(15);
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setPadding(new Insets(0, 0, 15, 0));

        Label chatTitle = new Label("Chat with " + patientName);
        chatTitle.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        chatTitle.setTextFill(Color.web("#333333"));
        HBox.setHgrow(chatTitle, Priority.ALWAYS);

        chatHeader.getChildren().addAll(chatTitle);

        VBox messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(15));
        messagesBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        messagesBox.setMinHeight(300);
        messagesBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(messagesBox, Priority.ALWAYS);

        ScrollPane messagesScrollPane = new ScrollPane(messagesBox);
        messagesScrollPane.setFitToWidth(true);
        messagesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagesScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        messagesBox.getChildren().addAll(
            createChatMessage("Hello " + patientName + "!", true),
            createChatMessage("Hi Dr. Sarah! Ready for our session.", false)
        );

        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(15, 0, 0, 0));
        inputArea.setAlignment(Pos.CENTER);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setPrefHeight(35);
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        messageInput.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-padding: 8 15;");

        Button sendButton = new Button("Send");
        sendButton.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        sendButton.setTextFill(Color.WHITE);
        sendButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 8 20;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #7b24b4; -fx-background-radius: 8; -fx-padding: 8 20;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #6a1b9a; -fx-background-radius: 8; -fx-padding: 8 20;"));

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

        Scene chatScene = new Scene(chatRoom, 600, 500);
        chatStage.setScene(chatScene);
        chatStage.show();
    }

    private HBox createChatMessage(String message, boolean isPsychologist) {
        HBox messageBubbleContainer = new HBox();
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(350);

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

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public void populateAppointmentsList(List<Appointment> appointments) {
        Platform.runLater(() -> { 
            this.appointmentsList = appointments;
            logger.info("Populating appointments list with {} items.", appointments.size());
            refreshAppointmentListDisplay();
        });
    }

    public void setRescheduleHandler(TriConsumer<Appointment, LocalDate, String> handler) { // Corrected: TriConsumer used directly
        this.rescheduleHandler = handler; // Assign the handler directly
        logger.debug("Reschedule handler set for appointments view.");
    }

    public void showLoading(boolean isLoading) {
        Platform.runLater(() -> { 
            logger.info("Showing loading state for AppointmentsView: {}", isLoading);
            if (appointmentCardsListContainer != null) {
                // IMPORTANT FIX: Only clear if going into loading state.
                // refreshAppointmentListDisplay() is responsible for clearing and populating the actual list.
                if (isLoading) { 
                    appointmentCardsListContainer.getChildren().clear();
                    appointmentCardsListContainer.getChildren().add(loadingLabel); // Show loading text
                    loadingLabel.setVisible(true);
                    loadingLabel.setManaged(true);
                } else { 
                    // When loading finishes, just hide the loading label.
                    // Do NOT clear appointmentCardsListContainer here. refreshAppointmentListDisplay() will repopulate.
                    loadingLabel.setVisible(false);
                    loadingLabel.setManaged(false);
                }
            } else {
                logger.warn("appointmentCardsListContainer is null in showLoading. UI might not be fully initialized.");
            }
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(!isLoading && appointmentsList.size() > displayedAppointmentCount);
                viewMoreButton.setManaged(!isLoading && appointmentsList.size() > displayedAppointmentCount);
            }
        });
    }

    public void showError(String message) {
        Platform.runLater(() -> {
            logger.error("Displaying error in AppointmentsView: {}", message);
            if (appointmentCardsListContainer != null) {
                appointmentCardsListContainer.getChildren().clear();
                appointmentCardsListContainer.getChildren().add(new Label("Error loading appointments: " + message));
            }
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(false);
                viewMoreButton.setManaged(false);
            }
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Appointment Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(message);
            errorAlert.showAndWait(); 
        });
    }

    public void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}