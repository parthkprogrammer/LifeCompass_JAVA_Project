
// package com.lifecompass.view; // Corrected package name
// import javafx.animation.TranslateTransition;
// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.geometry.Insets;

// import javafx.geometry.Pos;
// import javafx.scene.Node;
// import javafx.scene.Scene; // Only if MoodTrackerApp spawns its own internal Stage/Scene (e.g. for sub-popups)
// import javafx.scene.control.*;
// import javafx.scene.image.Image; // Added for createImageView
// import javafx.scene.image.ImageView; // Added for createImageView
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle; // Retained from original top bar if needed by MoodTrackerApp itself
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight; // For FontWeight in dashboard styles
// import javafx.stage.Stage; // Only if MoodTrackerApp spawns its own internal Stage

// import javafx.util.Duration;

// // Specific imports for User Dashboard UI components for editing profile
// import javafx.stage.Modality; // For Modality in alerts

// import java.io.File;
// import java.net.URL;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import java.util.concurrent.ExecutionException;

// import com.lifecompass.LoginScreen;
// import com.lifecompass.controller.AuthController;
// // Removed MoodDaoFirestoreImpl and MoodEntry imports as they are managed by MoodTrackerApp
// import com.lifecompass.dao.impl.UserDaoFirestoreImpl;
// import com.lifecompass.model.MoodEntry; // Re-added for MoodEntry type in dashboard display
// import com.lifecompass.model.User;
// import com.lifecompass.util.SceneManager;
// import com.lifecompass.view.MoodTrackerApp;


// public class UserDashboardScreen extends Application {
//     private Label journalCountLabel; // Label for overall journal count
//     // REMOVED: private Label dashboardValue2; // This field is no longer needed

//     // Class-level variable to hold the current journalCount.
//     private int currentJournalCount = 0;

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

//     // --- User Data (will be loaded from Firestore and updated) ---
//     private String username;
//     private String userId;
//     private String userEmail;

//     // --- Core UI Components ---
//     private Stage primaryStage;
//     private Button activeTabButton;
//     private VBox mainContentArea;
//     private StackPane settingsPanelContainer;
//     private HBox buttonStrip;
//     private Label welcomeLabelHeader;
//     private ImageView profileImageHeader;

//     // --- Child Screen Instances (initialized once for reuse) ---
//     private CBTChatApp cbtChatScreen;
//     private Explore exploreScreen;
//     private JournalAppUIFull journalScreen;
//     private MoodTrackerApp moodScreen; // Instance of MoodTrackerApp
//     private LifeCompassArtApp artScreen;
//     private MentalHealthAppView analyticsScreen;
//     private NotificationScreen notificationScreen;
//     private ProfileScreen profileScreen;
//     private SettingsScreen settingsScreen;
//     private static User user;

//     public static void setUser(User user1){
//         user=user1;
//     }
//     public static User getUser(){return user;}

//     // --- Controllers and DAOs for Firebase functionality ---
//     private final AuthController authController = new AuthController();
//     private final UserDaoFirestoreImpl userDao = new UserDaoFirestoreImpl();

//     // REMOVED: private List<VBox> moodEmojiButtons = new ArrayList<>(); // Managed by MoodTrackerApp

//     // REMOVED: private ObservableList<MoodEntry> recentMoodEntries = FXCollections.observableArrayList(); // Managed by MoodTrackerApp
//     // REMOVED: private final MoodDaoFirestoreImpl moodDao = new MoodDaoFirestoreImpl(); // Managed by MoodTrackerApp


//     //------------------------------------------------------------------------------------------------------------------
//     // Application Lifecycle
//     //------------------------------------------------------------------------------------------------------------------

//     @Override
//     public void start(Stage primaryStage) throws Exception {
//         this.primaryStage = primaryStage;
//         primaryStage.setTitle("LifeCompass Dashboard");

//         journalCountLabel = new Label("Journal Entries: Loading...");
//         journalCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

//         boolean userLoaded = loadInitialUserData();
//         if (!userLoaded) {
//             return;
//         }

//         // Initialize child screen instances, passing context
//         this.cbtChatScreen = new CBTChatApp(primaryStage, this);
//         this.moodScreen = new MoodTrackerApp(primaryStage, this); // Initialized MoodTrackerApp here
//         this.exploreScreen = new Explore(primaryStage, this);
//         this.journalScreen = new JournalAppUIFull(primaryStage, this); // Pass 'this'
//         this.artScreen = new LifeCompassArtApp(primaryStage, this);
//         this.analyticsScreen = new MentalHealthAppView(); // Assuming this doesn't need context
//         this.notificationScreen = new NotificationScreen(primaryStage, this);
//         this.profileScreen = new ProfileScreen(primaryStage,this);
//         this.settingsScreen = new SettingsScreen(primaryStage, this, this::hideSettingsPanel);

//         StackPane rootLayout = createRootLayout();
//         Scene scene = new Scene(rootLayout, 1200, 800);

//         if (settingsPanelContainer != null) {
//             settingsPanelContainer.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
//             settingsPanelContainer.setTranslateX(settingsPanelContainer.prefWidthProperty().get());
//         }

//         primaryStage.setScene(scene);
//         primaryStage.show();

//         switchContent("Dashboard"); // Initial content display

//         primaryStage.setOnCloseRequest(event -> {
//             if (journalScreen != null) {
//                 journalScreen.stopFirebaseListeners(); // Stop Firebase listeners in Journal module
//             }
//             // Add similar cleanup for other modules if they have ongoing processes
//             // e.g., if exploreScreen has a MediaPlayer playing, stop/dispose it here:
//             // if (exploreScreen != null) { exploreScreen.stopMediaPlayer(); } // Assuming Explore has a stop method
//         });
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Firebase Data Loading (Initial on Dashboard Start)
//     //------------------------------------------------------------------------------------------------------------------

//     private boolean loadInitialUserData() {
//         this.userId = AuthController.loggedInUserId;
//         String userRole = AuthController.loggedInUserRole;

//         if (this.userId == null || !"user".equals(userRole)) {
//             Platform.runLater(() -> {
//                 SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "No user logged in or unauthorized access.");
//                 authController.clearLoggedInUser();
//                 SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
//             });
//             return false;
//         }

//         try {
//             Optional<User> userOptional = userDao.getUserById(this.userId);
//             if (userOptional.isPresent()) {
//                 User user = userOptional.get();
//                 this.username = user.getFullName();
//                 this.userEmail = user.getEmail();
//                 return true;
//             } else {
//                 Platform.runLater(() -> {
//                     SceneManager.showAlert(Alert.AlertType.ERROR, "Data Error", "Your user profile data could not be found in Firestore. Please contact support.");
//                     authController.clearLoggedInUser();
//                     SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
//                 });
//                 return false;
//             }
//         } catch (ExecutionException | InterruptedException e) {
//             System.err.println("Error fetching user data from Firestore: " + e.getMessage());
//             Platform.runLater(() -> {
//                 SceneManager.showAlert(Alert.AlertType.ERROR, "Data Load Error", "Failed to load user data. Please check your internet connection and try again.");
//                 authController.clearLoggedInUser();
//                 SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
//             });
//             return false;
//         }
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Root Layout and Core UI Structure
//     //------------------------------------------------------------------------------------------------------------------

//     private StackPane createRootLayout() {
//         VBox baseDashboardContent = new VBox(20);
//         baseDashboardContent.setPadding(new Insets(20));
//         baseDashboardContent.setStyle("-fx-background-color: #f9fafb;");

//         HBox header = buildHeader();
//         profileImageHeader = (ImageView) ((Button)((HBox)header.getChildren().get(0)).getChildren().get(0)).getGraphic();
//         VBox leftTextContentVBox = (VBox) ((HBox)header.getChildren().get(0)).getChildren().get(1);
//         if (leftTextContentVBox.getChildren().size() > 1) {
//             welcomeLabelHeader = (Label) leftTextContentVBox.getChildren().get(1);
//         }

//         baseDashboardContent.getChildren().addAll(
//                 header,
//                 buildTabBar()
//         );

//         mainContentArea = new VBox(20);
//         mainContentArea.setPadding(new Insets(0, 0, 20, 0));
//         VBox.setVgrow(mainContentArea, Priority.ALWAYS);

//         ScrollPane scrollableDashboard = new ScrollPane(new VBox(baseDashboardContent, mainContentArea));
//         scrollableDashboard.setFitToWidth(true);
//         scrollableDashboard.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollableDashboard.setStyle("-fx-background: #f9fafb; -fx-border-insets: 0;");

