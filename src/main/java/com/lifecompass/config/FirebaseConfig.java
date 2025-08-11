package com.lifecompass.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {

    private static FirebaseApp firebaseApp;
    private static Firestore firestoreDb;
    private static FirebaseAuth firebaseAuth;
    private static final String SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/assets/firebase-config.json"; // Correct filename

    public static void initialize() {
        if (firebaseApp == null) {
            try {
                FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_KEY_PATH);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                firebaseApp = FirebaseApp.initializeApp(options);
                firestoreDb = FirestoreClient.getFirestore();
                firebaseAuth = FirebaseAuth.getInstance();
                System.out.println("Firebase initialized successfully!");

            } catch (IOException e) {
                System.err.println("Error initializing Firebase: " + e.getMessage());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                System.err.println("Firebase already initialized: " + e.getMessage());
                firebaseApp = FirebaseApp.getInstance();
                firestoreDb = FirestoreClient.getFirestore();
                firebaseAuth = FirebaseAuth.getInstance();
            }
        }
    }

    public static Firestore getFirestore() {
        if (firestoreDb == null) {
            initialize();
        }
        return firestoreDb;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            initialize();
        }
        return firebaseAuth;
    }
}