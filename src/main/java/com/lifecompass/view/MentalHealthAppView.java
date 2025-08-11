
// package com.lifecompass.view;

// import javafx.application.Platform;
// import javafx.beans.binding.Bindings;
// import javafx.beans.property.SimpleStringProperty;
// import javafx.beans.property.StringProperty;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.geometry.Bounds;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Node;
// import javafx.scene.chart.BarChart;
// import javafx.scene.chart.CategoryAxis;
// import javafx.scene.chart.LineChart;
// import javafx.scene.chart.NumberAxis;
// import javafx.scene.chart.PieChart;
// import javafx.scene.chart.XYChart;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.Tooltip;
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;

// public class MentalHealthAppView {

//     // --- Dynamic Properties for Metrics (Overview Tab) ---
//     private StringProperty avgMoodValueProperty;
//     private StringProperty avgMoodChangeProperty;
//     private StringProperty moodStabilityValueProperty;
//     private StringProperty moodStabilityDescProperty;
//     private StringProperty bestDayValueProperty;
//     private StringProperty bestDayAvgProperty;
//     private StringProperty entriesThisWeekValueProperty;
//     private StringProperty entriesThisWeekDescProperty;

//     // --- Dynamic Data for Charts (Overview Tab) ---
//     private ObservableList<XYChart.Data<String, Number>> overviewMoodSeriesData;
//     private ObservableList<XYChart.Data<String, Number>> overviewEnergySeriesData;
//     private ObservableList<PieChart.Data> overviewMoodDistributionData;

//     // --- Tab Management ---
//     private StringProperty selectedAnalyticsSubTab = new SimpleStringProperty("Overview");
//     private StackPane analyticsContentStack;
//     private VBox moodDistributionLegendVBox; // Used for dynamic legend updates

//     // --- Trends Screen Data ---
//     private ObservableList<XYChart.Data<String, Number>> trendsMonthlyMoodTrendBarData;
//     private StringProperty trendsSleepQualityImpactProperty;
//     private StringProperty trendsExerciseFrequencyProperty;
//     private StringProperty trendsSocialInteractionProperty;

//     // --- Pattern View Data ---
//     private ObservableList<MoodTriggerItem> patternTopMoodTriggersData;
//     private ObservableList<TimePatternItem> patternTimeBasedPatternsData;
//     private VBox topMoodTriggersSectionContent; // To update Pattern tab lists dynamically
//     private VBox timeBasedPatternsSectionContent; // To update Pattern tab lists dynamically


//     // --- Insights View Data ---
//     private StringProperty insightsKeyInsightProperty;
//     private StringProperty insightsPositivePatternProperty;
//     private StringProperty insightsAreaForImprovementProperty;
//     private StringProperty insightsRecommendationProperty;

//     // Constructor: Initialize properties and empty lists/properties. Controller will populate them.
//     public MentalHealthAppView() {
//         initializeProperties();
//         initializeEmptyChartData();
//         initializeEmptyTrendsData();
//         initializeEmptyPatternData();
//         initializeEmptyInsightsData();
//     }

//     public Node createAnalyticsDashboardContent() {
//         BorderPane root = new BorderPane();
//         root.setStyle("-fx-background-color: #F5F5F5;");

//         VBox analyticsDashboardContainer = createAnalyticsDashboardContainer();
//         analyticsDashboardContainer.setPadding(new Insets(20));
//         analyticsDashboardContainer.setSpacing(20);
//         VBox.setVgrow(analyticsDashboardContainer, Priority.ALWAYS);

//         ScrollPane scrollPane = new ScrollPane();
//         scrollPane.setContent(analyticsDashboardContainer);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

//         root.setCenter(scrollPane);

//         selectedAnalyticsSubTab.addListener((obs, oldVal, newVal) -> {
//             updateAnalyticsContent(newVal);
//         });

//         Platform.runLater(() -> updateAnalyticsContent(selectedAnalyticsSubTab.get()));

//         return root;
//     }

//     private void initializeProperties() {
//         // Initialize with defaults/empty values, as data will be pushed dynamically
//         avgMoodValueProperty = new SimpleStringProperty("N/A");
//         avgMoodChangeProperty = new SimpleStringProperty("N/A");
//         moodStabilityValueProperty = new SimpleStringProperty("N/A");
//         moodStabilityDescProperty = new SimpleStringProperty("No data");
//         bestDayValueProperty = new SimpleStringProperty("N/A");
//         bestDayAvgProperty = new SimpleStringProperty("N/A");
//         entriesThisWeekValueProperty = new SimpleStringProperty("0");
//         entriesThisWeekDescProperty = new SimpleStringProperty("No entries");
//     }

//     private void initializeEmptyChartData() {
//         overviewMoodSeriesData = FXCollections.observableArrayList();
//         overviewEnergySeriesData = FXCollections.observableArrayList();
//         overviewMoodDistributionData = FXCollections.observableArrayList();
//     }

//     private void initializeEmptyTrendsData() {
//         trendsMonthlyMoodTrendBarData = FXCollections.observableArrayList();
//         trendsSleepQualityImpactProperty = new SimpleStringProperty("Calculating...");
//         trendsExerciseFrequencyProperty = new SimpleStringProperty("Calculating...");
//         trendsSocialInteractionProperty = new SimpleStringProperty("Calculating...");
//     }

//     private void initializeEmptyPatternData() {
//         patternTopMoodTriggersData = FXCollections.observableArrayList();
//         patternTimeBasedPatternsData = FXCollections.observableArrayList();
//     }

//     private void initializeEmptyInsightsData() {
//         insightsKeyInsightProperty = new SimpleStringProperty("No insights yet.");
//         insightsPositivePatternProperty = new SimpleStringProperty("No positive patterns identified.");
//         insightsAreaForImprovementProperty = new SimpleStringProperty("No areas for improvement identified.");
//         insightsRecommendationProperty = new SimpleStringProperty("Keep tracking your mood for personalized recommendations!");
//     }

//     // --- Update Methods for Overview Tab Data ---
//     public void updateMetrics(String newAvgMood, String newAvgMoodChange,
//                               String newMoodStability, String newMoodStabilityDesc,
//                               String newBestDay, String newBestDayAvg,
//                               String newEntriesThisWeek) {
//         avgMoodValueProperty.set(newAvgMood);
//         avgMoodChangeProperty.set(newAvgMoodChange);
//         moodStabilityValueProperty.set(newMoodStability);
//         moodStabilityDescProperty.set(newMoodStabilityDesc);
//         bestDayValueProperty.set(newBestDay);
//         bestDayAvgProperty.set(newBestDayAvg);
//         entriesThisWeekValueProperty.set(newEntriesThisWeek);
//         entriesThisWeekDescProperty.set(newEntriesThisWeek.equals("0") ? "No entries" : "Daily tracking");
//     }

//     public void updateOverviewWeeklyMoodTrend(ObservableList<XYChart.Data<String, Number>> newMoodData,
//                                               ObservableList<XYChart.Data<String, Number>> newEnergyData) {
//         overviewMoodSeriesData.setAll(newMoodData);
//         overviewEnergySeriesData.setAll(newEnergyData);
//     }

//     public void updateOverviewMoodDistribution(ObservableList<PieChart.Data> newMoodDistributionData) {
//         overviewMoodDistributionData.setAll(newMoodDistributionData);
//         rebuildMoodDistributionLegend(newMoodDistributionData); // Rebuild legend when data changes
//     }

//     // --- Update Methods for Trends Tab Data ---
//     public void updateTrendsMonthlyMoodTrend(ObservableList<XYChart.Data<String, Number>> newMonthlyBarData) {
//         trendsMonthlyMoodTrendBarData.setAll(newMonthlyBarData);
//     }

//     public void updateTrendsMoodImprovementAreas(String sleepImpact, String exerciseImpact, String socialImpact) {
//         trendsSleepQualityImpactProperty.set(sleepImpact);
//         trendsExerciseFrequencyProperty.set(exerciseImpact);
//         trendsSocialInteractionProperty.set(socialImpact);
//     }

//     // --- Update Methods for Patterns Tab Data ---
//     public void updatePatternTopMoodTriggers(ObservableList<MoodTriggerItem> newTriggersData) {
//         patternTopMoodTriggersData.setAll(newTriggersData);
//         if (topMoodTriggersSectionContent != null) {
//             topMoodTriggersSectionContent.getChildren().removeIf(node -> node instanceof HBox); // Remove old items
//             for (MoodTriggerItem item : patternTopMoodTriggersData) {
//                 topMoodTriggersSectionContent.getChildren().add(createTriggerItem(item)); // Add new items
//             }
//         }
//     }

//     public void updatePatternTimeBasedPatterns(ObservableList<TimePatternItem> newTimePatternsData) {
//         patternTimeBasedPatternsData.setAll(newTimePatternsData);
//         if (timeBasedPatternsSectionContent != null) {
//             timeBasedPatternsSectionContent.getChildren().removeIf(node -> node instanceof HBox); // Remove old items
//             for (TimePatternItem item : patternTimeBasedPatternsData) {
//                 timeBasedPatternsSectionContent.getChildren().add(createTimePatternItem(item)); // Add new items
//             }
//         }
//     }

//     // --- Update Methods for Insights Tab Data ---
//     public void updateInsights(String keyInsight, String positivePattern, String areaForImprovement, String recommendation) {
//         this.insightsKeyInsightProperty.set(keyInsight);
//         this.insightsPositivePatternProperty.set(positivePattern);
//         this.insightsAreaForImprovementProperty.set(areaForImprovement);
//         this.insightsRecommendationProperty.set(recommendation);
//         // Note: Tooltips are bound to properties so they should update automatically
//     }

//     public void updateMainInsight(String newInsight) {
//         this.insightsKeyInsightProperty.set(newInsight);
//     }

//     public void updateRecommendation(String newRecommendation) {
//         this.insightsRecommendationProperty.set(newRecommendation);
//     }

//     // This createTopNavBar is likely redundant if MentalHealthAppView is embedded in another app.
//     // Consider if you really need it, or if your main application's header handles this.
//     private HBox createTopNavBar() {
//         HBox topNavBar = new HBox(10);
//         topNavBar.setAlignment(Pos.CENTER_LEFT);
//         topNavBar.setPadding(new Insets(15, 25, 15, 25));
//         topNavBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

//         Label logo = new Label("LifeCompass");
//         logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//         logo.setTextFill(Color.web("#333333"));
//         Circle logoIcon = new Circle(8, Color.web("#6A5ACD"));
//         HBox logoBox = new HBox(5, logoIcon, logo);
//         logoBox.setAlignment(Pos.CENTER_LEFT);

//         Label welcomeLabel = new Label("Welcome back, Sarah!");
//         welcomeLabel.setFont(Font.font("Arial", 14));
//         welcomeLabel.setTextFill(Color.web("#666666"));

//         Region spacerLeft = new Region();
//         HBox.setHgrow(spacerLeft, Priority.ALWAYS);

//         Label settingsIcon = new Label("\u2699");
//         settingsIcon.setFont(Font.font("Arial", 20));
//         settingsIcon.setTextFill(Color.web("#666666"));

//         Label bellIcon = new Label("\uD83D\uDD14");
//         bellIcon.setFont(Font.font("Arial", 20));
//         bellIcon.setTextFill(Color.web("#666666"));