//         settingsPanelContainer = new StackPane();
//         settingsPanelContainer.setAlignment(Pos.TOP_RIGHT);
//         settingsPanelContainer.setManaged(false);
//         settingsPanelContainer.setVisible(false);
//         settingsPanelContainer.setStyle("-fx-background-color: transparent;");

//         StackPane rootStack = new StackPane();
//         rootStack.getChildren().addAll(scrollableDashboard, settingsPanelContainer);
//         return rootStack;
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Header Section (Profile, Welcome, Notifications, Settings)
//     //------------------------------------------------------------------------------------------------------------------

//     private HBox buildHeader() {
//         profileImageHeader = createImageView("/assets/images/profile.png", 50, 50);
//         Circle clip = new Circle(25, 25, 25);
//         profileImageHeader.setClip(clip);

//         Button profileButton = new Button();
//         profileButton.setGraphic(profileImageHeader);
//         profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
//         Tooltip.install(profileButton, new Tooltip("View Profile"));

//         profileButton.setOnAction(event -> {
//             if (profileScreen != null) {
//                 switchContent("Profile");
//             } else {
//                 System.err.println("Error: ProfileScreen instance not initialized in UserDashboardScreen.start()!");
//             }
//         });
//         setHoverStyle(profileButton, "-fx-background-color: #e0e0e0; -fx-padding: 0; -fx-background-radius: 25;",
//                                      "-fx-background-color: transparent; -fx-padding: 0;");

//         Label title = new Label("LifeCompass");
//         title.setFont(Font.font("System", FontWeight.BOLD, 24));
//         title.setStyle("-fx-text-fill: black;");

//         welcomeLabelHeader = new Label("Welcome back, " + getUsername() + "!");
//         welcomeLabelHeader.setFont(Font.font(14));
//         welcomeLabelHeader.setTextFill(Color.GRAY);

//         VBox leftTextContent = new VBox(5, title, welcomeLabelHeader);
//         leftTextContent.setAlignment(Pos.CENTER_LEFT);

//         HBox leftSection = new HBox(10, profileButton, leftTextContent);
//         leftSection.setAlignment(Pos.CENTER_LEFT);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Button notificationButton = createButton("/assets/images/notification.png", "Notifications");
//         notificationButton.setOnAction(event -> {
//             if (notificationScreen != null) {
//                 notificationScreen.show();
//             } else {
//                 System.err.println("Error: NotificationScreen instance not initialized!");
//             }
//         });

//         Button settingButton = createIconButton("/assets/images/settings.png", "Settings");
//         settingButton.setOnAction(event -> openSettingsPanel());

//         HBox rightIcons = new HBox(15, notificationButton, settingButton);
//         rightIcons.setAlignment(Pos.CENTER_RIGHT);

//         HBox header = new HBox(leftSection, spacer, rightIcons);
//         header.setAlignment(Pos.CENTER_LEFT);

//         Platform.runLater(this::updateHeaderProfileInfo);

//         return header;
//     }

//     /**
//      * Updates the welcome label and profile picture in the header based on the loaded user data.
//      */
//     private void updateHeaderProfileInfo() {
//         if (welcomeLabelHeader != null && username != null) {
//             welcomeLabelHeader.setText("Welcome back, " + username + "!");
//         }
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Sliding Settings Panel
//     //------------------------------------------------------------------------------------------------------------------

//     private void openSettingsPanel() {
//         System.out.println("Opening Settings Panel...");

//         settingsPanelContainer.getChildren().clear();

//         if (settingsScreen != null) {
//             VBox settingsPanelUI = settingsScreen.createSettingsPanel();
//             settingsPanelContainer.getChildren().add(settingsPanelUI);
//         } else {
//             System.err.println("Error: SettingsScreen instance not initialized!");
//             return;
//         }

//         settingsPanelContainer.setVisible(true);
//         settingsPanelContainer.setManaged(true);

//         TranslateTransition openTransition = new TranslateTransition(Duration.millis(300), settingsPanelContainer);
//         openTransition.setToX(0);
//         openTransition.play();
//     }

//     public void hideSettingsPanel() {
//         System.out.println("Closing Settings Panel...");
//         TranslateTransition closeTransition = new TranslateTransition(Duration.millis(300), settingsPanelContainer);
//         closeTransition.setToX(settingsPanelContainer.prefWidthProperty().get());
//         closeTransition.setOnFinished(event -> {
//             settingsPanelContainer.setVisible(false);
//             settingsPanelContainer.setManaged(false);
//             settingsPanelContainer.getChildren().clear();
//         });
//         closeTransition.play();
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Tab Bar Navigation
//     //------------------------------------------------------------------------------------------------------------------

//     private HBox buildTabBar() {
//         String[] tabs = {"Dashboard", "Mood", "Journal", "Analytics", "Chat", "Art", "Explore"};
//         String[] imagePaths = {
//                 "/assets/images/home.png",
//                 "/assets/images/heart2.png",
//                 "/assets/images/journal.jpg",
//                 "/assets/images/analytics.png",
//                 "/assets/images/chats.png",
//                 "/assets/images/art.png",
//                 "/assets/images/explore.png"
//         };

//         HBox container = new HBox();
//         container.setPadding(new Insets(10));
//         container.setStyle("-fx-background-color: #f3eeeee1; -fx-background-radius: 10;");
//         container.setAlignment(Pos.CENTER);

//         buttonStrip = new HBox(10);
//         buttonStrip.setAlignment(Pos.CENTER);
//         HBox.setHgrow(buttonStrip, Priority.ALWAYS);

//         final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555;";
//         final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;" +
//                                      " -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 15;";
//         final String hoverInactiveStyle = "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-background-radius: 10;";


//         for (int i = 0; i < tabs.length; i++) {
//             String tabName = tabs[i];
//             String imagePath = imagePaths[i];

//             ImageView iconView = createImageView(imagePath, 20, 20);

//             Button btn = new Button(tabName, iconView);
//             btn.setMaxWidth(Double.MAX_VALUE);
//             HBox.setHgrow(btn, Priority.ALWAYS);
//             buttonStrip.getChildren().add(btn);

//             btn.setStyle(inactiveStyle);

//             if (tabName.equals("Dashboard")) {
//                 btn.setStyle(activeStyle);
//                 activeTabButton = btn;
//             }

//             btn.setOnAction(event -> {
//                 String currentTabName = (activeTabButton != null) ? activeTabButton.getText() : "";
//                 if (currentTabName.equals("Chat") && cbtChatScreen != null) {
//                     cbtChatScreen.stopChatServices();
//                 }

//                 if (activeTabButton != null) {
//                     activeTabButton.setStyle(inactiveStyle);
//                 }
//                 btn.setStyle(activeStyle);
//                 activeTabButton = btn;

//                 switchContent(tabName);
//                 System.out.println("Clicked Tab: " + tabName);
//             });

//             setHoverStyle(btn, hoverInactiveStyle, inactiveStyle);
//         }

//         container.getChildren().add(buttonStrip);
//         return container;
//     }

//     /**
//      * Switches the content displayed in the mainContentArea based on the selected tab.
//      */
//     private void switchContent(String tabName) {
//         mainContentArea.getChildren().clear();

//         switch (tabName) {
//             case "Dashboard":
//                 // This is the point where the Dashboard content is added.
//                 // It will now always rebuild with the latest currentJournalCount.
//                 rebuildDashboardMetrics();
//                 break;
//             case "Mood":
//                 if (moodScreen != null) {
//                     mainContentArea.getChildren().add(moodScreen.createMoodScreenContent());
//                 } else {
//                     System.err.println("Mood Screen instance is null. Ensure it is initialized.");
//                     mainContentArea.getChildren().add(new Label("Error: Mood Screen not initialized."));
//                 }
//                 break;
//             case "Journal":
//                 if (journalScreen != null) {
//                     mainContentArea.getChildren().add(journalScreen.createJournalScreenContent());
//                 } else {
//                     mainContentArea.getChildren().add(new Label("Error: Journal Screen not initialized."));
//                 }
//                 break;
//             case "Analytics":
//                 if (analyticsScreen != null) {
//                     mainContentArea.getChildren().add(analyticsScreen.createAnalyticsDashboardContent());
//                 } else {
//                     mainContentArea.getChildren().add(new Label("Error: Analytics Screen not initialized."));
//                 }
//                 break;
//             case "Chat":
//                 if (cbtChatScreen != null) {
//                     mainContentArea.getChildren().add(cbtChatScreen.createChatScreenContent());
//                 } else {
//                     mainContentArea.getChildren().add(new Label("Error: Chat Screen not initialized."));
//                 }
//                 break;
//             case "Art":
//                 if (artScreen != null) {
//                     mainContentArea.getChildren().add(artScreen.createArtScreenContent());
//                 } else {
//                     System.err.println("Art Screen instance is null. Ensure it is initialized.");
//                     mainContentArea.getChildren().add(new Label("Error: Art Screen not initialized."));
//                 }
//                 break;
//             case "Explore":
//                 if (exploreScreen != null) {
//                     mainContentArea.getChildren().add(exploreScreen.createExploreScreenContent());
//                 } else {
//                     System.err.println("Explore Screen instance is null. Ensure it is initialized.");
//                     mainContentArea.getChildren().add(new Label("Error: Explore Screen not initialized."));
//                 }
//                 break;
//             case "Profile":
//                 if (profileScreen != null) {
//                     mainContentArea.getChildren().add(profileScreen.createDisplayProfileContent()); // Assuming ProfileScreen has a getProfileContent method
//                 } else {
//                     System.err.println("Profile Screen instance is null. Ensure it is initialized.");
//                     mainContentArea.getChildren().add(new Label("Error: Profile Screen not initialized."));
//                 }
//                 break;
//             default:
//                 mainContentArea.getChildren().add(new Label("Content for '" + tabName + "' is not yet implemented."));
//                 break;
//         }
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Dashboard Tab Content Sections
//     //------------------------------------------------------------------------------------------------------------------

