
package com.lifecompass.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
// No javafx.scene.web.WebView or WebEngine imports needed as articles open externally

import java.awt.Desktop; // Required for opening URLs in external browser
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Firebase and DAO imports for Psychologist data
import com.lifecompass.model.Psychologist;
import com.lifecompass.dao.impl.PsychologistDaoFirestoreImpl;

// Model imports
import com.lifecompass.model.MusicTrack;
import com.lifecompass.model.Article;
import com.lifecompass.model.Community;
import com.lifecompass.model.Activity;
import com.lifecompass.model.DailyChallenge;

// API Client imports
import com.lifecompass.api.ArticleApiClient;
import com.lifecompass.api.MusicApiClient;
import com.lifecompass.api.CommunityApiClient;
import com.lifecompass.api.ActivityApiClient;
import com.lifecompass.api.DailyChallengeApiClient;


public class Explore {

    // --- Styling Constants ---
    private static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
    private static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
    private static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
    private static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
    private static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
    private static final String TAG_STYLE_OUTLINE = "-fx-background-color: #e0e0e0; -fx-padding: 5px 10px; -fx-background-radius: 15px; -fx-font-size: 12px; "
            + TEXT_COLOR_DARK_GREY;
    private static final String ACTIVE_TAB_BUTTON_STYLE = BACKGROUND_COLOR_WHITE
            + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 1px; -fx-border-radius: 5px 5px 0 0; "
            + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;";
    private static final String INACTIVE_TAB_BUTTON_STYLE = BACKGROUND_COLOR_LIGHT_GREY
            + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 0; -fx-border-radius: 0 5px 0 0; "
            + TEXT_COLOR_GREY + ";-fx-cursor: hand;";
    private static final String DIFFICULTY_EASY_STYLE = "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 3px 8px; -fx-background-radius: 5px; -fx-font-size: 10px;";
    private static final String DIFFICULTY_MEDIUM_STYLE = "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 3px 8px; -fx-background-radius: 5px; -fx-font-size: 10px;";
    private static final String DIFFICULTY_HARD_STYLE = "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-padding: 3px 8px; -fx-background-radius: 5px; -fx-font-size: 10px;";
    private static final String STATUS_ACTIVE_STYLE = "-fx-background-color: #d4edda; -fx-background-radius: 50%; -fx-pref-width: 8px; -fx-pref-height: 8px;";
    private static final String STATUS_QUIET_STYLE = "-fx-background-color: #f8d7da; -fx-background-radius: 50%; -fx-pref-width: 8px; -fx-pref-height: 8px;";

    // --- Global MediaPlayer and UI Elements for Music Control ---
    private MediaPlayer currentMediaPlayer;
    private Slider volumeSlider; // Global volume slider
    private Label currentlyPlayingTitleLabel; // To show current track title

    // --- Data Lists (now populated from APIs or dummy data) ---
    private List<MusicTrack> therapeuticMusicTracks = new ArrayList<>();
    private List<Article> featuredArticles = new ArrayList<>();
    private List<String> popularTopics = new ArrayList<>();
    private List<Community> communities = new ArrayList<>();
    private List<String> communityGuidelines = new ArrayList<>();
    private List<Activity> featuredActivities = new ArrayList<>();
    private List<DailyChallenge> dailyChallenges = new ArrayList<>();

    // --- Lists for holding Psychologist data (Firebase/Firestore) ---
    private List<Psychologist> sessionHistoryPsychologists = new ArrayList<>();
    private List<Psychologist> explorePsychologists = new ArrayList<>();

    // Display counts for Psychologist sections
    private final int INITIAL_DISPLAY_COUNT = 3;
    private int currentDisplayedHistoryCount = INITIAL_DISPLAY_COUNT;
    private int currentDisplayedExploreCount = INITIAL_DISPLAY_COUNT;

    // UI containers for Psychologist sections
    private VBox sessionHistoryCardsContainer;
    private VBox explorePsychologistCardsContainer;

    // "View More/Less" buttons for Psychologist sections
    private Button viewMoreHistoryButton;
    private Button viewLessHistoryButton;
    private Button viewMoreExploreButton;
    private Button viewLessExploreButton;

    // --- UI Elements for dynamic content switching ---
    private Label musicTabBtn;
    private Label articlesTabBtn;
    private Label psychologistsTabBtn;
    private Label communityTabBtn;
    private Label activitiesTabBtn;
    private VBox contentDisplayArea;

    // --- References from Dashboard ---
    private Stage primaryStage;
    private UserDashboardScreen dashboardInstance;

    // DAO for fetching Psychologist data from Firestore
    private final PsychologistDaoFirestoreImpl psychologistDao = new PsychologistDaoFirestoreImpl();

    // API clients for various sections
    private final ArticleApiClient articleApiClient = new ArticleApiClient();
    private final MusicApiClient musicApiClient = new MusicApiClient(); // Now serves local files
    private final CommunityApiClient communityApiClient = new CommunityApiClient(); // Dummy data
    private final ActivityApiClient activityApiClient = new ActivityApiClient(); // Dummy data
    private final DailyChallengeApiClient dailyChallengeApiClient = new DailyChallengeApiClient(); // Daily challenges from Gemini API