//         Label closeButton = new Label("Close");
//         closeButton.setFont(Font.font("Arial", 14));
//         closeButton.setTextFill(Color.web("#666666"));
//         closeButton.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");

//         topNavBar.getChildren().addAll(logoBox, welcomeLabel, spacerLeft, settingsIcon, bellIcon, closeButton);
//         return topNavBar;
//     }

//     private VBox createAnalyticsDashboardContainer() {
//         VBox container = new VBox(20);
//         container.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

//         VBox header = new VBox(5);
//         Label title = new Label("Mood Analytics Dashboard");
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//         title.setTextFill(Color.web("#333333"));

//         Label description = new Label("Visualize your emotional patterns and gain insights into your mental wellness journey");
//         description.setFont(Font.font("Arial", 14));
//         description.setTextFill(Color.web("#666666"));
//         header.getChildren().addAll(title, description);
//         container.getChildren().add(header);

//         HBox subTabs = createSubTabs();
//         container.getChildren().add(subTabs);

//         analyticsContentStack = new StackPane();
//         analyticsContentStack.setPadding(new Insets(0, 0, 0, 0));
//         VBox.setVgrow(analyticsContentStack, Priority.ALWAYS);

//         analyticsContentStack.getChildren().addAll(
//             createOverviewContent(),
//             createTrendsLayout(),
//             createPatternLayout(),
//             createInsightsLayout()
//         );

//         container.getChildren().add(analyticsContentStack);

//         return container;
//     }

//     private void updateAnalyticsContent(String selectedTab) {
//         for (Node node : analyticsContentStack.getChildren()) {
//             node.setVisible(false);
//             node.setManaged(false);
//         }

//         Node contentToShow = null;
//         switch (selectedTab) {
//             case "Overview":
//                 contentToShow = analyticsContentStack.getChildren().get(0);
//                 break;
//             case "Trends":
//                 contentToShow = analyticsContentStack.getChildren().get(1);
//                 break;
//             case "Patterns":
//                 contentToShow = analyticsContentStack.getChildren().get(2);
//                 break;
//             case "Insights":
//                 contentToShow = analyticsContentStack.getChildren().get(3);
//                 break;
//         }

//         if (contentToShow != null) {
//             contentToShow.setVisible(true);
//             contentToShow.setManaged(true);
//         }
//     }

//     private HBox createSubTabs() {
//         HBox subTabs = new HBox(10);
//         subTabs.setPadding(new Insets(0));
//         subTabs.setMaxWidth(Double.MAX_VALUE);
//         HBox.setHgrow(subTabs, Priority.ALWAYS);

//         String[] tabNames = {"Overview", "Trends", "Patterns", "Insights"};

//         for (String tabName : tabNames) {
//             Label tabLabel = new Label(tabName);
//             tabLabel.setFont(Font.font("Arial", 14));
//             tabLabel.setPadding(new Insets(8, 15, 8, 15));
//             tabLabel.setTextFill(Color.web("#666666"));

//             HBox.setHgrow(tabLabel, Priority.ALWAYS);
//             tabLabel.setMaxWidth(Double.MAX_VALUE);
//             tabLabel.setAlignment(Pos.CENTER);

//             tabLabel.styleProperty().bind(
//                 Bindings.when(selectedAnalyticsSubTab.isEqualTo(tabName))
//                     .then("-fx-background-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;")
//                     .otherwise("-fx-background-color: transparent; -fx-border-color: transparent;")
//             );
//             tabLabel.textFillProperty().bind(
//                 Bindings.when(selectedAnalyticsSubTab.isEqualTo(tabName))
//                     .then(Color.web("#333333"))
//                     .otherwise(Color.web("#666666"))
//             );

//             tabLabel.setOnMouseClicked(event -> selectedAnalyticsSubTab.set(tabName));
//             subTabs.getChildren().add(tabLabel);
//         }
//         return subTabs;
//     }

//     // --- Content for the "Overview" Sub-Tab ---
//     private VBox createOverviewContent() {
//         VBox overviewLayout = new VBox(20);
//         overviewLayout.setPadding(new Insets(0, 20, 20, 20));

//         GridPane metricBoxes = createMetricBoxes();
//         overviewLayout.getChildren().add(metricBoxes);

//         VBox weeklyMoodTrendSection = createWeeklyMoodTrendChart();
//         overviewLayout.getChildren().add(weeklyMoodTrendSection);

//         VBox moodDistributionSection = createMoodDistributionChart();
//         overviewLayout.getChildren().add(moodDistributionSection);

//         return overviewLayout;
//     }

//     private GridPane createMetricBoxes() {
//         GridPane grid = new GridPane();
//         grid.setHgap(20);
//         grid.setVgap(20);
//         grid.setAlignment(Pos.CENTER_LEFT);

//         ColumnConstraints col1 = new ColumnConstraints();
//         col1.setHgrow(Priority.ALWAYS);
//         col1.setPercentWidth(25);
//         ColumnConstraints col2 = new ColumnConstraints();
//         col2.setHgrow(Priority.ALWAYS);
//         col2.setPercentWidth(25);
//         ColumnConstraints col3 = new ColumnConstraints();
//         col3.setHgrow(Priority.ALWAYS);
//         col3.setPercentWidth(25);
//         ColumnConstraints col4 = new ColumnConstraints();
//         col4.setHgrow(Priority.ALWAYS);
//         col4.setPercentWidth(25);
//         grid.getColumnConstraints().addAll(col1, col2, col3, col4);

//         grid.add(createDynamicMetricBox("Average Mood", avgMoodValueProperty, avgMoodChangeProperty, "#00C853"), 0, 0);
//         grid.add(createDynamicMetricBox("Mood Stability", moodStabilityValueProperty, moodStabilityDescProperty, "#007BFF"), 1, 0);
//         grid.add(createDynamicMetricBox("Best Day", bestDayValueProperty, bestDayAvgProperty, "#6A5ACD"), 2, 0);
//         grid.add(createDynamicMetricBox("Entries This Week", entriesThisWeekValueProperty, entriesThisWeekDescProperty, "#FFC107"), 3, 0);

//         return grid;
//     }

//     private VBox createDynamicMetricBox(String title, StringProperty valueProperty, StringProperty descriptionProperty, String valueColorHex) {
//         VBox box = new VBox(5);
//         box.setPadding(new Insets(20));
//         box.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

//         Label titleLabel = new Label(title);
//         titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
//         titleLabel.setTextFill(Color.web("#666666"));

//         Label valueLabel = new Label();
//         valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//         valueLabel.setTextFill(Color.web(valueColorHex));
//         valueLabel.textProperty().bind(valueProperty);

//         Label descriptionLabel = new Label();
//         descriptionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
//         descriptionLabel.setTextFill(Color.web("#999999"));
//         descriptionLabel.textProperty().bind(descriptionProperty);

//         box.getChildren().addAll(titleLabel, valueLabel, descriptionLabel);
//         return box;
//     }

//     private VBox createWeeklyMoodTrendChart() {
//         VBox chartSection = new VBox(10);
//         Label sectionTitle = new Label("Weekly Mood Trend");
//         sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//         sectionTitle.setTextFill(Color.web("#333333"));

//         Label sectionDescription = new Label("Your mood and energy levels over the past week");
//         sectionDescription.setFont(Font.font("Arial", 14));
//         sectionDescription.setTextFill(Color.web("#666666"));

//         CategoryAxis xAxis = new CategoryAxis();
//         NumberAxis yAxis = new NumberAxis(0, 10, 1);
//         yAxis.setLabel("");
//         xAxis.setLabel("");
//         xAxis.setTickLabelsVisible(true);
//         yAxis.setTickLabelsVisible(true);
//         xAxis.setTickMarkVisible(false);
//         yAxis.setTickMarkVisible(false);

//         LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
//         lineChart.setTitle("");
//         lineChart.setLegendVisible(false);
//         lineChart.setCreateSymbols(true);
//         lineChart.setPrefHeight(300);
//         lineChart.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

//         lineChart.setHorizontalGridLinesVisible(true);
//         lineChart.setVerticalGridLinesVisible(true);
//         lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
//         lineChart.lookup(".chart-vertical-grid-lines").setStyle("-fx-stroke: #E0E0E0;");
//         lineChart.lookup(".chart-horizontal-grid-lines").setStyle("-fx-stroke: #E0E0E0;");

//         XYChart.Series<String, Number> series1 = new XYChart.Series<>();
//         series1.setName("Mood");
//         series1.setData(overviewMoodSeriesData);

//         XYChart.Series<String, Number> series2 = new XYChart.Series<>();
//         series2.setName("Energy");
//         series2.setData(overviewEnergySeriesData);

//         lineChart.getData().addAll(series1, series2);

//         // Apply styles and tooltips for Mood series
//         series1.nodeProperty().addListener((obs, oldNode, newNode) -> {
//             if (newNode != null) {
//                 newNode.setStyle("-fx-stroke: #28A745; -fx-stroke-width: 2px;");
//             }
//         });
//         overviewMoodSeriesData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
//             Platform.runLater(() -> {
//                 while (c.next()) {
//                     if (c.wasAdded() || c.wasUpdated()) {
//                         for (XYChart.Data<String, Number> data : c.getAddedSubList()) {
//                             if (data.getNode() != null) {
//                                 data.getNode().setStyle("-fx-background-color: #28A745, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
//                                 addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                             }
//                         }
//                     }
//                 }
//             });
//         });

//         // Apply styles and tooltips for Energy series
//         series2.nodeProperty().addListener((obs, oldNode, newNode) -> {
//             if (newNode != null) {
//                 newNode.setStyle("-fx-stroke: #007BFF; -fx-stroke-width: 2px;");
//             }
//         });
//         overviewEnergySeriesData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
//             Platform.runLater(() -> {
//                 while (c.next()) {
//                     if (c.wasAdded() || c.wasUpdated()) {
//                         for (XYChart.Data<String, Number> data : c.getAddedSubList()) {
//                             if (data.getNode() != null) {
//                                 data.getNode().setStyle("-fx-background-color: #007BFF, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
//                                 addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                             }
//                         }
//                     }
//                 }
//             });
//         });

//         // Apply initial symbol styles and add tooltips (for initial data or after data reset)
//         Platform.runLater(() -> {
//             for (XYChart.Data<String, Number> data : series1.getData()) {
//                 if (data.getNode() != null) {
//                     data.getNode().setStyle("-fx-background-color: #28A745, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
//                     addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                 }
//             }
//             for (XYChart.Data<String, Number> data : series2.getData()) {
//                 if (data.getNode() != null) {
//                     data.getNode().setStyle("-fx-background-color: #007BFF, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
//                     addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                 }
//             }
//         });

//         chartSection.getChildren().addAll(sectionTitle, sectionDescription, lineChart);
//         return chartSection;
//     }

//     private VBox createMoodDistributionChart() {
//         VBox chartSection = new VBox(10);
//         Label sectionTitle = new Label("Mood Distribution");
//         sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//         sectionTitle.setTextFill(Color.web("#333333"));

//         Label sectionDescription = new Label("How your emotions are distributed this month");
//         sectionDescription.setFont(Font.font("Arial", 14));
//         sectionDescription.setTextFill(Color.web("#666666"));