//     /**
//      * Rebuilds and displays the dashboard metrics section.
//      * This method is called whenever the currentJournalCount (or other metrics) might have changed,
//      * or when switching to the Dashboard tab.
//      */
//     private void rebuildDashboardMetrics() {
//         mainContentArea.getChildren().clear(); // Clear previous dashboard content
//         mainContentArea.getChildren().addAll(
//             buildMetricsRow(),
//             buildMoodAndActionsRow(),
//             buildGoalsCard()
//         );
//     }

//     /**
//      * Public method called by JournalAppUIFull to update the journal entry count.
//      * @param count The current number of journal entries.
//      */
//     public void updateJournalCount(int count) {
//         Platform.runLater(() -> {
//             // Update the internal class variable
//             this.currentJournalCount = count;

//             // Update the journalCountLabel displayed elsewhere (e.g., header)
//             journalCountLabel.setText("Journal Entries: " + count);

//             // Rebuild the dashboard metrics if the Dashboard tab is currently active.
//             // This ensures the "Journal Entries" metric card reflects the new count.
//             if (activeTabButton != null && "Dashboard".equals(activeTabButton.getText())) {
//                 rebuildDashboardMetrics();
//             }
//             // NO NEED for else-branch with dashboardValue2.setText, as rebuildMetricsRow handles it.
//             System.out.println("Dashboard metric updated: Journal entries: " + count);
//         });
//     }

//     private HBox buildMetricsRow() {
//         HBox hbox = new HBox(20);
//         hbox.setPadding(new Insets(10, 0, 10, 0));
//         hbox.setAlignment(Pos.CENTER);

//         String[] titles = {"Current Streak", "Journal Entries", "Mood Score", "Badges Earned"};
//         // CORRECTED: Use the class-level currentJournalCount for the "Journal Entries" value
//         String[] values = {"1 day", String.valueOf(this.currentJournalCount), "7.2/10", "0"};
//         String[] subtitles = {"Keep it up!", "Total entries", "This week average", "Achievement badges"};
//         String[] images = {
//                 "/assets/images/trophy.png",
//                 "/assets/images/journal.jpg",
//                 "/assets/images/heart2.png",
//                 "/assets/images/trophy2.png"
//         };

//         for (int i = 0; i < titles.length; i++) {
//             HBox card = createMetricCard(titles[i], values[i], subtitles[i], images[i]);
//             HBox.setHgrow(card, Priority.ALWAYS);
//             card.setMaxWidth(Double.MAX_VALUE);
//             hbox.getChildren().add(card);

//             // REMOVED: No longer need to store a direct reference to dashboardValue2 here.
//             // The value will be set when the card is created from 'values[i]'.
//             // If you need to update a specific label within a single existing card
//             // without rebuilding the whole row, that would require a different approach
//             // (e.g., having ObservableValue properties for each metric).
//         }
//         return hbox;
//     }

//     private HBox createMetricCard(String title, String value, String subtitle, String imagePath) {
//         Label titleLbl = new Label(title);
//         titleLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

//         Label valueLbl = new Label(value); // This label's text is set from the 'value' parameter
//         valueLbl.setFont(Font.font("System", FontWeight.BOLD, 24));

//         Label subLbl = new Label(subtitle);
//         subLbl.setFont(Font.font(12));
//         subLbl.setTextFill(Color.GRAY);

//         VBox textContent = new VBox(10, titleLbl, valueLbl, subLbl);
//         textContent.setAlignment(Pos.TOP_LEFT);

//         ImageView imageView = createImageView(imagePath, 24, 24);

//         HBox cardBox = new HBox(10);
//         cardBox.setPadding(new Insets(15));
//         cardBox.setAlignment(Pos.TOP_RIGHT);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         cardBox.getChildren().addAll(textContent, spacer, imageView);


//         String originalStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;";
//         cardBox.setStyle(originalStyle);
//         cardBox.setPrefWidth(220);

//         setHoverStyle(cardBox, "-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#d0d0d0; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);",
//                                  originalStyle);
//         return cardBox;
//     }

//     private HBox buildMoodAndActionsRow() {
//         HBox hbox = new HBox(20);

//         // --- UPDATED: Use a new method to create the summarized recent mood entries card ---
//         VBox moodSection = createDashboardRecentMoodsSummaryCard();
//         // --- END UPDATED ---

//         VBox actionsSection = buildQuickActions();

//         HBox.setHgrow(actionsSection, Priority.ALWAYS);
//         HBox.setHgrow(moodSection, Priority.ALWAYS); // Ensure mood section also grows

//         hbox.getChildren().addAll(moodSection, actionsSection);
//         return hbox;
//     }

    

//     /**
//      * NEW METHOD: Creates a summarized card for recent mood entries on the dashboard.
//      * Shows only the top 3 entries and a "View All" link if more exist.
//      */
//     private VBox createDashboardRecentMoodsSummaryCard() {
//         VBox card = new VBox(10);
//         card.setPadding(new Insets(20));
//         card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
//         card.setMaxWidth(Double.MAX_VALUE);
//         HBox.setHgrow(card, Priority.ALWAYS);

//         Label title = new Label("Recent Mood Entries");
//         title.setFont(new Font("Arial Bold", 18));
//         title.setStyle(TEXT_COLOR_DARK_GREY);

//         Label description = new Label("Your latest mood tracking history");
//         description.setFont(new Font("Arial", 12));
//         description.setStyle(TEXT_COLOR_GREY);

//         VBox entriesListContainer = new VBox(10); // Container for the mood entry rows
        
//         // Listen to changes in MoodTrackerApp's recentMoodEntries to update this dashboard view
//         moodScreen.recentMoodEntries.addListener((javafx.collections.ListChangeListener.Change<? extends MoodEntry> change) -> {
//             Platform.runLater(() -> updateDashboardMoodEntriesDisplay(entriesListContainer));
//         });

//         // Initial population
//         updateDashboardMoodEntriesDisplay(entriesListContainer);

//         card.getChildren().addAll(title, description, entriesListContainer);
//         return card;
//     }

//     /**
//      * Helper method to update the display of recent mood entries in the dashboard summary card.
//      * @param entriesListContainer The VBox to populate with mood entry rows.
//      */
//     private void updateDashboardMoodEntriesDisplay(VBox entriesListContainer) {
//         entriesListContainer.getChildren().clear(); // Clear existing entries

//         List<MoodEntry> allEntries = moodScreen.recentMoodEntries; // Get data from MoodTrackerApp
//         int displayLimit = 3;
//         int entriesToDisplay = Math.min(allEntries.size(), displayLimit);

//         if (allEntries.isEmpty()) {
//             Label noEntriesLabel = new Label("No recent mood entries. Log your mood!");
//             noEntriesLabel.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 14px;");
//             entriesListContainer.getChildren().add(noEntriesLabel);
//             return;
//         }

//         // Display the top 3 (or fewer if less than 3) recent entries
//         for (int i = 0; i < entriesToDisplay; i++) {
//             MoodEntry entry = allEntries.get(i);
//             entriesListContainer.getChildren().add(createMoodEntryRow(entry));
//         }

//         // Show "View All" if there are more than 3 entries
//         if (allEntries.size() > displayLimit) {
//             int remainingCount = allEntries.size() - displayLimit;
//             Hyperlink viewAllLink = new Hyperlink("View All Moods (" + remainingCount + " more)");
//             viewAllLink.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_BLUE_ACCENT + "; -fx-underline: true;");
//             viewAllLink.setOnAction(e -> {
//                 switchContent("Mood"); // Switch to the full Mood tab
//                 updateActiveTabButton("Mood"); // Update the active tab button
//             });
//             HBox linkContainer = new HBox(viewAllLink);
//             linkContainer.setAlignment(Pos.CENTER_RIGHT);
//             linkContainer.setPadding(new Insets(5, 0, 0, 0));
//             entriesListContainer.getChildren().add(linkContainer);
//         }
//     }

