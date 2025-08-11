package com.lifecompass.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MusicTrack {
    private String id;
    private String title;
    @JsonProperty("artistOrDescription")
    private String artistOrDescription;
    private String duration;
    private String moodTag;
    private String audioUrl; // This will now be a local resource path like "/assets/audio/file.mp3"

    public MusicTrack() {
    }

    public MusicTrack(String id, String title, String artistOrDescription, String duration, String moodTag, String audioUrl) {
        this.id = id;
        this.title = title;
        this.artistOrDescription = artistOrDescription;
        this.duration = duration;
        this.moodTag = moodTag;
        this.audioUrl = audioUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    @JsonProperty("artistOrDescription")
    public String getArtistOrDescription() { return artistOrDescription; }
    public String getDuration() { return duration; }
    public String getMoodTag() { return moodTag; }
    public String getAudioUrl() { return audioUrl; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    @JsonProperty("artistOrDescription")
    public void setArtistOrDescription(String artistOrDescription) { this.artistOrDescription = artistOrDescription; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setMoodTag(String moodTag) { this.moodTag = moodTag; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}