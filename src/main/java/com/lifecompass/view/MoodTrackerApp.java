
// package com.lifecompass.view; // Corrected package name
// import javafx.application.Platform;
// import javafx.beans.property.DoubleProperty;
// import javafx.beans.property.SimpleDoubleProperty;
// import javafx.beans.property.SimpleIntegerProperty;
// import javafx.beans.value.ObservableValue;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.geometry.Insets;

// import javafx.geometry.Pos;
// import javafx.scene.Scene; // Only if MoodTrackerApp spawns its own internal Stage/Scene (e.g. for sub-popups)
// import javafx.scene.control.*;
// import javafx.scene.image.Image; // Added for createImageView
// import javafx.scene.image.ImageView; // Added for createImageView
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle; // Retained from original top bar if needed by MoodTrackerApp itself
// import javafx.scene.shape.SVGPath; // Retained from original top bar if needed by MoodTrackerApp itself
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight; // For FontWeight in dashboard styles
// import javafx.stage.Stage; // Only if MoodTrackerApp spawns its own internal Stage

// import java.net.URL; // Added for createImageView
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors; // Added for stream operations

// // Firebase and DAO imports
// import com.lifecompass.controller.AuthController;
// import com.lifecompass.dao.impl.MoodDaoFirestoreImpl;
// import com.lifecompass.model.MoodEntry; // Import the MoodEntry model
// import com.lifecompass.util.SceneManager; // For alerts


// // Refactored: No longer extends Application
// public class MoodTrackerApp {

//     // --- Styling Constants ---
//     public static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
//     public static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
//     public static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
//     public static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
//     public static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
//     public static final String COLOR_RED_ACCENT = "#ef4444";
//     public static final String COLOR_BLUE_ACCENT = "#3b82f6";
//     public static final String TAG_STYLE_OUTLINE = "-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 12px; -fx-font-size: 14px; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;";
//     public static final String TAG_STYLE_SELECTED = "-fx-background-color: #e0f2fe; -fx-border-color: #93c5fd; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 12px; -fx-font-size: 14px; " + TEXT_COLOR_DARK_GREY + ";-fx-font-weight: bold;-fx-cursor: hand;";
//     public static final String BADGE_SECONDARY_STYLE = "-fx-background-color: #e6e6e6; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; " + TEXT_COLOR_DARK_GREY + ";";

//     // --- Mood Data ---
//     static class MoodEmoji {
//         String emoji;
//         String label;
//         int value;

//         public MoodEmoji(String emoji, String label, int value) {
//             this.emoji = emoji;
//             this.label = label;
//             this.value = value;
//         }
//     }

//     static public List<MoodEmoji> MOOD_EMOJIS_DATA = Arrays.asList(
//             new MoodEmoji("🤩", "Ecstatic", 1),
//             new MoodEmoji("😄", "Delighted", 2),
//             new MoodEmoji("😊", "Happy", 3),
//             new MoodEmoji("🙂", "Good", 4),
//             new MoodEmoji("😌", "Relaxed", 5),
//             new MoodEmoji("😐", "Neutral", 6),
//             new MoodEmoji("😟", "Worried", 7),
//             new MoodEmoji("😔", "Sad", 8),
//             new MoodEmoji("😢", "Distraught", 9),
//             new MoodEmoji("😠", "Angry", 10),
//             new MoodEmoji("😤", "Frustrated", 11),
//             new MoodEmoji("😖", "Anxious", 12),
//             new MoodEmoji("😴", "Tired", 13),
//             new MoodEmoji("😮", "Surprised", 14),
//             new MoodEmoji("🤔", "Confused", 15),
//             new MoodEmoji("😷", "Sick", 16)
//     );

//     private static final List<String> MOOD_TAGS_DATA = Arrays.asList(
//             "Anxious", "Calm", "Excited", "Tired", "Focused", "Stressed",
//             "Grateful", "Lonely", "Confident", "Overwhelmed", "Peaceful", "Energetic",
//             "Relaxed", "Inspired", "Bored", "Angry", "Frustrated", "Hopeful"
//     );

//     // Selected state properties (class-level fields)
//     private SimpleIntegerProperty selectedMoodValue = new SimpleIntegerProperty(0);
//     private DoubleProperty moodIntensityValue = new SimpleDoubleProperty(5.0);
//     private ObservableList<String> selectedTags = FXCollections.observableArrayList();
//     private TextArea notesTextArea;

//     // To hold references to the mood emoji buttons for easy styling updates
//     private List<VBox> moodEmojiButtons = new ArrayList<>();

//     // Recent Mood Entries Data Model (Now uses com.lifecompass.model.MoodEntry)
//     public ObservableList<MoodEntry> recentMoodEntries = FXCollections.observableArrayList();

//     // DAO for Firebase operations
//     private final MoodDaoFirestoreImpl moodDao = new MoodDaoFirestoreImpl();

//     // --- References from Dashboard ---
//     private Stage primaryStage; // The main dashboard stage
//     private UserDashboardScreen dashboardInstance; // The dashboard instance (corrected type)

//     // Constructor to receive dashboard context if needed for actions like opening pop-ups from Mood screen
//     public MoodTrackerApp(Stage primaryStage, UserDashboardScreen dashboardInstance) { // Corrected type
//         this.primaryStage = primaryStage;
//         this.dashboardInstance = dashboardInstance;
//         loadRecentMoodEntries(); // Load moods from Firebase when initialized
//     }