//     /**
//      * Helper method to create a single row for a mood entry.
//      * This is similar to what was in MoodTrackerApp's repopulateRecentEntries.
//      */
//     private HBox createMoodEntryRow(MoodEntry entry) {
//         HBox entryRow = new HBox(15);
//         entryRow.setAlignment(Pos.CENTER_LEFT);
//         entryRow.setPadding(new Insets(10));
//         entryRow.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 8px;");

//         Label moodEmojiLabel = new Label(entry.getMoodEmoji());
//         moodEmojiLabel.setFont(new Font("Segoe UI Emoji", 24));

//         VBox textContent = new VBox(2);
//         Label dateLabel = new Label(entry.getFormattedDate());
//         dateLabel.setFont(new Font("Arial Bold", 13));
//         dateLabel.setStyle(TEXT_COLOR_DARK_GREY);

//         FlowPane tagsRow = new FlowPane(5, 5);
//         if (entry.getTags() != null) {
//             for (String tag : entry.getTags()) {
//                 Label tagBadge = new Label(tag);
//                 tagBadge.setStyle(BADGE_SECONDARY_STYLE);
//                 tagsRow.getChildren().add(tagBadge);
//             }
//         }
//         textContent.getChildren().addAll(dateLabel, tagsRow);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Label intensityBadge = new Label(entry.getIntensity() + "/10");
//         intensityBadge.setStyle(TAG_STYLE_OUTLINE);

//         entryRow.getChildren().addAll(moodEmojiLabel, textContent, spacer, intensityBadge);
//         return entryRow;
//     }


//     private HBox moodRow(String day, String mood, String score) {
//         Circle circle = new Circle(12);
//         circle.setFill(Color.rgb(128, 177, 226, 1));

//         ImageView heartIcon = createImageView("/assets/images/heart2.png", 16, 16);
//         StackPane iconContainer = new StackPane(circle, heartIcon);
//         iconContainer.setAlignment(Pos.CENTER);

//         Label dayLabel = new Label(day);
//         dayLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
//         dayLabel.setTextFill(Color.GRAY);

//         Label moodLabel = new Label(mood);
//         moodLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
//         moodLabel.setTextFill(Color.BLACK);

//         VBox dayMoodVBox = new VBox(0, dayLabel, moodLabel);
//         dayMoodVBox.setAlignment(Pos.CENTER_LEFT);

//         Label scoreLabel = new Label(score + "/10");
//         scoreLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
//         final String scoreLabelOriginalStyle = "-fx-background-color: #d2d5d2; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5;";
//         scoreLabel.setStyle(scoreLabelOriginalStyle);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         HBox row = new HBox(10, iconContainer, dayMoodVBox, spacer, scoreLabel);
//         row.setAlignment(Pos.CENTER_LEFT);

//         String originalRowStyle = "-fx-background-color: transparent;";
//         row.setStyle(originalRowStyle);

//         setHoverStyle(row, "-fx-background-color: #f0f0f0; -fx-background-radius: 5;",
//                                  originalRowStyle);
//         row.setOnMouseEntered(e -> scoreLabel.setStyle("-fx-background-color: #c2c5c2; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-border-radius: 10;"));
//         row.setOnMouseExited(e -> scoreLabel.setStyle(scoreLabelOriginalStyle));

//         return row;
//     }

//     private VBox buildQuickActions() {
//         VBox box = new VBox(10);
//         box.setPadding(new Insets(15));
//         box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;");
//         box.setPrefWidth(500);

//         Label heading = new Label("Quick Actions");
//         heading.setFont(Font.font("System", FontWeight.BOLD, 18));

//         Label sub = new Label("Common tasks to support your wellness");
//         sub.setFont(Font.font(12));
//         sub.setTextFill(Color.GRAY);

//         GridPane grid = new GridPane();
//         grid.setHgap(10);
//         grid.setVgap(10);
//         grid.setAlignment(Pos.CENTER);

//         String[] actions = {"Log Mood", "Write Journal", "Find Psychologists", "Express Art"};
//         String[] actionImagePaths = {
//                 "/assets/images/heart2.png",
//                 "/assets/images/journal.jpg",
//                 "/assets/images/psyprofile.png",
//                 "/assets/images/art.png"
//         };

//         for (int i = 0; i < actions.length; i++) {
//             VBox actionMiniCard = createActionMiniCard(actions[i], actionImagePaths[i]);
//             GridPane.setHgrow(actionMiniCard, Priority.ALWAYS);
//             GridPane.setVgrow(actionMiniCard, Priority.ALWAYS);
//             grid.add(actionMiniCard, i % 2, i / 2);
//         }

//         box.getChildren().addAll(heading, sub, grid);
//         return box;
//     }

//     private VBox createActionMiniCard(String actionName, String imagePath) {
//         VBox miniCard = new VBox(5);
//         miniCard.setAlignment(Pos.CENTER);
//         miniCard.setPadding(new Insets(15, 10, 15, 10));
//         miniCard.setPrefSize(180, 100);

//         String originalStyle = "-fx-background-color: white; " +
//                                "-fx-background-radius: 8; " +
//                                "-fx-border-color: #e0e0e0; " +
//                                "-fx-border-width: 1; " +
//                                "-fx-border-radius: 8; " +
//                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);";
//         miniCard.setStyle(originalStyle);

//         ImageView iconView = createImageView(imagePath, 28, 28);

//         Label nameLabel = new Label(actionName);
//         nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
//         nameLabel.setTextFill(Color.DARKSLATEGRAY);

//         miniCard.getChildren().addAll(iconView, nameLabel);

//         miniCard.setOnMouseClicked(event -> {
//             System.out.println("Action '" + actionName + "' mini-card clicked!");
//             switch (actionName) {
//                 case "Log Mood":
//                     switchContent("Mood");
//                     updateActiveTabButton("Mood");
//                     break;
//                 case "Write Journal":
//                     switchContent("Journal");
//                     updateActiveTabButton("Journal");
//                     break;
//                 case "Express Art":
//                     switchContent("Art");
//                     updateActiveTabButton("Art");
//                     break;
//                 case "Find Psychologists":
//                     switchContent("Explore");
//                     updateActiveTabButton("Explore");
//                     break;
//                 default:
//                     System.out.println("Unhandled quick action: " + actionName);
//                     break;
//             }
//         });

//         setHoverStyle(miniCard, "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: #c0c0c0; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);",
//                                  originalStyle);
//         return miniCard;
//     }

//     /**
//      * Helper method to update the active tab button style in the main tab bar.
//      * @param tabName The name of the tab to set as active.
//      */
//     private void updateActiveTabButton(String tabName) {
//         final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555;";
//         final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;" +
//                                      " -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 15;";

//         if (activeTabButton != null) {
//             activeTabButton.setStyle(inactiveStyle);
//         }

//         if (buttonStrip != null) {
//             for (Node node : buttonStrip.getChildren()) {
//                 if (node instanceof Button) {
//                     Button btn = (Button) node;
//                     if (btn.getText().equals(tabName)) {
//                         btn.setStyle(activeStyle);
//                         activeTabButton = btn;
//                         break;
//                     }
//                 }
//             }
//         }
//     }

//     private VBox buildGoalsCard() {
//         VBox box = new VBox(10);
//         box.setPadding(new Insets(15));
//         box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;");
//         box.setPrefWidth(1020);

//         Label heading = new Label("Today's Wellness Goals");
//         heading.setFont(Font.font("System", FontWeight.BOLD, 18));
//         Label sub = new Label("Track your daily mental health activities");
//         sub.setFont(Font.font(12));
//         sub.setTextFill(Color.GRAY);

//         box.getChildren().addAll(heading, sub);

//         String[] goalTexts = {
//                 "Log your mood",
//                 "Write in journal",
//                 "Chat with CBT bot"
//         };
//         String[] goalImagePaths = {
//                 "/assets/images/heart2.png",
//                 "/assets/images/journal.jpg",
//                 "/assets/images/chats.png"
//         };
//         String[] goalBorderColors = {
//                 "#FF0000",
//                 "#008000",
//                 "#FFFF00"
//         };
//         boolean[] goalCompletedStatus = {true, false, false};

//         for (int i = 0; i < goalTexts.length; i++) {
//             box.getChildren().add(goalRow(
//                     goalTexts[i],
//                     goalImagePaths[i],
//                     goalBorderColors[i],
//                     goalCompletedStatus[i]
//             ));
//         }
//         return box;
//     }

