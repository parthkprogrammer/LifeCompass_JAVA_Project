package com.lifecompass.dao.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.lifecompass.config.FirebaseConfig;
import com.lifecompass.dao.UserDao;
import com.lifecompass.model.Psychologist;
import com.lifecompass.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class UserDaoFirestoreImpl implements UserDao {

    private final Firestore db;
    private static final String COLLECTION_NAME = "users";

    public UserDaoFirestoreImpl() {
        this.db = FirebaseConfig.getFirestore();
    }

    @Override
    public void addUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getId());
        ApiFuture<WriteResult> result = docRef.set(user.toMap());
        result.get();
        System.out.println("User " + user.getId() + " added to Firestore.");
    }


    


    @Override
    public Optional<User> getUserById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.of(User.fromMap(document.getId(), document.getData()));
        } else {
            System.out.println("No such user document!");
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            return Optional.of(User.fromMap(documents.get(0).getId(), documents.get(0).getData()));
        } else {
            System.out.println("No user found with email: " + email);
            return Optional.empty();
        }
    }

    @Override
    public void updateUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getId());
        ApiFuture<WriteResult> result = docRef.set(user.toMap());
        result.get();
        System.out.println("User " + user.getId() + " updated in Firestore.");
    }

    @Override
    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
        writeResult.get();
        System.out.println("User " + id + " deleted from Firestore.");
    }
}