//     /**
//      * Creates and returns the entire UI content for the Mood Tracker screen.
//      * This method is designed to be called by the `UserDashboardScreen` class.
//      * @return A VBox containing all UI elements for the Mood Tracker.
//      */
//     public VBox createMoodScreenContent() {
//         VBox moodTrackerContent = new VBox(20);
//         moodTrackerContent.setPadding(new Insets(20));
//         moodTrackerContent.setAlignment(Pos.TOP_CENTER);
//         moodTrackerContent.setStyle(BACKGROUND_COLOR_LIGHT_GREY); // Match dashboard background

//         // "How are you feeling today?" Card
//         VBox moodSelectionCard = createMoodSelectionCard();
//         VBox.setVgrow(moodSelectionCard, Priority.ALWAYS);

//         // "Recent Mood Entries" Card
//         VBox recentEntriesCard = createRecentEntriesCard();
//         VBox.setVgrow(recentEntriesCard, Priority.ALWAYS);

//         moodTrackerContent.getChildren().addAll(moodSelectionCard, recentEntriesCard);

//         return moodTrackerContent;
//     }

//     /**
//      * Loads recent mood entries for the current user from Firestore.
//      */
//     private void loadRecentMoodEntries() {
//         String currentUserId = AuthController.loggedInUserId;
//         if (currentUserId == null) {
//             System.err.println("MoodTrackerApp: No logged-in user to fetch mood entries for.");
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "Please log in to track your mood.");
//             recentMoodEntries.clear(); // Clear to show empty state if no user
//             return;
//         }

//         List<MoodEntry> fetchedEntries = moodDao.getMoodEntriesByUserId(currentUserId);
//         Platform.runLater(() -> {
//             recentMoodEntries.setAll(fetchedEntries);
//             System.out.println("Loaded " + fetchedEntries.size() + " mood entries for user: " + currentUserId);
//         });
//     }


//     // --- Creates the main mood selection card ---
//     private VBox createMoodSelectionCard() {
//         VBox card = new VBox(10);
//         card.setPadding(new Insets(20));
//         card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
//         card.setMaxWidth(Double.MAX_VALUE);
//         HBox.setHgrow(card, Priority.ALWAYS);

//         // Card Header
//         Label title = new Label("How are you feeling today?");
//         title.setFont(new Font("Arial Bold", 18));
//         title.setStyle(TEXT_COLOR_DARK_GREY);

//         Label description = new Label("Select your current mood and add any additional details");
//         description.setFont(new Font("Arial", 12));
//         description.setStyle(TEXT_COLOR_GREY);
//         description.setWrapText(true);

//         // Mood Selection Grid
//         Label chooseMoodLabel = new Label("Choose your mood");
//         chooseMoodLabel.setFont(new Font("Arial Bold", 14));
//         chooseMoodLabel.setStyle(TEXT_COLOR_DARK_GREY);

//         GridPane moodSelectionGrid = new GridPane();
//         moodSelectionGrid.setHgap(15);
//         moodSelectionGrid.setVgap(15);
//         moodSelectionGrid.setPadding(new Insets(10, 0, 10, 0));

//         // Column Constraints for a 5-column layout
//         for (int i = 0; i < 5; i++) {
//             ColumnConstraints moodCol = new ColumnConstraints();
//             moodCol.setHgrow(Priority.ALWAYS);
//             moodCol.setPercentWidth(100.0 / 5);
//             moodSelectionGrid.getColumnConstraints().add(moodCol);
//         }

//         for (int i = 0; i < MOOD_EMOJIS_DATA.size(); i++) {
//             MoodEmoji moodData = MOOD_EMOJIS_DATA.get(i);

//             VBox moodButtonWrapper = new VBox(2);
//             moodButtonWrapper.setAlignment(Pos.CENTER);
//             moodButtonWrapper.setPadding(new Insets(10));
//             moodButtonWrapper.setPrefSize(80, 80);
//             moodButtonWrapper.setStyle(TAG_STYLE_OUTLINE);

//             Label emojiLabel = new Label(moodData.emoji);
//             emojiLabel.setFont(new Font("Segoe UI Emoji", 30));

//             Label labelLabel = new Label(moodData.label);
//             labelLabel.setFont(new Font("Arial", 10));
//             labelLabel.setStyle(TEXT_COLOR_GREY);
//             labelLabel.setWrapText(true);

//             moodButtonWrapper.getChildren().addAll(emojiLabel, labelLabel);
//             moodEmojiButtons.add(moodButtonWrapper);

//             final int currentMoodValue = moodData.value;
//             moodButtonWrapper.setOnMouseClicked(e -> {
//                 selectedMoodValue.set(currentMoodValue);
//                 for (VBox otherButtonWrapper : moodEmojiButtons) {
//                     otherButtonWrapper.setStyle(TAG_STYLE_OUTLINE);
//                     if (otherButtonWrapper.getChildren().size() > 1 && otherButtonWrapper.getChildren().get(1) instanceof Label) {
//                         ((Label)otherButtonWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_GREY);
//                     }
//                 }
//                 moodButtonWrapper.setStyle(TAG_STYLE_SELECTED);
//                 if (moodButtonWrapper.getChildren().size() > 1 && moodButtonWrapper.getChildren().get(1) instanceof Label) {
//                     ((Label)moodButtonWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_DARK_GREY + "-fx-font-weight: bold;");
//                 }
//                 System.out.println("Selected mood: " + moodData.label + " (Value: " + selectedMoodValue.get() + ")");
//             });

