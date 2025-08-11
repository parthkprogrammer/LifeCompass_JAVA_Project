package com.lifecompass.dao; // Corrected package name

import com.lifecompass.model.MoodEntry;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future; // Use Future for async ops if implementation uses it, otherwise remove

public interface MoodDao {
    void addMoodEntry(MoodEntry entry);
    Optional<MoodEntry> getMoodEntryById(String id);
    List<MoodEntry> getMoodEntriesByUserId(String userId);
    List<MoodEntry> getAllMoodEntries();
    void updateMoodEntry(MoodEntry entry);
    void deleteMoodEntry(String id);
}