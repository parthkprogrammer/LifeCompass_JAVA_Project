package com.lifecompass.view; // Corrected package name

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath; // Retained if still used by internal parts of the art app
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL; // Added for createImageView
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

// Refactored: No longer extends Application
public class LifeCompassArtApp {

    // --- Styling Constants (consistent with dashboard and previous apps) ---
    private static final String BACKGROUND_COLOR_LIGHT_GREY = "-fx-background-color: #f9fafb;";
    private static final String BACKGROUND_COLOR_WHITE = "-fx-background-color: white;";
    private static final String BORDER_STYLE_LIGHT = "-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;";
    private static final String TEXT_COLOR_GREY = "-fx-text-fill: #606060;";
    private static final String TEXT_COLOR_DARK_GREY = "-fx-text-fill: #333333;";
    private static final String TEXT_COLOR_BLUE = "-fx-text-fill: #007bff;";
    private static final String TAG_STYLE_OUTLINE = "-fx-background-color: #e0e0e0; -fx-padding: 5px 10px; -fx-background-radius: 15px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY;
    private static final String TAG_STYLE_SELECTED = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 15px; -fx-font-size: 12px;";
    private static final String MOOD_PROMPT_STYLE = "-fx-background-color: #e0f2ff; -fx-padding: 10px; -fx-background-radius: 5px; -fx-border-color: #cce7ff; -fx-border-radius: 5px; -fx-border-width: 1px; " + TEXT_COLOR_BLUE;


    // Data for Mood Tags
    private static final List<String> MOOD_TAGS = Arrays.asList(
            "Happy", "Sad", "Anxious", "Calm", "Angry", "Excited", "Peaceful", "Confused"
    );
    private ObservableList<String> selectedMoodTags = FXCollections.observableArrayList();


    // Main TabPane for the "Create Art" / "My Gallery" content (internal to Art screen)
    private TabPane artContentTabPane;
    private Tab createArtTab;
    private Tab myGalleryTab;


    // Drawing Canvas related variables
    private Canvas drawingCanvas;
    private GraphicsContext gc;
    private Color currentColor = Color.BLACK;
    private double currentBrushSize = 10.0;
    private boolean isEraserMode = false;

    // Undo/Redo history
    private List<WritableImage> canvasHistory = new ArrayList<>();
    private int historyIndex = -1; // -1 means no state saved yet
    private Button undoButton;
    private Button redoButton;
    
    private Stage primaryStageRef; // Reference to the primary stage for FileChooser and dialogs

    // Flag to ensure initial canvas setup (fill and snapshot) happens only once and safely
    private AtomicBoolean initialCanvasSetupDone = new AtomicBoolean(false);


    // Gallery Data Model
    static class ArtworkData {
        int id;
        String title;
        String date; // Formatted date string
        String mood;
        WritableImage image; // Store the actual drawing