//         final PieChart pieChart = new PieChart(overviewMoodDistributionData);
//         pieChart.setTitle("");
//         pieChart.setLegendVisible(false);
//         pieChart.setLabelsVisible(false);
//         pieChart.setStartAngle(90);

//         StackPane donutChartContainer = new StackPane();
//         donutChartContainer.setPrefSize(200, 200); // Fixed size for the container to maintain circular shape

//         Circle donutHole = new Circle(60);
//         donutHole.setFill(Color.WHITE);

//         donutChartContainer.getChildren().addAll(pieChart, donutHole);

//         // Listener for circularity and tooltips
//         pieChart.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
//             double radius = Math.min(newBounds.getWidth(), newBounds.getHeight()) / 2.0;
//         });

//         overviewMoodDistributionData.addListener((javafx.collections.ListChangeListener.Change<? extends PieChart.Data> c) -> {
//             Platform.runLater(() -> {
//                 while (c.next()) {
//                     if (c.wasAdded() || c.wasUpdated()) {
//                         for (PieChart.Data data : c.getAddedSubList()) {
//                             applyPieChartSliceColor(data);
//                             if (data.getNode() != null) {
//                                 addChartTooltip(data.getNode(), data.getName() + ": " + ((int) data.getPieValue()) + "%");
//                             }
//                         }
//                     }
//                     if (c.wasRemoved() || c.wasAdded() || c.wasUpdated()) {
//                         rebuildMoodDistributionLegend(overviewMoodDistributionData);
//                     }
//                 }
//             });
//         });
//         Platform.runLater(() -> {
//             overviewMoodDistributionData.forEach(data -> {
//                 applyPieChartSliceColor(data);
//                 if (data.getNode() != null) {
//                     addChartTooltip(data.getNode(), data.getName() + ": " + ((int) data.getPieValue()) + "%");
//                 }
//             });
//         });

//         moodDistributionLegendVBox = new VBox(8);
//         moodDistributionLegendVBox.setAlignment(Pos.CENTER_LEFT);
//         moodDistributionLegendVBox.setPadding(new Insets(0, 0, 0, 20));

//         rebuildMoodDistributionLegend(overviewMoodDistributionData);

//         HBox chartAndLegend = new HBox(30);
//         chartAndLegend.setAlignment(Pos.CENTER_LEFT);
//         chartAndLegend.getChildren().addAll(donutChartContainer, moodDistributionLegendVBox);

//         chartSection.getChildren().addAll(sectionTitle, sectionDescription, chartAndLegend);
//         return chartSection;
//     }

//     private void rebuildMoodDistributionLegend(ObservableList<PieChart.Data> dataList) {
//         if (moodDistributionLegendVBox != null) {
//             moodDistributionLegendVBox.getChildren().clear();
//             // Sort data by percentage for consistent legend order, or by a predefined order
//             dataList.stream()
//                     .sorted(java.util.Comparator.comparingDouble(PieChart.Data::getPieValue).reversed())
//                     .forEach(data -> {
//                         String color = getMoodColor(data.getName());
//                         moodDistributionLegendVBox.getChildren().add(createLegendItem(data.getName(), String.format("%.0f%%", data.getPieValue()), color));
//                     });
//         }
//     }

//     private void applyPieChartSliceColor(PieChart.Data data) {
//         String color = getMoodColor(data.getName());
//         if (data.getNode() != null) {
//             data.getNode().setStyle("-fx-pie-color: " + color + ";");
//         }
//     }

//     private String getMoodColor(String moodName) {
//         switch (moodName) {
//             case "Happy": return "#28A745"; // Green
//             case "😊": return "#28A745";
//             case "😃": return "#28A745";
//             case "Calm": return "#007BFF"; // Blue
//             case "😌": return "#007BFF";
//             case "Neutral": return "#FFC107"; // Yellow/Orange
//             case "😐": return "#FFC107";
//             case "Anxious": return "#DC3545"; // Red
//             case "😟": return "#DC3545";
//             case "😖": return "#DC3545";
//             case "Sad": return "#6C757D"; // Grey
//             case "😔": return "#6C757D";
//             case "😢": return "#6C757D";
//             case "Angry": return "#6C757D";
//             case "😠": return "#6C757D";
//             case "😤": return "#6C757D";
//             case "Tired": return "#999999"; // Darker Grey
//             case "😴": return "#999999";
//             case "Surprised": return "#FF8C00"; // Darker Orange
//             case "😮": return "#FF8C00";
//             case "Confused": return "#9370DB"; // Medium Purple
//             case "🤔": return "#9370DB";
//             case "Sick": return "#808000"; // Olive
//             case "😷": return "#808000";
//             default: return "#CCCCCC";
//         }
//     }

//     private HBox createLegendItem(String mood, String percentage, String colorHex) {
//         HBox item = new HBox(8);
//         item.setAlignment(Pos.CENTER_LEFT);

//         Circle colorIndicator = new Circle(6);
//         colorIndicator.setFill(Color.web(colorHex));

//         Label moodLabel = new Label(mood);
//         moodLabel.setFont(Font.font("Arial", 14));
//         moodLabel.setTextFill(Color.web("#333333"));

//         Label percentageLabel = new Label(percentage);
//         percentageLabel.setFont(Font.font("Arial", 14));
//         percentageLabel.setTextFill(Color.web("#666666"));
//         percentageLabel.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 2 5; -fx-border-radius: 3; -fx-background-radius: 3;");

//         item.getChildren().addAll(colorIndicator, moodLabel, percentageLabel);
//         return item;
//     }

//     // --- Content for the "Trends" Sub-Tab ---
//     private VBox createTrendsLayout() {
//         VBox trendsLayout = new VBox(20);
//         trendsLayout.setPadding(new Insets(0, 20, 20, 20));
//         trendsLayout.setSpacing(20);
//         trendsLayout.setVisible(false);
//         trendsLayout.setManaged(false);

//         Label trendsTitle = new Label("Monthly Mood Trends");
//         trendsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//         trendsTitle.setTextFill(Color.web("#333333"));

//         Label trendsDescription = new Label("Average mood scores over the past month");
//         trendsDescription.setFont(Font.font("Arial", 14));
//         trendsDescription.setTextFill(Color.web("#666666"));

//         CategoryAxis monthBarXAxis = new CategoryAxis();
//         NumberAxis monthBarYAxis = new NumberAxis(0, 10, 1);
//         monthBarXAxis.setLabel("");
//         monthBarYAxis.setLabel("");
//         monthBarXAxis.setTickLabelsVisible(true);
//         monthBarYAxis.setTickLabelsVisible(true);
//         monthBarXAxis.setTickMarkVisible(false);
//         monthBarYAxis.setTickMarkVisible(false);

//         BarChart<String, Number> monthlyMoodBarChart = new BarChart<>(monthBarXAxis, monthBarYAxis);
//         monthlyMoodBarChart.setTitle("");
//         monthlyMoodBarChart.setLegendVisible(false);
//         monthlyMoodBarChart.setPrefHeight(300);
//         monthlyMoodBarChart.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");
//         monthlyMoodBarChart.setHorizontalGridLinesVisible(true);
//         monthlyMoodBarChart.setVerticalGridLinesVisible(false);

//         XYChart.Series<String, Number> monthlyBarSeries = new XYChart.Series<>();
//         monthlyBarSeries.setName("Average Mood");
//         monthlyBarSeries.setData(trendsMonthlyMoodTrendBarData);
//         monthlyMoodBarChart.getData().add(monthlyBarSeries);

//         // Add Tooltip to Bar Chart bars
//         trendsMonthlyMoodTrendBarData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
//             Platform.runLater(() -> {
//                 for (XYChart.Series<String, Number> s : monthlyMoodBarChart.getData()) {
//                     for (XYChart.Data<String, Number> data : s.getData()) {
//                         if (data.getNode() != null) {
//                             data.getNode().setStyle("-fx-bar-fill: #007BFF;");
//                             addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                         }
//                     }
//                 }
//             });
//         });
//         Platform.runLater(() -> {
//             for (XYChart.Series<String, Number> s : monthlyMoodBarChart.getData()) {
//                 for (XYChart.Data<String, Number> data : s.getData()) {
//                     if (data.getNode() != null) {
//                         data.getNode().setStyle("-fx-bar-fill: #007BFF;");
//                         addChartTooltip(data.getNode(), data.getXValue() + ": " + data.getYValue());
//                     }
//                 }
//             }
//         });

//         VBox moodImprovementAreas = createMoodImprovementAreas();

//         trendsLayout.getChildren().addAll(trendsTitle, trendsDescription, monthlyMoodBarChart, moodImprovementAreas);
//         return trendsLayout;
//     }

//     private VBox createMoodImprovementAreas() {
//         VBox improvementAreasLayout = new VBox(10);
//         improvementAreasLayout.setPadding(new Insets(20));
//         improvementAreasLayout.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

//         Label sectionTitle = new Label("Mood Improvement Areas");
//         sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//         sectionTitle.setTextFill(Color.web("#333333"));

//         Label sectionDescription = new Label("Insights based on your mood patterns");
//         sectionDescription.setFont(Font.font("Arial", 14));
//         sectionDescription.setTextFill(Color.web("#666666"));

//         improvementAreasLayout.getChildren().addAll(sectionTitle, sectionDescription);

//         improvementAreasLayout.getChildren().add(createCorrelationItem("Sleep Quality Impact", trendsSleepQualityImpactProperty));
//         improvementAreasLayout.getChildren().add(createCorrelationItem("Exercise Frequency", trendsExerciseFrequencyProperty));
//         improvementAreasLayout.getChildren().add(createCorrelationItem("Social Interaction", trendsSocialInteractionProperty));

//         return improvementAreasLayout;
//     }

//     private HBox createCorrelationItem(String areaName, StringProperty correlationProperty) {
//         HBox item = new HBox(10);
//         item.setAlignment(Pos.CENTER_LEFT);
//         item.setPadding(new Insets(5, 0, 5, 0));

//         Label areaLabel = new Label(areaName);
//         areaLabel.setFont(Font.font("Arial", 14));
//         areaLabel.setTextFill(Color.web("#333333"));

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Label correlationLabel = new Label();
//         correlationLabel.textProperty().bind(correlationProperty);
//         correlationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
//         correlationLabel.setTextFill(Color.WHITE);
//         correlationLabel.setPadding(new Insets(3, 8, 3, 8));

//         correlationLabel.styleProperty().bind(
//             Bindings.createStringBinding(() -> {
//                 String correlationText = correlationProperty.get();
//                 String colorHex = getCorrelationColor(correlationText);
//                 return "-fx-background-color: " + colorHex + "; -fx-background-radius: 15; -fx-border-radius: 15;";
//             }, correlationProperty)
//         );

//         item.getChildren().addAll(areaLabel, spacer, correlationLabel);
//         return item;
//     }

//     private String getCorrelationColor(String correlationText) {
//         switch (correlationText) {
//             case "Strong correlation": return "#28A745";
//             case "Good correlation": return "#007BFF";
//             case "Moderate correlation": return "#FFC107";
//             case "Weak correlation": return "#DC3545";
//             default: return "#6C757D";
//         }
//     }

//     // --- Data Models for Patterns Tab ---
//     public static class MoodTriggerItem {
//         String trigger;
//         int occurrences;
//         String impact;