//     private HBox goalRow(String text, String imagePath, String borderColor, boolean completed) {
//         Circle circle = new Circle(12);
//         circle.setFill(Color.rgb(128, 177, 226, 1));

//         ImageView heartIcon = createImageView("/assets/images/heart2.png", 16, 16);
//         StackPane iconContainer = new StackPane(circle, heartIcon);
//         iconContainer.setAlignment(Pos.CENTER);

//         Label dayLabel = new Label(text); // Changed to use 'text' for the goal description
//         dayLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
//         dayLabel.setTextFill(Color.GRAY);

//         // REMOVED: moodLabel as it's not applicable for general goals
//         // Label moodLabel = new Label(mood);
//         // moodLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
//         // moodLabel.setTextFill(Color.BLACK);

//         VBox textContentVBox = new VBox(0, dayLabel); // Only dayLabel (goal text)
//         textContentVBox.setAlignment(Pos.CENTER_LEFT);

//         Label statusLabel = new Label(completed ? "Completed" : "Pending");
//         statusLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
//         final String statusOriginalStyle = "-fx-background-color: " + (completed ? "#d2d5d2" : "#f0f0f0") + "; " +
//                                             "-fx-text-fill: black; " +
//                                             "-fx-padding: 3 10; " +
//                                             "-fx-background-radius: 10; " +
//                                             "-fx-border-color:#e0e0e0; " +
//                                             "-fx-border-width: 1; " +
//                                             "-fx-border-radius: 10;";
//         statusLabel.setStyle(statusOriginalStyle);


//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         HBox row = new HBox(10, iconContainer, textContentVBox, spacer, statusLabel);
//         row.setAlignment(Pos.CENTER_LEFT);

//         String originalRowStyle = "-fx-background-color: transparent;";
//         row.setStyle(originalRowStyle);

//         setHoverStyle(row, "-fx-background-color: #f0f0f0; -fx-background-radius: 5;",
//                                  originalRowStyle);
//         row.setOnMouseEntered(e -> statusLabel.setStyle("-fx-background-color: " + (completed ? "#c2c5c2" : "#e0e0e0") + "; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-border-radius: 10;"));
//         row.setOnMouseExited(e -> statusLabel.setStyle(statusOriginalStyle));

//         return row;
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // Data Update Methods (Public for external interaction, e.g., ProfileScreen)
//     //------------------------------------------------------------------------------------------------------------------

//     /**
//      * Updates the username in the dashboard header. This is called from ProfileScreen after a user updates their username.
//      * @param newUsername The updated username.
//      */
//     public void updateUsername(String newUsername) {
//         this.username = newUsername;
//         if (welcomeLabelHeader != null) {
//             welcomeLabelHeader.setText("Welcome back, " + this.username + "!");
//         }
//         System.out.println("Dashboard updated with new username: " + this.username);
//     }

//     /**
//      * Updates the profile picture in the dashboard header. This is called from ProfileScreen.
//      * @param newImageUrl The URL/path of the new profile picture.
//      */
//     public void updateProfilePicture(String newImageUrl) {
//         if (profileImageHeader != null) {
//             try {
//                 Image newImage = new Image(newImageUrl);
//                 if (!newImage.isError()) {
//                     profileImageHeader.setImage(newImage);
//                     System.out.println("Dashboard profile picture updated from: " + newImageUrl);
//                 } else {
//                     System.err.println("Header: Failed to load profile image from URL: " + newImageUrl + " - " + newImage.exceptionProperty().get().getMessage());
//                 }
//             } catch (Exception e) {
//                 System.err.println("Header: Exception loading profile image from URL: " + newImageUrl + " - " + e.getMessage());
//             }
//         }
//     }


//     /**
//      * Provides access to the current username. This is important for other screens
//      * like ProfileScreen to retrieve data from the dashboard.
//      * @return The current username.
//      */
//     public String getUsername() {
//         return username;
//     }

//     //------------------------------------------------------------------------------------------------------------------
//     // General Helper Methods (Moved inline/duplicated for self-containment)
//     //------------------------------------------------------------------------------------------------------------------

//     /**
//      * Helper to create an ImageView with error handling for missing image resources.
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
//                     System.err.println("Also failed to load placeholder.png!");
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
//                 System.err.println("Also failed to load placeholder.png during exception: " + pe.getMessage());
//             }
//         }
//         iconView.setFitWidth(fitWidth);
//         iconView.setFitHeight(fitHeight);
//         iconView.setPreserveRatio(true);
//         return iconView;
//     }

//     /**
//      * Helper method to apply generic hover styles to any Region.
//      */
//     private void setHoverStyle(Region region, String hoverStyle, String normalStyle) {
//         region.setOnMouseEntered(e -> region.setStyle(hoverStyle));
//         region.setOnMouseExited(e -> region.setStyle(normalStyle));
//     }

//     private Button createButton(String imagePath, String text) {
//         ImageView imageView = null;
//         try {
//             Image image = new Image(getClass().getResourceAsStream(imagePath));
//             imageView = new ImageView(image);
//             imageView.setFitWidth(24);
//             imageView.setFitHeight(24);
//         } catch (NullPointerException e) {
//             System.err.println("Error: Image not found at " + imagePath);
//         }

//         Button button = new Button(text, imageView);
//         button.setContentDisplay(ContentDisplay.LEFT);
//         return button;
//     }

//     private Button createIconButton(String imagePath, String text) {
//         ImageView imageView = null;
//         try {
//             Image image = new Image(getClass().getResourceAsStream(imagePath));
//             imageView = new ImageView(image);
//             imageView.setFitWidth(24);
//             imageView.setFitHeight(24);
//         } catch (NullPointerException e) {
//             System.err.println("Error: Image not found at " + imagePath);
//         }

//         Button button = new Button(text, imageView);
//         button.setContentDisplay(ContentDisplay.LEFT);
//         return button;
//     }
// }

package com.lifecompass.view;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

// Specific imports for User Dashboard UI components for editing profile
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.lifecompass.LoginScreen;
import com.lifecompass.controller.AuthController;
import com.lifecompass.dao.impl.MoodDaoFirestoreImpl;
import com.lifecompass.dao.impl.UserDaoFirestoreImpl;
import com.lifecompass.model.MoodEntry;
import com.lifecompass.model.User;
import com.lifecompass.util.SceneManager;
import com.lifecompass.services.MoodAnalyticsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty; // NEW: Import StringProperty


public class UserDashboardScreen extends Application {
    private Label journalCountLabel;
    private int currentJournalCount = 0;

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

    // --- User Data (will be loaded from Firestore and updated) ---
    private String username;
    private String userId;
    private String userEmail;

    // --- Core UI Components ---
    private Stage primaryStage;
    private Button activeTabButton;
    private VBox mainContentArea;
    private StackPane settingsPanelContainer;
    private HBox buttonStrip;
    private Label welcomeLabelHeader;
    private ImageView profileImageHeader;

    // NEW: Property for the dashboard's dynamic mood score
    private StringProperty dashboardMoodScoreProperty = new SimpleStringProperty("N/A");

    // --- Child Screen Instances (initialized once for reuse) ---
    private CBTChatApp cbtChatScreen;
    private Explore exploreScreen;
    private JournalAppUIFull journalScreen;
    private MoodTrackerApp moodScreen;
    private LifeCompassArtApp artScreen;
    private MentalHealthAppView analyticsScreen;
    private NotificationScreen notificationScreen;
    private ProfileScreen profileScreen;
    private SettingsScreen settingsScreen;
    private static User user;

    public static void setUser(User user1){
        user=user1;
    }
    public static User getUser(){return user;}

    // --- Controllers and DAOs for Firebase functionality ---
    private final AuthController authController = new AuthController();
    private final UserDaoFirestoreImpl userDao = new UserDaoFirestoreImpl();
    private final MoodAnalyticsService moodAnalyticsService = new MoodAnalyticsService();

    // Constructor to receive the primary stage from the LoginScreen
    public UserDashboardScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setTitle("LifeCompass Dashboard");

        journalCountLabel = new Label("Journal Entries: Loading...");
        journalCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        boolean userLoaded = loadInitialUserData();
        if (!userLoaded) {
            return;
        }

        // Initialize child screens
        this.cbtChatScreen = new CBTChatApp(primaryStage, this);
        this.moodScreen = new MoodTrackerApp(primaryStage, this);
        this.exploreScreen = new Explore(primaryStage, this);
        this.journalScreen = new JournalAppUIFull(primaryStage, this);
        this.artScreen = new LifeCompassArtApp(primaryStage, this);
        this.analyticsScreen = new MentalHealthAppView();
        this.notificationScreen = new NotificationScreen(primaryStage, this);
        this.profileScreen = new ProfileScreen(primaryStage,this);
        this.settingsScreen = new SettingsScreen(primaryStage, this, this::hideSettingsPanel);