    /**
     * Constructor for the Explore component.
     * Initializes UI and starts data loading processes.
     */
    public Explore(Stage primaryStage, UserDashboardScreen dashboardInstance) {
        this.primaryStage = primaryStage;
        this.dashboardInstance = dashboardInstance;

        // Initialize global UI elements related to music playback
        volumeSlider = new Slider(0, 1, 0.5); // Min, Max, Initial (50%)
        volumeSlider.setPrefWidth(150);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMediaPlayer != null) {
                currentMediaPlayer.setVolume(newVal.doubleValue());
            }
        });

        currentlyPlayingTitleLabel = new Label("No Track Playing");
        currentlyPlayingTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        currentlyPlayingTitleLabel.setStyle(TEXT_COLOR_DARK_GREY);

        // 1. Load initial dummy data immediately to populate UI and prevent blank screens.
        loadInitialDummyData();
        // 2. Load Psychologist data from Firebase (asynchronous, independent of other APIs)
        loadPsychologistsFromFirebase();
        // 3. Load all other data from various APIs (asynchronous)
        loadAllDataFromAPIs();
    }

    /**
     * Loads initial dummy data to ensure UI is not empty before asynchronous API calls complete.
     * These will be replaced by actual data once API calls return.
     */
    private void loadInitialDummyData() {
        // Dummy data for Articles
        featuredArticles.addAll(Arrays.asList(
                new Article("Loading...", "Fetching Articles...", "Please Wait", "about:blank", null, "N/A"),
                new Article("Loading...", "Content Loading...", "Team LifeCompass", "about:blank", null, "N/A"),
                new Article("Loading...", "Discovering Insights...", "Data Fetching", "about:blank", null, "N/A")
        ));

        // Dummy data for Music Tracks (local paths)
        // IMPORTANT: Ensure these paths exist in your src/main/resources/assets/audio/ directory
        // Example: src/main/resources/assets/audio/peaceful_morning.mp3
        therapeuticMusicTracks.addAll(Arrays.asList(
                new MusicTrack("temp-music-1", "Loading Music Track", "Fetching from server...", "0:00", "Calm", "/assets/audio/peaceful_morning.mp3"), // Placeholder, replace with actual audio file
                new MusicTrack("temp-music-2", "Music Incoming", "Please wait...", "0:00", "Relaxing", "/assets/audio/anxiety_relief.mp3") // Placeholder, replace with actual audio file
        ));

        // Dummy data for Communities
        communities.addAll(Arrays.asList(
                new Community("temp-comm-1", "Loading Communities...", 0, "Quiet", "Connecting you to supportive groups."),
                new Community("temp-comm-2", "Community Updates...", 0, "Quiet", "Discover shared experiences and discussions.")
        ));

        // Dummy data for Activities
        featuredActivities.addAll(Arrays.asList(
                new Activity("temp-act-1", "Loading Activity", "Easy", "0 min", 0),
                new Activity("temp-act-2", "Activity Incoming", "Medium", "0 min", 0)
        ));

        // Dummy data for Daily Challenges
        dailyChallenges.addAll(Arrays.asList(
                new DailyChallenge("temp-dc-1", "Loading daily challenges...", 0, false),
                new DailyChallenge("temp-dc-2", "New Challenge Arriving...", 0, false)
        ));

        popularTopics.addAll(Arrays.asList(
                "Anxiety Management", "Depression Support", "Mindfulness", "Sleep Health",
                "Stress Relief", "Self-Care", "Relationships", "Work-Life Balance"));

        communityGuidelines.addAll(Arrays.asList(
                "All conversations are anonymous and confidential",
                "Be respectful and supportive of others' experiences",
                "No medical advice - share experiences and coping strategies only",
                "Report any concerning content to moderators immediately"));

        System.out.println("Loaded initial dummy data.");
    }

    /**
     * Loads Psychologist data from Firebase Firestore. This runs asynchronously.
     */
    private void loadPsychologistsFromFirebase() {
        System.out.println("Fetching psychologists from Firebase...");
        sessionHistoryPsychologists.clear();
        explorePsychologists.clear();

        try {
            List<Psychologist> allPsychologists = psychologistDao.getAllPsychologists();

            if (allPsychologists != null && !allPsychologists.isEmpty()) {
                int splitPoint = Math.min(3, allPsychologists.size());
                for (int i = 0; i < allPsychologists.size(); i++) {
                    if (i < splitPoint) {
                        sessionHistoryPsychologists.add(allPsychologists.get(i));
                    } else {
                        explorePsychologists.add(allPsychologists.get(i));
                    }
                }
                System.out.println("Successfully loaded " + allPsychologists.size() + " psychologists from Firebase.");
            } else {
                System.out.println("No psychologists found in Firebase. Loading dummy data.");
                loadDummyPsychologists();
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error loading psychologists from Firebase: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Loading dummy psychologist data as fallback.");
            loadDummyPsychologists();
        }

        Platform.runLater(() -> {
            refreshSessionHistoryDisplay();
            refreshExplorePsychologistDisplay();
        });
    }

    /**
     * Fallback method to load dummy psychologist data if Firebase fetching fails or is empty.
     */
    private void loadDummyPsychologists() {
        sessionHistoryPsychologists.add(new Psychologist("dummy_psy1", "Dummy Michael Chen", "Licensed Clinical Psychologist", Arrays.asList("CBT", "Trauma"), 8, "https://placehold.co/60x60/E0BBE4/FFFFFF?text=MC"));
        sessionHistoryPsychologists.add(new Psychologist("dummy_psy2", "Dummy Sarah Williams", "Licensed Marriage & Family Psychologist", Arrays.asList("Family Therapy", "Adolescent"), 12, "https://placehold.co/60x60/E0BBE4/FFFFFF?text=SW"));

        explorePsychologists.add(new Psychologist("dummy_psy3", "Dummy David Lee", "Addiction Specialist", Arrays.asList("Addiction Counseling", "Group Therapy"), 10, "https://placehold.co/60x60/E0BBE4/FFFFFF?text=DL"));
        explorePsychologists.add(new Psychologist("dummy_psy4", "Dummy Olivia Green", "Trauma Therapist", Arrays.asList("PTSD", "EMDR"), 7, "https://placehold.co/60x60/E0BBE4/FFFFFF?text=OG"));
        explorePsychologists.add(new Psychologist("dummy_psy5", "Dummy Chris Redfield", "Cognitive Behavioral Therapist", Arrays.asList("Anxiety", "Depression", "CBT"), 6, "https://placehold.co/60x60/E0BBE4/FFFFFF?text=CR"));
        System.out.println("Loaded dummy psychologist data.");
    }

    /**
     * Loads all data from various APIs (or dummy data for categories without real external APIs).
     */
    private void loadAllDataFromAPIs() {
        // Define a comprehensive query for mental health articles
        String mentalHealthQuery = "mental health OR wellness OR anxiety OR depression OR mindfulness OR therapy OR psychologist OR psychiatrist OR self-care OR emotional wellbeing";

        // Load Articles from NewsAPI.org using the updated ArticleApiClient
        articleApiClient.fetchArticles(mentalHealthQuery)
                .thenAccept(articles -> Platform.runLater(() -> {
                    if (articles != null && !articles.isEmpty()) {
                        featuredArticles.clear();
                        featuredArticles.addAll(articles);
                        System.out.println("Successfully loaded " + articles.size() + " articles from NewsAPI.org.");
                        // If the Articles tab is currently active, refresh its content
                        if (articlesTabBtn != null && articlesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createArticlesContentArea());
                        }
                    } else {
                        System.out.println("No articles found from NewsAPI.org. Loading dummy data.");
                        loadDummyArticles();
                        if (articlesTabBtn != null && articlesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createArticlesContentArea());
                        }
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error loading articles from NewsAPI.org: " + ex.getMessage());
                        loadDummyArticles();
                        if (articlesTabBtn != null && articlesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createArticlesContentArea());
                        }
                    });
                    return null;
                });

        // Load Music Tracks (from local files via simplified MusicApiClient)
        musicApiClient.fetchMusicTracks("meditation music") // Query is just for logging here
                .thenAccept(musicTracks -> Platform.runLater(() -> {
                    if (musicTracks != null && !musicTracks.isEmpty()) {
                        therapeuticMusicTracks.clear();
                        therapeuticMusicTracks.addAll(musicTracks);
                        System.out.println("Successfully loaded " + musicTracks.size() + " music tracks (local files).");
                        if (musicTabBtn != null && musicTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createMusicContentArea());
                        }
                    } else {
                        System.out.println("No music tracks loaded (local files). Loading dummy data.");
                        loadDummyMusicTracks();
                        if (musicTabBtn != null && musicTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createMusicContentArea());
                        }
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error loading music tracks (local files): " + ex.getMessage());
                        loadDummyMusicTracks();
                        if (musicTabBtn != null && musicTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createMusicContentArea());
                        }
                    });
                    return null;
                });

        // Load Communities (using dummy data)
        communityApiClient.fetchCommunities()
                .thenAccept(communitiesList -> Platform.runLater(() -> {
                    if (communitiesList != null && !communitiesList.isEmpty()) {
                        communities.clear();
                        communities.addAll(communitiesList);
                        System.out.println("Successfully loaded " + communitiesList.size() + " communities (from dummy source).");
                        if (communityTabBtn != null && communityTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createCommunityContentArea());
                        }
                    } else {
                        System.out.println("No communities loaded. Using dummy data as fallback.");
                        loadDummyCommunities();
                        if (communityTabBtn != null && communityTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createCommunityContentArea());
                        }
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error loading communities: " + ex.getMessage());
                        loadDummyCommunities();
                        if (communityTabBtn != null && communityTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createCommunityContentArea());
                        }
                    });
                    return null;
                });

        // Load Activities (using dummy data)
        activityApiClient.fetchActivities()
                .thenAccept(activitiesList -> Platform.runLater(() -> {
                    if (activitiesList != null && !activitiesList.isEmpty()) {
                        featuredActivities.clear();
                        featuredActivities.addAll(activitiesList);
                        System.out.println("Successfully loaded " + activitiesList.size() + " activities (from dummy source).");
                        if (activitiesTabBtn != null && activitiesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                        }
                    } else {
                        System.out.println("No activities loaded. Using dummy data as fallback.");
                        loadDummyActivities();
                        if (activitiesTabBtn != null && activitiesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                        }
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error loading activities: " + ex.getMessage());
                        loadDummyActivities();
                        if (activitiesTabBtn != null && activitiesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                        }
                    });
                    return null;
                });

        // Load Daily Challenges (using Gemini API)
        dailyChallengeApiClient.fetchDailyChallenges("for overcoming procrastination") // Example: request a challenge for procrastination
                .thenAccept(challengesList -> Platform.runLater(() -> {
                    if (challengesList != null && !challengesList.isEmpty()) {
                        dailyChallenges.clear();
                        dailyChallenges.addAll(challengesList);
                        System.out.println("Successfully loaded " + challengesList.size() + " daily challenges (from Gemini API).");
                    } else {
                        System.out.println("No daily challenges loaded from Gemini API. Using dummy data as fallback.");
                        loadDummyDailyChallenges();
                    }
                    if (activitiesTabBtn != null && activitiesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                        contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error loading daily challenges from Gemini API: " + ex.getMessage());
                        loadDummyDailyChallenges();
                        if (activitiesTabBtn != null && activitiesTabBtn.getStyle().contains(ACTIVE_TAB_BUTTON_STYLE)) {
                            contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                        }
                    });
                    return null;
                });
    }

    /**
     * Fallback method to load dummy article data.
     */
    private void loadDummyArticles() {
        featuredArticles.clear();
        featuredArticles.addAll(Arrays.asList(
                new Article("dummy_art1", "Mental Health", "Understanding Anxiety: A Beginner's Guide (Dummy)", "Dr. Sarah Johnson", "5 min read",
                        "Learn about the basics of anxiety, its symptoms, and effective coping strategies. (Dummy Data)"),
                new Article("dummy_art2", "Mindfulness", "10 Mindfulness Exercises for Daily Practice (Dummy)", "Michael Chen", "7 min read",
                        "Simple mindfulness techniques you can incorporate into your daily routine. (Dummy Data)"),
                new Article("dummy_art3", "Research", "The Science of Gratitude and Mental Health (Dummy)", "Dr. Emily Rodriguez", "6 min read",
                        "Discover how practicing gratitude can improve your mental well-being. (Dummy Data)")
        ));
        System.out.println("Loaded dummy article data.");
    }

    /**
     * Fallback method to load dummy music track data.
     */
    private void loadDummyMusicTracks() {
        therapeuticMusicTracks.clear();
        therapeuticMusicTracks.addAll(Arrays.asList(
                new MusicTrack("dummy_m1", "Peaceful Morning (Dummy)", "Nature Sounds", "5:30", "Calm", "/assets/audio/peaceful_morning.mp3"),
                new MusicTrack("dummy_m2", "Anxiety Relief (Dummy)", "Meditation Music", "8:15", "Relaxing", "/assets/audio/anxiety_relief.mp3")
        ));
        System.out.println("Loaded dummy music track data.");
    }

    /**
     * Fallback method to load dummy community data.
     */
    private void loadDummyCommunities() {
        communities.clear();
        communities.addAll(Arrays.asList(
                new Community("dummy_c1", "Anxiety Support Circle (Dummy)", 1247, "Active",
                        "A safe space to share experiences and coping strategies for anxiety. (Dummy)"),
                new Community("dummy_c2", "Mindfulness Practitioners (Dummy)", 892, "Active",
                        "Share mindfulness techniques and meditation experiences. (Dummy)")
        ));
        System.out.println("Loaded dummy community data.");
    }

    /**
     * Fallback method to load dummy activity data.
     */
    private void loadDummyActivities() {
        featuredActivities.clear();
        featuredActivities.addAll(Arrays.asList(
                new Activity("dummy_a1", "Breathing Bubble (Dummy)", "Easy", "3-5 min", 10),
                new Activity("dummy_a2", "Gratitude Garden (Dummy)", "Easy", "5-10 min", 10)
        ));
        System.out.println("Loaded dummy activity data.");
    }

    /**
     * Fallback method to load dummy daily challenge data.
     */
    private void loadDummyDailyChallenges() {
        dailyChallenges.clear();
        dailyChallenges.addAll(Arrays.asList(
                new DailyChallenge("dummy_dc1", "Practice 5-minute breathing exercise (Dummy)", 15, false),
                new DailyChallenge("dummy_dc2", "Write 3 things you're grateful for (Dummy)", 10, false)
        ));
        System.out.println("Loaded dummy daily challenge data.");
    }


    /**
     * Creates and returns the entire UI content for the Explore screen.
     */
    public VBox createExploreScreenContent() {
        VBox exploreContentLayout = new VBox(20);
        exploreContentLayout.setPadding(new Insets(20));
        exploreContentLayout.setAlignment(Pos.TOP_CENTER);
        exploreContentLayout.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        exploreContentLayout.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(exploreContentLayout, Priority.ALWAYS);

        // Explore Wellness Resources Header
        Label title = new Label("Explore Wellness Resources");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setStyle(TEXT_COLOR_DARK_GREY);
        title.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(title, Priority.NEVER);

        Label description = new Label(
                "Discover music, articles, psychologists, and activities to enhance your mental wellness journey");
        description.setFont(Font.font("Arial", 13));
        description.setStyle(TEXT_COLOR_GREY);
        description.setWrapText(true);
        description.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(description, Priority.NEVER);

        // Search Bar
        StackPane searchInputContainer = new StackPane();
        TextField searchField = new TextField();
        searchField.setPromptText("Search resources, topics, or communities...");
        searchField.setStyle(
                "-fx-control-inner-background: #ffffff; -fx-background-radius: 5px; -fx-border-color: #d0d0d0; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-padding: 8px 8px 8px 30px;");
        searchField.setPrefHeight(38);
        searchField.setMaxWidth(Double.MAX_VALUE);

        Label searchIcon = new Label("\uD83D\uDD0D");
        searchIcon.setFont(Font.font("Arial", 16));
        searchIcon.setStyle(TEXT_COLOR_GREY);
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(searchIcon, new Insets(0, 0, 0, 10));
        searchInputContainer.getChildren().addAll(searchField, searchIcon);
        searchInputContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(searchInputContainer, Priority.ALWAYS);

        // Category Tabs (Music, Articles, Psychologists, Community, Activities)
        HBox categoryTabs = new HBox(0);
        categoryTabs.setAlignment(Pos.CENTER_LEFT);
        categoryTabs.setPadding(new Insets(0));
        categoryTabs.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(categoryTabs, Priority.ALWAYS);

        musicTabBtn = createCategoryTabButton("Music", "\uD83C\uDFB5", true);
        articlesTabBtn = createCategoryTabButton("Articles", "\uD83D\uDCD6", false);
        psychologistsTabBtn = createCategoryTabButton("Psychologists", "\uD83D\uDC64", false);
        communityTabBtn = createCategoryTabButton("Community", "\uD83D\uDC65", false);
        activitiesTabBtn = createCategoryTabButton("Activities", "\uD83C\uDFC3", false);

        HBox.setHgrow(musicTabBtn, Priority.ALWAYS);
        HBox.setHgrow(articlesTabBtn, Priority.ALWAYS);
        HBox.setHgrow(psychologistsTabBtn, Priority.ALWAYS);
        HBox.setHgrow(communityTabBtn, Priority.ALWAYS);
        HBox.setHgrow(activitiesTabBtn, Priority.ALWAYS);

        categoryTabs.getChildren().addAll(musicTabBtn, articlesTabBtn, psychologistsTabBtn, communityTabBtn, activitiesTabBtn);

        // Content Area for selected category (initially Music)
        contentDisplayArea = new VBox();
        VBox.setVgrow(contentDisplayArea, Priority.ALWAYS);
        contentDisplayArea.setMaxWidth(Double.MAX_VALUE);

        // Initial content set to Music as it's the default active tab
        contentDisplayArea.getChildren().setAll(createMusicContentArea());

        // Tab switching logic
        musicTabBtn.setOnMouseClicked(e -> {
            updateCategoryTabStyles(musicTabBtn, articlesTabBtn, psychologistsTabBtn, communityTabBtn, activitiesTabBtn);
            // Stop any currently playing music when switching tabs
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
            }

            if (therapeuticMusicTracks.isEmpty() || therapeuticMusicTracks.get(0).getTitle().contains("Loading")) {
                Label loadingLabel = new Label("Loading music tracks...");
                loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                contentDisplayArea.getChildren().setAll(new StackPane(loadingLabel));
                musicApiClient.fetchMusicTracks("meditation music")
                    .thenAccept(tracks -> Platform.runLater(() -> {
                        if (tracks != null && !tracks.isEmpty()) {
                            therapeuticMusicTracks.clear();
                            therapeuticMusicTracks.addAll(tracks);
                        } else {
                            loadDummyMusicTracks();
                        }
                        contentDisplayArea.getChildren().setAll(createMusicContentArea());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            System.err.println("Error re-loading music tracks: " + ex.getMessage());
                            loadDummyMusicTracks();
                            contentDisplayArea.getChildren().setAll(createMusicContentArea());
                        });
                        return null;
                    });
            } else {
                contentDisplayArea.getChildren().setAll(createMusicContentArea());
            }
        });

        articlesTabBtn.setOnMouseClicked(e -> {
            updateCategoryTabStyles(articlesTabBtn, musicTabBtn, psychologistsTabBtn, communityTabBtn, activitiesTabBtn);
            // Stop any currently playing music when switching tabs
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
            }

            // Define a comprehensive query for mental health articles
            String mentalHealthQuery = "mental health OR wellness OR anxiety OR depression OR mindfulness OR therapy OR psychologist OR psychiatrist OR self-care OR emotional wellbeing";

            if (featuredArticles.isEmpty() || featuredArticles.get(0).getTitle().contains("Loading")) {
                Label loadingLabel = new Label("Loading articles...");
                loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                contentDisplayArea.getChildren().setAll(new StackPane(loadingLabel));
                articleApiClient.fetchArticles(mentalHealthQuery)
                    .thenAccept(articles -> Platform.runLater(() -> {
                        if (articles != null && !articles.isEmpty()) {
                            featuredArticles.clear();
                            featuredArticles.addAll(articles);
                        } else {
                            loadDummyArticles();
                        }
                        contentDisplayArea.getChildren().setAll(createArticlesContentArea());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            System.err.println("Error re-loading articles: " + ex.getMessage());
                            loadDummyArticles();
                            contentDisplayArea.getChildren().setAll(createArticlesContentArea());
                        });
                        return null;
                    });
            } else {
                contentDisplayArea.getChildren().setAll(createArticlesContentArea());
            }
        });

        psychologistsTabBtn.setOnMouseClicked(e -> {
            updateCategoryTabStyles(psychologistsTabBtn, musicTabBtn, articlesTabBtn, communityTabBtn, activitiesTabBtn);
            // Stop any currently playing music when switching tabs
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
            }
            contentDisplayArea.getChildren().setAll(createPsychologistContentArea());
        });

        communityTabBtn.setOnMouseClicked(e -> {
            updateCategoryTabStyles(communityTabBtn, musicTabBtn, articlesTabBtn, psychologistsTabBtn, activitiesTabBtn);
            // Stop any currently playing music when switching tabs
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
            }

            if (communities.isEmpty() || communities.get(0).getName().contains("Loading")) {
                Label loadingLabel = new Label("Loading communities...");
                loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                contentDisplayArea.getChildren().setAll(new StackPane(loadingLabel));
                communityApiClient.fetchCommunities()
                    .thenAccept(communitiesList -> Platform.runLater(() -> {
                        if (communitiesList != null && !communitiesList.isEmpty()) {
                            communities.clear();
                            communities.addAll(communitiesList);
                        } else {
                            loadDummyCommunities();
                        }
                        contentDisplayArea.getChildren().setAll(createCommunityContentArea());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            System.err.println("Error re-loading communities: " + ex.getMessage());
                            loadDummyCommunities();
                            contentDisplayArea.getChildren().setAll(createCommunityContentArea());
                        });
                        return null;
                    });
            } else {
                contentDisplayArea.getChildren().setAll(createCommunityContentArea());
            }
        });

        activitiesTabBtn.setOnMouseClicked(e -> {
            updateCategoryTabStyles(activitiesTabBtn, musicTabBtn, articlesTabBtn, psychologistsTabBtn, communityTabBtn);
            // Stop any currently playing music when switching tabs
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
            }

            if (featuredActivities.isEmpty() || featuredActivities.get(0).getName().contains("Loading") ||
                dailyChallenges.isEmpty() || dailyChallenges.get(0).getDescription().contains("Loading")) {

                Label loadingLabel = new Label("Loading activities & challenges...");
                loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                contentDisplayArea.getChildren().setAll(new StackPane(loadingLabel));

                CompletableFuture.allOf(
                    activityApiClient.fetchActivities().thenAccept(activitiesList -> {
                        Platform.runLater(() -> {
                            if (activitiesList != null && !activitiesList.isEmpty()) {
                                featuredActivities.clear();
                                featuredActivities.addAll(activitiesList);
                            } else {
                                loadDummyActivities();
                            }
                        });
                    }),
                    dailyChallengeApiClient.fetchDailyChallenges("for a positive mindset") // Call Gemini for challenges
                        .thenAccept(challengesList -> {
                            Platform.runLater(() -> {
                                if (challengesList != null && !challengesList.isEmpty()) {
                                    dailyChallenges.clear();
                                    dailyChallenges.addAll(challengesList);
                                    System.out.println("Successfully loaded " + challengesList.size() + " daily challenges (from Gemini API).");
                                } else {
                                    System.out.println("No daily challenges loaded from Gemini API. Using dummy data as fallback.");
                                    loadDummyDailyChallenges();
                                }
                            });
                        })
                ).thenRun(() -> Platform.runLater(() -> contentDisplayArea.getChildren().setAll(createActivitiesContentArea())))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Error re-loading activities/challenges: " + ex.getMessage());
                        loadDummyActivities();
                        loadDummyDailyChallenges();
                        contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
                    });
                    return null;
                });
            } else {
                contentDisplayArea.getChildren().setAll(createActivitiesContentArea());
            }
        });

        exploreContentLayout.getChildren().addAll(
                title,
                description,
                searchInputContainer,
                categoryTabs,
                contentDisplayArea,
                createPlayerControls() // Add the global player controls at the bottom
        );

        // Ensure MediaPlayer resources are released if the main stage is closed
        // This assumes UserDashboardScreen handles the primary stage closure for the entire app.
        // If 'Explore' itself controls the main app lifecycle, this should be in an Application.stop() method.
        // For now, it's good to keep it for robustness.
        primaryStage.setOnCloseRequest(event -> {
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                System.out.println("MediaPlayer disposed on primary stage close.");
            }
        });

        return exploreContentLayout;
    }

    /**
     * Helper to create a standardized category tab button.
     */
    private Label createCategoryTabButton(String text, String iconUnicode, boolean isActive) {
        Label tabButton = new Label(iconUnicode + " " + text);
        tabButton.setFont(Font.font("Arial", 14));
        tabButton.setPadding(new Insets(10, 20, 10, 20));
        tabButton.setMaxWidth(Double.MAX_VALUE);
        tabButton.setAlignment(Pos.CENTER);
        tabButton.setStyle(isActive ? ACTIVE_TAB_BUTTON_STYLE : INACTIVE_TAB_BUTTON_STYLE);
        return tabButton;
    }

    /**
     * Updates the styles of category tab buttons to reflect the active tab.
     */
    private void updateCategoryTabStyles(Label activeTab, Label... otherTabs) {
        activeTab.setStyle(ACTIVE_TAB_BUTTON_STYLE);
        for (Label tab : otherTabs) {
            tab.setStyle(INACTIVE_TAB_BUTTON_STYLE);
        }
    }

    // --- Content Area Creation Methods for Each Category ---

    private VBox createMusicContentArea() {
        VBox musicContent = new VBox(20);
        musicContent.setPadding(new Insets(20));
        musicContent.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT
                + "-fx-border-width: 0 1px 1px 1px; -fx-border-radius: 0 5px 5px 5px;");
        musicContent.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(musicContent, Priority.ALWAYS);

        Label therapeuticMusicTitle = new Label("Therapeutic Music");
        therapeuticMusicTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        therapeuticMusicTitle.setStyle(TEXT_COLOR_DARK_GREY);

        Label therapeuticMusicDesc = new Label("Curated music tracks designed to support different emotional states");
        therapeuticMusicDesc.setFont(Font.font("Arial", 12));
        therapeuticMusicDesc.setStyle(TEXT_COLOR_GREY);

        VBox musicList = new VBox(10);
        for (MusicTrack track : therapeuticMusicTracks) {
            musicList.getChildren().add(createMusicTrackItem(track));
        }

        Label moodBasedPlaylistsTitle = new Label("Mood-Based Playlists");
        moodBasedPlaylistsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        moodBasedPlaylistsTitle.setStyle(TEXT_COLOR_DARK_GREY);
        VBox.setMargin(moodBasedPlaylistsTitle, new Insets(20, 0, 0, 0));

        GridPane playlistsGrid = new GridPane();
        playlistsGrid.setHgap(15);
        playlistsGrid.setVgap(15);
        playlistsGrid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints colConstraint = new ColumnConstraints();
        colConstraint.setHgrow(Priority.ALWAYS);
        colConstraint.setPercentWidth(100.0 / 4);

        for (int i = 0; i < 4; i++) {
            playlistsGrid.getColumnConstraints().add(colConstraint);
        }

        playlistsGrid.add(createMoodPlaylistButton("Calm & Peaceful", Color.web("#6a5acd")), 0, 0);
        playlistsGrid.add(createMoodPlaylistButton("Energy Boost", Color.web("#6a5acd")), 1, 0);
        playlistsGrid.add(createMoodPlaylistButton("Focus & Concentration", Color.web("#6a5acd")), 2, 0);
        playlistsGrid.add(createMoodPlaylistButton("Sleep & Relaxation", Color.web("#6a5acd")), 3, 0);

        for (Node node : playlistsGrid.getChildren()) {
            if (node instanceof VBox) {
                ((VBox) node).setMaxWidth(Double.MAX_VALUE);
                ((VBox) node).setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(node, Priority.ALWAYS);
                GridPane.setVgrow(node, Priority.ALWAYS);
            }
        }

        musicContent.getChildren().addAll(
                therapeuticMusicTitle,
                therapeuticMusicDesc,
                musicList,
                moodBasedPlaylistsTitle,
                playlistsGrid);

        return musicContent;
    }

    private HBox createMusicTrackItem(MusicTrack track) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 0, 10, 0));
        item.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1px 0;");

        // --- UPDATED MUSIC PLAYBACK LOGIC ---

        // Play/Pause Icon (toggles playback for this track item)
        Label playPauseIcon = new Label("\u25B6"); // Initial icon: Play (►)
        playPauseIcon.setFont(Font.font("Arial", 18));
        playPauseIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");

        // Stop Icon (explicitly stops playback for this track item)
        Label stopIcon = new Label("\u23F9"); // Stop symbol (■)
        stopIcon.setFont(Font.font("Arial", 18));
        stopIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");

        // Set initial icon based on whether this track is currently playing
        if (currentMediaPlayer != null &&
            currentMediaPlayer.getMedia().getSource().equals(
                getClass().getResource(track.getAudioUrl()).toExternalForm()
            ) && currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            playPauseIcon.setText("\u23F8"); // Set to Pause if currently playing this track
        }


        playPauseIcon.setOnMouseClicked(e -> {
            String trackUrl = track.getAudioUrl();
            URL audioUrl = getClass().getResource(trackUrl); // Get URL for this track

            if (audioUrl == null) {
                System.err.println("Local audio file not found: " + trackUrl);
                showAlert(Alert.AlertType.WARNING, "Audio Error", "Audio file not found locally: " + trackUrl + "\nPlease ensure it's in src/main/resources" + trackUrl);
                return;
            }

            final String mediaSourceExternalForm = audioUrl.toExternalForm();

            // Check if this track is already the current playing track
            boolean isThisTrackCurrent = currentMediaPlayer != null &&
                                         currentMediaPlayer.getMedia().getSource().equals(mediaSourceExternalForm);

            if (isThisTrackCurrent) {
                // If it's this track, toggle play/pause
                if (currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    currentMediaPlayer.pause();
                    playPauseIcon.setText("\u25B6"); // Change icon to Play
                    System.out.println("Paused: " + track.getTitle());
                } else { // PAUSED, READY, STALLED, etc.
                    currentMediaPlayer.play();
                    playPauseIcon.setText("\u23F8"); // Change icon to Pause (⏸)
                    System.out.println("Resumed: " + track.getTitle());
                }
            } else {
                // A new track is selected, or no track is playing
                // Stop and dispose of any currently playing track globally
                if (currentMediaPlayer != null) {
                    currentMediaPlayer.stop();
                    currentMediaPlayer.dispose();
                    // IMPORTANT: If you want to reset the icon of the *previous* track,
                    // you'd need a way to reference that specific playPauseIcon.
                    // This often involves storing references to active UI elements or using a more
                    // sophisticated global player state management. For now, it only affects the NEW icon.
                }

                try {
                    final Media media = new Media(mediaSourceExternalForm); // 'final' for lambda
                    currentMediaPlayer = new MediaPlayer(media);

                    // Link global volume slider to new MediaPlayer
                    currentMediaPlayer.setVolume(volumeSlider.getValue());
                    // Re-add listener for the new MediaPlayer instance, if not already handled by slider itself
                    // (the slider listener above handles this globally, so no need to re-add here)

                    currentMediaPlayer.play();
                    playPauseIcon.setText("\u23F8"); // Change icon to Pause

                    // Update currently playing title label
                    currentlyPlayingTitleLabel.setText("Now Playing: " + track.getTitle() + " - " + track.getArtistOrDescription());

                    // Set up event handlers for the newly created MediaPlayer
                    currentMediaPlayer.setOnEndOfMedia(() -> {
                        System.out.println("Finished playing: " + track.getTitle());
                        playPauseIcon.setText("\u25B6"); // Reset icon to Play at end
                        if (currentMediaPlayer != null) {
                            currentMediaPlayer.dispose();
                            currentMediaPlayer = null;
                            currentlyPlayingTitleLabel.setText("No Track Playing");
                        }
                    });
                    currentMediaPlayer.setOnError(() -> {
                        System.err.println("MediaPlayer error for " + track.getTitle() + ": " + currentMediaPlayer.getError());
                        showAlert(Alert.AlertType.ERROR, "Playback Error", "Could not play audio: " + (currentMediaPlayer.getError() != null ? currentMediaPlayer.getError().getMessage() : "Unknown error"));
                        playPauseIcon.setText("\u25B6"); // Reset icon on error
                        if (currentMediaPlayer != null) {
                            currentMediaPlayer.dispose();
                            currentMediaPlayer = null;
                            currentlyPlayingTitleLabel.setText("No Track Playing");
                        }
                    });
                    System.out.println("Playing: " + track.getTitle());

                } catch (Exception ex) {
                    System.err.println("Unexpected error during media playback setup for " + track.getTitle() + ": " + ex.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Playback Error", "Failed to start media playback: " + ex.getMessage());
                    playPauseIcon.setText("\u25B6"); // Reset icon on error
                }
            }
        });

        stopIcon.setOnMouseClicked(e -> {
            String trackUrl = track.getAudioUrl();
            URL audioUrl = getClass().getResource(trackUrl);
            final String mediaSourceExternalForm = (audioUrl != null) ? audioUrl.toExternalForm() : "";

            // Check if this track is the one currently playing globally
            if (currentMediaPlayer != null && currentMediaPlayer.getMedia().getSource().equals(mediaSourceExternalForm)) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                playPauseIcon.setText("\u25B6"); // Reset play/pause icon
                currentlyPlayingTitleLabel.setText("No Track Playing");
                System.out.println("Stopped: " + track.getTitle());
            } else {
                System.out.println("Track " + track.getTitle() + " is not the current playing track or not active.");
            }
        });

        // --- END UPDATED MUSIC PLAYBACK LOGIC ---


        VBox textInfo = new VBox(2);
        Label title = new Label(track.getTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setStyle(TEXT_COLOR_DARK_GREY);

        Label artistOrDesc = new Label(track.getArtistOrDescription());
        artistOrDesc.setFont(Font.font("Arial", 12));
        artistOrDesc.setStyle(TEXT_COLOR_GREY);

        textInfo.getChildren().addAll(title, artistOrDesc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label moodTagLabel = new Label(track.getMoodTag());
        moodTagLabel.setStyle(
                "-fx-background-color: #e0e0e0; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; "
                        + TEXT_COLOR_DARK_GREY);

        Label duration = new Label(track.getDuration());
        duration.setFont(Font.font("Arial", 12));
        duration.setStyle(TEXT_COLOR_GREY);
        HBox.setMargin(duration, new Insets(0, 10, 0, 0));

        Label heartIcon = new Label("\u2661"); // Unicode for empty heart
        heartIcon.setFont(Font.font("Arial", 18));
        heartIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");
        heartIcon.setOnMouseClicked(e -> {
            if ("\u2661".equals(heartIcon.getText())) {
                heartIcon.setText("\u2764"); // Unicode for solid heart
                heartIcon.setStyle("-fx-text-fill: #ff0000; -fx-cursor: hand;");
            } else {
                heartIcon.setText("\u2661");
                heartIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");
            }
        });

        // Add the new play/pause and stop icons
        item.getChildren().addAll(playPauseIcon, stopIcon, textInfo, spacer, moodTagLabel, duration, heartIcon);
        return item;
    }

    private VBox createMoodPlaylistButton(String playlistName, Color bgColor) {
        VBox button = new VBox(5);
        button.setAlignment(Pos.CENTER);
        button.setStyle("-fx-background-color: " + toHexString(bgColor) + ";" +
                "-fx-background-radius: 8px; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-cursor: hand;");
        button.setPrefSize(120, 100);

        Label icon = new Label("\uD83C\uDFA7");
        icon.setFont(Font.font("Arial", 30));
        icon.setTextFill(Color.WHITE);

        Label name = new Label(playlistName);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        name.setTextFill(Color.WHITE);
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);

        button.getChildren().addAll(icon, name);
        button.setOnMouseClicked(e -> showAlert(Alert.AlertType.INFORMATION, "Playlist", "You clicked on playlist: " + playlistName));
        return button;
    }

    private VBox createArticlesContentArea() {
        VBox articlesContent = new VBox(20);
        articlesContent.setPadding(new Insets(20));
        articlesContent.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT
                + "-fx-border-width: 0 1px 1px 1px; -fx-border-radius: 0 5px 5px 5px;");
        articlesContent.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(articlesContent, Priority.ALWAYS);

        GridPane articlesGrid = new GridPane();
        articlesGrid.setHgap(20);
        articlesGrid.setVgap(20);

        ColumnConstraints articleColConstraint = new ColumnConstraints();
        articleColConstraint.setHgrow(Priority.ALWAYS);
        articleColConstraint.setPercentWidth(100.0 / 3);

        for (int i = 0; i < 3; i++) {
            articlesGrid.getColumnConstraints().add(articleColConstraint);
        }

        int col = 0;
        int row = 0;
        for (int i = 0; i < Math.min(featuredArticles.size(), 3); i++) {
            Article article = featuredArticles.get(i);
            articlesGrid.add(createArticleCard(article), col++, row);
        }

        Label popularTopicsTitle = new Label("Popular Topics");
        popularTopicsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        popularTopicsTitle.setStyle(TEXT_COLOR_DARK_GREY);
        VBox.setMargin(popularTopicsTitle, new Insets(20, 0, 0, 0));

        FlowPane topicsFlowPane = new FlowPane(10, 10);
        for (String topic : popularTopics) {
            Label topicLabel = new Label(topic);
            topicLabel.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;");
            topicLabel.setOnMouseClicked(e -> showAlert(Alert.AlertType.INFORMATION, "Topic Selected", "You clicked on topic: " + topic));
            topicsFlowPane.getChildren().add(topicLabel);
        }

        articlesContent.getChildren().addAll(articlesGrid, popularTopicsTitle, topicsFlowPane);
        return articlesContent;
    }

    private VBox createArticleCard(Article article) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-cursor: hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label category = new Label(article.getCategory());
        category.setFont(Font.font("Arial", 12));
        category.setStyle(TEXT_COLOR_GREY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label starIcon = new Label("\u2B50");
        starIcon.setFont(Font.font("Arial", 16));
        starIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");
        starIcon.setOnMouseClicked(e -> {
            if ("\u2B50".equals(starIcon.getText())) {
                starIcon.setText("\u2764");
                starIcon.setStyle("-fx-text-fill: #ff0000; -fx-cursor: hand;");
            } else {
                starIcon.setText("\u2B50");
                starIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");
            }
        });

        header.getChildren().addAll(category, spacer, starIcon);

        Label title = new Label(article.getTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        title.setStyle(TEXT_COLOR_DARK_GREY);
        title.setWrapText(true);

        Label authorReadTime = new Label("By " + article.getAuthor() + " \u2022 " + article.getReadTime());
        authorReadTime.setFont(Font.font("Arial", 11));
        authorReadTime.setStyle(TEXT_COLOR_GREY);

        Label description = new Label(article.getDescription());
        description.setFont(Font.font("Arial", 13));
        description.setStyle(TEXT_COLOR_GREY);
        description.setWrapText(true);
        VBox.setVgrow(description, Priority.ALWAYS);

        Button readArticleButton = new Button("Read Article");
        readArticleButton.setMaxWidth(Double.MAX_VALUE);
        readArticleButton.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 8px 0; -fx-font-size: 14px; "
                        + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        VBox.setMargin(readArticleButton, new Insets(10, 0, 0, 0));
        readArticleButton.setOnAction(e -> {
            // --- REVERTED TO OPENING IN EXTERNAL BROWSER ---
            if (article.getId() != null && !article.getId().isEmpty() && !article.getId().equals("about:blank")) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(article.getId()));
                        showAlert(Alert.AlertType.INFORMATION, "Read Article", "Opening article in browser: " + article.getTitle());
                    } catch (IOException | java.net.URISyntaxException ex) {
                        System.err.println("Error opening article URL: " + ex.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not open article link.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Supported", "Browser opening is not supported on your system.");
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Read Article", "No direct link available for: " + article.getTitle());
            }
            // --- END REVERTED CHANGES ---
        });

        card.getChildren().addAll(header, title, authorReadTime, description, readArticleButton);
        return card;
    }

    /**
     * Creates the content area for the "Psychologists" tab.
     */
    private VBox createPsychologistContentArea() {
        VBox psychologistContent = new VBox(20);
        psychologistContent.setPadding(new Insets(0));
        psychologistContent.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        psychologistContent.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(psychologistContent, Priority.ALWAYS);

        psychologistContent.getChildren().add(createSectionWrapper(createSessionHistorySection(), new Insets(20, 20, 0, 20)));
        psychologistContent.getChildren().add(createSectionWrapper(createExplorePsychologistsSection(), new Insets(20)));

        return psychologistContent;
    }

    /**
     * Helper to wrap a section with white background, border, and specific padding.
     */
    private VBox createSectionWrapper(VBox content, Insets padding) {
        VBox wrapper = new VBox(content.getSpacing());
        wrapper.setPadding(padding);
        wrapper.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-border-width: 0 1px 1px 1px; -fx-border-radius: 0 5px 5px 5px;");
        wrapper.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        wrapper.getChildren().add(content);
        return wrapper;
    }


    private VBox createCommunityContentArea() {
        VBox communityContent = new VBox(20);
        communityContent.setPadding(new Insets(20));
        communityContent.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT
                + "-fx-border-width: 0 1px 1px 1px; -fx-border-radius: 0 5px 5px 5px;");
        communityContent.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(communityContent, Priority.ALWAYS);

        GridPane communityGrid = new GridPane();
        communityGrid.setHgap(20);
        communityGrid.setVgap(20);

        ColumnConstraints communityColConstraint = new ColumnConstraints();
        communityColConstraint.setHgrow(Priority.ALWAYS);
        communityColConstraint.setPercentWidth(50);
        communityGrid.getColumnConstraints().addAll(communityColConstraint, communityColConstraint);


        int col = 0;
        int row = 0;
        for (Community community : communities) {
            communityGrid.add(createCommunityCard(community), col, row);
            col++;
            if (col % 2 == 0) {
                col = 0;
                row++;
            }
        }

        VBox guidelinesSection = new VBox(10);
        VBox.setMargin(guidelinesSection, new Insets(20, 0, 0, 0));
        guidelinesSection.setPadding(new Insets(20));
        guidelinesSection.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        guidelinesSection.setMaxWidth(Double.MAX_VALUE);

        Label guidelinesTitle = new Label("Community Guidelines");
        guidelinesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        guidelinesTitle.setStyle(TEXT_COLOR_DARK_GREY);

        Label guidelinesDesc = new Label("Our community is built on respect, support, and anonymity");
        guidelinesDesc.setFont(Font.font("Arial", 12));
        guidelinesDesc.setStyle(TEXT_COLOR_GREY);

        VBox rulesList = new VBox(8);
        for (String rule : communityGuidelines) {
            HBox ruleItem = new HBox(5);
            Label bullet = new Label("\u2022");
            bullet.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            bullet.setStyle(TEXT_COLOR_GREY);
            Label ruleText = new Label(rule);
            ruleText.setFont(Font.font("Arial", 13));
            ruleText.setStyle(TEXT_COLOR_GREY);
            ruleItem.getChildren().addAll(bullet, ruleText);
            rulesList.getChildren().add(ruleItem);
        }

        guidelinesSection.getChildren().addAll(guidelinesTitle, guidelinesDesc, rulesList);

        communityContent.getChildren().addAll(communityGrid, guidelinesSection);
        return communityContent;
    }

    private VBox createCommunityCard(Community community) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-cursor: hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(community.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        name.setStyle(TEXT_COLOR_DARK_GREY);
        name.setWrapText(true);

        Label statusIndicator = new Label();
        statusIndicator.setPrefSize(10, 10);
        statusIndicator.setStyle(community.getStatus().equals("Active") ? STATUS_ACTIVE_STYLE : STATUS_QUIET_STYLE);

        Label members = new Label(community.getMembers() + " members");
        members.setFont(Font.font("Arial", 11));
        members.setStyle(TEXT_COLOR_GREY);

        header.getChildren().addAll(name, statusIndicator, new Label(""), members);

        Label description = new Label(community.getDescription());
        description.setFont(Font.font("Arial", 13));
        description.setStyle(TEXT_COLOR_GREY);
        description.setWrapText(true);
        VBox.setVgrow(description, Priority.ALWAYS);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);
        Button joinChatButton = new Button("Join Chat");
        joinChatButton.setStyle(
                "-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 14px; -fx-cursor: hand;");
        joinChatButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(joinChatButton, Priority.ALWAYS);
        joinChatButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Join Community", "You joined the '" + community.getName() + "' community!"));


        Label dropdownIcon = new Label("\u25BC");
        dropdownIcon.setFont(Font.font("Arial", 16));
        dropdownIcon.setStyle(TEXT_COLOR_GREY + "-fx-cursor: hand;");
        dropdownIcon.setPadding(new Insets(5));
        dropdownIcon.setStyle(
                "-fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-cursor: hand; -fx-padding: 5px;");

        buttons.getChildren().addAll(joinChatButton, dropdownIcon);
        VBox.setMargin(buttons, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(header, description, buttons);
        return card;
    }


    private VBox createActivitiesContentArea() {
        VBox activitiesContent = new VBox(20);
        activitiesContent.setPadding(new Insets(20));
        activitiesContent.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT
                + "-fx-border-width: 0 1px 1px 1px; -fx-border-radius: 0 5px 5px 5px;");
        activitiesContent.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(activitiesContent, Priority.ALWAYS);

        GridPane activitiesGrid = new GridPane();
        activitiesGrid.setHgap(20);
        activitiesGrid.setVgap(20);

        ColumnConstraints activityColConstraint = new ColumnConstraints();
        activityColConstraint.setHgrow(Priority.ALWAYS);
        activityColConstraint.setPercentWidth(100.0 / 3);
        for (int i = 0; i < 3; i++) {
            activitiesGrid.getColumnConstraints().add(activityColConstraint);
        }

        int col = 0;
        int row = 0;
        for (Activity activity : featuredActivities) {
            activitiesGrid.add(createActivityCard(activity), col++, row);
            if (col % 3 == 0) {
                col = 0;
                row++;
            }
        }

        VBox dailyChallengesSection = new VBox(10);
        VBox.setMargin(dailyChallengesSection, new Insets(20, 0, 0, 0));
        dailyChallengesSection.setPadding(new Insets(20));
        dailyChallengesSection.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        dailyChallengesSection.setMaxWidth(Double.MAX_VALUE);

        Label challengesTitle = new Label("Daily Challenges");
        challengesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        challengesTitle.setStyle(TEXT_COLOR_DARK_GREY);

        Label challengesDesc = new Label("Complete these activities to earn rewards and build healthy habits");
        challengesDesc.setFont(Font.font("Arial", 12));
        challengesDesc.setStyle(TEXT_COLOR_GREY);

        VBox challengeList = new VBox(8);
        for (DailyChallenge challenge : dailyChallenges) {
            challengeList.getChildren().add(createDailyChallengeItem(challenge));
        }

        dailyChallengesSection.getChildren().addAll(challengesTitle, challengesDesc, challengeList);

        activitiesContent.getChildren().addAll(activitiesGrid, dailyChallengesSection);
        return activitiesContent;
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-cursor: hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(activity.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        name.setStyle(TEXT_COLOR_DARK_GREY);
        name.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label difficulty = new Label(activity.getDifficulty());
        String difficultyStyle;
        switch (activity.getDifficulty()) {
            case "Easy":
                difficultyStyle = DIFFICULTY_EASY_STYLE;
                break;
            case "Medium":
                difficultyStyle = DIFFICULTY_MEDIUM_STYLE;
                break;
            case "Hard":
                difficultyStyle = DIFFICULTY_HARD_STYLE;
                break;
            default:
                difficultyStyle = TAG_STYLE_OUTLINE;
                break;
        }
        difficulty.setStyle(difficultyStyle);

        header.getChildren().addAll(name, spacer, difficulty);

        Label duration = new Label("Duration: " + activity.getDuration());
        duration.setFont(Font.font("Arial", 12));
        duration.setStyle(TEXT_COLOR_GREY);

        HBox xpReward = new HBox(3);
        xpReward.setAlignment(Pos.CENTER_LEFT);
        Label xpIcon = new Label("\uD83C\uDFC6");
        xpIcon.setFont(Font.font("Arial", 12));
        xpIcon.setStyle(TEXT_COLOR_GREY);
        Label xpText = new Label("+" + activity.getXpReward() + " XP");
        xpText.setFont(Font.font("Arial", 12));
        xpText.setStyle(TEXT_COLOR_GREY);
        xpReward.getChildren().addAll(xpIcon, xpText);

        Button startActivityButton = new Button("Start Activity");
        startActivityButton.setMaxWidth(Double.MAX_VALUE);
        startActivityButton.setStyle(
                "-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10px 0; -fx-font-size: 16px; -fx-cursor: hand;");
        VBox.setMargin(startActivityButton, new Insets(10, 0, 0, 0));
        startActivityButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Activity Started", "Starting activity: " + activity.getName()));


        card.getChildren().addAll(header, duration, xpReward, startActivityButton);
        return card;
    }

    private HBox createDailyChallengeItem(DailyChallenge challenge) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 0, 10, 0));
        item.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1px 0;");

        Label statusDot = new Label();
        statusDot.setPrefSize(10, 10);
        statusDot.setStyle(challenge.isCompleted() ? "-fx-background-color: #28a745; -fx-background-radius: 50%;"
                : "-fx-background-color: #cccccc; -fx-background-radius: 50%;");

        Label description = new Label(challenge.getDescription());
        description.setFont(Font.font("Arial", 14));
        description.setStyle(TEXT_COLOR_DARK_GREY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label xpReward = new Label(challenge.getXpReward() + " XP");
        xpReward.setFont(Font.font("Arial", 14));
        xpReward.setStyle(TEXT_COLOR_GREY);

        item.getChildren().addAll(statusDot, description, spacer, xpReward);
        return item;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    //------------------------------------------------------------------------------------------------------------------
    // Psychologist-related methods
    //------------------------------------------------------------------------------------------------------------------

    private VBox createSessionHistorySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(0));

        Label title = new Label("Explore Psychologists");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        section.getChildren().add(title);

        Label subtitle = new Label("Find new psychologists based on your preferences");
        subtitle.setFont(Font.font("Inter", 14));
        subtitle.setTextFill(Color.web("#777777"));
        section.getChildren().add(subtitle);

        if (sessionHistoryCardsContainer == null) {
            sessionHistoryCardsContainer = new VBox(15);
        }
        refreshSessionHistoryDisplay();
        section.getChildren().add(sessionHistoryCardsContainer);

        HBox historyButtonsBox = new HBox(10);
        historyButtonsBox.setAlignment(Pos.CENTER);

        viewMoreHistoryButton = new Button("View More History");
        viewMoreHistoryButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreHistoryButton.setTextFill(Color.web("#6a1b9a"));
        viewMoreHistoryButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreHistoryButton.setOnMouseEntered(e -> viewMoreHistoryButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreHistoryButton.setOnMouseExited(e -> viewMoreHistoryButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreHistoryButton.setOnAction(e -> {
            currentDisplayedHistoryCount = sessionHistoryPsychologists.size();
            refreshSessionHistoryDisplay();
        });

        viewLessHistoryButton = new Button("View Less History");
        viewLessHistoryButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewLessHistoryButton.setTextFill(Color.web("#6a1b9a"));
        viewLessHistoryButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewLessHistoryButton.setOnMouseEntered(e -> viewLessHistoryButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewLessHistoryButton.setOnMouseExited(e -> viewLessHistoryButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewLessHistoryButton.setOnAction(e -> {
            currentDisplayedHistoryCount = INITIAL_DISPLAY_COUNT;
            refreshSessionHistoryDisplay();
        });

        historyButtonsBox.getChildren().addAll(viewMoreHistoryButton, viewLessHistoryButton);
        section.getChildren().add(historyButtonsBox);

        return section;
    }

    /**
     * Refreshes the display of session history psychologist cards.
     */
    private void refreshSessionHistoryDisplay() {
        if (sessionHistoryCardsContainer == null) {
            sessionHistoryCardsContainer = new VBox(15);
        }
        sessionHistoryCardsContainer.getChildren().clear();
        int count = Math.min(currentDisplayedHistoryCount, sessionHistoryPsychologists.size());
        for (int i = 0; i < count; i++) {
            sessionHistoryCardsContainer.getChildren().add(createPsychologistCard(sessionHistoryPsychologists.get(i), true));
        }

        if (viewMoreHistoryButton != null) {
            viewMoreHistoryButton.setVisible(currentDisplayedHistoryCount < sessionHistoryPsychologists.size());
            viewMoreHistoryButton.setManaged(currentDisplayedHistoryCount < sessionHistoryPsychologists.size());
        }
        if (viewLessHistoryButton != null) {
            viewLessHistoryButton.setVisible(currentDisplayedHistoryCount == sessionHistoryPsychologists.size() && sessionHistoryPsychologists.size() > INITIAL_DISPLAY_COUNT);
            viewLessHistoryButton.setManaged(currentDisplayedHistoryCount == sessionHistoryPsychologists.size() && sessionHistoryPsychologists.size() > INITIAL_DISPLAY_COUNT);
        }
    }

    private VBox createExplorePsychologistsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(0));

        Label title = new Label("History of Sessions");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        section.getChildren().add(title);

        Label subtitle = new Label("Psychologists you've had sessions with previously");
        subtitle.setFont(Font.font("Inter", 14));
        subtitle.setTextFill(Color.web("#777777"));
        section.getChildren().add(subtitle);

        if (explorePsychologistCardsContainer == null) {
            explorePsychologistCardsContainer = new VBox(15);
        }
        refreshExplorePsychologistDisplay();
        section.getChildren().add(explorePsychologistCardsContainer);

        HBox exploreButtonsBox = new HBox(10);
        exploreButtonsBox.setAlignment(Pos.CENTER);

        viewMoreExploreButton = new Button("View More Psychologists");
        viewMoreExploreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewMoreExploreButton.setTextFill(Color.web("#6a1b9a"));
        viewMoreExploreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewMoreExploreButton.setOnMouseEntered(e -> viewMoreExploreButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreExploreButton.setOnMouseExited(e -> viewMoreExploreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewMoreExploreButton.setOnAction(e -> {
            currentDisplayedExploreCount = explorePsychologists.size();
            refreshExplorePsychologistDisplay();
        });

        viewLessExploreButton = new Button("View Less Psychologists");
        viewLessExploreButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        viewLessExploreButton.setTextFill(Color.web("#6a1b9a"));
        viewLessExploreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;");
        viewLessExploreButton.setOnMouseEntered(e -> viewLessExploreButton.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewLessExploreButton.setOnMouseExited(e -> viewLessExploreButton.setStyle("-fx-background-color: transparent; -fx-border-color: #6a1b9a; -fx-border-width: 1; -fx-background-radius: 8; -fx-padding: 10 20;"));
        viewLessExploreButton.setOnAction(e -> {
            currentDisplayedExploreCount = INITIAL_DISPLAY_COUNT;
            refreshExplorePsychologistDisplay();
        });

        exploreButtonsBox.getChildren().addAll(viewMoreExploreButton, viewLessExploreButton);
        section.getChildren().add(exploreButtonsBox);

        return section;
    }


    /**
     * Refreshes the display of explore psychologist cards.
     */
    private void refreshExplorePsychologistDisplay() {
        if (explorePsychologistCardsContainer == null) {
            explorePsychologistCardsContainer = new VBox(15);
        }
        explorePsychologistCardsContainer.getChildren().clear();
        int count = Math.min(currentDisplayedExploreCount, explorePsychologists.size());
        for (int i = 0; i < count; i++) {
            explorePsychologistCardsContainer.getChildren().add(createPsychologistCard(explorePsychologists.get(i), false));
        }

        if (viewMoreExploreButton != null) {
            viewMoreExploreButton.setVisible(currentDisplayedExploreCount < explorePsychologists.size());
            viewMoreExploreButton.setManaged(currentDisplayedExploreCount < explorePsychologists.size());
        }
        if (viewLessExploreButton != null) {
            viewLessExploreButton.setVisible(currentDisplayedExploreCount == explorePsychologists.size() && explorePsychologists.size() > INITIAL_DISPLAY_COUNT);
            viewLessExploreButton.setManaged(currentDisplayedExploreCount == explorePsychologists.size() && explorePsychologists.size() > INITIAL_DISPLAY_COUNT);
        }
    }

    private HBox createPsychologistCard(Psychologist psychologist, boolean isHistoryCard) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ImageView profilePic = createImageView(psychologist.getProfilePictureUrl(), 60, 60);
        Circle clip = new Circle(30, 30, 30);
        profilePic.setClip(clip);
        profilePic.setStyle("-fx-border-color: #6a1b9a; -fx-border-width: 2; -fx-border-radius: 30;");

        VBox detailsBox = new VBox(2);
        Label nameLabel = new Label("Dr. " + psychologist.getFullName());
        nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.web("#333333"));

        Label qualificationLabel = new Label(psychologist.getQualification());
        qualificationLabel.setFont(Font.font("Inter", 12));
        qualificationLabel.setTextFill(Color.web("#777777"));

        Label experienceLabel = new Label(psychologist.getYearsOfExperience() + " years of experience");
        experienceLabel.setFont(Font.font("Inter", 12));
        experienceLabel.setTextFill(Color.web("#555555"));

        detailsBox.getChildren().addAll(nameLabel, qualificationLabel, experienceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        if (isHistoryCard) {
            Button chatButton = createActionButton("Chat", "#2196F3", Color.WHITE);
            chatButton.setOnAction(e -> openChatRoom(psychologist));
            actionButtonsBox.getChildren().add(chatButton);

            Button bookAgainButton = createActionButton("Book Again", "#2196F3", Color.WHITE);
            bookAgainButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Action", "Book Again with Dr. " + psychologist.getFullName()));
            actionButtonsBox.getChildren().add(bookAgainButton);
        } else {
            Button sendRequestButton = createActionButton("Send Request", "#2196F3", Color.WHITE);
            sendRequestButton.setOnAction(e -> handleSendRequest(psychologist));
            actionButtonsBox.getChildren().add(sendRequestButton);
        }

        Button viewProfileButton = createActionButton("View Profile", "#f0f2f5", Color.web("#555555"));
        viewProfileButton.setStyle(viewProfileButton.getStyle() + "; -fx-border-color: #cccccc; -fx-border-width: 1;");
        viewProfileButton.setOnMouseEntered(e -> viewProfileButton.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #555555; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        viewProfileButton.setOnMouseExited(e -> viewProfileButton.setStyle("-fx-background-color: #f0f2f5; -fx-text-fill: #555555; -fx-background-radius: 8; -fx-padding: 8 12; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        viewProfileButton.setOnAction(e -> handleViewProfile(psychologist));
        actionButtonsBox.getChildren().add(viewProfileButton);

        card.getChildren().addAll(profilePic, detailsBox, spacer, actionButtonsBox);
        return card;
    }

    private Button createActionButton(String text, String bgColorHex, Color textColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Inter", 12));
        button.setTextFill(textColor);
        button.setStyle("-fx-background-color: " + bgColorHex + "; -fx-background-radius: 8; -fx-padding: 8 12;");
        button.setOnMouseEntered(e -> {
            Color color = Color.web(bgColorHex);
            button.setStyle("-fx-background-color: " + color.darker().deriveColor(0, 1, 0.9, 1).toString().replace("0x", "#") + "; -fx-background-radius: 8; -fx-padding: 8 12;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + bgColorHex + "; -fx-background-radius: 8; -fx-padding: 8 12;");
        });
        return button;
    }

    private void handleSendRequest(Psychologist psychologist) {
        showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Session request sent to Dr. " + psychologist.getFullName() + " (ID: " + psychologist.getId() + ").");
    }

    private void openChatRoom(Psychologist psychologist) {
        Stage chatStage = new Stage();
        chatStage.initModality(Modality.WINDOW_MODAL);
        chatStage.initOwner(primaryStage);
        chatStage.setTitle("Chat with Dr. " + psychologist.getFullName());

        VBox chatRoom = new VBox(10);
        chatRoom.setPadding(new Insets(20));
        chatRoom.setAlignment(Pos.TOP_CENTER);
        chatRoom.setStyle("-fx-background-color: #f0f2f5;");

        HBox chatHeader = new HBox(15);
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setPadding(new Insets(0, 0, 15, 0));

        Label chatTitle = new Label("Chat with Dr. " + psychologist.getFullName());
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
                createChatMessage("Hello Dr. " + psychologist.getFullName() + "!", true),
                createChatMessage("Hi! How can I help you today?", false)
        );

        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(15, 0, 0, 0));
        inputArea.setAlignment(Pos.CENTER);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setPrefHeight(35);
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        messageInput.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 8 15;");

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

    private HBox createChatMessage(String message, boolean isUser) {
        HBox messageBubbleContainer = new HBox();
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(350);

        if (isUser) {
            msgLabel.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 15;");
            messageBubbleContainer.setAlignment(Pos.CENTER_RIGHT);
        } else {
            msgLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-background-radius: 15; -fx-padding: 10 15;");
            messageBubbleContainer.setAlignment(Pos.CENTER_LEFT);
        }
        messageBubbleContainer.getChildren().add(msgLabel);
        return messageBubbleContainer;
    }

    private void handleViewProfile(Psychologist psychologist) {
        showAlert(Alert.AlertType.INFORMATION, "View Profile", "Viewing profile for Dr. " + psychologist.getFullName() + " (ID: " + psychologist.getId() + ").\n" +
                "Qualifications: " + psychologist.getQualification() + "\n" +
                "Specializations: " + String.join(", ", psychologist.getSpecialties()) + "\n" +
                "Experience: " + psychologist.getYearsOfExperience() + " years");
    }

    /**
     * Helper method to show an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> { // Ensure UI updates are on FX Application Thread
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    // General Helper Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Helper to create an ImageView with error handling for missing image resources or network images.
     */
    private ImageView createImageView(String imagePathOrUrl, int fitWidth, int fitHeight) {
        ImageView iconView = new ImageView();
        Image image = null;

        if (imagePathOrUrl != null && !imagePathOrUrl.isEmpty()) {
            try {
                if (imagePathOrUrl.startsWith("http://") || imagePathOrUrl.startsWith("https://")) {
                    image = new Image(imagePathOrUrl, true);
                    image.errorProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            System.err.println("Failed to load network image: " + imagePathOrUrl);
                            try {
                                URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
                                if (fallbackUrl != null) {
                                    Platform.runLater(() -> iconView.setImage(new Image(fallbackUrl.toExternalForm())));
                                }
                            } catch (Exception e) {
                                System.err.println("Exception loading fallback image: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    URL imageUrl = getClass().getResource(imagePathOrUrl);
                    if (imageUrl != null) {
                        image = new Image(imageUrl.toExternalForm());
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception while loading image: " + imagePathOrUrl + " - " + e.getMessage());
            }
        }

        if (image == null || (image != null && image.isError())) {
            if (imagePathOrUrl != null && !imagePathOrUrl.isEmpty()) {
                System.err.println("Falling back to local placeholder for image: " + imagePathOrUrl);
            }
            try {
                URL fallbackUrl = getClass().getResource("/assets/images/placeholder.png");
                if (fallbackUrl != null) {
                    image = new Image(fallbackUrl.toExternalForm());
                } else {
                    System.err.println("Also failed to load placeholder.png! Please ensure it exists at src/main/resources/assets/images/placeholder.png");
                }
            } catch (Exception pe) {
                System.err.println("Exception loading hardcoded placeholder image: " + pe.getMessage());
            }
        }

        if (image != null) {
            iconView.setImage(image);
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

    /**
     * Creates a common control panel for the player (global play/pause/stop/volume).
     */
    private HBox createPlayerControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5px;");
        controls.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(controls, Priority.ALWAYS);

        // Global Play/Pause Button
        Button globalPlayPauseButton = new Button("▶ / ⏸");
        globalPlayPauseButton.setOnAction(e -> {
            if (currentMediaPlayer != null) {
                if (currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    currentMediaPlayer.pause();
                } else {
                    currentMediaPlayer.play();
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Track", "Please select a track to play.");
            }
        });

        // Global Stop Button
        Button globalStopButton = new Button("■ Stop");
        globalStopButton.setOnAction(e -> {
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.dispose();
                currentMediaPlayer = null;
                currentlyPlayingTitleLabel.setText("No Track Playing");
                System.out.println("Global Stop: All playback ceased.");
            }
        });

        // Volume Label
        Label volumeLabel = new Label("Volume:");
        volumeLabel.setStyle(TEXT_COLOR_DARK_GREY);

        controls.getChildren().addAll(
            currentlyPlayingTitleLabel,
            new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, // Spacer
            volumeLabel,
            volumeSlider,
            globalPlayPauseButton,
            globalStopButton
        );
        return controls;
    }
}
