package com.lifecompass.api;

import com.lifecompass.model.MusicTrack;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MusicApiClient {

    public CompletableFuture<List<MusicTrack>>fetchMusicTracks(String query) {
        System.out.println("Fetching music tracks (serving local MP3s for query: '" + query + "')...");
        // This simulates an API by returning pre-defined local MP3 files.
        // Ensure these paths correspond to actual .mp3 files in your src/main/resources/assets/audio/ directory.
        return CompletableFuture.completedFuture(Arrays.asList(
            new MusicTrack("m001", "Peaceful Morning", "Nature Sounds", "5:30", "Calm", "/assets/audio/peaceful_morning.mp3"),
            new MusicTrack("m002", "Anxiety Relief", "Meditation Music", "8:15", "Relaxing", "/assets/audio/anxiety_relief.mp3"),
            new MusicTrack("m003", "Focus Flow", "Study Beats", "4:00", "Focus", "/assets/audio/energy_boost.mp3"), // Example additional local track
            new MusicTrack("m004", "Sleep Journey", "Ambient Sleep", "60:00", "Sleepy", "/assets/audio/sleep_sounds.mp3"), // Example additional local track

              new MusicTrack("m005", "Calm Music", "Meditation", "1:47", "Calm", "/assets/audio/calm.mp3"),
            new MusicTrack("m006", "Spiritual", "Meditation Music", "22.20", "Relaxing", "/assets/audio/spiritual.mp3"),
            new MusicTrack("m007", "Morden Classic", "Morden classic", "1.42", "Focus", "/assets/audio/morden_classic.mp3") // Example additional local track
        ));
    }
}