        // NEW: Bind dashboardMoodScoreProperty to analyticsScreen's avgMoodValueProperty
        dashboardMoodScoreProperty.bind(analyticsScreen.avgMoodValueProperty());


        // Set callback for mood saved to refresh analytics
        moodScreen.setOnMoodSavedCallback(() -> {
            if (AuthController.loggedInUserId != null) {
                System.out.println("UserDashboardScreen: Mood saved callback - Refreshing Analytics for user: " + AuthController.loggedInUserId);
                analyticsScreen.refreshAnalyticsData(AuthController.loggedInUserId, moodAnalyticsService);
            } else {
                System.err.println("UserDashboardScreen: Mood saved, but no logged-in user to refresh analytics for.");
            }
        });

        StackPane rootLayout = createRootLayout();
        Scene scene = new Scene(rootLayout, 1540, 860);

        if (settingsPanelContainer != null) {
            settingsPanelContainer.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
            settingsPanelContainer.setTranslateX(settingsPanelContainer.prefWidthProperty().get());
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        switchContent("Dashboard"); // Initial content display
        // Ensure analytics are refreshed on Dashboard/initial load if it includes analytics snippets
        // or when the analytics tab is explicitly clicked.

        primaryStage.setOnCloseRequest(event -> {
            if (journalScreen != null) {
                journalScreen.stopFirebaseListeners();
            }
        });
    }
      
    //------------------------------------------------------------------------------------------------------------------
    // Firebase Data Loading (Initial on Dashboard Start)
    //------------------------------------------------------------------------------------------------------------------

