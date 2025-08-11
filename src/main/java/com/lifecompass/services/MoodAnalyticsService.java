package com.lifecompass.services;

import com.lifecompass.dao.MoodDao;
import com.lifecompass.dao.impl.MoodDaoFirestoreImpl;
import com.lifecompass.model.MoodEntry;
import com.lifecompass.view.MoodTrackerApp;
import com.lifecompass.view.MentalHealthAppView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MoodAnalyticsService {

    private final MoodDao moodDao;

    public MoodAnalyticsService() {
        this.moodDao = new MoodDaoFirestoreImpl();
    }

    public MoodAnalyticsService(MoodDao moodDao) {
        this.moodDao = moodDao;
    }

    /**
     * Retrieves all mood entries for a given user.
     * @param userId The ID of the user.
     * @return A list of MoodEntry objects.
     */
    public List<MoodEntry> getUserMoodEntries(String userId) {
        System.out.println("MoodAnalyticsService: Attempting to get mood entries for user: " + userId); // DEBUG
        List<MoodEntry> entries = moodDao.getMoodEntriesByUserId(userId);
        System.out.println("MoodAnalyticsService: Found " + entries.size() + " entries for user: " + userId); // DEBUG
        return entries;
    }

    /**
     * Calculates weekly mood and energy trends for the line chart.
     * Uses MoodEntry.getIntensity() for mood and MoodEntry.getEnergyLevel() for energy.
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return A Map containing two ObservableLists: "mood" and "energy".
     */
    public Map<String, ObservableList<XYChart.Data<String, Number>>> getWeeklyMoodAndEnergyTrend(String userId, List<MoodEntry> entries) {
        Map<String, List<Integer>> moodScoresByDay = new HashMap<>();
        Map<String, List<Integer>> energyScoresByDay = new HashMap<>(); // Now uses distinct energy level

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : daysOfWeek) {
            moodScoresByDay.put(day, new ArrayList<>());
            energyScoresByDay.put(day, new ArrayList<>());
        }

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<MoodEntry> recentEntries = entries.stream()
                .filter(entry -> entry.getTimestamp().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());

        System.out.println("MoodAnalyticsService: Processing " + recentEntries.size() + " recent entries for weekly trend."); // DEBUG

        for (MoodEntry entry : recentEntries) {
            DayOfWeek day = entry.getTimestamp().getDayOfWeek();
            String dayLabel;
            switch (day) {
                case MONDAY: dayLabel = "Mon"; break;
                case TUESDAY: dayLabel = "Tue"; break;
                case WEDNESDAY: dayLabel = "Wed"; break;
                case THURSDAY: dayLabel = "Thu"; break;
                case FRIDAY: dayLabel = "Fri"; break;
                case SATURDAY: dayLabel = "Sat"; break;
                case SUNDAY: dayLabel = "Sun"; break;
                default: dayLabel = "";
            }

            moodScoresByDay.get(dayLabel).add(entry.getIntensity());
            energyScoresByDay.get(dayLabel).add(entry.getEnergyLevel()); // NEW: Use getEnergyLevel()
        }

        ObservableList<XYChart.Data<String, Number>> moodSeriesData = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<String, Number>> energySeriesData = FXCollections.observableArrayList();

        for (String day : daysOfWeek) {
            OptionalDouble avgMood = moodScoresByDay.get(day).stream().mapToInt(Integer::intValue).average();
            OptionalDouble avgEnergy = energyScoresByDay.get(day).stream().mapToInt(Integer::intValue).average();
            moodSeriesData.add(new XYChart.Data<>(day, avgMood.orElse(0.0)));
            energySeriesData.add(new XYChart.Data<>(day, avgEnergy.orElse(0.0)));
        }

        Map<String, ObservableList<XYChart.Data<String, Number>>> result = new HashMap<>();
        result.put("mood", moodSeriesData);
        result.put("energy", energySeriesData);
        return result;
    }

    /**
     * Calculates mood distribution for the pie chart.
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return An ObservableList of PieChart.Data.
     */
    public ObservableList<PieChart.Data> getMoodDistributionData(String userId, List<MoodEntry> entries) {
        Map<String, Long> moodCounts = entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMoodEmoji, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        long totalEntries = entries.size();

        if (totalEntries == 0) {
            pieChartData.add(new PieChart.Data("No Data", 100));
            return pieChartData;
        }

        Map<String, String> emojiToLabelMap = MoodTrackerApp.MOOD_EMOJIS_DATA.stream()
            .collect(Collectors.toMap(MoodTrackerApp.MoodEmoji::getEmoji, MoodTrackerApp.MoodEmoji::getLabel));

        for (Map.Entry<String, Long> entry : moodCounts.entrySet()) {
            String emoji = entry.getKey();
            long count = entry.getValue();
            double percentage = (double) count / totalEntries * 100;
            String label = emojiToLabelMap.getOrDefault(emoji, "Unknown Mood");
            pieChartData.add(new PieChart.Data(label, percentage));
        }

        pieChartData.sort(Comparator.comparing(PieChart.Data::getPieValue).reversed());

        return pieChartData;
    }

    /**
     * Calculates monthly average mood for the bar chart.
     * Aggregates average mood intensity per week of the current month.
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return An ObservableList of XYChart.Data.
     */
    public ObservableList<XYChart.Data<String, Number>> getMonthlyMoodTrend(String userId, List<MoodEntry> entries) {
        Map<Integer, List<Integer>> moodScoresByWeek = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        List<MoodEntry> currentMonthEntries = entries.stream()
                .filter(entry -> entry.getTimestamp().getYear() == now.getYear() &&
                                 entry.getTimestamp().getMonth() == now.getMonth())
                .collect(Collectors.toList());

        for (MoodEntry entry : currentMonthEntries) {
            int weekOfMonth = entry.getTimestamp().get(weekFields.weekOfMonth());
            moodScoresByWeek.computeIfAbsent(weekOfMonth, k -> new ArrayList<>()).add(entry.getIntensity());
        }

        ObservableList<XYChart.Data<String, Number>> monthlyTrendData = FXCollections.observableArrayList();

        for (int i = 1; i <= 5; i++) {
            final int weekNum = i;
            OptionalDouble avgMood = moodScoresByWeek.getOrDefault(weekNum, new ArrayList<>()).stream()
                    .mapToInt(Integer::intValue)
                    .average();
            monthlyTrendData.add(new XYChart.Data<>("Week " + weekNum, avgMood.orElse(0.0)));
        }

        return monthlyTrendData;
    }

    /**
     * Provides aggregated insights for "Mood Improvement Areas".
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return A Map containing string descriptions for sleep, exercise, and social interaction impact.
     */
    public Map<String, String> getMoodImprovementAreas(String userId, List<MoodEntry> entries) {
        Map<String, String> insights = new HashMap<>();

        Function<String, OptionalDouble> getAvgMoodForTag = (tag) ->
            entries.stream()
                .filter(entry -> entry.getTags() != null && entry.getTags().contains(tag))
                .mapToInt(MoodEntry::getIntensity)
                .average();

        // For sleep impact, using energyLevel alongside a 'Tired' tag as an example
        OptionalDouble sleepAvg = entries.stream()
            .filter(e -> e.getEnergyLevel() <= 3 && e.getTags() != null && e.getTags().contains("Tired")) // Use energyLevel for sleep impact
            .mapToInt(MoodEntry::getIntensity) // Still average mood intensity for correlation strength
            .average();

        OptionalDouble exerciseAvg = getAvgMoodForTag.apply("Energetic");
        OptionalDouble socialAvg = getAvgMoodForTag.apply("Social");

        insights.put("sleepQuality", interpretMoodImpact(sleepAvg, "sleep"));
        insights.put("exerciseFrequency", interpretMoodImpact(exerciseAvg, "exercise"));
        insights.put("socialInteraction", interpretMoodImpact(socialAvg, "social"));

        return insights;
    }

    private String interpretMoodImpact(OptionalDouble avgMood, String activityType) {
        if (!avgMood.isPresent() || avgMood.getAsDouble() == 0.0) {
            return "No data recorded";
        }
        double moodScore = avgMood.getAsDouble();

        if (moodScore >= 8.0) {
            return "Strong correlation";
        } else if (moodScore >= 6.0) {
            return "Good correlation";
        } else if (moodScore >= 4.0) {
            return "Moderate correlation";
        } else {
            return "Weak correlation";
        }
    }

    /**
     * Identifies top mood triggers based on tag frequency and associated mood intensity.
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return An ObservableList of MoodTriggerItem.
     */
    public ObservableList<MentalHealthAppView.MoodTriggerItem> getTopMoodTriggers(String userId, List<MoodEntry> entries) {
        Map<String, List<Integer>> tagMoods = new HashMap<>();
        Map<String, Long> tagCounts = new HashMap<>();

        for (MoodEntry entry : entries) {
            if (entry.getTags() != null) {
                for (String tag : entry.getTags()) {
                    tagMoods.computeIfAbsent(tag, k -> new ArrayList<>()).add(entry.getIntensity());
                    tagCounts.merge(tag, 1L, Long::sum);
                }
            }
        }

        ObservableList<MentalHealthAppView.MoodTriggerItem> triggerItems = FXCollections.observableArrayList();

        tagCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(entry -> {
                    String tag = entry.getKey();
                    long occurrences = entry.getValue();
                    OptionalDouble avgMood = tagMoods.get(tag).stream().mapToInt(Integer::intValue).average();
                    String impact = "Neutral";
                    if (avgMood.isPresent()) {
                        double moodVal = avgMood.getAsDouble();
                        if (moodVal >= 7.0) {
                            impact = "Positive";
                        } else if (moodVal <= 4.0) {
                            impact = "High";
                        } else {
                            impact = "Mixed";
                        }
                    }
                    triggerItems.add(new MentalHealthAppView.MoodTriggerItem(tag, (int) occurrences, impact));
                });
        return triggerItems;
    }

    /**
     * Determines time-based mood patterns (Morning, Afternoon, Evening).
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return An ObservableList of TimePatternItem.
     */
    public ObservableList<MentalHealthAppView.TimePatternItem> getTimeBasedPatterns(String userId, List<MoodEntry> entries) {
        Map<String, List<Integer>> moodScoresByTimeOfDay = new HashMap<>();
        moodScoresByTimeOfDay.put("Morning", new ArrayList<>());
        moodScoresByTimeOfDay.put("Afternoon", new ArrayList<>());
        moodScoresByTimeOfDay.put("Evening", new ArrayList<>());
        moodScoresByTimeOfDay.put("Night", new ArrayList<>());

        for (MoodEntry entry : entries) {
            int hour = entry.getTimestamp().getHour();
            if (hour >= 6 && hour < 12) {
                moodScoresByTimeOfDay.get("Morning").add(entry.getIntensity());
            } else if (hour >= 12 && hour < 18) {
                moodScoresByTimeOfDay.get("Afternoon").add(entry.getIntensity());
            } else if (hour >= 18 && hour < 24) {
                moodScoresByTimeOfDay.get("Evening").add(entry.getIntensity());
            } else {
                moodScoresByTimeOfDay.get("Night").add(entry.getIntensity());
            }
        }

        ObservableList<MentalHealthAppView.TimePatternItem> timePatternItems = FXCollections.observableArrayList();

        String[] timeRanges = {"Morning", "Afternoon", "Evening", "Night"};

        for (String range : timeRanges) {
            OptionalDouble avgMood = moodScoresByTimeOfDay.get(range).stream().mapToInt(Integer::intValue).average();
            String description = "No data";
            String emoji = "❓";

            if (avgMood.isPresent() && avgMood.getAsDouble() != 0.0) {
                double moodVal = avgMood.getAsDouble();
                if (moodVal >= 7.5) {
                    description = "Generally positive";
                    emoji = "😊";
                } else if (moodVal >= 5.0) {
                    description = "Mixed emotions";
                    emoji = "😐";
                } else {
                    description = "Challenging mood";
                    emoji = "😔";
                }
            }
            if (moodScoresByTimeOfDay.get(range).size() > 0 || !range.equals("Night")) {
                 timePatternItems.add(new MentalHealthAppView.TimePatternItem(range + getTimeRangeLabel(range), description, emoji));
            }
        }
        return timePatternItems;
    }

    private String getTimeRangeLabel(String range) {
        switch (range) {
            case "Morning": return " (6AM - 12PM)";
            case "Afternoon": return " (12PM - 6PM)";
            case "Evening": return " (6PM - 12AM)";
            case "Night": return " (12AM - 6AM)";
            default: return "";
        }
    }


    /**
     * Generates personalized insights and recommendations.
     *
     * @param userId The ID of the user.
     * @param entries The list of mood entries for the user.
     * @return A Map containing insights: keyInsight, positivePattern, areaForImprovement, recommendation.
     */
    public Map<String, String> getPersonalizedInsights(String userId, List<MoodEntry> entries) {
        Map<String, String> insights = new HashMap<>();

        if (entries.isEmpty()) {
            insights.put("keyInsight", "Start logging your mood to uncover insights!");
            insights.put("positivePattern", "No patterns yet.");
            insights.put("areaForImprovement", "More data needed.");
            insights.put("recommendation", "Log at least 5-7 mood entries to get initial insights.");
            return insights;
        }

        long totalEntries = entries.size();
        double overallAvgMood = entries.stream().mapToInt(MoodEntry::getIntensity).average().orElse(0.0);

        String keyInsight = "Keep tracking your mood to see deeper patterns!";
        if (overallAvgMood >= 7.5) {
            keyInsight = "Overall, you've been experiencing a positive emotional state. Keep up the good habits!";
        } else if (overallAvgMood <= 4.0) {
            keyInsight = "It seems you've been navigating some challenging emotions recently. We're here to help.";
        } else {
            keyInsight = "Your mood has been generally stable, with some fluctuations. Let's explore what influences it.";
        }
        insights.put("keyInsight", keyInsight);

        String positivePattern = "No clear positive patterns yet.";
        OptionalDouble avgMoodWithExercise = entries.stream()
            .filter(e -> e.getTags() != null && (e.getTags().contains("Energetic") || e.getTags().contains("Exercise")))
            .mapToInt(MoodEntry::getIntensity)
            .average();
        if (avgMoodWithExercise.isPresent() && avgMoodWithExercise.getAsDouble() >= 8.0 && entries.stream().filter(e -> e.getTags() != null && (e.getTags().contains("Energetic") || e.getTags().contains("Exercise"))).count() >= 3) {
            positivePattern = "You often report higher moods when engaging in physical activity. This is a great coping strategy!";
        } else if (totalEntries >= 5 && entries.stream().anyMatch(e -> e.getNotes() != null && e.getNotes().toLowerCase().contains("journal"))) {
            positivePattern = "Your journaling habit is a strong positive. Keep reflecting on your thoughts and feelings.";
        }
        insights.put("positivePattern", positivePattern);


        String areaForImprovement = "More data needed for specific areas.";
        long anxiousCount = entries.stream().filter(e -> e.getMoodEmoji().equals("😖")).count();
        if (anxiousCount > totalEntries * 0.2) {
            areaForImprovement = "Anxiety seems to be a recurring theme. Exploring relaxation techniques might be beneficial.";
        }
        OptionalDouble avgMoodWithStress = entries.stream()
            .filter(e -> e.getTags() != null && e.getTags().contains("Stressed"))
            .mapToInt(MoodEntry::getIntensity)
            .average();
        if (avgMoodWithStress.isPresent() && avgMoodWithStress.getAsDouble() <= 4.0 && entries.stream().filter(e -> e.getTags() != null && e.getTags().contains("Stressed")).count() >= 2) {
            areaForImprovement = "Your mood significantly dips when you tag yourself as 'Stressed'. Let's find ways to manage stress.";
        }
        insights.put("areaForImprovement", areaForImprovement);

        String recommendation = "Continue tracking consistently to receive tailored advice.";
        if (insights.get("areaForImprovement").contains("Anxiety")) {
            recommendation = "Recommendation: Try guided breathing exercises or progressive muscle relaxation when you feel anxious.";
        } else if (insights.get("areaForImprovement").contains("Stressed")) {
            recommendation = "Recommendation: Consider scheduling short breaks throughout your day to reduce stress buildup.";
        } else if (insights.get("positivePattern").contains("physical activity")) {
            recommendation = "Recommendation: Keep up your exercise routine! Maybe try a new type of workout to keep it engaging.";
        } else if (insights.get("keyInsight").contains("positive emotional state")) {
            recommendation = "Recommendation: Share your positive experiences in your journal or with a friend!";
        }
        insights.put("recommendation", recommendation);

        return insights;
    }
}