package com.lifecompass.dao.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.lifecompass.config.FirebaseConfig; // Assuming FirebaseConfig for Firestore instance
import com.lifecompass.dao.FeedbackDao;
import com.lifecompass.model.Feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FeedbackDaoFirestoreImpl implements FeedbackDao {

    private final Firestore db;
    private static final String COLLECTION_NAME = "feedback"; // Collection name in Firestore

    public FeedbackDaoFirestoreImpl() {
        this.db = FirebaseConfig.getFirestore(); // Get Firestore instance from your config
    }

    @Override
    public void addFeedback(Feedback feedback) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(); // Firestore generates ID
        feedback.setId(docRef.getId()); // Set the ID back to the model
        ApiFuture<WriteResult> result = docRef.set(feedback.toMap()); // Save the map representation
        result.get(); // Block until write is complete
        System.out.println("Feedback submitted with ID: " + feedback.getId());
    }

    @Override
    public Optional<Feedback> getFeedbackById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.of(Feedback.fromMap(document.getId(), document.getData()));
        } else {
            System.out.println("No feedback found with ID: " + id);
            return Optional.empty();
        }
    }

    @Override
    public List<Feedback> getAllFeedback() throws ExecutionException, InterruptedException {
        List<Feedback> feedbackList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<com.google.cloud.firestore.QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (com.google.cloud.firestore.QueryDocumentSnapshot document : documents) {
            feedbackList.add(Feedback.fromMap(document.getId(), document.getData()));
        }
        return feedbackList;
    }

    @Override
    public void updateFeedback(Feedback feedback) throws ExecutionException, InterruptedException {
        if (feedback.getId() == null || feedback.getId().isEmpty()) {
            throw new IllegalArgumentException("Feedback ID must be set for update operations.");
        }
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(feedback.getId());
        ApiFuture<WriteResult> result = docRef.set(feedback.toMap());
        result.get();
        System.out.println("Feedback " + feedback.getId() + " updated in Firestore.");
    }

    @Override
    public void deleteFeedback(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
        writeResult.get();
        System.out.println("Feedback " + id + " deleted from Firestore.");
    }
}