//             GridPane.setConstraints(moodButtonWrapper, i % 5, i / 5);
//             moodSelectionGrid.getChildren().add(moodButtonWrapper);
//         }

//         // Mood Intensity Slider
//         Label intensityLabel = new Label("Mood Intensity: " + (int)moodIntensityValue.get() + "/10");
//         intensityLabel.setFont(new Font("Arial Bold", 14));
//         intensityLabel.setStyle(TEXT_COLOR_DARK_GREY);

//         Slider intensitySlider = new Slider(1, 10, moodIntensityValue.get());
//         intensitySlider.setShowTickLabels(true);
//         intensitySlider.setShowTickMarks(true);
//         intensitySlider.setMajorTickUnit(1);
//         intensitySlider.setMinorTickCount(0);
//         intensitySlider.setSnapToTicks(true);
//         intensitySlider.setMaxWidth(Double.MAX_VALUE);
//         intensitySlider.valueProperty().bindBidirectional(moodIntensityValue);
//         intensitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//             intensityLabel.setText("Mood Intensity: " + (int)newVal.doubleValue() + "/10");
//         });

//         HBox intensityLabels = new HBox();
//         intensityLabels.setAlignment(Pos.CENTER);
//         intensityLabels.setMaxWidth(Double.MAX_VALUE);
//         Label lowLabel = new Label("Low");
//         lowLabel.setFont(new Font("Arial", 10));
//         lowLabel.setStyle(TEXT_COLOR_GREY);
//         Label highLabel = new Label("High");
//         highLabel.setFont(new Font("Arial", 10));
//         highLabel.setStyle(TEXT_COLOR_GREY);
//         Region intensitySpacer = new Region();
//         HBox.setHgrow(intensitySpacer, Priority.ALWAYS);
//         intensityLabels.getChildren().addAll(lowLabel, intensitySpacer, highLabel);

//         // Mood Tags
//         Label tagsLabel = new Label("What else describes how you feel? (Optional)");
//         tagsLabel.setFont(new Font("Arial Bold", 14));
//         tagsLabel.setStyle(TEXT_COLOR_DARK_GREY);

//         FlowPane moodTagsPane = new FlowPane(8, 8);
//         for (String tag : MOOD_TAGS_DATA) {
//             Label tagButton = new Label(tag);
//             tagButton.setStyle(TAG_STYLE_OUTLINE);
//             tagButton.setOnMouseClicked(e -> {
//                 if (selectedTags.contains(tag)) {
//                     selectedTags.remove(tag);
//                     tagButton.setStyle(TAG_STYLE_OUTLINE);
//                 } else {
//                     selectedTags.add(tag);
//                     tagButton.setStyle(TAG_STYLE_SELECTED);
//                 }
//                 System.out.println("Selected Mood Tags: " + selectedTags);
//             });
//             moodTagsPane.getChildren().add(tagButton);
//         }

//         // Notes TextArea
//         Label notesHeaderLabel = new Label("Additional notes (Optional)");
//         notesHeaderLabel.setFont(new Font("Arial Bold", 14));
//         notesHeaderLabel.setStyle(TEXT_COLOR_DARK_GREY);

//         notesTextArea = new TextArea();
//         notesTextArea.setPromptText("What's on your mind? Any specific thoughts or events affecting your mood?");
//         notesTextArea.setPrefRowCount(4);
//         notesTextArea.setWrapText(true);
//         notesTextArea.setStyle("-fx-control-inner-background: white; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px;");

//         // Save Mood Button
//         Button saveMoodEntryButton = new Button("Save Mood Entry");
//         saveMoodEntryButton.setMaxWidth(Double.MAX_VALUE);
//         saveMoodEntryButton.setStyle("-fx-background-color: " + COLOR_BLUE_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10px 0; -fx-font-size: 16px; -fx-cursor: hand;");
//         saveMoodEntryButton.disableProperty().bind(selectedMoodValue.isEqualTo(0));
//         saveMoodEntryButton.setOnAction(e -> handleSaveMood());

//         card.getChildren().addAll(
//                 title, description,
//                 chooseMoodLabel, moodSelectionGrid,
//                 intensityLabel, intensitySlider, intensityLabels,
//                 tagsLabel, moodTagsPane,
//                 notesHeaderLabel, notesTextArea,
//                 saveMoodEntryButton
//         );
//         return card;
//     }

//     // --- Creates the "Recent Mood Entries" card ---
//     public VBox createRecentEntriesCard() {
//         VBox card = new VBox(10);
//         card.setPadding(new Insets(20));
//         card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
//         card.setMaxWidth(Double.MAX_VALUE);
//         HBox.setHgrow(card, Priority.ALWAYS);

//         Label title = new Label("Recent Mood Entries");
//         title.setFont(new Font("Arial Bold", 18));
//         title.setStyle(TEXT_COLOR_DARK_GREY);

//         Label description = new Label("Your mood tracking history");
//         description.setFont(new Font("Arial", 12));
//         description.setStyle(TEXT_COLOR_GREY);

//         VBox entriesList = new VBox(10);
//         // Listener now observes com.lifecompass.model.MoodEntry
//         recentMoodEntries.addListener((javafx.collections.ListChangeListener.Change<? extends MoodEntry> change) -> {
//             Platform.runLater(() -> repopulateRecentEntries(entriesList));
//         });
//         repopulateRecentEntries(entriesList); // Initial population when the card is created

