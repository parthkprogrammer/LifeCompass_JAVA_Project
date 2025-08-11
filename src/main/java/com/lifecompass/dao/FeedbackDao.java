package com.lifecompass.dao;

import com.lifecompass.model.Feedback;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface FeedbackDao {
    void addFeedback(Feedback feedback) throws ExecutionException, InterruptedException;
    Optional<Feedback> getFeedbackById(String id) throws ExecutionException, InterruptedException;
    List<Feedback> getAllFeedback() throws ExecutionException, InterruptedException;
    void updateFeedback(Feedback feedback) throws ExecutionException, InterruptedException; // If you need to change status, etc.
    void deleteFeedback(String id) throws ExecutionException, InterruptedException;
}