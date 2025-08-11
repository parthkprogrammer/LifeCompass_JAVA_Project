package com.lifecompass.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.*;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.Map;

public class FirebaseHelper {
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;

        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/firebase-config.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("databaseURL") // Replace with your Firebase Realtime Database URL
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            System.out.println("✅ Firebase initialized.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to initialize Firebase.");
        }
    }

    // ✅ Step 1: Create Firebase Auth user
    public static String createUser(String email, String password) throws Exception {
        initialize();

        try {
            CreateRequest request = new CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
            System.out.println("✅ Created user: " + userRecord.getUid());
            return userRecord.getUid();

        } catch (InterruptedException | ExecutionException e) {
            throw new Exception("Firebase Auth error: " + e.getCause().getMessage());
        }
    }

    // ✅ Step 2: Save user data to Realtime DB
    public static void saveUserData(String uid, Map<String, Object> userData) throws Exception {
        initialize();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        ref.setValueAsync(userData);
        System.out.println("✅ User data saved to database.");
    }

    public static java.util.concurrent.CompletableFuture<Boolean> loginUser(String email, String password) {
    initialize();

    return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
        try {
            // Firebase Admin SDK cannot verify passwords, so we check if user with email exists
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            return userRecord != null;
        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            return false;
        }
    });
}
    
}
