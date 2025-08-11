
package com.lifecompass.view;

import com.lifecompass.model.JournalEntry;
import com.lifecompass.services.FirebaseJournalService; // CONFIRMED: Using 'services' as per your last provided code

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.cloud.firestore.ListenerRegistration;


public class JournalAppUIFull {

    private static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
    private static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
    private static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
    private static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
    private static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
    private static final String TEXT_COLOR_BLUE = "-fx-text-fill: #007bff;";
    private static final String TAG_STYLE_OUTLINE = "-fx-background-color: #e0e0e0; -fx-padding: 5px 10px; -fx-background-radius: 15px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";";
    private static final String TAG_STYLE_SELECTED = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 15px; -fx-font-size: 12px;";
    private static final String PROMPT_STYLE = "-fx-background-color: #e0f2ff; -fx-padding: 10px; -fx-background-radius: 5px; -fx-border-color: #cce7ff; -fx-border-radius: 5px; -fx-border-width: 1px; " + TEXT_COLOR_BLUE;

    private static final List<String> JOURNAL_TAGS = Arrays.asList(
            "Work", "Family", "Relationships", "Health", "Goals", "Gratitude",
            "Stress", "Achievement", "Learning", "Travel", "Hobbies", "Reflection"
    );

    private ObservableList<String> selectedTags = FXCollections.observableArrayList();
    private ObservableList<JournalEntry> journalEntries = FXCollections.observableArrayList();
    private String currentAttachedImagePath = null;

    private TextField entryTitleField;
    private TextArea yourThoughtsArea;
    private FlowPane tagButtonsPane;

    private Tab writeEntryTab;
    private Tab journalHistoryTab;
    private TabPane mainTabContentPane;

    private Stage primaryStage;
    private UserDashboardScreen dashboardInstance;

    private FirebaseJournalService firebaseJournalService;

    private ExecutorService firebaseExecutor = Executors.newSingleThreadExecutor();

    private ListenerRegistration journalEntriesListenerRegistration;
    private ListenerRegistration journalCountListenerRegistration;

    private VBox historyEntriesListContainer;
    private FilteredList<JournalEntry> journalHistoryFilteredData;
    private TextField historySearchField; // Keep a reference to the search field in history


    public JournalAppUIFull(Stage primaryStage, UserDashboardScreen dashboardInstance) {
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;

        this.firebaseJournalService = new FirebaseJournalService();
        System.out.println("JournalAppUIFull: Initialized with FirebaseJournalService.");
    }

    public VBox createJournalScreenContent() {
        System.out.println("JournalAppUIFull: createJournalScreenContent called.");

        VBox journalContentLayout = new VBox();
        journalContentLayout.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        journalContentLayout.setAlignment(Pos.TOP_CENTER);
        journalContentLayout.setPadding(new Insets(20));
        journalContentLayout.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(journalContentLayout, Priority.ALWAYS);

        mainTabContentPane = new TabPane();
        mainTabContentPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainTabContentPane.tabMinHeightProperty().set(0);
        mainTabContentPane.tabMaxHeightProperty().set(0);
        mainTabContentPane.setTabMaxHeight(0);
        mainTabContentPane.setTabMinHeight(0);
        mainTabContentPane.setPadding(Insets.EMPTY);
        mainTabContentPane.setTabMinWidth(0);
        mainTabContentPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(mainTabContentPane, Priority.ALWAYS);

        writeEntryTab = new Tab("Write Entry");
        writeEntryTab.setContent(createWriteEntryTabContent());
        writeEntryTab.setClosable(false);

        journalHistoryTab = new Tab("Journal History");
        journalHistoryTab.setContent(createJournalHistoryTabContent());
        journalHistoryTab.setClosable(false);

        mainTabContentPane.getTabs().addAll(writeEntryTab, journalHistoryTab);

        HBox journalNavigationButtons = createJournalInternalNavBar();

        journalContentLayout.getChildren().addAll(journalNavigationButtons, mainTabContentPane);

        Platform.runLater(() -> {
            mainTabContentPane.getSelectionModel().select(writeEntryTab);
            System.out.println("JournalAppUIFull: Initial tab set to Write Entry.");
        });

        // Start listeners on the JavaFX Application Thread, after UI is built
        Platform.runLater(this::startFirebaseListeners);

        return journalContentLayout;
    }

