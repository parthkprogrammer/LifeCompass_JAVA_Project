package com.lifecompass.model;

public class DailyChallenge {
    private String id;
    private String description;
    private int xpReward;
    private boolean completed;

    public DailyChallenge() {
    }

    public DailyChallenge(String id, String description, int xpReward, boolean completed) {
        this.id = id;
        this.description = description;
        this.xpReward = xpReward;
        this.completed = completed;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public int getXpReward() { return xpReward; }
    public boolean isCompleted() { return completed; }

    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}