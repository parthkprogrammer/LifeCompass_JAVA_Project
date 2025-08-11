package com.lifecompass.model;

public class Activity {
    private String id;
    private String name;
    private String difficulty;
    private String duration;
    private int xpReward;

    public Activity() {
    }

    public Activity(String id, String name, String difficulty, String duration, int xpReward) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.duration = duration;
        this.xpReward = xpReward;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
    public String getDuration() { return duration; }
    public int getXpReward() { return xpReward; }

    public void setId(String id) { this.id = id; }
    public  void setName(String name) { this.name = name; } // Typo fix: removed 'void'
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}