//         public MoodTriggerItem(String trigger, int occurrences, String impact) {
//             this.trigger = trigger;
//             this.occurrences = occurrences;
//             this.impact = impact;
//         }

//         public String getTrigger() { return trigger; }
//         public int getOccurrences() { return occurrences; }
//         public String getImpact() { return impact; }
//     }

//     public static class TimePatternItem {
//         String timeRange;
//         String moodDescription;
//         String emoji;

//         public TimePatternItem(String timeRange, String moodDescription, String emoji) {
//             this.timeRange = timeRange;
//             this.moodDescription = moodDescription;
//             this.emoji = emoji;
//         }

//         public String getTimeRange() { return timeRange; }
//         public String getMoodDescription() { return moodDescription; }
//         public String getEmoji() { return emoji; }
//     }

//     // --- Content for the "Patterns" Sub-Tab ---
//     private VBox createPatternLayout() {
//         VBox patternLayout = new VBox(20);
//         patternLayout.setPadding(new Insets(0, 20, 20, 20));
//         patternLayout.setSpacing(20);
//         patternLayout.setVisible(false);
//         patternLayout.setManaged(false);

//         topMoodTriggersSectionContent = createTopMoodTriggersSection(); // Store reference
//         timeBasedPatternsSectionContent = createTimeBasedPatternsSection(); // Store reference

//         patternLayout.getChildren().addAll(topMoodTriggersSectionContent, timeBasedPatternsSectionContent);
//         return patternLayout;
//     }

//     private VBox createTopMoodTriggersSection() {
//         VBox section = new VBox();
//         section.setSpacing(10);
//         section.setPadding(new Insets(20));
//         section.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
//         section.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

//         Label title = new Label("Top Mood Triggers");
//         title.setFont(Font.font("Inter", FontWeight.BOLD, 18));
//         title.setTextFill(Color.BLACK);

//         Label description = new Label("Events and activities that most impact your mood");
//         description.setFont(Font.font("Inter", 12));
//         description.setTextFill(Color.GREY);

//         section.getChildren().addAll(title, description);

//         // This section will be populated dynamically by updatePatternTopMoodTriggers
//         // No need to add initial static items here.

//         return section;
//     }

//     private HBox createTriggerItem(MoodTriggerItem item) {
//         HBox hBox = new HBox();
//         hBox.setAlignment(Pos.CENTER_LEFT);
//         hBox.setPadding(new Insets(15));
//         hBox.setSpacing(10);
//         hBox.setBackground(new Background(new BackgroundFill(Color.web("#F9F9F9"), new CornerRadii(8), Insets.EMPTY)));
//         hBox.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
//         HBox.setHgrow(hBox, Priority.ALWAYS);

//         VBox textContent = new VBox();
//         textContent.setSpacing(2);
//         Label triggerLabel = new Label(item.getTrigger());
//         triggerLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
//         triggerLabel.setTextFill(Color.BLACK);

//         Label occurrencesLabel = new Label("Occurred " + item.getOccurrences() + " times");
//         occurrencesLabel.setFont(Font.font("Inter", 12));
//         occurrencesLabel.setTextFill(Color.GREY);
//         textContent.getChildren().addAll(triggerLabel, occurrencesLabel);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Label impactTag = new Label(item.getImpact());
//         impactTag.setFont(Font.font("Inter", FontWeight.BOLD, 12));
//         impactTag.setTextFill(Color.WHITE);
//         impactTag.setPadding(new Insets(5, 10, 5, 10));
//         impactTag.setBackground(new Background(new BackgroundFill(
//             getImpactColor(item.getImpact()),
//             new CornerRadii(6), Insets.EMPTY)));

//         hBox.getChildren().addAll(textContent, spacer, impactTag);

//         // Add Tooltip to the entire HBox item
//         Tooltip.install(hBox, new Tooltip("Trigger: " + item.getTrigger() + "\nOccurrences: " + item.getOccurrences() + "\nImpact: " + item.getImpact()));

//         return hBox;
//     }

//     private Color getImpactColor(String impact) {
//         switch (impact) {
//             case "High": return Color.web("#EF4444");
//             case "Positive": return Color.web("#22C55E");
//             case "Mixed": return Color.web("#F59E0B");
//             default: return Color.web("#9CA3AF");
//         }
//     }

//     private VBox createTimeBasedPatternsSection() {
//         VBox section = new VBox();
//         section.setSpacing(10);
//         section.setPadding(new Insets(20));
//         section.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
//         section.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

//         Label title = new Label("Time-based Patterns");
//         title.setFont(Font.font("Inter", FontWeight.BOLD, 18));
//         title.setTextFill(Color.BLACK);

//         Label description = new Label("When you typically experience different moods");
//         description.setFont(Font.font("Inter", 12));
//         description.setTextFill(Color.GREY);

//         section.getChildren().addAll(title, description);

//         // This section will be populated dynamically by updatePatternTimeBasedPatterns
//         // No need to add initial static items here.

//         return section;
//     }

//     private HBox createTimePatternItem(TimePatternItem item) {
//         HBox hBox = new HBox();
//         hBox.setAlignment(Pos.CENTER_LEFT);
//         hBox.setPadding(new Insets(15));
//         hBox.setSpacing(10);
//         hBox.setBackground(new Background(new BackgroundFill(Color.web("#F9F9F9"), new CornerRadii(8), Insets.EMPTY)));
//         hBox.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
//         HBox.setHgrow(hBox, Priority.ALWAYS);

//         VBox textContent = new VBox();
//         textContent.setSpacing(2);
//         Label timeLabel = new Label(item.getTimeRange());
//         timeLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
//         timeLabel.setTextFill(Color.BLACK);

//         textContent.getChildren().addAll(timeLabel);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Label moodLabel = new Label(item.getMoodDescription());
//         moodLabel.setFont(Font.font("Inter", 14));
//         moodLabel.setTextFill(Color.GREY);

//         Label emojiLabel = new Label(item.getEmoji());
//         emojiLabel.setFont(Font.font("Inter", 20));

//         hBox.getChildren().addAll(textContent, spacer, moodLabel, emojiLabel);

//         // Add Tooltip to the entire HBox item
//         Tooltip.install(hBox, new Tooltip("Time: " + item.getTimeRange() + "\nMood: " + item.getMoodDescription() + " " + item.getEmoji()));

//         return hBox;
//     }

//     // --- Content for the "Insights" Sub-Tab ---
//     private VBox createInsightsLayout() {
//         VBox insightsLayout = new VBox(20);
//         insightsLayout.setPadding(new Insets(0, 20, 20, 20));
//         insightsLayout.setSpacing(20);
//         insightsLayout.setVisible(false);
//         insightsLayout.setManaged(false);

//         Label sectionTitle = new Label("Personalized Insights");
//         sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//         sectionTitle.setTextFill(Color.web("#333333"));

//         Label sectionDescription = new Label("AI-generated recommendations based on your mood patterns");
//         sectionDescription.setFont(Font.font("Arial", 14));
//         sectionDescription.setTextFill(Color.web("#666666"));

//         insightsLayout.getChildren().addAll(sectionTitle, sectionDescription);

//         insightsLayout.getChildren().add(createInsightCard("Key Insight", insightsKeyInsightProperty, "#E0F2F7", "#2196F3", "\uD83D\uDCA1"));
//         insightsLayout.getChildren().add(createInsightCard("Positive Pattern", insightsPositivePatternProperty, "#E8F5E9", "#4CAF50", "✅"));
//         insightsLayout.getChildren().add(createInsightCard("Area for Improvement", insightsAreaForImprovementProperty, "#FFFDE7", "#FFC107", "⚠"));
//         insightsLayout.getChildren().add(createInsightCard("Recommendation", insightsRecommendationProperty, "#F3E5F5", "#9C27B0", "💡"));

//         return insightsLayout;
//     }

//     private HBox createInsightCard(String title, StringProperty insightTextProperty, String bgColorHex, String iconColorHex, String iconEmoji) {
//         HBox card = new HBox(15);
//         card.setPadding(new Insets(20));
//         card.setStyle(
//             "-fx-background-color: " + bgColorHex + "; " +
//             "-fx-border-radius: 10; " +
//             "-fx-background-radius: 10;"
//         );
//         card.setAlignment(Pos.TOP_LEFT);
//         HBox.setHgrow(card, Priority.ALWAYS);

//         Label iconLabel = new Label(iconEmoji);
//         iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//         iconLabel.setTextFill(Color.web(iconColorHex));
//         iconLabel.setPadding(new Insets(0, 5, 0, 0));

//         VBox textContent = new VBox(5);

//         Label cardTitle = new Label(title);
//         cardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//         cardTitle.setTextFill(Color.web("#333333"));

//         Label insightLabel = new Label();
//         insightLabel.setFont(Font.font("Arial", 14));
//         insightLabel.setTextFill(Color.web("#666666"));
//         insightLabel.setWrapText(true);
//         insightLabel.textProperty().bind(insightTextProperty);

//         textContent.getChildren().addAll(cardTitle, insightLabel);
//         card.getChildren().addAll(iconLabel, textContent);

//         // Add Tooltip to the entire HBox card
//         Tooltip tooltip = new Tooltip();
//         tooltip.textProperty().bind(Bindings.createStringBinding(
//             () -> title + ":\n" + insightTextProperty.get(), insightTextProperty));
//         Tooltip.install(card, tooltip);

//         return card;
//     }

//     // Helper method to add tooltips to chart nodes
//     private void addChartTooltip(Node node, String text) {
//         Tooltip tooltip = new Tooltip(text);
//         Tooltip.install(node, tooltip);

//         node.setOnMouseClicked(event -> {
//             if (tooltip.isShowing()) {
//                 tooltip.hide();
//             } else {
//                 Bounds bounds = node.localToScreen(node.getBoundsInLocal());
//                 tooltip.show(node, bounds.getMaxX(), bounds.getMaxY());
//             }
//         });

//         node.setOnMouseExited(event -> tooltip.hide());
//     }
// }


package com.lifecompass.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.lifecompass.model.MoodEntry;
import com.lifecompass.services.MoodAnalyticsService;
import com.lifecompass.view.MoodTrackerApp.MoodEmoji;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;


public class MentalHealthAppView {

    // --- Initial Values for Metrics (Overview Tab) ---
    private StringProperty avgMoodValueProperty;
    private StringProperty avgMoodChangeProperty;
    private StringProperty moodStabilityValueProperty;
    private StringProperty moodStabilityDescProperty;
    private StringProperty bestDayValueProperty;
    private StringProperty bestDayAvgProperty;
    private StringProperty entriesThisWeekValueProperty;
    private StringProperty entriesThisWeekDescProperty;

    // --- Dynamic Data for Charts (Overview Tab) ---
    private ObservableList<XYChart.Data<String, Number>> overviewMoodSeriesData;
    private ObservableList<XYChart.Data<String, Number>> overviewEnergySeriesData;
    private ObservableList<PieChart.Data> overviewMoodDistributionData;

    // --- Tab Management ---
    private StringProperty selectedAnalyticsSubTab = new SimpleStringProperty("Overview");
    private StackPane analyticsContentStack;
    private VBox moodDistributionLegendVBox;

    // --- Trends Screen Data ---
    private ObservableList<XYChart.Data<String, Number>> trendsMonthlyMoodTrendBarData;
    private StringProperty trendsSleepQualityImpactProperty;
    private StringProperty trendsExerciseFrequencyProperty;
    private StringProperty trendsSocialInteractionProperty;