    private boolean loadInitialUserData() {
        this.userId = AuthController.loggedInUserId;
        String userRole = AuthController.loggedInUserRole;

        if (this.userId == null || !"user".equals(userRole)) {
            Platform.runLater(() -> {
                SceneManager.showAlert(Alert.AlertType.ERROR, "Authentication Error", "No user logged in or unauthorized access. Please login again.");
                authController.clearLoggedInUser();
                try {
                    SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
                } catch (Exception e) {
                    System.err.println("Error switching to LoginScreen: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            return false;
        }

        try {
            Optional<User> userOptional = userDao.getUserById(this.userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                this.username = user.getFullName();
                this.userEmail = user.getEmail();
                System.out.println("User profile data loaded for: " + username);
                return true;
            } else {
                Platform.runLater(() -> {
                    SceneManager.showAlert(Alert.AlertType.ERROR, "Data Error", "Your user profile data could not be found in Firestore. Please contact support.");
                    authController.clearLoggedInUser();
                    try {
                        SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
                    } catch (Exception e) {
                        System.err.println("Error switching to LoginScreen: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                return false;
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error fetching user data from Firestore: " + e.getMessage());
            Platform.runLater(() -> {
                SceneManager.showAlert(Alert.AlertType.ERROR, "Data Load Error", "Failed to load user data. Please check your internet connection and try again.");
                authController.clearLoggedInUser();
                try {
                    SceneManager.switchScreen(new LoginScreen(), "LifeCompass - Role Selection");
                } catch (Exception ex) {
                    System.err.println("Error switching to LoginScreen: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Root Layout and Core UI Structure
    //------------------------------------------------------------------------------------------------------------------

    private StackPane createRootLayout() {
        VBox baseDashboardContent = new VBox(20);
        baseDashboardContent.setPadding(new Insets(20));
        baseDashboardContent.setStyle("-fx-background-color: #f9fafb;");

        HBox header = buildHeader();
        profileImageHeader = (ImageView) ((Button)((HBox)header.getChildren().get(0)).getChildren().get(0)).getGraphic();
        VBox leftTextContentVBox = (VBox) ((HBox)header.getChildren().get(0)).getChildren().get(1);
        if (leftTextContentVBox.getChildren().size() > 1) {
            welcomeLabelHeader = (Label) leftTextContentVBox.getChildren().get(1);
        }

        baseDashboardContent.getChildren().addAll(
                header,
                buildTabBar()
        );

        mainContentArea = new VBox(20);
        mainContentArea.setPadding(new Insets(0, 0, 20, 0));
        VBox.setVgrow(mainContentArea, Priority.ALWAYS);

        ScrollPane scrollableDashboard = new ScrollPane(new VBox(baseDashboardContent, mainContentArea));
        scrollableDashboard.setFitToWidth(true);
        scrollableDashboard.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableDashboard.setStyle("-fx-background: #f9fafb; -fx-border-insets: 0;");

        settingsPanelContainer = new StackPane();
        settingsPanelContainer.setAlignment(Pos.TOP_RIGHT);
        settingsPanelContainer.setManaged(false);
        settingsPanelContainer.setVisible(false);
        settingsPanelContainer.setStyle("-fx-background-color: transparent;");

        StackPane rootStack = new StackPane();
        rootStack.getChildren().addAll(scrollableDashboard, settingsPanelContainer);
        return rootStack;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Header Section (Profile, Welcome, Notifications, Settings)
    //------------------------------------------------------------------------------------------------------------------

    private HBox buildHeader() {
        profileImageHeader = createImageView("/assets/images/profile.png", 50, 50);
        Circle clip = new Circle(25, 25, 25);
        profileImageHeader.setClip(clip);

        Button profileButton = new Button();
        profileButton.setGraphic(profileImageHeader);
        profileButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        Tooltip.install(profileButton, new Tooltip("View Profile"));

        profileButton.setOnAction(event -> {
            if (profileScreen != null) {
                switchContent("Profile");
            } else {
                System.err.println("Error: ProfileScreen instance not initialized in UserDashboardScreen.start()!");
            }
        });
        setHoverStyle(profileButton, "-fx-background-color: #e0e0e0; -fx-padding: 0; -fx-background-radius: 25;",
                                     "-fx-background-color: transparent; -fx-padding: 0;");

        Label title = new Label("LifeCompass");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: black;");

        welcomeLabelHeader = new Label("Welcome back, " + getUsername() + "!");
        welcomeLabelHeader.setFont(Font.font(14));
        welcomeLabelHeader.setTextFill(Color.GRAY);

        VBox leftTextContent = new VBox(5, title, welcomeLabelHeader);
        leftTextContent.setAlignment(Pos.CENTER_LEFT);

        HBox leftSection = new HBox(10, profileButton, leftTextContent);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notificationButton = createButton("/assets/images/notification.png", "Notifications");
        notificationButton.setOnAction(event -> {
            if (notificationScreen != null) {
                notificationScreen.show();
            } else {
                System.err.println("Error: NotificationScreen instance not initialized!");
            }
        });

        Button settingButton = createIconButton("/assets/images/settings.png", "Settings");
        settingButton.setOnAction(event -> openSettingsPanel());

        HBox rightIcons = new HBox(15, notificationButton, settingButton);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(leftSection, spacer, rightIcons);
        header.setAlignment(Pos.CENTER_LEFT);

        Platform.runLater(this::updateHeaderProfileInfo);

        return header;
    }

    /**
     * Updates the welcome label and profile picture in the header based on the loaded user data.
     */
    private void updateHeaderProfileInfo() {
        if (welcomeLabelHeader != null && username != null) {
            welcomeLabelHeader.setText("Welcome back, " + username + "!");
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Sliding Settings Panel
    //------------------------------------------------------------------------------------------------------------------

    private void openSettingsPanel() {
        System.out.println("Opening Settings Panel...");

        settingsPanelContainer.getChildren().clear();

        if (settingsScreen != null) {
            VBox settingsPanelUI = settingsScreen.createSettingsPanel();
            settingsPanelContainer.getChildren().add(settingsPanelUI);
        } else {
            System.err.println("Error: SettingsScreen instance not initialized!");
            return;
        }

        settingsPanelContainer.setVisible(true);
        settingsPanelContainer.setManaged(true);

        TranslateTransition openTransition = new TranslateTransition(Duration.millis(300), settingsPanelContainer);
        openTransition.setToX(0);
        openTransition.play();
    }

    public void hideSettingsPanel() {
        System.out.println("Closing Settings Panel...");
        TranslateTransition closeTransition = new TranslateTransition(Duration.millis(300), settingsPanelContainer);
        closeTransition.setToX(settingsPanelContainer.prefWidthProperty().get());
        closeTransition.setOnFinished(event -> {
            settingsPanelContainer.setVisible(false);
            settingsPanelContainer.setManaged(false);
            settingsPanelContainer.getChildren().clear();
        });
        closeTransition.play();
    }

    //------------------------------------------------------------------------------------------------------------------
    // Tab Bar Navigation
    //------------------------------------------------------------------------------------------------------------------

    private HBox buildTabBar() {
        String[] tabs = {"Dashboard", "Mood", "Journal", "Analytics", "Chat", "Art", "Explore"};
        String[] imagePaths = {
                "/assets/images/home.png",
                "/assets/images/heart2.png",
                "/assets/images/journal.jpg",
                "/assets/images/analytics.png",
                "/assets/images/chats.png",
                "/assets/images/art.png",
                "/assets/images/explore.png"
        };

        HBox container = new HBox();
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #f3eeeee1; -fx-background-radius: 10;");
        container.setAlignment(Pos.CENTER);

        buttonStrip = new HBox(10);
        buttonStrip.setAlignment(Pos.CENTER);
        HBox.setHgrow(buttonStrip, Priority.ALWAYS);

        final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555;";
        final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;" +
                                   " -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 15;";
        final String hoverInactiveStyle = "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-background-radius: 10;";


        for (int i = 0; i < tabs.length; i++) {
            String tabName = tabs[i];
            String imagePath = imagePaths[i];

            ImageView iconView = createImageView(imagePath, 20, 20);

            Button btn = new Button(tabName, iconView);
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);
            buttonStrip.getChildren().add(btn);

            btn.setStyle(inactiveStyle);

            if (tabName.equals("Dashboard")) {
                btn.setStyle(activeStyle);
                activeTabButton = btn;
            }

            btn.setOnAction(event -> {
                String currentTabName = (activeTabButton != null) ? activeTabButton.getText() : "";
                if (currentTabName.equals("Chat") && cbtChatScreen != null) {
                    cbtChatScreen.stopChatServices();
                }

                if (activeTabButton != null) {
                    activeTabButton.setStyle(inactiveStyle);
                }
                btn.setStyle(activeStyle);
                activeTabButton = btn;

                switchContent(tabName);
                System.out.println("Clicked Tab: " + tabName);
            });

            setHoverStyle(btn, hoverInactiveStyle, inactiveStyle);
        }

        container.getChildren().add(buttonStrip);
        return container;
    }

    /**
     * Switches the content displayed in the `mainContentArea` based on the selected tab.
     */
    private void switchContent(String tabName) {
        mainContentArea.getChildren().clear();
        System.out.println("UserDashboardScreen: Switching content to: " + tabName);

        switch (tabName) {
            case "Dashboard":
                rebuildDashboardMetrics();
                break;
            case "Mood":
                if (moodScreen != null) {
                    mainContentArea.getChildren().add(moodScreen.createMoodScreenContent());
                    moodScreen.loadRecentMoodEntries();
                } else {
                    System.err.println("Mood Screen instance is null. Ensure it is initialized.");
                    mainContentArea.getChildren().add(new Label("Error: Mood Screen not initialized."));
                }
                break;
            case "Journal":
                if (journalScreen != null) {
                    mainContentArea.getChildren().add(journalScreen.createJournalScreenContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Journal Screen not initialized."));
                }
                break;
            case "Analytics":
                if (analyticsScreen != null) {
                    mainContentArea.getChildren().add(analyticsScreen.createAnalyticsDashboardContent());
                    if (AuthController.loggedInUserId != null) {
                        System.out.println("UserDashboardScreen: Calling refreshAnalyticsData for user: " + AuthController.loggedInUserId);
                        analyticsScreen.refreshAnalyticsData(AuthController.loggedInUserId, moodAnalyticsService);
                    } else {
                        System.err.println("UserDashboardScreen: No logged-in user for analytics. Cannot refresh.");
                    }
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Analytics Screen not initialized."));
                }
                break;
            case "Chat":
                if (cbtChatScreen != null) {
                    mainContentArea.getChildren().add(cbtChatScreen.createChatScreenContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Chat Screen not initialized."));
                }
                break;
            case "Art":
                if (artScreen != null) {
                    mainContentArea.getChildren().add(artScreen.createArtScreenContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Art Screen not initialized."));
                }
                break;
            case "Explore":
                if (exploreScreen != null) {
                    mainContentArea.getChildren().add(exploreScreen.createExploreScreenContent());
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Explore Screen not initialized."));
                }
                break;
            case "Profile":
                if (profileScreen != null) {
                    mainContentArea.getChildren().add(profileScreen);
                } else {
                    mainContentArea.getChildren().add(new Label("Error: Profile Screen not initialized."));
                }
                break;
            default:
                mainContentArea.getChildren().add(new Label("Content for '" + tabName + "' is not yet implemented."));
                break;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Dashboard Tab Content Sections
    //------------------------------------------------------------------------------------------------------------------

    private void rebuildDashboardMetrics() {
        mainContentArea.getChildren().clear();
        mainContentArea.getChildren().addAll(
            buildMetricsRow(),
            buildMoodAndActionsRow(),
            buildGoalsCard()
        );
        if (AuthController.loggedInUserId != null) {
            System.out.println("UserDashboardScreen: Rebuilding Dashboard Metrics - Refreshing Analytics for user: " + AuthController.loggedInUserId);
            analyticsScreen.refreshAnalyticsData(AuthController.loggedInUserId, moodAnalyticsService);
        }
    }

    public void updateJournalCount(int count) {
        Platform.runLater(() -> {
            this.currentJournalCount = count;
            journalCountLabel.setText("Journal Entries: " + count);
            if (activeTabButton != null && "Dashboard".equals(activeTabButton.getText())) {
                rebuildDashboardMetrics();
            }
            System.out.println("Dashboard metric updated: Journal entries: " + count);
        });
    }

    private HBox buildMetricsRow() {
        HBox hbox = new HBox(20);
        hbox.setPadding(new Insets(10, 0, 10, 0));
        hbox.setAlignment(Pos.CENTER);

        String[] titles = {"Current Streak", "Journal Entries", "Mood Score", "Badges Earned"};
        // FIX: Use dashboardMoodScoreProperty for the Mood Score value
        String[] values = {"7 days", String.valueOf(this.currentJournalCount), dashboardMoodScoreProperty.get(), "12"};
        String[] subtitles = {"Keep it up!", "Total entries", "This week average", "Achievement badges"};
        String[] images = {
                "/assets/images/trophy.png",
                "/assets/images/journal.jpg",
                "/assets/images/heart2.png",
                "/assets/images/trophy2.png"
        };

        for (int i = 0; i < titles.length; i++) {
            HBox card = createMetricCard(titles[i], values[i], subtitles[i], images[i]);
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
            hbox.getChildren().add(card);
        }
        return hbox;
    }

    private HBox createMetricCard(String title, String value, String subtitle, String imagePath) {
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font(12));
        subLbl.setTextFill(Color.GRAY);

        VBox textContent = new VBox(10, titleLbl, valueLbl, subLbl);
        textContent.setAlignment(Pos.TOP_LEFT);

        ImageView imageView = createImageView(imagePath, 24, 24);

        HBox cardBox = new HBox(10);
        cardBox.setPadding(new Insets(15));
        cardBox.setAlignment(Pos.TOP_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cardBox.getChildren().addAll(textContent, spacer, imageView);


        String originalStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;";
        cardBox.setStyle(originalStyle);
        cardBox.setPrefWidth(220);

        setHoverStyle(cardBox, "-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#d0d0d0; -fx-border-width: 2; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);",
                                originalStyle);
        return cardBox;
    }

    private HBox buildMoodAndActionsRow() {
        HBox hbox = new HBox(20);

        VBox moodSection = moodScreen.createRecentEntriesCardForDashboard();
        VBox actionsSection = buildQuickActions();

        HBox.setHgrow(actionsSection, Priority.ALWAYS);
        HBox.setHgrow(moodSection, Priority.ALWAYS);

        hbox.getChildren().addAll(moodSection, actionsSection);
        return hbox;
    }

    private HBox moodRow(String day, String mood, String score) {
        Circle circle = new Circle(12);
        circle.setFill(Color.rgb(128, 177, 226, 1));

        ImageView heartIcon = createImageView("/assets/images/heart2.png", 16, 16);
        StackPane iconContainer = new StackPane(circle, heartIcon);
        iconContainer.setAlignment(Pos.CENTER);

        Label dayLabel = new Label(day);
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        dayLabel.setTextFill(Color.GRAY);

        Label moodLabel = new Label(mood);
        moodLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        moodLabel.setTextFill(Color.BLACK);

        VBox dayMoodVBox = new VBox(0, dayLabel, moodLabel);
        dayMoodVBox.setAlignment(Pos.CENTER_LEFT);

        Label scoreLabel = new Label(score + "/10");
        scoreLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        final String scoreLabelOriginalStyle = "-fx-background-color: #d2d5d2; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5;";
        scoreLabel.setStyle(scoreLabelOriginalStyle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, iconContainer, dayMoodVBox, spacer, scoreLabel);
        row.setAlignment(Pos.CENTER_LEFT);

        String originalRowStyle = "-fx-background-color: transparent;";
        row.setStyle(originalRowStyle);

        setHoverStyle(row, "-fx-background-color: #f0f0f0; -fx-background-radius: 5;",
                                originalRowStyle);
        row.setOnMouseEntered(e -> scoreLabel.setStyle("-fx-background-color: #c2c5c2; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-border-radius: 10;"));
        row.setOnMouseExited(e -> scoreLabel.setStyle(scoreLabelOriginalStyle));

        return row;
    }

    private VBox buildQuickActions() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;");
        box.setPrefWidth(500);

        Label heading = new Label("Quick Actions");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label sub = new Label("Common tasks to support your wellness");
        sub.setFont(Font.font(12));
        sub.setTextFill(Color.GRAY);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        String[] actions = {"Log Mood", "Write Journal", "Find Psychologists", "Express Art"};
        String[] actionImagePaths = {
                "/assets/images/heart2.png",
                "/assets/images/journal.jpg",
                "/assets/images/psyprofile.png",
                "/assets/images/art.png"
        };

        for (int i = 0; i < actions.length; i++) {
            VBox actionMiniCard = createActionMiniCard(actions[i], actionImagePaths[i]);
            GridPane.setHgrow(actionMiniCard, Priority.ALWAYS);
            GridPane.setVgrow(actionMiniCard, Priority.ALWAYS);
            grid.add(actionMiniCard, i % 2, i / 2);
        }

        box.getChildren().addAll(heading, sub, grid);
        return box;
    }

    private VBox createActionMiniCard(String actionName, String imagePath) {
        VBox miniCard = new VBox(5);
        miniCard.setAlignment(Pos.CENTER);
        miniCard.setPadding(new Insets(15, 10, 15, 10));
        miniCard.setPrefSize(180, 100);

        String originalStyle = "-fx-background-color: white; " +
                               "-fx-background-radius: 8; " +
                               "-fx-border-color: #e0e0e0; " +
                               "-fx-border-width: 1; " +
                               "-fx-border-radius: 8; " +
                               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);";
        miniCard.setStyle(originalStyle);

        ImageView iconView = createImageView(imagePath, 28, 28);

        Label nameLabel = new Label(actionName);
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        nameLabel.setTextFill(Color.DARKSLATEGRAY);

        miniCard.getChildren().addAll(iconView, nameLabel);

        miniCard.setOnMouseClicked(event -> {
            System.out.println("Action '" + actionName + "' mini-card clicked!");
            switch (actionName) {
                case "Log Mood":
                    switchContent("Mood");
                    updateActiveTabButton("Mood");
                    break;
                case "Write Journal":
                    switchContent("Journal");
                    updateActiveTabButton("Journal");
                    break;
                case "Express Art":
                    switchContent("Art");
                    updateActiveTabButton("Art");
                    break;
                case "Find Psychologists":
                    switchContent("Explore");
                    updateActiveTabButton("Explore");
                    break;
                default:
                    System.out.println("Unhandled quick action: " + actionName);
                    break;
            }
        });

        setHoverStyle(miniCard, "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: #c0c0c0; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);",
                                originalStyle);
        return miniCard;
    }

    private void updateActiveTabButton(String tabName) {
        final String inactiveStyle = "-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #555555;";
        final String activeStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;" +
                                   " -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 15;";

        if (activeTabButton != null) {
            activeTabButton.setStyle(inactiveStyle);
        }

        if (buttonStrip != null) {
            for (Node node : buttonStrip.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn.getText().equals(tabName)) {
                        btn.setStyle(activeStyle);
                        activeTabButton = btn;
                        break;
                    }
                }
            }
        }
    }

    private VBox buildGoalsCard() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color:#e0e0e0; -fx-border-width: 2;");
        box.setPrefWidth(1020);

        Label heading = new Label("Today's Wellness Goals");
        heading.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label sub = new Label("Track your daily mental health activities");
        sub.setFont(Font.font(12));
        sub.setTextFill(Color.GRAY);

        box.getChildren().addAll(heading, sub);

        String[] goalTexts = {
                "Log your mood",
                "Write in journal",
                "Chat with CBT bot"
        };
        String[] goalImagePaths = {
                "/assets/images/heart2.png",
                "/assets/images/journal.jpg",
                "/assets/images/chats.png"
        };
        String[] goalBorderColors = {
                "#FF0000",
                "#008000",
                "#FFFF00"
        };
        boolean[] goalCompletedStatus = {true, false, false};

        for (int i = 0; i < goalTexts.length; i++) {
            box.getChildren().add(goalRow(
                    goalTexts[i],
                    goalImagePaths[i],
                    goalBorderColors[i],
                    goalCompletedStatus[i]
            ));
        }
        return box;
    }

    private HBox goalRow(String text, String imagePath, String borderColor, boolean completed) {
        ImageView iconView = createImageView(imagePath, 20, 20);
        StackPane iconContainer = new StackPane(iconView);
        iconContainer.setPrefSize(34, 34);
        String iconContainerOriginalStyle = "-fx-background-color: white; " +
                                            "-fx-background-radius: 5; " +
                                            "-fx-border-color: " + borderColor + "; " +
                                            "-fx-border-width: 2; " +
                                            "-fx-border-radius: 5; " +
                                            "-fx-padding: 3;";
        iconContainer.setStyle(iconContainerOriginalStyle);
        iconContainer.setAlignment(Pos.CENTER);

        Label goalTextLabel = new Label(text);
        goalTextLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        goalTextLabel.setTextFill(Color.GRAY);

        VBox textContentVBox = new VBox(goalTextLabel);
        textContentVBox.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label(completed ? "Completed" : "Pending");
        statusLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        final String statusOriginalStyle = "-fx-background-color: " + (completed ? "#d2d5d2" : "#f0f0f0") + "; " +
                                           "-fx-text-fill: black; " +
                                           "-fx-padding: 3 10; " +
                                           "-fx-background-radius: 10; " +
                                           "-fx-border-color:#e0e0e0; " +
                                           "-fx-border-width: 1; " +
                                           "-fx-border-radius: 10;";
        statusLabel.setStyle(statusOriginalStyle);


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, iconContainer, textContentVBox, spacer, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);

        String originalRowStyle = "-fx-background-color: transparent;";
        row.setStyle(originalRowStyle);

        setHoverStyle(row, "-fx-background-color: #f0f0f0; -fx-background-radius: 5;",
                                originalRowStyle);
        row.setOnMouseEntered(e -> statusLabel.setStyle("-fx-background-color: " + (completed ? "#c2c5c2" : "#e0e0e0") + "; -fx-text-fill: black; -fx-padding: 3 8; -fx-background-radius: 5; -fx-border-color:#d0d0d0; -fx-border-width: 1; -fx-border-radius: 10;"));
        row.setOnMouseExited(e -> statusLabel.setStyle(statusOriginalStyle));

        return row;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Data Update Methods (Public for external interaction, e.g., ProfileScreen)
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Updates the username in the dashboard header. This is called from ProfileScreen after a user updates their username.
     * @param newUsername The updated username.
     */
    public void updateUsername(String newUsername) {
        this.username = newUsername;
        if (welcomeLabelHeader != null) {
            welcomeLabelHeader.setText("Welcome back, " + this.username + "!");
        }
        System.out.println("Dashboard updated with new username: " + this.username);
    }

    /**
     * Updates the profile picture in the dashboard header. This is called from ProfileScreen.
     * @param newImageUrl The URL/path of the new profile picture.
     */
    public void updateProfilePicture(String newImageUrl) {
        if (profileImageHeader != null) {
            try {
                Image newImage = new Image(newImageUrl);
                if (!newImage.isError()) {
                    profileImageHeader.setImage(newImage);
                    System.out.println("Dashboard profile picture updated from: " + newImageUrl);
                } else {
                    System.err.println("Header: Failed to load profile image from URL: " + newImageUrl + " - " + newImage.exceptionProperty().get().getMessage());
                }
            } catch (Exception e) {
                System.err.println("Header: Exception loading profile image from URL: " + newImageUrl + " - " + e.getMessage());
            }
        }
    }

    /**
     * Provides access to the current username. This is important for other screens
     * like ProfileScreen to retrieve data from the dashboard.
     * @return The current username.
     */
    public String getUsername() {
        return username;
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods (Moved inline/duplicated for self-containment)
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