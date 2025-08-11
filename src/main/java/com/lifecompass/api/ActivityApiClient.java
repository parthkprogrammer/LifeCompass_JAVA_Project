package com.lifecompass.api;

import com.lifecompass.model.Activity;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActivityApiClient { // Does not extend BaseApiClient

    public CompletableFuture<List<Activity>> fetchActivities() {
        System.out.println("Fetching activities (using dummy data)...");
        return CompletableFuture.completedFuture(Arrays.asList(
            new Activity("a001", "Breathing Bubble", "Easy", "3-5 min", 10),
            new Activity("a002", "Gratitude Garden", "Easy", "5-10 min", 10),
            new Activity("a003", "Mood Maze", "Medium", "10-15 min", 10),
            new Activity("a004", "Mindful Walk", "Easy", "15-20 min", 15)
        ));
    }
}