        public ArtworkData(int id, String title, String date, String mood, WritableImage image) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.mood = mood;
            this.image = image;
        }
    }

    private ObservableList<ArtworkData> savedArtworks = FXCollections.observableArrayList(
            // Initial sample artworks with dummy images
            new ArtworkData(1, "Peaceful Moment", "2024-01-15", "Calm", null),
            new ArtworkData(2, "Stormy Feelings", "2024-01-14", "Anxious", null),
            new ArtworkData(3, "Joy Burst", "2024-01-13", "Happy", null)
    );

    // --- References from Dashboard (if this component needs to interact back with the dashboard) ---
    private UserDashboardScreen dashboardInstance; // Corrected dashboard reference type

    // Constructor to receive dashboard context and primary stage reference
    public LifeCompassArtApp(Stage primaryStage, UserDashboardScreen dashboardInstance) { // Corrected type
        this.primaryStageRef = primaryStage; // Store for FileChooser
        this.dashboardInstance = dashboardInstance;
    }

    /**
     * Creates and returns the entire UI content for the Art screen.
     * This method is designed to be called by the `UserDashboardScreen` class.
     * @return A VBox containing all UI elements for the Art screen.
     */
    public VBox createArtScreenContent() {
        VBox artScreenLayout = new VBox(20); // Spacing between major sections
        artScreenLayout.setPadding(new Insets(20)); // Padding around the content
        artScreenLayout.setAlignment(Pos.TOP_CENTER);
        artScreenLayout.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        artScreenLayout.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(artScreenLayout, Priority.ALWAYS);

        // --- Main TabPane for the Art Screen content ("Create Art" / "My Gallery") ---
        artContentTabPane = new TabPane();
        artContentTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // Hide default tab headers
        artContentTabPane.tabMinHeightProperty().set(0);
        artContentTabPane.tabMaxHeightProperty().set(0);
        artContentTabPane.setTabMaxHeight(0);
        artContentTabPane.setTabMinHeight(0);
        artContentTabPane.setPadding(Insets.EMPTY);
        artContentTabPane.setTabMinWidth(0);
        artContentTabPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(artContentTabPane, Priority.ALWAYS);

        // Create content for each internal tab
        createArtTab = new Tab("Create Art");
        createArtTab.setContent(createCreateArtTabContent());
        createArtTab.setClosable(false);

        myGalleryTab = new Tab("My Gallery");
        myGalleryTab.setContent(createMyGalleryTabContent());
        myGalleryTab.setClosable(false);

        artContentTabPane.getTabs().addAll(createArtTab, myGalleryTab);

        // --- Internal Tab-like buttons for Create Art / My Gallery ---
        HBox artSectionTabs = createArtInternalNavBar(); // New helper for these buttons

        artScreenLayout.getChildren().addAll(artSectionTabs, artContentTabPane);
        
        // This ensures the initial canvas state is correctly saved after it's rendered.
        // It relies on the canvas being part of the scene graph and having its dimensions.
        Platform.runLater(() -> {
            if (drawingCanvas != null && gc != null && drawingCanvas.getWidth() > 0 && drawingCanvas.getHeight() > 0 && !initialCanvasSetupDone.get()) {
                redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight());
                saveCanvasStateForHistory();
                initialCanvasSetupDone.set(true);
                System.out.println("Canvas initial setup and first snapshot complete.");
            }
        });

        return artScreenLayout;
    }

    // --- Helper to create the internal navigation for Art (Create Art / My Gallery) ---
    private HBox createArtInternalNavBar() {
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(0));
        HBox.setHgrow(navBar, Priority.ALWAYS);

        Label createArtTabBtn = new Label("Create Art");
        createArtTabBtn.setFont(new Font("Arial Bold", 14));
        createArtTabBtn.setPadding(new Insets(10, 20, 10, 20));
        createArtTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 1px; -fx-background-radius: 5px 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");

        Label myGalleryTabBtn = new Label("My Gallery");
        myGalleryTabBtn.setFont(new Font("Arial", 14));
        myGalleryTabBtn.setPadding(new Insets(10, 20, 10, 20));
        myGalleryTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");

        navBar.getChildren().addAll(createArtTabBtn, myGalleryTabBtn);

        createArtTabBtn.setOnMouseClicked(e -> artContentTabPane.getSelectionModel().select(createArtTab));
        myGalleryTabBtn.setOnMouseClicked(e -> artContentTabPane.getSelectionModel().select(myGalleryTab));

        // Listener to update button styles when artContentTabPane selection changes
        artContentTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == createArtTab) {
                createArtTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 1px; -fx-background-radius: 5px 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
                myGalleryTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
            } else if (newTab == myGalleryTab) {
                createArtTabBtn.setStyle(BACKGROUND_COLOR_LIGHT_GREY + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 1px 1px; -fx-background-radius: 5px 0 0 0; " + TEXT_COLOR_GREY + ";-fx-cursor: hand;");
                myGalleryTabBtn.setStyle(BACKGROUND_COLOR_WHITE + "-fx-border-color: #e0e0e0; -fx-border-width: 1px 1px 0 0; -fx-background-radius: 0 5px 0 0; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
            }
        });

        return navBar;
    }


    // --- Content for "Create Art" Tab ---
    private VBox createCreateArtTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0)); // Padding controlled by parent layout now
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(BACKGROUND_COLOR_LIGHT_GREY); // Background for this tab's content
        VBox.setVgrow(content, Priority.ALWAYS); // Allow this tab content to grow

        // "Expressive Art Board" Header Card
        VBox artBoardHeaderCard = new VBox(10);
        artBoardHeaderCard.setPadding(new Insets(20));
        artBoardHeaderCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        artBoardHeaderCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(artBoardHeaderCard, Priority.ALWAYS);

        Label artTitle = new Label("Expressive Art Board");
        artTitle.setFont(new Font("Arial Bold", 16));
        artTitle.setStyle(TEXT_COLOR_DARK_GREY);
        Label paletteGraphic = new Label("\uD83C\uDFA8");
        paletteGraphic.setFont(new Font("Arial", 16));
        paletteGraphic.setTextFill(Color.web("#800080"));
        artTitle.setGraphic(paletteGraphic);
        artTitle.setContentDisplay(ContentDisplay.LEFT);

        Label artDescription = new Label("Express your emotions through art. Draw, paint, and create to explore your feelings visually.");
        artDescription.setFont(new Font("Arial", 12));
        artDescription.setStyle(TEXT_COLOR_GREY);
        artDescription.setWrapText(true);

        artBoardHeaderCard.getChildren().addAll(artTitle, artDescription);

        // --- Tools and Canvas Area ---
        HBox mainArtArea = new HBox(20); // Spacing between tools and canvas
        mainArtArea.setAlignment(Pos.TOP_LEFT);
        mainArtArea.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(mainArtArea, Priority.ALWAYS);
        VBox.setVgrow(mainArtArea, Priority.ALWAYS); // Ensure this area stretches vertically

        // Left Panel (Tools)
        VBox toolsPanel = new VBox(15);
        toolsPanel.setPadding(new Insets(20));
        toolsPanel.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        toolsPanel.setPrefWidth(250); // Fixed preferred width for the tools panel
        toolsPanel.setMinWidth(200);
        toolsPanel.setMaxWidth(300);

        Label toolsTitle = new Label("Tools");
        toolsTitle.setFont(Font.font("Arial Bold", 16));
        toolsTitle.setStyle(TEXT_COLOR_DARK_GREY);

        // Drawing Tool (Brush/Eraser)
        Label drawingToolLabel = new Label("Drawing Tool");
        drawingToolLabel.setFont(new Font("Arial", 12));
        drawingToolLabel.setStyle(TEXT_COLOR_GREY);

        HBox drawingToolButtons = new HBox(10);
        Button brushButton = new Button("Brush");
        brushButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; -fx-cursor: hand;");
        brushButton.setGraphic(new Label("\uD83D\uDDE2\uFE0F"));

        Button eraserButton = new Button("Eraser");
        eraserButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        eraserButton.setGraphic(new Label("\uD83D\uDDE1\uFE0F"));

        brushButton.setOnAction(e -> {
            isEraserMode = false;
            brushButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; -fx-cursor: hand;");
            eraserButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
            if (gc != null) gc.setStroke(currentColor);
        });
        eraserButton.setOnAction(e -> {
            isEraserMode = true;
            eraserButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; -fx-cursor: hand;");
            brushButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
            if (gc != null) gc.setStroke(Color.WHITE); // Eraser effectively draws with white
        });
        drawingToolButtons.getChildren().addAll(brushButton, eraserButton);

        // Colors
        Label colorsLabel = new Label("Colors");
        colorsLabel.setFont(new Font("Arial", 12));
        colorsLabel.setStyle(TEXT_COLOR_GREY);

        FlowPane colorPalette = new FlowPane(8, 8);
        Color[] colors = {
                Color.BLACK, Color.RED, Color.LIMEGREEN, Color.BLUE, Color.PURPLE,
                Color.YELLOW, Color.CYAN, Color.ORANGE, Color.HOTPINK, Color.BROWN,
                Color.web("#800080"), Color.web("#A52A2A"), Color.web("#808080"), Color.WHITE
        };
        for (Color color : colors) {
            Circle colorSwatch = new Circle(12, color);
            colorSwatch.setStroke(Color.GREY);
            colorSwatch.setStrokeWidth(1);
            colorSwatch.setStyle("-fx-cursor: hand;");
            colorSwatch.setOnMouseClicked(e -> {
                currentColor = color;
                if (!isEraserMode && gc != null) gc.setStroke(currentColor);
                colorPalette.getChildren().forEach(node -> node.setEffect(null));
                colorSwatch.setEffect(new javafx.scene.effect.DropShadow(5, Color.DARKGRAY)); // Highlight selected color
            });
            colorPalette.getChildren().add(colorSwatch);
        }
        // Select black as initial color and highlight it
        colorPalette.getChildren().get(0).fireEvent(new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, javafx.scene.input.MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));

        // Brush Size
        Label brushSizeLabel = new Label("Brush Size: " + (int)currentBrushSize + "px");
        brushSizeLabel.setFont(new Font("Arial", 12));
        brushSizeLabel.setStyle(TEXT_COLOR_GREY);

        Slider brushSizeSlider = new Slider(1, 50, currentBrushSize);
        brushSizeSlider.setShowTickLabels(false);
        brushSizeSlider.setShowTickMarks(false);
        brushSizeSlider.setMajorTickUnit(10);
        brushSizeSlider.setMinorTickCount(1);
        brushSizeSlider.setBlockIncrement(1);
        brushSizeSlider.setMaxWidth(Double.MAX_VALUE);
        brushSizeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            currentBrushSize = newValue.doubleValue();
            brushSizeLabel.setText("Brush Size: " + (int) currentBrushSize + "px");
            if (gc != null) gc.setLineWidth(currentBrushSize);
        });

        // Undo/Redo/Clear/Download buttons
        HBox undoRedoButtons = new HBox(10);
        undoButton = new Button("\u21B6"); // Unicode for undo arrow
        undoButton.setFont(new Font("Arial", 16));
        undoButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        undoButton.setOnAction(e -> undoCanvas());
        undoButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(undoButton, Priority.ALWAYS);

        redoButton = new Button("\u21B7"); // Unicode for redo arrow
        redoButton.setFont(new Font("Arial", 16));
        redoButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        redoButton.setOnAction(e -> redoCanvas());
        redoButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(redoButton, Priority.ALWAYS);
        undoRedoButtons.getChildren().addAll(undoButton, redoButton);
        updateUndoRedoButtonStates(); // Update initial state (both disabled)


        Button clearCanvasButton = new Button("Clear Canvas");
        clearCanvasButton.setMaxWidth(Double.MAX_VALUE);
        clearCanvasButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 8px 0; -fx-font-size: 14px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        clearCanvasButton.setGraphic(new Label("\uD83D\uDDD1\uFE0F")); // Trash can emoji
        clearCanvasButton.setContentDisplay(ContentDisplay.LEFT);
        clearCanvasButton.setOnAction(e -> {
            if (gc != null) {
                gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
                redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight()); // Fill with white
                saveCanvasStateForHistory(); // Save clear state
            }
        });

        Button downloadButton = new Button("Download");
        downloadButton.setMaxWidth(Double.MAX_VALUE);
        downloadButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 8px 0; -fx-font-size: 14px; -fx-cursor: hand;");
        downloadButton.setGraphic(new Label("\u2B07\uFE0F")); // Download arrow emoji
        downloadButton.setContentDisplay(ContentDisplay.LEFT);
        downloadButton.setOnAction(e -> downloadArt());


        toolsPanel.getChildren().addAll(
                toolsTitle,
                drawingToolLabel, drawingToolButtons,
                colorsLabel, colorPalette,
                brushSizeLabel, brushSizeSlider,
                undoRedoButtons,
                clearCanvasButton,
                downloadButton
        );

        // Right Panel (Canvas)
        BorderPane canvasPanel = new BorderPane();
        canvasPanel.setPadding(new Insets(1)); // Small border-like padding
        canvasPanel.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        HBox.setHgrow(canvasPanel, Priority.ALWAYS); // Canvas panel grows horizontally
        VBox.setVgrow(canvasPanel, Priority.ALWAYS); // Canvas panel grows vertically

        Label canvasTitle = new Label("Canvas");
        canvasTitle.setFont(Font.font("Arial Bold", 16));
        canvasTitle.setStyle(TEXT_COLOR_DARK_GREY);
        canvasTitle.setPadding(new Insets(15, 0, 0, 15)); // Padding for title within canvas panel

        HBox canvasTopRightButtons = new HBox(10);
        canvasTopRightButtons.setPadding(new Insets(10, 15, 0, 0));
        canvasTopRightButtons.setAlignment(Pos.CENTER_RIGHT);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        saveButton.setGraphic(new Label("\uD83D\uDCBE")); // Diskette emoji
        saveButton.setContentDisplay(ContentDisplay.LEFT);
        saveButton.setOnAction(e -> saveArtworkToGallery());

        Button shareButton = new Button("Share");
        shareButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-background-radius: 5px; -fx-padding: 5px 10px; -fx-font-size: 12px; " + TEXT_COLOR_DARK_GREY + ";-fx-cursor: hand;");
        shareButton.setGraphic(new Label("\uD83D\uDCE4")); // Outbox emoji
        shareButton.setContentDisplay(ContentDisplay.LEFT);
        shareButton.setOnAction(e -> System.out.println("Share art clicked (functionality not implemented)"));

        canvasTopRightButtons.getChildren().addAll(saveButton, shareButton);

        BorderPane canvasHeader = new BorderPane();
        canvasHeader.setLeft(canvasTitle);
        canvasHeader.setRight(canvasTopRightButtons);
        canvasHeader.setPadding(new Insets(5)); // Padding around the internal header elements

        canvasPanel.setTop(canvasHeader);

        // Canvas initialization - It will now stretch with its parent
        drawingCanvas = new Canvas();
        drawingCanvas.setManaged(true);
        
        // Bind canvas dimensions to parent (canvasPanel) size, subtracting padding/header height
        drawingCanvas.widthProperty().bind(canvasPanel.widthProperty().subtract(canvasPanel.getPadding().getLeft() + canvasPanel.getPadding().getRight() + 2)); // 2 for border
        drawingCanvas.heightProperty().bind(canvasPanel.heightProperty().subtract(canvasHeader.heightProperty()).subtract(canvasPanel.getPadding().getTop() + canvasPanel.getPadding().getBottom() + 2)); // 2 for border

        gc = drawingCanvas.getGraphicsContext2D();
        gc.setLineWidth(currentBrushSize);
        gc.setStroke(currentColor);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        // Drawing event handlers
        drawingCanvas.setOnMousePressed(e -> {
            if (gc == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) return;
            gc.setLineWidth(currentBrushSize);
            if (isEraserMode) {
                gc.setStroke(Color.WHITE);
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_ATOP);
            } else {
                gc.setStroke(currentColor);
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
            }
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
        drawingCanvas.setOnMouseDragged(e -> {
            if (gc == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) return;
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
        drawingCanvas.setOnMouseReleased(e -> {
            if (gc == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) return;
            gc.closePath();
            saveCanvasStateForHistory();
        });

        canvasPanel.setCenter(drawingCanvas);

        mainArtArea.getChildren().addAll(toolsPanel, canvasPanel);


        // --- "Connect Your Art to Your Mood" Card ---
        VBox moodConnectionCard = new VBox(10);
        moodConnectionCard.setPadding(new Insets(20));
        moodConnectionCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        moodConnectionCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(moodConnectionCard, Priority.ALWAYS);

        Label moodTitle = new Label("Connect Your Art to Your Mood");
        moodTitle.setFont(new Font("Arial Bold", 16));
        moodTitle.setStyle(TEXT_COLOR_DARK_GREY);
        Label moodHeartGraphic = new Label("\u2764\uFE0F");
        moodHeartGraphic.setTextFill(Color.RED);
        moodHeartGraphic.setFont(new Font("Arial", 16));
        moodTitle.setGraphic(moodHeartGraphic);
        moodTitle.setContentDisplay(ContentDisplay.LEFT);

        Label moodDescription = new Label("Help us understand what your art represents emotionally");
        moodDescription.setFont(new Font("Arial", 12));
        moodDescription.setStyle(TEXT_COLOR_GREY);
        moodDescription.setWrapText(true);

        FlowPane moodTagsPane = new FlowPane(10, 10);
        for (String tag : MOOD_TAGS) {
            Label moodTagButton = new Label(tag);
            moodTagButton.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;");
            moodTagButton.setOnMouseClicked(e -> {
                if (selectedMoodTags.contains(tag)) {
                    selectedMoodTags.remove(tag);
                    moodTagButton.setStyle(TAG_STYLE_OUTLINE + "-fx-cursor: hand;");
                } else {
                    selectedMoodTags.add(tag);
                    moodTagButton.setStyle(TAG_STYLE_SELECTED + "-fx-cursor: hand;");
                }
                System.out.println("Selected Mood Tags for Art: " + selectedMoodTags);
            });
            moodTagsPane.getChildren().add(moodTagButton);
        }

        moodConnectionCard.getChildren().addAll(moodTitle, moodDescription, moodTagsPane);

        // Add all main content sections for the "Create Art" tab
        content.getChildren().addAll(artBoardHeaderCard, mainArtArea, moodConnectionCard);
        return content;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Canvas Utility Methods (Undo/Redo, Draw Background, Download)
    //------------------------------------------------------------------------------------------------------------------

    // private void redrawCanvasBackground(double width, double height) { // Fixed parameter: 'double double height' -> 'double height'
    //     if (gc == null || width <= 0 || height <= 0) {
    //         System.err.println("Cannot redraw background: GraphicsContext or dimensions invalid.");
    //         return;
    //     }
    //     gc.clearRect(0, 0, width, height);
    //     gc.setFill(Color.WHITE);
    //     gc.fillRect(0, 0, width, height);
    // }

    // --- Canvas History (Undo/Redo) ---
    private void saveCanvasStateForHistory() {
        if (drawingCanvas == null || gc == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) {
            return;
        }

        // Clear any 'redo' states if a new drawing action occurred
        if (historyIndex < canvasHistory.size() - 1) {
            canvasHistory = new ArrayList<>(canvasHistory.subList(0, historyIndex + 1));
        }

        // Capture current canvas state
        WritableImage snapshot = new WritableImage((int)drawingCanvas.getWidth(), (int)drawingCanvas.getHeight());
        drawingCanvas.snapshot(null, snapshot);
        canvasHistory.add(snapshot);
        historyIndex = canvasHistory.size() - 1;
        updateUndoRedoButtonStates();
    }

    private void restoreCanvasStateForHistory(int index) {
        if (index < 0 || index >= canvasHistory.size() || drawingCanvas == null || gc == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) {
            return;
        }

        WritableImage snapshot = canvasHistory.get(index);
        redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight()); // Clear and fill background

        // Draw the snapshot onto the canvas, scaling it if canvas dimensions changed
        if (snapshot.getWidth() > 0 && snapshot.getHeight() > 0) {
            gc.drawImage(snapshot, 0, 0, snapshot.getWidth(), snapshot.getHeight(),
                                    0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        } else {
            System.err.println("Snapshot image has zero dimensions, cannot restore.");
        }
        updateUndoRedoButtonStates();
    }

    private void undoCanvas() {
        if (historyIndex > 0) { // Can't undo past the first state (initial white canvas)
            historyIndex--;
            restoreCanvasStateForHistory(historyIndex);
            System.out.println("Undo: historyIndex = " + historyIndex);
        }
    }

    private void redoCanvas() {
        if (historyIndex < canvasHistory.size() - 1) {
            historyIndex++;
            restoreCanvasStateForHistory(historyIndex);
            System.out.println("Redo: historyIndex = " + historyIndex);
        }
    }

    private void updateUndoRedoButtonStates() {
        if (undoButton != null) {
            undoButton.setDisable(historyIndex <= 0); // Disable undo if at initial state or no history
        }
        if (redoButton != null) {
            redoButton.setDisable(historyIndex >= canvasHistory.size() - 1); // Disable redo if at latest state
        }
    }

    private void downloadArt() {
        if (drawingCanvas == null || primaryStageRef == null || drawingCanvas.getWidth() <=0 || drawingCanvas.getHeight() <=0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot download: Canvas or Window not ready.");
            alert.setHeaderText(null);
            alert.showAndWait();
            System.err.println("Cannot download: Canvas or Stage not ready.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Artwork");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(primaryStageRef); // Use the dashboard's primary stage

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int)drawingCanvas.getWidth(), (int)drawingCanvas.getHeight());
                drawingCanvas.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("Artwork saved to: " + file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Artwork saved successfully to: " + file.getName());
                alert.setHeaderText(null);
                alert.showAndWait();
            } catch (IOException ex) {
                System.err.println("Error saving artwork: " + ex.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save image: " + ex.getMessage());
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }
    }

    // --- New function to save current canvas to gallery ---
    private void saveArtworkToGallery() {
        if (drawingCanvas == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Canvas is empty or not ready to save.");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Untitled Artwork");
        dialog.setTitle("Save Artwork to Gallery");
        dialog.setHeaderText("Enter a title for your artwork:");
        dialog.setContentText("Title:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            if (title.trim().isEmpty()) {
                title = "Untitled Artwork";
            }

            WritableImage currentArtwork = new WritableImage((int)drawingCanvas.getWidth(), (int)drawingCanvas.getHeight());
            drawingCanvas.snapshot(null, currentArtwork);

            ArtworkData newArtwork = new ArtworkData(
                    savedArtworks.size() + 1, // Simple ID
                    title,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // Current Date
                    selectedMoodTags.isEmpty() ? "Mixed Mood" : String.join(", ", selectedMoodTags), // Moods
                    currentArtwork
            );

            savedArtworks.add(0, newArtwork); // Add to beginning to show latest first
            System.out.println("Artwork '" + title + "' saved to gallery.");

            // Clear canvas and history for new drawing after saving
            Platform.runLater(() -> {
                gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
                redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight());
                canvasHistory.clear();
                historyIndex = -1;
                initialCanvasSetupDone.set(false); // Allow re-initialization of history for new drawing
                updateUndoRedoButtonStates();
                // Take a new initial snapshot for the now blank canvas
                Platform.runLater(() -> {
                    if (drawingCanvas.getWidth() > 0 && drawingCanvas.getHeight() > 0 && !initialCanvasSetupDone.get()) {
                        redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight());
                        saveCanvasStateForHistory();
                        initialCanvasSetupDone.set(true);
                    }
                });
            });

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Artwork '" + title + "' added to your gallery!");
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    // --- New function to load artwork from gallery to canvas ---
    private void loadArtworkToCanvas(WritableImage artworkImage) {
        if (drawingCanvas == null || gc == null || artworkImage == null || drawingCanvas.getWidth() <= 0 || drawingCanvas.getHeight() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot load artwork: Canvas not ready or image missing.");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        // Clear current canvas state
        redrawCanvasBackground(drawingCanvas.getWidth(), drawingCanvas.getHeight());

        // Draw the selected artwork onto the canvas, scaled to fit
        if (artworkImage.getWidth() > 0 && artworkImage.getHeight() > 0) {
            // Clear history and add the loaded image as the first state
            canvasHistory.clear();
            historyIndex = -1;
            gc.drawImage(artworkImage, 0, 0, artworkImage.getWidth(), artworkImage.getHeight(),
                                    0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
            saveCanvasStateForHistory(); // Save the loaded image as the first state
        } else {
            System.err.println("Attempted to load artwork with zero dimensions.");
        }

        // Switch to Create Art tab
        artContentTabPane.getSelectionModel().select(createArtTab);
        System.out.println("Artwork loaded to canvas.");
    }


    // --- Content for "My Gallery" Tab ---
    private VBox createMyGalleryTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);
        content.setStyle(BACKGROUND_COLOR_LIGHT_GREY);
        VBox.setVgrow(content, Priority.ALWAYS); // Gallery content should also stretch

        // "Your Art Gallery" Header Card
        VBox galleryHeaderCard = new VBox(10);
        galleryHeaderCard.setPadding(new Insets(20));
        galleryHeaderCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT);
        galleryHeaderCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(galleryHeaderCard, Priority.ALWAYS);

        Label galleryTitle = new Label("Your Art Gallery");
        galleryTitle.setFont(Font.font("Arial Bold", 16));
        galleryTitle.setStyle(TEXT_COLOR_DARK_GREY);

        Label galleryDescription = new Label("View and manage your expressive artwork collection");
        galleryDescription.setFont(Font.font("Arial", 12));
        galleryDescription.setStyle(TEXT_COLOR_GREY);
        galleryDescription.setWrapText(true);

        galleryHeaderCard.getChildren().addAll(galleryTitle, galleryDescription);

        // Artworks Grid/FlowPane - will be updated dynamically
        FlowPane artworksGrid = new FlowPane(20, 20); // Horizontal and vertical gap
        artworksGrid.setPadding(new Insets(10, 0, 10, 0));
        artworksGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(artworksGrid, Priority.ALWAYS);
        VBox.setVgrow(artworksGrid, Priority.ALWAYS);

        // Bind artworksGrid's children to the observableList
        savedArtworks.addListener((javafx.collections.ListChangeListener.Change<? extends ArtworkData> change) -> {
            Platform.runLater(() -> repopulateArtworksGrid(artworksGrid));
        });
        repopulateArtworksGrid(artworksGrid); // Initial population

        content.getChildren().addAll(galleryHeaderCard, artworksGrid); // No internal tab buttons here, handled above
        return content;
    }

    private void repopulateArtworksGrid(FlowPane artworksGrid) {
        artworksGrid.getChildren().clear();
        if (savedArtworks.isEmpty()) {
            Label noArtLabel = new Label("No artworks saved yet. Start creating!");
            noArtLabel.setStyle(TEXT_COLOR_GREY + "-fx-font-size: 14px;");
            artworksGrid.getChildren().add(noArtLabel);
            return;
        }

        for (ArtworkData artwork : savedArtworks) {
            VBox artworkCard = new VBox(5);
            artworkCard.setPrefWidth(200);
            artworkCard.setMaxWidth(200);
            artworkCard.setPadding(new Insets(15));
            artworkCard.setStyle(BACKGROUND_COLOR_WHITE + BORDER_STYLE_LIGHT + "-fx-cursor: hand; -fx-background-radius: 8px;");

            // --- Artwork Image or Placeholder ---
            StackPane imageContainer = new StackPane();
            imageContainer.setPrefSize(180, 180); // Fixed size for display consistency
            imageContainer.setStyle("-fx-background-radius: 5px;");

            if (artwork.image != null) {
                ImageView imageView = new ImageView(artwork.image);
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
                imageContainer.getChildren().add(imageView);
                imageContainer.setStyle(imageContainer.getStyle() + "-fx-background-color: white;");
            } else {
                // Placeholder for empty image (e.g., if loaded from non-visual data)
                imageContainer.setStyle(imageContainer.getStyle() + "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #e0f2ff, #ede0ff);");
                Label paletteIcon = new Label("\uD83C\uDFA8");
                paletteIcon.setFont(new Font("Arial", 48));
                paletteIcon.setTextFill(Color.web("#808080"));
                imageContainer.getChildren().add(paletteIcon);
            }
            imageContainer.setStyle(imageContainer.getStyle() + "-fx-border-color: #f0f0f0; -fx-border-width: 1px;");


            Label artworkTitle = new Label(artwork.title);
            artworkTitle.setFont(Font.font("Arial Bold", 14));
            artworkTitle.setStyle(TEXT_COLOR_DARK_GREY);

            HBox dateAndMood = new HBox();
            dateAndMood.setAlignment(Pos.CENTER_LEFT);
            Label artworkDate = new Label(artwork.date);
            artworkDate.setFont(Font.font("Arial", 11));
            artworkDate.setStyle(TEXT_COLOR_GREY);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label moodBadge = new Label(artwork.mood);
            moodBadge.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; " + TEXT_COLOR_DARK_GREY + ";");

            dateAndMood.getChildren().addAll(artworkDate, spacer, moodBadge);

            artworkCard.getChildren().addAll(imageContainer, artworkTitle, dateAndMood);

            // --- Navigation Functionality for Artwork Cards (load to canvas on click) ---
            artworkCard.setOnMouseClicked(e -> {
                loadArtworkToCanvas(artwork.image);
            });

            artworksGrid.getChildren().add(artworkCard);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Canvas Utility Methods (Undo/Redo, Draw Background, Download)
    //------------------------------------------------------------------------------------------------------------------

    private void redrawCanvasBackground(double width, double height) { // Fixed: 'double double height' to 'double height'
        if (gc == null || width <= 0 || height <= 0) {
            System.err.println("Cannot redraw background: GraphicsContext or dimensions invalid.");
            return;
        }
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
    }
}