    private void startFirebaseListeners() {
        System.out.println("JournalAppUIFull: Attempting to start Firebase listeners.");

        if (journalEntriesListenerRegistration == null) {
            journalEntriesListenerRegistration = firebaseJournalService.listenForJournalEntries(entries -> {
                Platform.runLater(() -> {
                    System.out.println("JournalAppUIFull (Platform.runLater): Received " + entries.size() + " entries from service callback. Updating ObservableList.");
                    journalEntries.setAll(entries);
                    System.out.println("JournalAppUIFull (Platform.runLater): ObservableList 'journalEntries' now has " + journalEntries.size() + " entries.");

                    // IMPORTANT: Trigger update of FilteredList and view after journalEntries is updated
                    // This listener is crucial for updating the history view in real-time
                    if (journalHistoryFilteredData != null) { // Ensure history UI components are initialized
                        // Re-apply the current filter (empty or search text) to update filteredData
                        journalHistoryFilteredData.setPredicate(journalHistoryFilteredData.getPredicate());
                        System.out.println("JournalAppUIFull: FilteredList predicate re-evaluated after journalEntries update.");
                        // Then explicitly update the view with the new filtered data
                        if (historyEntriesListContainer != null) {
                            updateJournalEntriesView(historyEntriesListContainer, journalHistoryFilteredData);
                            System.out.println("JournalAppUIFull: Forced history view update after Firebase data arrived.");
                        }
                    }
                });
            });
            System.out.println("JournalAppUIFull: Journal entries listener registered.");
        } else {
            System.out.println("JournalAppUIFull: Journal entries listener already registered.");
        }

        if (journalCountListenerRegistration == null) {
            journalCountListenerRegistration = firebaseJournalService.listenForJournalEntryCount(count -> {
                Platform.runLater(() -> {
                    if (dashboardInstance != null) {
                        dashboardInstance.updateJournalCount(count);
                        System.out.println("JournalAppUIFull: Updated dashboard journal count via callback: " + count);
                    }
                });
            });
            System.out.println("JournalAppUIFull: Journal count listener registered.");
        } else {
            System.out.println("JournalAppUIFull: Journal count listener already registered.");
        }
    }


    public void stopFirebaseListeners() {
        System.out.println("JournalAppUIFull: Attempting to stop Firebase listeners.");
        if (journalEntriesListenerRegistration != null) {
            journalEntriesListenerRegistration.remove();
            journalEntriesListenerRegistration = null;
            System.out.println("JournalAppUIFull: Journal entries listener stopped.");
        }
        if (journalCountListenerRegistration != null) {
            journalCountListenerRegistration.remove();
            journalCountListenerRegistration = null;
            System.out.println("JournalAppUIFull: Journal count listener stopped.");
        }
        if (firebaseExecutor != null && !firebaseExecutor.isShutdown()) {
            firebaseExecutor.shutdownNow();
            System.out.println("JournalAppUIFull: Firebase Executor shutdown.");
        }
    }


    private HBox createJournalInternalNavBar() {
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(0));
        HBox.setHgrow(navBar, Priority.ALWAYS);

        Label writeEntryTabBtn = new Label("Write Entry");
        writeEntryTabBtn.setFont(new Font("Arial Bold", 14));
        writeEntryTabBtn.setPadding(new Insets(10, 20, 10, 20));
        writeEntryTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 1px; -fx-background-radius: 5px 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");

        Label journalHistoryTabBtn = new Label("Journal History");
        journalHistoryTabBtn.setFont(new Font("Arial", 14));
        journalHistoryTabBtn.setPadding(new Insets(10, 20, 10, 20));
        journalHistoryTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");

        navBar.getChildren().addAll(writeEntryTabBtn, journalHistoryTabBtn);

        writeEntryTabBtn.setOnMouseClicked(e -> {
            mainTabContentPane.getSelectionModel().select(writeEntryTab);
            System.out.println("JournalAppUIFull: Switched to Write Entry tab.");
        });
        journalHistoryTabBtn.setOnMouseClicked(e -> {
            mainTabContentPane.getSelectionModel().select(journalHistoryTab);
            System.out.println("JournalAppUIFull: Switched to Journal History tab. Forcing refresh on tab switch.");
            // When history tab is clicked, explicitly update the view using the current filtered data
            if (historyEntriesListContainer != null && journalHistoryFilteredData != null) {
                updateJournalEntriesView(historyEntriesListContainer, journalHistoryFilteredData);
            }
        });

        mainTabContentPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == writeEntryTab) {
                writeEntryTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 1px; -fx-background-radius: 5px 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
                journalHistoryTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
            } else if (newTab == journalHistoryTab) {
                writeEntryTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 1px; -fx-background-radius: 5px 0 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
                journalHistoryTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
            }
        });

        return navBar;
    }

    private VBox createWriteEntryTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox newEntryCard = new VBox(10);
        newEntryCard.setPadding(new Insets(20));
        newEntryCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        newEntryCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(newEntryCard, Priority.ALWAYS);

        Label newEntryTitleLabel = new Label("New Journal Entry");
        newEntryTitleLabel.setFont(new Font("Arial Bold", 16));
        newEntryTitleLabel.setStyle(TEXT_COLOR_DARK_GREY);

        Label newEntryDescription = new Label("Express your thoughts, feelings, and experiences in a safe space");
        newEntryDescription.setFont(new Font("Arial", 12));
        newEntryDescription.setStyle(TEXT_COLOR_GREY);
        newEntryDescription.setWrapText(true);

        Label entryTitleHintLabel = new Label("Entry Title (Optional)");
        entryTitleHintLabel.setFont(new Font("Arial", 12));
        entryTitleHintLabel.setStyle(TEXT_COLOR_GREY);

        entryTitleField = new TextField();
        entryTitleField.setPromptText("Give your entry a title...");
        entryTitleField.setStyle("-fx-control-inner-background: #ffffff; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px;");

        Label yourThoughtsHintLabel = new Label("Your thoughts");
        yourThoughtsHintLabel.setFont(new Font("Arial", 12));
        yourThoughtsHintLabel.setStyle(TEXT_COLOR_GREY);

        yourThoughtsArea = new TextArea();
        yourThoughtsArea.setPromptText("What's on your mind today? How are you feeling? What happened that you'd like to remember or reflect on?");
        yourThoughtsArea.setPrefRowCount(8);
        yourThoughtsArea.setWrapText(true);
        yourThoughtsArea.setStyle("-fx-control-inner-background: #ffffff; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px;");
        VBox.setVgrow(yourThoughtsArea, Priority.ALWAYS);

        newEntryCard.getChildren().addAll(
                newEntryTitleLabel,
                newEntryDescription,
                entryTitleHintLabel,
                entryTitleField,
                yourThoughtsHintLabel,
                yourThoughtsArea
        );

        VBox tagsSection = new VBox(10);
        tagsSection.setPadding(new Insets(20));
        tagsSection.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        tagsSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tagsSection, Priority.ALWAYS);

        Label tagsLabel = new Label("Tags (Optional)");
        tagsLabel.setFont(new Font("Arial Bold", 14));
        tagsLabel.setStyle(TEXT_COLOR_DARK_GREY);

        tagButtonsPane = new FlowPane(10, 10);
        tagButtonsPane.setPadding(new Insets(5, 0, 5, 0));

        for (String tag : JOURNAL_TAGS) {
            Label tagButton = new Label(tag);
            tagButton.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;");
            tagButton.setOnMouseClicked(e -> {
                if (selectedTags.contains(tag)) {
                    selectedTags.remove(tag);
                    tagButton.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;");
                } else {
                    selectedTags.add(tag);
                    tagButton.setStyle(TAG_STYLE_SELECTED + "-fx-cursor: hand;");
                }
            });
            tagButtonsPane.getChildren().add(tagButton);
        }

        HBox mediaButtons = new HBox(15);
        mediaButtons.setAlignment(Pos.CENTER_LEFT);

        Button voiceNoteButton = new Button("Voice Note");
        voiceNoteButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 14px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        voiceNoteButton.setGraphic(new Label("\uD83C\uDFA4"));
        voiceNoteButton.setOnAction(e -> {
            System.out.println("Voice Note button clicked. (Implement voice recording/upload)");
            Alert voiceAlert = new Alert(Alert.AlertType.INFORMATION, "Voice Note functionality coming soon!", ButtonType.OK);
            voiceAlert.setHeaderText(null);
            voiceAlert.showAndWait();
        });

        Button addPhotoButton = new Button("Add Photo");
        addPhotoButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 14px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        addPhotoButton.setGraphic(new Label("\uD83D\uDCF7"));
        addPhotoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image for Journal Entry");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                currentAttachedImagePath = selectedFile.toURI().toString();
                System.out.println("JournalAppUIFull: Selected image for attachment: " + currentAttachedImagePath);
                Alert photoAlert = new Alert(Alert.AlertType.INFORMATION, "Photo selected: \n" + selectedFile.getName(), ButtonType.OK);
                photoAlert.setHeaderText(null);
                photoAlert.showAndWait();
            } else {
                currentAttachedImagePath = null;
                System.out.println("JournalAppUIFull: Image selection cancelled.");
            }
        });

        mediaButtons.getChildren().addAll(voiceNoteButton, addPhotoButton);

        Button saveEntryButton = new Button("Save Entry");
        saveEntryButton.setMaxWidth(Double.MAX_VALUE);
        saveEntryButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10px 0; -fx-font-size: 16px; -fx-cursor: hand;");
        saveEntryButton.disableProperty().bind(yourThoughtsArea.textProperty().isEmpty());
        saveEntryButton.setOnAction(e -> {
            if (!yourThoughtsArea.getText().trim().isEmpty()) {
                String title = entryTitleField.getText().trim().isEmpty() ? "Untitled Entry" : entryTitleField.getText().trim();

                JournalEntry newEntry = new JournalEntry(
                        FirebaseJournalService.DEFAULT_USER_ID,
                        title,
                        yourThoughtsArea.getText(),
                        new ArrayList<>(selectedTags),
                        "📝",
                        currentAttachedImagePath
                );
                System.out.println("JournalAppUIFull: Attempting to save new journal entry: " + newEntry.getTitle());

                firebaseExecutor.submit(() -> {
                    try {
                        firebaseJournalService.addJournalEntry(newEntry);
                        Platform.runLater(() -> {
                            System.out.println("JournalAppUIFull: Journal entry saved successfully to Firebase. Clearing form.");
                            entryTitleField.clear();
                            yourThoughtsArea.clear();
                            selectedTags.clear();
                            currentAttachedImagePath = null;
                            tagButtonsPane.getChildren().forEach(node -> node.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;"));

                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Journal entry saved to cloud!", ButtonType.OK);
                            alert.setHeaderText(null);
                            alert.showAndWait();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            System.err.println("JournalAppUIFull: Failed to save journal entry to Firebase: " + ex.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save journal entry: " + ex.getMessage(), ButtonType.OK);
                            alert.setHeaderText("Save Error");
                            alert.showAndWait();
                        });
                        ex.printStackTrace();
                    }
                });

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please write something in your journal entry before saving.", ButtonType.OK);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        });

        tagsSection.getChildren().addAll(tagsLabel, tagButtonsPane, mediaButtons, saveEntryButton);

        VBox promptsSection = new VBox(10);
        promptsSection.setPadding(new Insets(20));
        promptsSection.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        promptsSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(promptsSection, Priority.ALWAYS);

        Label promptsTitle = new Label("Writing Prompts");
        promptsTitle.setFont(new Font("Arial Bold", 16));
        promptsTitle.setStyle(TEXT_COLOR_DARK_GREY);

        Label promptsDescription = new Label("Need inspiration? Try one of these prompts.");
        promptsDescription.setFont(new Font("Arial", 12));
        promptsDescription.setStyle(TEXT_COLOR_GREY);

        String[] prompts = {
                "What are three things I'm grateful for today?",
                "What challenged me today and how did I handle it?",
                "What made me smile or laugh today?",
                "What would I tell my past self about today?",
                "What am I looking forward to tomorrow?"
        };

        VBox promptsList = new VBox(10);
        for (String prompt : prompts) {
            Label promptLabel = new Label(prompt);
            promptLabel.setFont(new Font("Arial", 14));
            promptLabel.setStyle(PROMPT_STYLE + "-fx-cursor: hand;");
            promptLabel.setMaxWidth(Double.MAX_VALUE);
            promptLabel.setOnMouseClicked(e -> {
                yourThoughtsArea.setText(prompt + "\n\n" + yourThoughtsArea.getText());
                yourThoughtsArea.requestFocus();
                yourThoughtsArea.positionCaret(0);
            });
            promptsList.getChildren().add(promptLabel);
        }

        promptsSection.getChildren().addAll(promptsTitle, promptsDescription, promptsList);

        content.getChildren().addAll(newEntryCard, tagsSection, promptsSection);
        return content;
    }

    private VBox createJournalHistoryTabContent() {
        System.out.println("JournalAppUIFull: createJournalHistoryTabContent called. Setting up history view components.");

        VBox content = new VBox(20);
        content.setPadding(new Insets(0));
        content.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox searchCard = new VBox(10);
        searchCard.setPadding(new Insets(20));
        searchCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        searchCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(searchCard, Priority.ALWAYS);

        StackPane searchInputContainer = new StackPane();
        historySearchField = new TextField(); // Assign to class field
        historySearchField.setPromptText("Search your journal entries...");
        historySearchField.setStyle("-fx-control-inner-background: #ffffff; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px 8px 8px 30px;");
        historySearchField.setPrefHeight(38);

        Label searchIcon = new Label("\uD83D\uDD0D");
        searchIcon.setFont(new Font("Arial", 16));
        searchIcon.setStyle(TEXT_COLOR_GREY);
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(searchIcon, new Insets(0, 0, 0, 10));

        searchInputContainer.getChildren().addAll(historySearchField, searchIcon);
        searchCard.getChildren().add(searchInputContainer);
        VBox.setVgrow(searchInputContainer, Priority.NEVER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Store reference to the VBox that holds entries
        historyEntriesListContainer = new VBox(10);
        historyEntriesListContainer.setPadding(new Insets(0));
        historyEntriesListContainer.setAlignment(Pos.TOP_LEFT);
        historyEntriesListContainer.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(historyEntriesListContainer, Priority.ALWAYS);
        scrollPane.setContent(historyEntriesListContainer);

        // Store reference to the FilteredList
        journalHistoryFilteredData = new FilteredList<>(journalEntries, p -> true);
        System.out.println("JournalAppUIFull: FilteredList 'journalHistoryFilteredData' initialized. Initial size: " + journalHistoryFilteredData.size());

        // Attach listener to search field
        historySearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("JournalAppUIFull: Search field changed. New value: '" + newValue + "'");
            String lowerCaseFilter = newValue.toLowerCase();
            journalHistoryFilteredData.setPredicate(entry -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                boolean matches = entry.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                                  entry.getContent().toLowerCase().contains(lowerCaseFilter) ||
                                  (entry.getTags() != null && entry.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerCaseFilter)));
                System.out.println("  Entry '" + entry.getTitle() + "' matches search: " + matches);
                return matches;
            });
            // Update the view immediately after filtering
            updateJournalEntriesView(historyEntriesListContainer, journalHistoryFilteredData);
        });


        // Initial call to populate view (will show "No entries" until data arrives from Firebase)
        updateJournalEntriesView(historyEntriesListContainer, journalHistoryFilteredData);
        System.out.println("JournalAppUIFull: Initial updateJournalEntriesView call for history tab finished.");

        content.getChildren().addAll(searchCard, scrollPane);
        return content;
    }

    private void updateJournalEntriesView(VBox container, ObservableList<JournalEntry> data) {
        System.out.println("JournalAppUIFull: updateJournalEntriesView called. Displaying data from FilteredList. Size: " + data.size());
        container.getChildren().clear();

        if (data.isEmpty()) {
            System.out.println("JournalAppUIFull: updateJournalEntriesView - Data is empty, showing 'No entries found' label.");
            Label noEntriesLabel = new Label("No journal entries found matching your search.");
            noEntriesLabel.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 14px;");
            container.setAlignment(Pos.CENTER);
            container.getChildren().add(noEntriesLabel);
            return;
        }
        System.out.println("JournalAppUIFull: updateJournalEntriesView - Populating " + data.size() + " journal entries into UI.");
        container.setAlignment(Pos.TOP_LEFT);

        for (JournalEntry entry : data) {
            // CRUCIAL LOGS: Check what data is actually in the Java object
            System.out.println("  Adding entry card for: '" + entry.getTitle() + "' (ID: " + entry.getId() + ")");
            System.out.println("    Raw createdAt: " + entry.getCreatedAt() + ", Converted LocalDateTime: " + entry.getTimestampAsLocalDateTime());
            System.out.println("    Formatted Date: " + entry.getFormattedDate());

            // Ensure the entry is valid before attempting to render (basic check)
            if (entry.getId() == null || entry.getTitle() == null || entry.getContent() == null) {
                System.err.println("JournalAppUIFull: Skipping invalid/incomplete entry for rendering (ID: " + entry.getId() + ", Title: " + entry.getTitle() + ")");
                continue;
            }


            VBox entryCard = new VBox(10);
            entryCard.setPadding(new Insets(15));
            entryCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-cursor: hand;");
            entryCard.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(entryCard, Priority.ALWAYS);

            HBox header = new HBox(15);
            header.setAlignment(Pos.CENTER_LEFT);

            Label moodIcon = new Label(entry.getMood() != null ? entry.getMood() : "❓"); // Default emoji if null
            moodIcon.setFont(new Font("Arial", 22));

            VBox titleAndDate = new VBox(2);
            Label entryTitle = new Label(entry.getTitle());
            entryTitle.setFont(new Font("Arial Bold", 15));
            entryTitle.setStyle(TEXT_COLOR_DARK_GREY);

            Label entryDate = new Label(entry.getFormattedDate());
            entryDate.setFont(new Font("Arial", 11));
            entryDate.setStyle(TEXT_COLOR_GREY);

            titleAndDate.getChildren().addAll(entryTitle, entryDate);
            header.getChildren().addAll(moodIcon, titleAndDate);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            FlowPane tagsPane = new FlowPane(5, 5);
            if (entry.getTags() != null) {
                for (String tag : entry.getTags()) {
                    Label tagBadge = new Label(tag);
                    tagBadge.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; " + TEXT_COLOR_DARK_GREY);
                    tagsPane.getChildren().add(tagBadge);
                }
            }

            Button deleteButton = new Button("✕");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #b0b0b0; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            deleteButton.setTooltip(new Tooltip("Delete this entry"));
            deleteButton.setOnAction(e -> confirmAndDeleteEntry(entry));

            header.getChildren().addAll(spacer, tagsPane, deleteButton);

            Label entryContent = new Label(entry.getContent());
            entryContent.setFont(new Font("Arial", 13));
            entryContent.setStyle(TEXT_COLOR_GREY);
            entryContent.setWrapText(true);
            entryContent.setPrefHeight(45);
            entryContent.setMinHeight(15);
            entryContent.setMaxHeight(70);
            entryContent.setEllipsisString("...");

            entryCard.getChildren().addAll(header, entryContent);

            if (entry.getAttachedImagePath() != null && !entry.getAttachedImagePath().isEmpty()) {
                try {
                    ImageView attachedImageView = createImageView(entry.getAttachedImagePath(), 100, 100);
                    attachedImageView.setPreserveRatio(true);
                    StackPane imageClipContainer = new StackPane(attachedImageView);
                    Circle clip = new Circle(50, 50, 50);
                    imageClipContainer.setClip(clip);
                    imageClipContainer.setPrefSize(100, 100);
                    imageClipContainer.setMaxSize(100, 100);
                    imageClipContainer.setMinSize(100, 100);

                    VBox.setMargin(imageClipContainer, new Insets(10, 0, 0, 0));
                    entryCard.getChildren().add(imageClipContainer);
                } catch (Exception ex) {
                    System.err.println("Could not load attached image for entry ID " + entry.getId() + ": " + ex.getMessage());
                }
            }

            container.getChildren().add(entryCard);
        }
    }

    private void confirmAndDeleteEntry(JournalEntry entry) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Journal Entry");
        alert.setHeaderText("Are you sure you want to delete this entry?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            firebaseExecutor.submit(() -> {
                try {
                    firebaseJournalService.deleteJournalEntry(entry.getId());
                    Platform.runLater(() -> {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Entry deleted successfully!", ButtonType.OK);
                        successAlert.setHeaderText(null);
                        successAlert.showAndWait();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to delete entry: " + ex.getMessage(), ButtonType.OK);
                        errorAlert.setHeaderText("Deletion Error");
                        errorAlert.showAndWait();
                    });
                    System.err.println("Error deleting journal entry from Firebase: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }
    }


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
                System.err.println("Also failed to load placeholder.png during exception: " + pe.getMessage());
            }
        }
        iconView.setFitWidth(fitWidth);
        iconView.setFitHeight(fitHeight);
        iconView.setPreserveRatio(true);
        return iconView;
    }

    private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
        region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
        region.setOnMouseExited(e -> region.setStyle(normalStyle));
    }

    private Button createButton(String imagePath, String text) {
        ImageView imageView = null;
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView = new ImageView(image);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
        } catch (NullPointerException e) {
            System.err.println("Error: Image not found at " + imagePath);
        }

        Button button = new Button(text, imageView);
        button.setContentDisplay(ContentDisplay.LEFT);
        return button;
    }

    private Button createIconButton(String imagePath, String text) {
        ImageView imageView = null;
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView = new ImageView(image);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
        } catch (NullPointerException e) {
            System.err.println("Error: Image not found at " + imagePath);
        }

        Button button = new Button(text, imageView);
        button.setContentDisplay(ContentDisplay.LEFT);
        return button;
    }
}