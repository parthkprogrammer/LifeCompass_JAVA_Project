package com.lifecompass.api;

import com.lifecompass.model.Community;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommunityApiClient { // Does not extend BaseApiClient

    public CompletableFuture<List<Community>> fetchCommunities() {
        System.out.println("Fetching communities (using dummy data)...");
        return CompletableFuture.completedFuture(Arrays.asList(
            new Community("c001", "Anxiety Support Circle", 1247, "Active",
                    "A safe space to share experiences and coping strategies for anxiety."),
            new Community("c002", "Mindfulness Practitioners", 892, "Active",
                    "Share mindfulness techniques and meditation experiences."),
            new Community("c003", "Creative Expression", 634, "Quiet",
                    "Share your art, writing, and creative outlets for emotional expression."),
            new Community("c004", "Daily Gratitude", 1156, "Active", "Share what you're grateful for and spread positivity.")
        ));
    }
}