//         card.getChildren().addAll(title, description, entriesList);
//         return card;
//     }

//     public void repopulateRecentEntries(VBox entriesList) {
//         entriesList.getChildren().clear(); // Clear existing entries before repopulating
//         if (recentMoodEntries.isEmpty()) {
//             Label noEntriesLabel = new Label("No recent mood entries. Log your mood!");
//             noEntriesLabel.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 14px;");
//             entriesList.getChildren().add(noEntriesLabel);
//             return;
//         }

//         // Iterate over com.lifecompass.model.MoodEntry objects
//         for (MoodEntry entry : recentMoodEntries) {
//             HBox entryRow = new HBox(15);
//             entryRow.setAlignment(Pos.CENTER_LEFT);
//             entryRow.setPadding(new Insets(10));
//             entryRow.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 8px;");

//             Label moodEmojiLabel = new Label(entry.getMoodEmoji()); // Get emoji from MoodEntry model
//             moodEmojiLabel.setFont(new Font("Segoe UI Emoji", 24));

//             VBox textContent = new VBox(2);
//             Label dateLabel = new Label(entry.getFormattedDate()); // Get formatted date from MoodEntry model
//             dateLabel.setFont(new Font("Arial Bold", 13));
//             dateLabel.setStyle(TEXT_COLOR_DARK_GREY);

//             FlowPane tagsRow = new FlowPane(5, 5); // Use FlowPane for tags for better layout
//             if (entry.getTags() != null) {
//                 for (String tag : entry.getTags()) { // Iterate over tags from MoodEntry model
//                     Label tagBadge = new Label(tag);
//                     tagBadge.setStyle(BADGE_SECONDARY_STYLE);
//                     tagsRow.getChildren().add(tagBadge);
//                 }
//             }
//             textContent.getChildren().addAll(dateLabel, tagsRow);

//             Region spacer = new Region();
//             HBox.setHgrow(spacer, Priority.ALWAYS);

//             Label intensityBadge = new Label(entry.getIntensity() + "/10"); // Get intensity from MoodEntry model
//             intensityBadge.setStyle(TAG_STYLE_OUTLINE);

//             entryRow.getChildren().addAll(moodEmojiLabel, textContent, spacer, intensityBadge);
//             entriesList.getChildren().add(entryRow);
//         }
//     }

//     // --- Mood Tracker Logic ---
//     private void handleSaveMood() {
//         String currentUserId = AuthController.loggedInUserId;
//         if (currentUserId == null) {
//             SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "Please log in to save your mood.");
//             return;
//         }

//         if (selectedMoodValue.get() == 0) {
//             SceneManager.showAlert(Alert.AlertType.WARNING, "Input Error", "Please select your mood (emoji) before saving.");
//             return;
//         }

//         String moodEmoji = "";
//         for (MoodEmoji data : MOOD_EMOJIS_DATA) {
//             if (data.value == selectedMoodValue.get()) {
//                 moodEmoji = data.emoji;
//                 break;
//             }
//         }
//         String notesContent = notesTextArea.getText().trim();
//         List<String> currentTags = new ArrayList<>(selectedTags);

//         // Create a new MoodEntry model object
//         MoodEntry newEntry = new MoodEntry(
//                 null, // ID will be auto-generated by Firestore
//                 currentUserId,
//                 moodEmoji,
//                 (int) moodIntensityValue.get(),
//                 currentTags,
//                 notesContent,
//                 LocalDateTime.now()
//         );

//         // Save to Firestore using the DAO
//         moodDao.addMoodEntry(newEntry);

//         System.out.println("Mood Logged to Firebase: Mood=" + newEntry.getMoodEmoji() + ", Intensity=" + newEntry.getIntensity() +
//                            ", Tags=" + newEntry.getTags() + ", Notes='" + newEntry.getNotes() + "'");

//         SceneManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Mood logged successfully!");

//         resetMoodTrackerForm();
//         loadRecentMoodEntries(); // Reload recent entries to show the newly added one
//     }

//     private void resetMoodTrackerForm() {
//         selectedMoodValue.set(0);
//         moodIntensityValue.set(5.0);
//         selectedTags.clear();
//         notesTextArea.clear();

//         for (VBox btnWrapper : moodEmojiButtons) {
//             btnWrapper.setStyle(TAG_STYLE_OUTLINE);
//             if (btnWrapper.getChildren().size() > 1 && btnWrapper.getChildren().get(1) instanceof Label) {
//                 ((Label)btnWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_GREY);
//             }
//         }
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // General Helper Methods (Copied from UserDashboardScreen.java for this component)
//     //------------------------------------------------------------------------------------------------------------------