    // --- Pattern View Data ---
    private ObservableList<MoodTriggerItem> patternTopMoodTriggersData;
    private ObservableList<TimePatternItem> patternTimeBasedPatternsData;
    private VBox topMoodTriggersSection;
    private VBox timeBasedPatternsSection;


    // --- Insights View Data ---
    private StringProperty insightsKeyInsightProperty;
    private StringProperty insightsPositivePatternProperty;
    private StringProperty insightsAreaForImprovementProperty;
    private StringProperty insightsRecommendationProperty;


    // Constructor to initialize everything
    public MentalHealthAppView() {
        initializeProperties();
        initializeChartData();
        initializeTrendsData();
        initializePatternData();
        initializeInsightsData();
    }

    // NEW: Public getter for avgMoodValueProperty
    public StringProperty avgMoodValueProperty() {
        return avgMoodValueProperty;
    }

    public Node createAnalyticsDashboardContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F5;");

        VBox analyticsDashboardContainer = createAnalyticsDashboardContainer();
        analyticsDashboardContainer.setPadding(new Insets(20));
        analyticsDashboardContainer.setSpacing(20);
        VBox.setVgrow(analyticsDashboardContainer, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(analyticsDashboardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        root.setCenter(scrollPane);

        selectedAnalyticsSubTab.addListener((obs, oldVal, newVal) -> {
            updateAnalyticsContent(newVal);
        });

        Platform.runLater(() -> updateAnalyticsContent(selectedAnalyticsSubTab.get()));

        return root;
    }

    private void initializeProperties() {
        avgMoodValueProperty = new SimpleStringProperty("N/A");
        avgMoodChangeProperty = new SimpleStringProperty("");
        moodStabilityValueProperty = new SimpleStringProperty("N/A");
        moodStabilityDescProperty = new SimpleStringProperty("");
        bestDayValueProperty = new SimpleStringProperty("N/A");
        bestDayAvgProperty = new SimpleStringProperty("");
        entriesThisWeekValueProperty = new SimpleStringProperty("0");
        entriesThisWeekDescProperty = new SimpleStringProperty("No entries yet");
    }

    private void initializeChartData() {
        overviewMoodSeriesData = FXCollections.observableArrayList();
        overviewEnergySeriesData = FXCollections.observableArrayList();
        overviewMoodDistributionData = FXCollections.observableArrayList(
                new PieChart.Data("No Data", 100)
        );
    }

    private void initializeTrendsData() {
        trendsMonthlyMoodTrendBarData = FXCollections.observableArrayList();
        trendsSleepQualityImpactProperty = new SimpleStringProperty("No data recorded");
        trendsExerciseFrequencyProperty = new SimpleStringProperty("No data recorded");
        trendsSocialInteractionProperty = new SimpleStringProperty("No data recorded");
    }

    private void initializePatternData() {
        patternTopMoodTriggersData = FXCollections.observableArrayList();
        patternTimeBasedPatternsData = FXCollections.observableArrayList();
    }

    private void initializeInsightsData() {
        insightsKeyInsightProperty = new SimpleStringProperty("No insights yet. Log your mood!");
        insightsPositivePatternProperty = new SimpleStringProperty("No patterns yet.");
        insightsAreaForImprovementProperty = new SimpleStringProperty("More data needed.");
        insightsRecommendationProperty = new SimpleStringProperty("Log at least 5-7 mood entries to get initial insights.");
    }

    /**
     * Public method to refresh all analytics data.
     *
     * @param userId The ID of the currently logged-in user.
     * @param service The MoodAnalyticsService instance to fetch data from.
     */
    public void refreshAnalyticsData(String userId, MoodAnalyticsService service) {
        if (userId == null || userId.isEmpty() || service == null) {
            System.err.println("Cannot refresh analytics: userId is null/empty or service is null.");
            updateMetrics("N/A", "", "N/A", "", "N/A", "", "0");
            updateOverviewWeeklyMoodTrend(FXCollections.emptyObservableList(), FXCollections.emptyObservableList());
            updateOverviewMoodDistribution(FXCollections.observableArrayList(new PieChart.Data("No Data", 100)));
            updateTrendsMonthlyMoodTrend(FXCollections.emptyObservableList());
            updateTrendsMoodImprovementAreas("No data recorded", "No data recorded", "No data recorded");
            updatePatternTopMoodTriggers(FXCollections.emptyObservableList());
            updatePatternTimeBasedPatterns(FXCollections.emptyObservableList());
            updateInsights("No insights yet. Log your mood!", "No patterns yet.", "More data needed.", "Log at least 5-7 mood entries to get initial insights.");
            return;
        }

        List<MoodEntry> userEntries = service.getUserMoodEntries(userId);
        if (userEntries.isEmpty()) {
             System.out.println("No mood entries found for user: " + userId);
             updateMetrics("N/A", "", "N/A", "", "N/A", "", "0");
             updateOverviewWeeklyMoodTrend(FXCollections.emptyObservableList(), FXCollections.emptyObservableList());
             updateOverviewMoodDistribution(FXCollections.observableArrayList(new PieChart.Data("No Data", 100)));
             updateTrendsMonthlyMoodTrend(FXCollections.emptyObservableList());
             updateTrendsMoodImprovementAreas("No data recorded", "No data recorded", "No data recorded");
             updatePatternTopMoodTriggers(FXCollections.emptyObservableList());
             updatePatternTimeBasedPatterns(FXCollections.emptyObservableList());
             updateInsights("No insights yet. Log your mood!", "No patterns yet.", "More data needed.", "Log at least 5-7 mood entries to get initial insights.");
             return;
        }

        // --- Overview Tab Data ---
        OptionalDouble avgMoodOpt = userEntries.stream().mapToInt(MoodEntry::getIntensity).average();
        String avgMoodStr = String.format("%.1f/10", avgMoodOpt.orElse(0.0));
        
        double moodStability;
        if (userEntries.size() > 1) {
            double mean = userEntries.stream().mapToInt(MoodEntry::getIntensity).average().orElse(0.0);
            double sumSqDiff = userEntries.stream().mapToDouble(e -> Math.pow(e.getIntensity() - mean, 2)).sum();
            double variance = sumSqDiff / userEntries.size();
            moodStability = Math.max(0, 100 - (variance * 10));
        } else {
            moodStability = 100;
        }
        String moodStabilityStr = String.format("%.0f%%", moodStability);
        String moodStabilityDesc = moodStability > 80 ? "Highly consistent" : (moodStability > 50 ? "Consistent patterns" : "Fluctuating patterns");

        Map<DayOfWeek, Double> avgMoodPerDay = userEntries.stream()
            .collect(Collectors.groupingBy(e -> e.getTimestamp().getDayOfWeek(),
                Collectors.mapping(MoodEntry::getIntensity, Collectors.averagingInt(Integer::intValue))));
        
        String bestDay = "N/A";
        String bestDayAvg = "";
        OptionalDouble maxAvg = OptionalDouble.empty();

        for (Map.Entry<DayOfWeek, Double> entry : avgMoodPerDay.entrySet()) {
            if (entry.getValue() != null) {
                if (!maxAvg.isPresent() || entry.getValue() > maxAvg.getAsDouble()) {
                    maxAvg = OptionalDouble.of(entry.getValue());
                    bestDay = entry.getKey().name();
                    bestDayAvg = String.format("Avg %.1f/10", maxAvg.getAsDouble());
                }
            }
        }
        
        long entriesThisWeek = userEntries.stream()
            .filter(e -> e.getTimestamp().isAfter(LocalDateTime.now().minusDays(7)))
            .count();
        String entriesThisWeekStr = String.valueOf(entriesThisWeek);
        entriesThisWeekDescProperty.set((entriesThisWeek > 0) ? "Daily tracking" : "No entries this week");


        updateMetrics(avgMoodStr, "+0.0 from last week", moodStabilityStr, moodStabilityDesc,
                      bestDay, bestDayAvg, entriesThisWeekStr);

        Map<String, ObservableList<XYChart.Data<String, Number>>> weeklyTrends = service.getWeeklyMoodAndEnergyTrend(userId, userEntries);
        updateOverviewWeeklyMoodTrend(weeklyTrends.get("mood"), weeklyTrends.get("energy"));
        updateOverviewMoodDistribution(service.getMoodDistributionData(userId, userEntries));

        // --- Trends Tab Data ---
        updateTrendsMonthlyMoodTrend(service.getMonthlyMoodTrend(userId, userEntries));
        Map<String, String> moodImprovementAreas = service.getMoodImprovementAreas(userId, userEntries);
        updateTrendsMoodImprovementAreas(
            moodImprovementAreas.getOrDefault("sleepQuality", "No data recorded"),
            moodImprovementAreas.getOrDefault("exerciseFrequency", "No data recorded"),
            moodImprovementAreas.getOrDefault("socialInteraction", "No data recorded")
        );

        // --- Patterns Tab Data ---
        updatePatternTopMoodTriggers(service.getTopMoodTriggers(userId, userEntries));
        updatePatternTimeBasedPatterns(service.getTimeBasedPatterns(userId, userEntries));

        // --- Insights Tab Data ---
        Map<String, String> personalizedInsights = service.getPersonalizedInsights(userId, userEntries);
        updateInsights(
            personalizedInsights.getOrDefault("keyInsight", ""),
            personalizedInsights.getOrDefault("positivePattern", ""),
            personalizedInsights.getOrDefault("areaForImprovement", ""),
            personalizedInsights.getOrDefault("recommendation", "")
        );
    }

    public void updateMetrics(String newAvgMood, String newAvgMoodChange,
                              String newMoodStability, String newMoodStabilityDesc,
                              String newBestDay, String newBestDayAvg,
                              String newEntriesThisWeek) {
        avgMoodValueProperty.set(newAvgMood);
        avgMoodChangeProperty.set(newAvgMoodChange);
        moodStabilityValueProperty.set(newMoodStability);
        moodStabilityDescProperty.set(newMoodStabilityDesc);
        bestDayValueProperty.set(newBestDay);
        bestDayAvgProperty.set(newBestDayAvg);
        entriesThisWeekValueProperty.set(newEntriesThisWeek);
        entriesThisWeekDescProperty.set((newEntriesThisWeek.equals("0") || newEntriesThisWeek.equals("N/A")) ? "No entries yet" : "Daily tracking");
    }

    @SuppressWarnings("unchecked")
    public void updateOverviewWeeklyMoodTrend(ObservableList<XYChart.Data<String, Number>> newMoodData,
                                              ObservableList<XYChart.Data<String, Number>> newEnergyData) {
        overviewMoodSeriesData.setAll(newMoodData);
        overviewEnergySeriesData.setAll(newEnergyData);
        Platform.runLater(() -> {
            LineChart<String, Number> lineChart = null;
            if (analyticsContentStack != null && analyticsContentStack.getChildren().size() > 0) {
                VBox overviewLayout = (VBox) analyticsContentStack.getChildren().get(0);
                if (overviewLayout != null && overviewLayout.getChildren().size() > 1 && overviewLayout.getChildren().get(1) instanceof VBox) {
                    VBox weeklyMoodTrendSection = (VBox) overviewLayout.getChildren().get(1);
                    if (weeklyMoodTrendSection.getChildren().size() > 2 && weeklyMoodTrendSection.getChildren().get(2) instanceof LineChart) {
                        lineChart = (LineChart<String, Number>) weeklyMoodTrendSection.getChildren().get(2);
                    }
                }
            }

            if (lineChart != null && lineChart.getData().size() >= 2) {
                lineChart.getData().get(0).setData(overviewMoodSeriesData);
                lineChart.getData().get(1).setData(overviewEnergySeriesData);

                for (XYChart.Series<String, Number> s : lineChart.getData()) {
                     for (XYChart.Data<String, Number> data : s.getData()) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-background-color: " + (s.getName().equals("Mood") ? "#28A745" : "#007BFF") + ", white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
                            addChartTooltip(data.getNode(), data.getXValue() + ": " + s.getName() + " " + String.format("%.1f", data.getYValue().doubleValue()));
                        }
                    }
                }
            }
        });
    }

    public void updateOverviewMoodDistribution(ObservableList<PieChart.Data> newMoodDistributionData) {
        overviewMoodDistributionData.setAll(newMoodDistributionData);
        Platform.runLater(() -> {
            PieChart pieChart = null;
            if (analyticsContentStack != null && analyticsContentStack.getChildren().size() > 0) {
                VBox overviewLayout = (VBox) analyticsContentStack.getChildren().get(0);
                if (overviewLayout != null && overviewLayout.getChildren().size() > 2 && overviewLayout.getChildren().get(2) instanceof VBox) {
                    VBox moodDistributionSection = (VBox) overviewLayout.getChildren().get(2);
                    if (moodDistributionSection.getChildren().size() > 2 && moodDistributionSection.getChildren().get(2) instanceof HBox) {
                        HBox chartAndLegend = (HBox) moodDistributionSection.getChildren().get(2);
                        if (chartAndLegend.getChildren().size() > 0 && chartAndLegend.getChildren().get(0) instanceof StackPane) {
                             StackPane donutChartContainer = (StackPane) chartAndLegend.getChildren().get(0);
                             if (donutChartContainer.getChildren().size() > 0 && donutChartContainer.getChildren().get(0) instanceof PieChart) {
                                pieChart = (PieChart) donutChartContainer.getChildren().get(0);
                             }
                        }
                    }
                }
            }
            
            if (pieChart != null) {
                pieChart.setData(overviewMoodDistributionData);
                overviewMoodDistributionData.forEach(data -> {
                    applyPieChartSliceColor(data);
                    if (data.getNode() != null) {
                        addChartTooltip(data.getNode(), data.getName() + ": " + ((int) data.getPieValue()) + "%");
                    }
                });
                rebuildMoodDistributionLegend(overviewMoodDistributionData);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void updateTrendsMonthlyMoodTrend(ObservableList<XYChart.Data<String, Number>> newMonthlyBarData) {
        trendsMonthlyMoodTrendBarData.setAll(newMonthlyBarData);
        Platform.runLater(() -> {
            BarChart<String, Number> monthlyMoodBarChart = null;
            if (analyticsContentStack != null && analyticsContentStack.getChildren().size() > 1) {
                VBox trendsLayout = (VBox) analyticsContentStack.getChildren().get(1);
                if (trendsLayout != null && trendsLayout.getChildren().size() > 2 && trendsLayout.getChildren().get(2) instanceof BarChart) {
                    monthlyMoodBarChart = (BarChart<String, Number>) trendsLayout.getChildren().get(2);
                }
            }
            
            if (monthlyMoodBarChart != null && !monthlyMoodBarChart.getData().isEmpty()) {
                monthlyMoodBarChart.getData().get(0).setData(trendsMonthlyMoodTrendBarData);
                for (XYChart.Data<String, Number> data : monthlyMoodBarChart.getData().get(0).getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: #007BFF;");
                        addChartTooltip(data.getNode(), data.getXValue() + ": " + String.format("%.1f", data.getYValue().doubleValue()));
                    }
                }
            }
        });
    }

    public void updateTrendsMoodImprovementAreas(String sleepImpact, String exerciseImpact, String socialImpact) {
        trendsSleepQualityImpactProperty.set(sleepImpact);
        trendsExerciseFrequencyProperty.set(exerciseImpact);
        trendsSocialInteractionProperty.set(socialImpact);
    }

    public void updatePatternTopMoodTriggers(ObservableList<MoodTriggerItem> newTriggersData) {
        patternTopMoodTriggersData.setAll(newTriggersData);
        Platform.runLater(() -> {
            if (topMoodTriggersSection != null) {
                topMoodTriggersSection.getChildren().remove(2, topMoodTriggersSection.getChildren().size());
                if(newTriggersData.isEmpty()) {
                    Label noTriggers = new Label("No trigger data available yet.");
                    noTriggers.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");
                    topMoodTriggersSection.getChildren().add(noTriggers);
                } else {
                    for (MoodTriggerItem item : patternTopMoodTriggersData) {
                        topMoodTriggersSection.getChildren().add(createTriggerItem(item));
                    }
                }
            }
        });
    }

    public void updatePatternTimeBasedPatterns(ObservableList<TimePatternItem> newTimePatternsData) {
        patternTimeBasedPatternsData.setAll(newTimePatternsData);
        Platform.runLater(() -> {
            if (timeBasedPatternsSection != null) {
                timeBasedPatternsSection.getChildren().remove(2, timeBasedPatternsSection.getChildren().size());
                if(newTimePatternsData.isEmpty()) {
                    Label noPatterns = new Label("No time-based pattern data available yet.");
                    noPatterns.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");
                    timeBasedPatternsSection.getChildren().add(noPatterns);
                } else {
                    for (TimePatternItem item : patternTimeBasedPatternsData) {
                        timeBasedPatternsSection.getChildren().add(createTimePatternItem(item));
                    }
                }
            }
        });
    }

    public void updateInsights(String keyInsight, String positivePattern, String areaForImprovement, String recommendation) {
        this.insightsKeyInsightProperty.set(keyInsight);
        this.insightsPositivePatternProperty.set(positivePattern);
        this.insightsAreaForImprovementProperty.set(areaForImprovement);
        this.insightsRecommendationProperty.set(recommendation);
    }

    // This method is likely not used anymore as UserDashboardScreen handles the top navigation.
    // Commenting it out as per previous observations.
    /*
    private HBox createTopNavBar() {
        HBox topNavBar = new HBox(10);
        topNavBar.setAlignment(Pos.CENTER_LEFT);
        topNavBar.setPadding(new Insets(15, 25, 15, 25));
        topNavBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("LifeCompass");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logo.setTextFill(Color.web("#333333"));
        Circle logoIcon = new Circle(25, 25, 25);
        HBox logoBox = new HBox(5, logoIcon, logo);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Welcome back, Sarah!");
        welcomeLabel.setFont(Font.font("Arial", 14));
        welcomeLabel.setTextFill(Color.web("#666666"));

        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);

        Label settingsIcon = new Label("\u2699");
        settingsIcon.setFont(Font.font("Arial", 20));
        settingsIcon.setTextFill(Color.web("#666666"));

        Label bellIcon = new Label("\uD83D\uDD14");
        bellIcon.setFont(Font.font("Arial", 20));
        bellIcon.setTextFill(Color.web("#666666"));

        Label closeButton = new Label("Close");
        closeButton.setFont(Font.font("Arial", 14));
        closeButton.setTextFill(Color.web("#666666"));
        closeButton.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        topNavBar.getChildren().addAll(logoBox, welcomeLabel, spacerLeft, settingsIcon, bellIcon, closeButton);
        return topNavBar;
    }
    */

    private VBox createAnalyticsDashboardContainer() {
        VBox container = new VBox(20);
        container.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox header = new VBox(5);
        Label title = new Label("Mood Analytics Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        Label description = new Label("Visualize your emotional patterns and gain insights into your mental wellness journey");
        description.setFont(Font.font("Arial", 14));
        description.setTextFill(Color.web("#666666"));
        header.getChildren().addAll(title, description);
        container.getChildren().add(header);

        HBox subTabs = createSubTabs();
        container.getChildren().add(subTabs);

        analyticsContentStack = new StackPane();
        analyticsContentStack.setPadding(new Insets(0, 0, 0, 0));
        VBox.setVgrow(analyticsContentStack, Priority.ALWAYS);

        analyticsContentStack.getChildren().addAll(
            createOverviewContent(),
            createTrendsLayout(),
            createPatternLayout(),
            createInsightsLayout()
        );

        container.getChildren().add(analyticsContentStack);

        return container;
    }

    private void updateAnalyticsContent(String selectedTab) {
        for (Node node : analyticsContentStack.getChildren()) {
            node.setVisible(false);
            node.setManaged(false);
        }

        Node contentToShow = null;
        switch (selectedTab) {
            case "Overview":
                contentToShow = analyticsContentStack.getChildren().get(0);
                break;
            case "Trends":
                contentToShow = analyticsContentStack.getChildren().get(1);
                break;
            case "Patterns":
                contentToShow = analyticsContentStack.getChildren().get(2);
                break;
            case "Insights":
                contentToShow = analyticsContentStack.getChildren().get(3);
                break;
        }

        if (contentToShow != null) {
            contentToShow.setVisible(true);
            contentToShow.setManaged(true);
        }
    }

    private HBox createSubTabs() {
        HBox subTabs = new HBox(10);
        subTabs.setPadding(new Insets(0));
        subTabs.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(subTabs, Priority.ALWAYS);

        String[] tabNames = {"Overview", "Trends", "Patterns", "Insights"};

        for (String tabName : tabNames) {
            Label tabLabel = new Label(tabName);
            tabLabel.setFont(Font.font("Arial", 14));
            tabLabel.setPadding(new Insets(8, 15, 8, 15));
            tabLabel.setTextFill(Color.web("#666666"));

            HBox.setHgrow(tabLabel, Priority.ALWAYS);
            tabLabel.setMaxWidth(Double.MAX_VALUE);
            tabLabel.setAlignment(Pos.CENTER);

            tabLabel.styleProperty().bind(
                Bindings.when(selectedAnalyticsSubTab.isEqualTo(tabName))
                    .then("-fx-background-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;")
                    .otherwise("-fx-background-color: transparent; -fx-border-color: transparent;")
            );
            tabLabel.textFillProperty().bind(
                Bindings.when(selectedAnalyticsSubTab.isEqualTo(tabName))
                    .then(Color.web("#333333"))
                    .otherwise(Color.web("#666666"))
            );

            tabLabel.setOnMouseClicked(event -> selectedAnalyticsSubTab.set(tabName));
            subTabs.getChildren().add(tabLabel);
        }
        return subTabs;
    }

    private VBox createOverviewContent() {
        VBox overviewLayout = new VBox(20);
        overviewLayout.setPadding(new Insets(0, 20, 20, 20));

        GridPane metricBoxes = createMetricBoxes();
        overviewLayout.getChildren().add(metricBoxes);

        VBox weeklyMoodTrendSection = createWeeklyMoodTrendChart();
        overviewLayout.getChildren().add(weeklyMoodTrendSection);

        VBox moodDistributionSection = createMoodDistributionChart();
        overviewLayout.getChildren().add(moodDistributionSection);

        return overviewLayout;
    }

    private GridPane createMetricBoxes() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        grid.add(createDynamicMetricBox("Average Mood", avgMoodValueProperty, avgMoodChangeProperty, "#00C853"), 0, 0);
        grid.add(createDynamicMetricBox("Mood Stability", moodStabilityValueProperty, moodStabilityDescProperty, "#007BFF"), 1, 0);
        grid.add(createDynamicMetricBox("Best Day", bestDayValueProperty, bestDayAvgProperty, "#6A5ACD"), 2, 0);
        grid.add(createDynamicMetricBox("Entries This Week", entriesThisWeekValueProperty, entriesThisWeekDescProperty, "#FFC107"), 3, 0);

        return grid;
    }

    private VBox createDynamicMetricBox(String title, StringProperty valueProperty, StringProperty descriptionProperty, String valueColorHex) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#666666"));

        Label valueLabel = new Label();
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(valueColorHex));
        valueLabel.textProperty().bind(valueProperty);

        Label descriptionLabel = new Label();
        descriptionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        descriptionLabel.setTextFill(Color.web("#999999"));
        descriptionLabel.textProperty().bind(descriptionProperty);

        box.getChildren().addAll(titleLabel, valueLabel, descriptionLabel);
        return box;
    }

    private VBox createWeeklyMoodTrendChart() {
        VBox chartSection = new VBox(10);
        Label sectionTitle = new Label("Weekly Mood Trend");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#333333"));

        Label sectionDescription = new Label("Your mood and energy levels over the past week");
        sectionDescription.setFont(Font.font("Arial", 14));
        sectionDescription.setTextFill(Color.web("#666666"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 10, 1);
        yAxis.setLabel("");
        xAxis.setLabel("");
        xAxis.setTickLabelsVisible(true);
        yAxis.setTickLabelsVisible(true);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(300);
        lineChart.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

        lineChart.setHorizontalGridLinesVisible(true);
        lineChart.setVerticalGridLinesVisible(true);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lineChart.lookup(".chart-vertical-grid-lines").setStyle("-fx-stroke: #E0E0E0;");
        lineChart.lookup(".chart-horizontal-grid-lines").setStyle("-fx-stroke: #E0E0E0;");

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Mood");
        series1.setData(overviewMoodSeriesData);

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Energy");
        series2.setData(overviewEnergySeriesData);

        lineChart.getData().addAll(series1, series2);

        series1.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-stroke: #28A745; -fx-stroke-width: 2px;");
            }
        });
        series2.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-stroke: #007BFF; -fx-stroke-width: 2px;");
            }
        });

        overviewMoodSeriesData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
            Platform.runLater(() -> {
                while (c.next()) {
                    if (c.wasAdded() || c.wasUpdated()) {
                        for (XYChart.Data<String, Number> data : c.getAddedSubList()) {
                            if (data.getNode() != null) {
                                 data.getNode().setStyle("-fx-background-color: #28A745, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
                                 addChartTooltip(data.getNode(), data.getXValue() + ": Mood " + String.format("%.1f", data.getYValue().doubleValue()));
                            }
                        }
                    }
                }
            });
        });
        overviewEnergySeriesData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
            Platform.runLater(() -> {
                while (c.next()) {
                    if (c.wasAdded() || c.wasUpdated()) {
                        for (XYChart.Data<String, Number> data : c.getAddedSubList()) {
                            if (data.getNode() != null) {
                                 data.getNode().setStyle("-fx-background-color: #007BFF, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
                                 addChartTooltip(data.getNode(), data.getXValue() + ": Energy " + String.format("%.1f", data.getYValue().doubleValue()));
                            }
                        }
                    }
                }
            });
        });

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series1.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: #28A745, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
                    addChartTooltip(data.getNode(), data.getXValue() + ": Mood " + String.format("%.1f", data.getYValue().doubleValue()));
                }
            }
            for (XYChart.Data<String, Number> data : series2.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: #007BFF, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;");
                    addChartTooltip(data.getNode(), data.getXValue() + ": Energy " + String.format("%.1f", data.getYValue().doubleValue()));
                }
            }
        });

        chartSection.getChildren().addAll(sectionTitle, sectionDescription, lineChart);
        return chartSection;
    }

    private VBox createMoodDistributionChart() {
        VBox chartSection = new VBox(10);
        Label sectionTitle = new Label("Mood Distribution");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#333333"));

        Label sectionDescription = new Label("How your emotions are distributed this month");
        sectionDescription.setFont(Font.font("Arial", 14));
        sectionDescription.setTextFill(Color.web("#666666"));

        final PieChart pieChart = new PieChart(overviewMoodDistributionData);
        pieChart.setTitle("");
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(90);

        StackPane donutChartContainer = new StackPane();
        donutChartContainer.setPrefSize(200, 200);

        Circle donutHole = new Circle(60);
        donutHole.setFill(Color.WHITE);

        donutChartContainer.getChildren().addAll(pieChart, donutHole);

        pieChart.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double radius = Math.min(newBounds.getWidth(), newBounds.getHeight()) / 2.0;
        });

        overviewMoodDistributionData.addListener((javafx.collections.ListChangeListener.Change<? extends PieChart.Data> c) -> {
            Platform.runLater(() -> {
                while (c.next()) {
                    if (c.wasAdded() || c.wasUpdated()) {
                        for (PieChart.Data data : c.getAddedSubList()) {
                            applyPieChartSliceColor(data);
                            if (data.getNode() != null) {
                                addChartTooltip(data.getNode(), data.getName() + ": " + ((int) data.getPieValue()) + "%");
                            }
                        }
                    }
                    if (c.wasRemoved() || c.wasAdded() || c.wasUpdated()) {
                         rebuildMoodDistributionLegend(overviewMoodDistributionData);
                    }
                }
            });
        });
        Platform.runLater(() -> {
            overviewMoodDistributionData.forEach(data -> {
                applyPieChartSliceColor(data);
                if (data.getNode() != null) {
                    addChartTooltip(data.getNode(), data.getName() + ": " + ((int) data.getPieValue()) + "%");
                }
            });
        });

        moodDistributionLegendVBox = new VBox(8);
        moodDistributionLegendVBox.setAlignment(Pos.CENTER_LEFT);
        moodDistributionLegendVBox.setPadding(new Insets(0, 0, 0, 20));

        rebuildMoodDistributionLegend(overviewMoodDistributionData);

        HBox chartAndLegend = new HBox(30);
        chartAndLegend.setAlignment(Pos.CENTER_LEFT);
        chartAndLegend.getChildren().addAll(donutChartContainer, moodDistributionLegendVBox);

        chartSection.getChildren().addAll(sectionTitle, sectionDescription, chartAndLegend);
        return chartSection;
    }

    private void rebuildMoodDistributionLegend(ObservableList<PieChart.Data> dataList) {
        if (moodDistributionLegendVBox != null) {
            moodDistributionLegendVBox.getChildren().clear();
            for (PieChart.Data data : dataList) {
                String color = getMoodColor(data.getName());
                moodDistributionLegendVBox.getChildren().add(createLegendItem(data.getName(), ((int) data.getPieValue()) + "%", color));
            }
        }
    }

    private void applyPieChartSliceColor(PieChart.Data data) {
        Optional<MoodEmoji> matchingEmoji = MoodTrackerApp.MOOD_EMOJIS_DATA.stream()
                .filter(e -> e.getLabel().equals(data.getName()))
                .findFirst();

        String color = matchingEmoji.map(e -> getMoodColor(e.getLabel())).orElse("#CCCCCC");

        if (data.getNode() != null) {
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        }
    }

    private String getMoodColor(String moodLabel) {
        switch (moodLabel) {
            case "Happy":
            case "Ecstatic":
            case "Delighted":
            case "Good":
            case "Relaxed": return "#28A745";
            case "Calm": return "#007BFF";
            case "Neutral": return "#FFC107";
            case "Anxious":
            case "Worried":
            case "Distraught":
            case "Sad":
            case "Angry":
            case "Frustrated":
            case "Sick": return "#DC3545";
            case "Tired": return "#6C757D";
            case "Surprised":
            case "Confused": return "#9C27B0";
            default: return "#CCCCCC";
        }
    }

    private HBox createLegendItem(String mood, String percentage, String colorHex) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);

        Circle colorIndicator = new Circle(6);
        colorIndicator.setFill(Color.web(colorHex));

        Label moodLabel = new Label(mood);
        moodLabel.setFont(Font.font("Arial", 14));
        moodLabel.setTextFill(Color.web("#333333"));

        Label percentageLabel = new Label(percentage);
        percentageLabel.setFont(Font.font("Arial", 14));
        percentageLabel.setTextFill(Color.web("#666666"));
        percentageLabel.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 2 5; -fx-border-radius: 3; -fx-background-radius: 3;");

        item.getChildren().addAll(colorIndicator, moodLabel, percentageLabel);
        return item;
    }

    private VBox createTrendsLayout() {
        VBox trendsLayout = new VBox(20);
        trendsLayout.setPadding(new Insets(0, 20, 20, 20));
        trendsLayout.setSpacing(20);
        trendsLayout.setVisible(false);
        trendsLayout.setManaged(false);

        Label trendsTitle = new Label("Monthly Mood Trends");
        trendsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        trendsTitle.setTextFill(Color.web("#333333"));

        Label trendsDescription = new Label("Average mood scores over the past month");
        trendsDescription.setFont(Font.font("Arial", 14));
        trendsDescription.setTextFill(Color.web("#666666"));

        CategoryAxis monthBarXAxis = new CategoryAxis();
        NumberAxis monthBarYAxis = new NumberAxis(0, 10, 1);
        monthBarXAxis.setLabel("");
        monthBarYAxis.setLabel("");
        monthBarXAxis.setTickLabelsVisible(true);
        monthBarYAxis.setTickLabelsVisible(true);
        monthBarXAxis.setTickMarkVisible(false);
        monthBarYAxis.setTickMarkVisible(false);

        BarChart<String, Number> monthlyMoodBarChart = new BarChart<>(monthBarXAxis, monthBarYAxis);
        monthlyMoodBarChart.setTitle("");
        monthlyMoodBarChart.setLegendVisible(false);
        monthlyMoodBarChart.setPrefHeight(300);
        monthlyMoodBarChart.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");
        monthlyMoodBarChart.setHorizontalGridLinesVisible(true);
        monthlyMoodBarChart.setVerticalGridLinesVisible(false);

        @SuppressWarnings("unchecked")
        XYChart.Series<String, Number> monthlyBarSeries = new XYChart.Series<>();
        monthlyBarSeries.setName("Average Mood");
        monthlyBarSeries.setData(trendsMonthlyMoodTrendBarData);
        monthlyMoodBarChart.getData().addAll(monthlyBarSeries);

        trendsMonthlyMoodTrendBarData.addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Data<String, Number>> c) -> {
            Platform.runLater(() -> {
                while (c.next()) {
                    if (c.wasAdded() || c.wasUpdated()) {
                        for (XYChart.Data<String, Number> data : c.getAddedSubList()) {
                            if (data.getNode() != null) {
                                data.getNode().setStyle("-fx-bar-fill: #007BFF;");
                                addChartTooltip(data.getNode(), data.getXValue() + ": " + String.format("%.1f", data.getYValue().doubleValue()));
                            }
                        }
                    }
                }
            });
        });
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> s : monthlyMoodBarChart.getData()) {
                for (XYChart.Data<String, Number> data : s.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: #007BFF;");
                        addChartTooltip(data.getNode(), data.getXValue() + ": " + String.format("%.1f", data.getYValue().doubleValue()));
                    }
                }
            }
        });


        VBox moodImprovementAreas = createMoodImprovementAreas();

        trendsLayout.getChildren().addAll(trendsTitle, trendsDescription, monthlyMoodBarChart, moodImprovementAreas);
        return trendsLayout;
    }

    private VBox createMoodImprovementAreas() {
        VBox improvementAreasLayout = new VBox(10);
        improvementAreasLayout.setPadding(new Insets(20));
        improvementAreasLayout.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label sectionTitle = new Label("Mood Improvement Areas");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#333333"));

        Label sectionDescription = new Label("Insights based on your mood patterns");
        sectionDescription.setFont(Font.font("Arial", 14));
        sectionDescription.setTextFill(Color.web("#666666"));

        improvementAreasLayout.getChildren().addAll(sectionTitle, sectionDescription);

        improvementAreasLayout.getChildren().add(createCorrelationItem("Sleep Quality Impact", trendsSleepQualityImpactProperty));
        improvementAreasLayout.getChildren().add(createCorrelationItem("Exercise Frequency", trendsExerciseFrequencyProperty));
        improvementAreasLayout.getChildren().add(createCorrelationItem("Social Interaction", trendsSocialInteractionProperty));

        return improvementAreasLayout;
    }

    private HBox createCorrelationItem(String areaName, StringProperty correlationProperty) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(5, 0, 5, 0));

        Label areaLabel = new Label(areaName);
        areaLabel.setFont(Font.font("Arial", 14));
        areaLabel.setTextFill(Color.web("#333333"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label correlationLabel = new Label();
        correlationLabel.textProperty().bind(correlationProperty);
        correlationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        correlationLabel.setTextFill(Color.WHITE);
        correlationLabel.setPadding(new Insets(3, 8, 3, 8));

        correlationLabel.styleProperty().bind(
            Bindings.createStringBinding(() -> {
                String correlationText = correlationProperty.get();
                String colorHex = getImpactColor(correlationText);
                return "-fx-background-color: " + colorHex + "; -fx-background-radius: 15; -fx-border-radius: 15;";
            }, correlationProperty)
        );

        item.getChildren().addAll(areaLabel, spacer, correlationLabel);
        return item;
    }

    private String getImpactColor(String impactText) {
        switch (impactText) {
            case "Strong correlation": return "#28A745";
            case "Good correlation": return "#007BFF";
            case "Moderate correlation": return "#FFC107";
            case "Weak correlation": return "#DC3545";
            case "No data recorded": return "#6C757D";
            default: return "#6C757D";
        }
    }

    public static class MoodTriggerItem {
        String trigger;
        int occurrences;
        String impact;

        public MoodTriggerItem(String trigger, int occurrences, String impact) {
            this.trigger = trigger;
            this.occurrences = occurrences;
            this.impact = impact;
        }

        public String getTrigger() { return trigger; }
        public int getOccurrences() { return occurrences; }
        public String getImpact() { return impact; }
    }

    public static class TimePatternItem {
        String timeRange;
        String moodDescription;
        String emoji;

        public TimePatternItem(String timeRange, String moodDescription, String emoji) {
            this.timeRange = timeRange;
            this.moodDescription = moodDescription;
            this.emoji = emoji;
        }

        public String getTimeRange() { return timeRange; }
        public String getMoodDescription() { return moodDescription; }
        public String getEmoji() { return emoji; }
    }

    private VBox createPatternLayout() {
        VBox patternLayout = new VBox(20);
        patternLayout.setPadding(new Insets(0, 20, 20, 20));
        patternLayout.setSpacing(20);
        patternLayout.setVisible(false);
        patternLayout.setManaged(false);

        topMoodTriggersSection = createTopMoodTriggersSection();
        timeBasedPatternsSection = createTimeBasedPatternsSection();

        patternLayout.getChildren().addAll(topMoodTriggersSection, timeBasedPatternsSection);
        return patternLayout;
    }

    private VBox createTopMoodTriggersSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setPadding(new Insets(20));
        section.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        section.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        Label title = new Label("Top Mood Triggers");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        title.setTextFill(Color.BLACK);

        Label description = new Label("Events and activities that most impact your mood");
        description.setFont(Font.font("Inter", 12));
        description.setTextFill(Color.GREY);

        section.getChildren().addAll(title, description);

        if(patternTopMoodTriggersData.isEmpty()) {
            Label noTriggers = new Label("No trigger data available yet.");
            noTriggers.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");
            section.getChildren().add(noTriggers);
        } else {
            for (MoodTriggerItem item : patternTopMoodTriggersData) {
                section.getChildren().add(createTriggerItem(item));
            }
        }
        return section;
    }

    private HBox createTriggerItem(MoodTriggerItem item) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(15));
        hBox.setSpacing(10);
        hBox.setBackground(new Background(new BackgroundFill(Color.web("#F9F9F9"), new CornerRadii(8), Insets.EMPTY)));
        hBox.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        HBox.setHgrow(hBox, Priority.ALWAYS);

        VBox textContent = new VBox(2);
        Label triggerLabel = new Label(item.getTrigger());
        triggerLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        triggerLabel.setTextFill(Color.BLACK);

        Label occurrencesLabel = new Label("Occurred " + item.getOccurrences() + " times");
        occurrencesLabel.setFont(Font.font("Inter", 12));
        occurrencesLabel.setTextFill(Color.GREY);
        textContent.getChildren().addAll(triggerLabel, occurrencesLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label impactTag = new Label(item.getImpact());
        impactTag.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        impactTag.setTextFill(Color.WHITE);
        impactTag.setPadding(new Insets(5, 10, 5, 10));
        impactTag.setBackground(new Background(new BackgroundFill(Color.web(getImpactColor(item.getImpact())), new CornerRadii(6), Insets.EMPTY)));

        hBox.getChildren().addAll(textContent, spacer, impactTag);

        Tooltip.install(hBox, new Tooltip("Trigger: " + item.getTrigger() + "\nOccurrences: " + item.getOccurrences() + "\nImpact: " + item.getImpact()));

        return hBox;
    }

    private VBox createTimeBasedPatternsSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setPadding(new Insets(20));
        section.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        section.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        Label title = new Label("Time-based Patterns");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        title.setTextFill(Color.BLACK);

        Label description = new Label("When you typically experience different moods");
        description.setFont(Font.font("Inter", 12));
        description.setTextFill(Color.GREY);

        section.getChildren().addAll(title, description);

        if(patternTimeBasedPatternsData.isEmpty()) {
            Label noPatterns = new Label("No time-based pattern data available yet.");
            noPatterns.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");
            section.getChildren().add(noPatterns);
        } else {
            for (TimePatternItem item : patternTimeBasedPatternsData) {
                section.getChildren().add(createTimePatternItem(item));
            }
        }

        return section;
    }

    private HBox createTimePatternItem(TimePatternItem item) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(15));
        hBox.setSpacing(10);
        hBox.setBackground(new Background(new BackgroundFill(Color.web("#F9F9F9"), new CornerRadii(8), Insets.EMPTY)));
        hBox.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        HBox.setHgrow(hBox, Priority.ALWAYS);

        VBox textContent = new VBox(2);
        Label timeLabel = new Label(item.getTimeRange());
        timeLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        timeLabel.setTextFill(Color.BLACK);

        textContent.getChildren().addAll(timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label moodLabel = new Label(item.getMoodDescription());
        moodLabel.setFont(Font.font("Inter", 14));
        moodLabel.setTextFill(Color.GREY);

        Label emojiLabel = new Label(item.getEmoji());
        emojiLabel.setFont(Font.font("Inter", 20));

        hBox.getChildren().addAll(textContent, spacer, moodLabel, emojiLabel);

        Tooltip.install(hBox, new Tooltip("Time: " + item.getTimeRange() + "\nMood: " + item.getMoodDescription() + " " + item.getEmoji()));

        return hBox;
    }

    private VBox createInsightsLayout() {
        VBox insightsLayout = new VBox(20);
        insightsLayout.setPadding(new Insets(0, 20, 20, 20));
        insightsLayout.setSpacing(20);
        insightsLayout.setVisible(false);
        insightsLayout.setManaged(false);

        Label sectionTitle = new Label("Personalized Insights");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#333333"));

        Label sectionDescription = new Label("AI-generated recommendations based on your mood patterns");
        sectionDescription.setFont(Font.font("Arial", 14));
        sectionDescription.setTextFill(Color.web("#666666"));

        insightsLayout.getChildren().addAll(sectionTitle, sectionDescription);

        insightsLayout.getChildren().add(createInsightCard("Key Insight", insightsKeyInsightProperty, "#E0F2F7", "#2196F3", "\uD83D\uDCA1"));
        insightsLayout.getChildren().add(createInsightCard("Positive Pattern", insightsPositivePatternProperty, "#E8F5E9", "#4CAF50", "✅"));
        insightsLayout.getChildren().add(createInsightCard("Area for Improvement", insightsAreaForImprovementProperty, "#FFFDE7", "#FFC107", "⚠️"));
        insightsLayout.getChildren().add(createInsightCard("Recommendation", insightsRecommendationProperty, "#F3E5F5", "#9C27B0", "💡"));

        return insightsLayout;
    }

    private HBox createInsightCard(String title, StringProperty insightTextProperty, String bgColorHex, String iconColorHex, String iconEmoji) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + bgColorHex + "; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        card.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLabel = new Label(iconEmoji);
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        iconLabel.setTextFill(Color.web(iconColorHex));
        iconLabel.setPadding(new Insets(0, 5, 0, 0));

        VBox textContent = new VBox(5);

        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cardTitle.setTextFill(Color.web("#333333"));

        Label insightLabel = new Label();
        insightLabel.setFont(Font.font("Arial", 14));
        insightLabel.setTextFill(Color.web("#666666"));
        insightLabel.setWrapText(true);
        insightLabel.textProperty().bind(insightTextProperty);

        textContent.getChildren().addAll(cardTitle, insightLabel);
        card.getChildren().addAll(iconLabel, textContent);

        final Tooltip tooltip = new Tooltip(title + ":\n" + insightTextProperty.get());
        Tooltip.install(card, tooltip);
        insightTextProperty.addListener((obs, oldVal, newVal) -> {
            tooltip.setText(title + ":\n" + newVal);
        });

        return card;
    }

    private void addChartTooltip(Node node, String text) {
        Tooltip tooltip = (Tooltip) node.getProperties().get("fx.tooltip");
        if (tooltip == null) {
            tooltip = new Tooltip();
            Tooltip.install(node, tooltip);
            node.getProperties().put("fx.tooltip", tooltip);

            final Tooltip finalTooltip = tooltip;
            node.setOnMouseClicked(event -> {
                if (finalTooltip.isShowing()) {
                    finalTooltip.hide();
                } else {
                    Bounds bounds = node.localToScreen(node.getBoundsInLocal());
                    finalTooltip.show(node, bounds.getMaxX(), bounds.getMaxY());
                }
            });

            node.setOnMouseExited(event -> finalTooltip.hide());
        }
        tooltip.setText(text);
    }
}