//     /**
//      * Helper to create an ImageView with error handling for missing image resources.
//      * @param imagePath Path to the image resource (e.g., "/assets/images/my_icon.png").
//      * @param fitWidth Desired width of the image.
//      * @param fitHeight Desired height of the image.
//      * @return Configured ImageView.
//      */
//     private ImageView createImageView(String imagePath, int fitWidth, int fitHeight) {
//         ImageView iconView = new ImageView();
//         try {
//             URL imageUrl = getClass().getResource(imagePath);
//             if (imageUrl == null) {
//                 System.err.println("Failed to load image: " + imagePath);
//                 URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
//                 if (fallbackUrl != null) {
//                     iconView.setImage(new Image(fallbackUrl.toExternalForm()));
//                 } else {
//                     System.err.println("Also failed to load placeholder.png: Input stream must not be null for placeholder!");
//                 }
//             } else {
//                 iconView.setImage(new Image(imageUrl.toExternalForm()));
//             }
//         } catch (Exception e) {
//             System.err.println("Exception while loading image: " + imagePath + " - " + e.getMessage());
//             try {
//                 URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
//                 if (fallbackUrl != null) {
//                     iconView.setImage(new Image(fallbackUrl.toExternalForm()));
//                 }
//             } catch (Exception pe) {
//                 System.err.println("Also failed to load placeholder.png: " + pe.getMessage());
//             }
//         }
//         iconView.setFitWidth(fitWidth);
//         iconView.setFitHeight(fitHeight);
//         iconView.setPreserveRatio(true);
//         return iconView;
//     }

//     /**
//      * Helper method to apply generic hover styles to any Region (like HBox, VBox, Button etc.).
//      * @param region The JavaFX Region to apply styles to.
//      * @param hoverStyle CSS string for the hover state.
//      * @param normalStyle CSS string for the normal (non-hover) state.
//      */
//     private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
//         region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
//         region.setOnMouseExited(e -> region.setStyle(normalStyle));
//     }

//     public ObservableValue<Number> getRecentMoodEntries() {

//         throw new UnsupportedOperationException("Unimplemented method 'getRecentMoodEntries'");

//     }
// }


package com.lifecompass.view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.lifecompass.controller.AuthController;
import com.lifecompass.dao.impl.MoodDaoFirestoreImpl;
import com.lifecompass.model.MoodEntry;
import com.lifecompass.util.SceneManager;


public class MoodTrackerApp {

    // --- Styling Constants ---
    public static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
    public static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
    public static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
    public static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
    public static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
    public static final String COLOR_RED_ACCENT = "#ef4444";
    public static final String COLOR_BLUE_ACCENT = "#3b82f6";
    public static final String TAG_STYLE_OUTLINE = "-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 12px; -fx-font-size: 14px; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;";
    public static final String TAG_STYLE_SELECTED = "-fx-background-color: #e0f2fe; -fx-border-color: #93c5fd; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 12px; -fx-font-size: 14px; " + TEXT_COLOR_DARK_GREY + ";-fx-font-weight: bold;-fx-cursor: hand;";
    public static final String BADGE_SECONDARY_STYLE = "-fx-background-color: #e6e6e6; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; " + TEXT_COLOR_DARK_GREY + ";";

    // --- Mood Data ---
    public static class MoodEmoji {
        private String emoji;
        private String label;
        private int value;

        public MoodEmoji(String emoji, String label, int value) {
            this.emoji = emoji;
            this.label = label;
            this.value = value;
        }

        public String getEmoji() { return emoji; }
        public String getLabel() { return label; }
        public int getValue() { return value; }
    }

    public static final List<MoodEmoji> MOOD_EMOJIS_DATA = Collections.unmodifiableList(
        Arrays.asList(
            new MoodEmoji("🤩", "Ecstatic", 1),
            new MoodEmoji("😄", "Delighted", 2),
            new MoodEmoji("😊", "Happy", 3),
            new MoodEmoji("🙂", "Good", 4),
            new MoodEmoji("😌", "Relaxed", 5),
            new MoodEmoji("😐", "Neutral", 6),
            new MoodEmoji("😟", "Worried", 7),
            new MoodEmoji("😔", "Sad", 8),
            new MoodEmoji("😢", "Distraught", 9),
            new MoodEmoji("😠", "Angry", 10),
            new MoodEmoji("😤", "Frustrated", 11),
            new MoodEmoji("😖", "Anxious", 12),
            new MoodEmoji("😴", "Tired", 13),
            new MoodEmoji("😮", "Surprised", 14),
            new MoodEmoji("🤔", "Confused", 15),
            new MoodEmoji("😷", "Sick", 16)
        )
    );

    private static final List<String> MOOD_TAGS_DATA = Arrays.asList(
            "Anxious", "Calm", "Excited", "Tired", "Focused", "Stressed",
            "Grateful", "Lonely", "Confident", "Overwhelmed", "Peaceful", "Energetic",
            "Relaxed", "Inspired", "Bored", "Angry", "Frustrated", "Hopeful"
    );

    // Selected state properties (class-level fields)
    private SimpleIntegerProperty selectedMoodValue = new SimpleIntegerProperty(0);
    private DoubleProperty moodIntensityValue = new SimpleDoubleProperty(5.0);
    private DoubleProperty energyLevelValue = new SimpleDoubleProperty(5.0); // NEW: Energy Level property
    private ObservableList<String> selectedTags = FXCollections.observableArrayList();
    private TextArea notesTextArea;

    // To hold references to the mood emoji buttons for easy styling updates
    private List<VBox> moodEmojiButtons = new ArrayList<>();

    // Recent Mood Entries Data Model
    public ObservableList<MoodEntry> recentMoodEntries = FXCollections.observableArrayList();

    // DAO for Firebase operations
    private final MoodDaoFirestoreImpl moodDao = new MoodDaoFirestoreImpl();

    // --- References from Dashboard ---
    private Stage primaryStage;
    private UserDashboardScreen dashboardInstance;

    // Callback for when mood is saved, to refresh analytics
    private Runnable onMoodSavedCallback;

    public MoodTrackerApp(Stage primaryStage, UserDashboardScreen dashboardInstance) {
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;
        loadRecentMoodEntries();
    }

    public void setOnMoodSavedCallback(Runnable callback) {
        this.onMoodSavedCallback = callback;
    }

    /**
     * Creates and returns the entire UI content for the Mood Tracker screen.
     * @return A VBox containing all UI elements for the Mood Tracker.
     */
    public VBox createMoodScreenContent() {
        VBox moodTrackerContent = new VBox(20);
        moodTrackerContent.setPadding(new Insets(20));
        moodTrackerContent.setAlignment(Pos.TOP_CENTER);
        moodTrackerContent.setStyle(BACKGROUND_COLOR_LIGHT_GREY);

        VBox moodSelectionCard = createMoodSelectionCard();
        VBox.setVgrow(moodSelectionCard, Priority.ALWAYS);

        VBox recentEntriesCard = createRecentEntriesCard(); // This is the MoodTrackerApp's own recent entries card
        VBox.setVgrow(recentEntriesCard, Priority.ALWAYS);

        moodTrackerContent.getChildren().addAll(moodSelectionCard, recentEntriesCard);

        return moodTrackerContent;
    }

    /**
     * NEW: Creates and returns only the "Recent Mood Entries" card, for use in other screens like Dashboard.
     * @return A VBox containing the UI for recent mood entries.
     */
    public VBox createRecentEntriesCardForDashboard() {
        return createRecentEntriesCard(); // Reuse the existing method
    }


    /**
     * Loads recent mood entries for the current user from Firestore.
     */
    public void loadRecentMoodEntries() {
        String currentUserId = AuthController.loggedInUserId;
        if (currentUserId == null) {
            System.err.println("MoodTrackerApp: No logged-in user to fetch mood entries for.");
            Platform.runLater(() -> SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "Please log in to track your mood."));
            recentMoodEntries.clear();
            return;
        }

        List<MoodEntry> fetchedEntries = moodDao.getMoodEntriesByUserId(currentUserId);
        Platform.runLater(() -> {
            recentMoodEntries.setAll(fetchedEntries);
            System.out.println("Loaded " + fetchedEntries.size() + " mood entries for user: " + currentUserId);
        });
    }

    private VBox createMoodSelectionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label title = new Label("How are you feeling today?");
        title.setFont(new Font("Arial Bold", 18));
        title.setStyle(TEXT_COLOR_DARK_GREY);

        Label description = new Label("Select your current mood and add any additional details");
        description.setFont(new Font("Arial", 12));
        description.setStyle(TEXT_COLOR_GREY);
        description.setWrapText(true);

        Label chooseMoodLabel = new Label("Choose your mood");
        chooseMoodLabel.setFont(new Font("Arial Bold", 14));
        chooseMoodLabel.setStyle(TEXT_COLOR_DARK_GREY);

        GridPane moodSelectionGrid = new GridPane();
        moodSelectionGrid.setHgap(15);
        moodSelectionGrid.setVgap(15);
        moodSelectionGrid.setPadding(new Insets(10, 0, 10, 0));

        for (int i = 0; i < 5; i++) {
            ColumnConstraints moodCol = new ColumnConstraints();
            moodCol.setHgrow(Priority.ALWAYS);
            moodCol.setPercentWidth(100.0 / 5);
            moodSelectionGrid.getColumnConstraints().add(moodCol);
        }

        for (int i = 0; i < MOOD_EMOJIS_DATA.size(); i++) {
            MoodEmoji moodData = MOOD_EMOJIS_DATA.get(i);

            VBox moodButtonWrapper = new VBox(2);
            moodButtonWrapper.setAlignment(Pos.CENTER);
            moodButtonWrapper.setPadding(new Insets(10));
            moodButtonWrapper.setPrefSize(80, 80);
            moodButtonWrapper.setStyle(TAG_STYLE_OUTLINE);

            Label emojiLabel = new Label(moodData.getEmoji());
            emojiLabel.setFont(new Font("Segoe UI Emoji", 30));

            Label labelLabel = new Label(moodData.getLabel());
            labelLabel.setFont(new Font("Arial", 10));
            labelLabel.setStyle(TEXT_COLOR_GREY);
            labelLabel.setWrapText(true);

            moodButtonWrapper.getChildren().addAll(emojiLabel, labelLabel);
            moodEmojiButtons.add(moodButtonWrapper);

            final int currentMoodValue = moodData.getValue();
            moodButtonWrapper.setOnMouseClicked(e -> {
                selectedMoodValue.set(currentMoodValue);
                for (VBox otherWrapper : moodEmojiButtons) {
                    otherWrapper.setStyle(TAG_STYLE_OUTLINE);
                    if (otherWrapper.getChildren().size() > 1 && otherWrapper.getChildren().get(1) instanceof Label) {
                        ((Label)otherWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_GREY);
                    }
                }
                moodButtonWrapper.setStyle(TAG_STYLE_SELECTED);
                if (moodButtonWrapper.getChildren().size() > 1 && moodButtonWrapper.getChildren().get(1) instanceof Label) {
                    ((Label)moodButtonWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_DARK_GREY + "-fx-font-weight: bold;");
                }
                System.out.println("Selected mood: " + moodData.getLabel() + " (Value: " + selectedMoodValue.get() + ")");
            });

            GridPane.setConstraints(moodButtonWrapper, i % 5, i / 5);
            moodSelectionGrid.getChildren().add(moodButtonWrapper);
        }

        // Mood Intensity Slider
        Label intensityLabel = new Label("Mood Intensity: " + (int)moodIntensityValue.get() + "/10");
        intensityLabel.setFont(new Font("Arial Bold", 14));
        intensityLabel.setStyle(TEXT_COLOR_DARK_GREY);

        Slider intensitySlider = new Slider(1, 10, moodIntensityValue.get());
        intensitySlider.setShowTickLabels(true);
        intensitySlider.setShowTickMarks(true);
        intensitySlider.setMajorTickUnit(1);
        intensitySlider.setMinorTickCount(0);
        intensitySlider.setSnapToTicks(true);
        intensitySlider.setMaxWidth(Double.MAX_VALUE);
        intensitySlider.valueProperty().bindBidirectional(moodIntensityValue);
        intensitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            intensityLabel.setText("Mood Intensity: " + (int)newVal.doubleValue() + "/10");
        });

        // NEW: Energy Level Slider
        Label energyLabel = new Label("Energy Level: " + (int)energyLevelValue.get() + "/10");
        energyLabel.setFont(new Font("Arial Bold", 14));
        energyLabel.setStyle(TEXT_COLOR_DARK_GREY);

        Slider energySlider = new Slider(1, 10, energyLevelValue.get());
        energySlider.setShowTickLabels(true);
        energySlider.setShowTickMarks(true);
        energySlider.setMajorTickUnit(1);
        energySlider.setMinorTickCount(0);
        energySlider.setSnapToTicks(true);
        energySlider.setMaxWidth(Double.MAX_VALUE);
        energySlider.valueProperty().bindBidirectional(energyLevelValue);
        energySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            energyLabel.setText("Energy Level: " + (int)newVal.doubleValue() + "/10");
        });

        HBox intensityLabels = new HBox();
        intensityLabels.setAlignment(Pos.CENTER);
        intensityLabels.setMaxWidth(Double.MAX_VALUE);
        Label lowLabel = new Label("Low");
        lowLabel.setFont(new Font("Arial", 10));
        lowLabel.setStyle(TEXT_COLOR_GREY);
        Label highLabel = new Label("High");
        highLabel.setFont(new Font("Arial", 10));
        highLabel.setStyle(TEXT_COLOR_GREY);
        Region intensitySpacer = new Region();
        HBox.setHgrow(intensitySpacer, Priority.ALWAYS);
        intensityLabels.getChildren().addAll(lowLabel, intensitySpacer, highLabel);


        HBox energyLabels = new HBox();
        energyLabels.setAlignment(Pos.CENTER);
        energyLabels.setMaxWidth(Double.MAX_VALUE);
        Label lowEnergyLabel = new Label("Low");
        lowEnergyLabel.setFont(new Font("Arial", 10));
        lowEnergyLabel.setStyle(TEXT_COLOR_GREY);
        Label highEnergyLabel = new Label("High");
        highEnergyLabel.setFont(new Font("Arial", 10));
        highEnergyLabel.setStyle(TEXT_COLOR_GREY);
        Region energySpacer = new Region();
        HBox.setHgrow(energySpacer, Priority.ALWAYS);
        energyLabels.getChildren().addAll(lowEnergyLabel, energySpacer, highEnergyLabel);


        // Mood Tags
        Label tagsLabel = new Label("What else describes how you feel? (Optional)");
        tagsLabel.setFont(new Font("Arial Bold", 14));
        tagsLabel.setStyle(TEXT_COLOR_DARK_GREY);

        FlowPane moodTagsPane = new FlowPane(8, 8);
        for (String tag : MOOD_TAGS_DATA) {
            Label tagButton = new Label(tag);
            tagButton.setStyle(TAG_STYLE_OUTLINE);
            tagButton.setOnMouseClicked(e -> {
                if (selectedTags.contains(tag)) {
                    selectedTags.remove(tag);
                    tagButton.setStyle(TAG_STYLE_OUTLINE);
                } else {
                    selectedTags.add(tag);
                    tagButton.setStyle(TAG_STYLE_SELECTED);
                }
                System.out.println("Selected Mood Tags: " + selectedTags);
            });
            moodTagsPane.getChildren().add(tagButton);
        }

        // Notes TextArea
        Label notesHeaderLabel = new Label("Additional notes (Optional)");
        notesHeaderLabel.setFont(new Font("Arial Bold", 14));
        notesHeaderLabel.setStyle(TEXT_COLOR_DARK_GREY);

        notesTextArea = new TextArea();
        notesTextArea.setPromptText("What's on your mind? Any specific thoughts or events affecting your mood?");
        notesTextArea.setPrefRowCount(4);
        notesTextArea.setWrapText(true);
        notesTextArea.setStyle("-fx-control-inner-background: white; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-padding: 8px;");

        // Save Mood Button
        Button saveMoodEntryButton = new Button("Save Mood Entry");
        saveMoodEntryButton.setMaxWidth(Double.MAX_VALUE);
        saveMoodEntryButton.setStyle("-fx-background-color: " + COLOR_BLUE_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10px 0; -fx-font-size: 16px; -fx-cursor: hand;");
        saveMoodEntryButton.disableProperty().bind(selectedMoodValue.isEqualTo(0));
        saveMoodEntryButton.setOnAction(e -> handleSaveMood());

        card.getChildren().addAll(
                title, description,
                chooseMoodLabel, moodSelectionGrid,
                intensityLabel, intensitySlider, intensityLabels,
                energyLabel, energySlider, energyLabels, // NEW: Add energy level UI
                tagsLabel, moodTagsPane,
                notesHeaderLabel, notesTextArea,
                saveMoodEntryButton
        );
        return card;
    }

    // --- Creates the "Recent Mood Entries" card (for Mood tab) ---
    private VBox createRecentEntriesCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label title = new Label("Recent Mood Entries");
        title.setFont(new Font("Arial Bold", 18));
        title.setStyle(TEXT_COLOR_DARK_GREY);

        Label description = new Label("Your mood tracking history");
        description.setFont(new Font("Arial", 12));
        description.setStyle(TEXT_COLOR_GREY);

        VBox entriesList = new VBox(10);
        recentMoodEntries.addListener((javafx.collections.ListChangeListener.Change<? extends MoodEntry> change) -> {
            Platform.runLater(() -> repopulateRecentEntries(entriesList));
        });
        repopulateRecentEntries(entriesList); // Initial population when the card is created

        card.getChildren().addAll(title, description, entriesList);
        return card;
    }

    public void repopulateRecentEntries(VBox entriesList) {
        entriesList.getChildren().clear();
        if (recentMoodEntries.isEmpty()) {
            Label noEntriesLabel = new Label("No recent mood entries. Log your mood!");
            noEntriesLabel.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 14px;");
            entriesList.getChildren().add(noEntriesLabel);
            return;
        }

        for (MoodEntry entry : recentMoodEntries) {
            HBox entryRow = new HBox(15);
            entryRow.setAlignment(Pos.CENTER_LEFT);
            entryRow.setPadding(new Insets(10));
            entryRow.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 8px;");

            Label moodEmojiLabel = new Label(entry.getMoodEmoji());
            moodEmojiLabel.setFont(new Font("Segoe UI Emoji", 24));

            VBox textContent = new VBox(2);
            Label dateLabel = new Label(entry.getFormattedDate());
            dateLabel.setFont(new Font("Arial Bold", 13));
            dateLabel.setStyle(TEXT_COLOR_DARK_GREY);

            FlowPane tagsRow = new FlowPane(5, 5);
            if (entry.getTags() != null) {
                for (String tag : entry.getTags()) {
                    Label tagBadge = new Label(tag);
                    tagBadge.setStyle(BADGE_SECONDARY_STYLE);
                    tagsRow.getChildren().add(tagBadge);
                }
            }
            textContent.getChildren().addAll(dateLabel, tagsRow);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label intensityBadge = new Label(entry.getIntensity() + "/10");
            intensityBadge.setStyle(TAG_STYLE_OUTLINE);

            Label energyBadge = new Label(entry.getEnergyLevel() + "/10 Energy");
            energyBadge.setStyle(TAG_STYLE_OUTLINE);


            entryRow.getChildren().addAll(moodEmojiLabel, textContent, spacer, intensityBadge, energyBadge);
            entriesList.getChildren().add(entryRow);
        }
    }

    // --- Mood Tracker Logic ---
    private void handleSaveMood() {
        String currentUserId = AuthController.loggedInUserId;
        if (currentUserId == null) {
            SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "Please log in to save your mood.");
            return;
        }

        if (selectedMoodValue.get() == 0) {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Input Error", "Please select your mood (emoji) before saving.");
            return;
        }

        String moodEmoji = "";
        for (MoodEmoji data : MOOD_EMOJIS_DATA) {
            if (data.getValue() == selectedMoodValue.get()) {
                moodEmoji = data.getEmoji();
                break;
            }
        }
        String notesContent = notesTextArea.getText().trim();
        List<String> currentTags = new ArrayList<>(selectedTags);

        MoodEntry newEntry = new MoodEntry(
                null, // ID will be auto-generated by Firestore
                currentUserId,
                moodEmoji,
                (int) moodIntensityValue.get(),
                (int) energyLevelValue.get(), // NEW: Pass energyLevel here
                currentTags,
                notesContent,
                LocalDateTime.now()
        );

        moodDao.addMoodEntry(newEntry);

        System.out.println("Mood Logged to Firebase: Mood=" + newEntry.getMoodEmoji() + ", Intensity=" + newEntry.getIntensity() +
                           ", Energy=" + newEntry.getEnergyLevel() +
                           ", Tags=" + newEntry.getTags() + ", Notes='" + newEntry.getNotes() + "'");

        SceneManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Mood logged successfully!");

        resetMoodTrackerForm();
        loadRecentMoodEntries();

        if (onMoodSavedCallback != null) {
            onMoodSavedCallback.run();
        }
    }

    private void resetMoodTrackerForm() {
        selectedMoodValue.set(0);
        moodIntensityValue.set(5.0);
        energyLevelValue.set(5.0); // NEW: Reset energy level
        selectedTags.clear();
        notesTextArea.clear();

        for (VBox btnWrapper : moodEmojiButtons) {
            btnWrapper.setStyle(TAG_STYLE_OUTLINE);
            if (btnWrapper.getChildren().size() > 1 && btnWrapper.getChildren().get(1) instanceof Label) {
                ((Label)btnWrapper.getChildren().get(1)).setStyle(TEXT_COLOR_GREY);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods
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
                System.err.println("Also failed to load placeholder.png during exception: " + pe.getMessage());
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

    public ObservableValue<Number> getRecentMoodEntries() {
        throw new UnsupportedOperationException("Unimplemented method 'getRecentMoodEntries'");
    }
}