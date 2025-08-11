
package com.lifecompass.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream; // Added for FileInputStream option

public class FirebaseService {

    // IMPORTANT: Update this path to the EXACT filename of your service account key.
    // Ensure the JSON file is in your Maven project's src/main/resources/assets/ directory
    // and the path matches its filename exactly.

    // --- CHOOSE ONE PATH STRATEGY FOR YOUR SERVICE ACCOUNT KEY ---

    // OPTION A: ABSOLUTE PATH (Best for local development/debugging where path is fixed)
    // IMPORTANT: REPLACE "U:/New_Final/lifecompasfinal11/" WITH THE ACTUAL ABSOLUTE PATH TO YOUR PROJECT ROOT.
    // private static final String SERVICE_ACCOUNT_KEY_FILE_PATH = "U:/New_Final/lifecompasfinal11/src/main/resources/assets/firebase-config.json";

    // OPTION B: CLASSPATH RESOURCE (Recommended for deployment, e.g., in a JAR)
    // This assumes 'firebase-config.json' is directly inside 'src/main/resources/assets/'
    private static final String SERVICE_ACCOUNT_KEY_CLASSPATH = "/assets/firebase-config.json";


    private static volatile Firestore db;

    // Private constructor: Prevents direct instantiation of this utility class.
    // All interaction should be via static methods.
    private FirebaseService() {
        // Initialization logic is in the static initialize() method.
    }

    /**
     * Initializes the Firebase application and Firestore client.
     * This method MUST be called exactly ONCE at the very beginning of your application's lifecycle,
     * typically at your application's startup point (e.g., main method, or JavaFX Application's start method).
     */
    public static void initialize() {
        // Double-check locking for thread-safe lazy initialization
        if (db == null) {
            synchronized (FirebaseService.class) {
                if (db == null) {
                    if (FirebaseApp.getApps().isEmpty()) { // Check if FirebaseApp is already initialized
                        System.out.println("FirebaseService.initialize() called. FirebaseApp not initialized. Attempting full initialization...");
                        try {
                            InputStream serviceAccountStream = null;

                            // --- UNCOMMENT ONE OF THE FOLLOWING BLOCKS TO CHOOSE YOUR LOADING METHOD ---

                            // // OPTION A: Loading using FileInputStream with the ABSOLUTE_PATH (Uncomment this for local testing)
                            // System.out.println("Attempting to load service account key from absolute path: " + SERVICE_ACCOUNT_KEY_FILE_PATH);
                            // serviceAccountStream = new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH);


                            // OPTION B: Loading using ClassLoader.getResourceAsStream (Uncomment this for bundled JARs)
                            System.out.println("Attempting to load service account key from classpath: " + SERVICE_ACCOUNT_KEY_CLASSPATH);
                            // Use getResourceAsStream on the class itself for resources relative to the class's package, or ClassLoader for absolute classpath resources.
                            // "/assets/firebase-config.json" means it looks in the root of the classpath (e.g., src/main/resources/).
                            serviceAccountStream = FirebaseService.class.getResourceAsStream(SERVICE_ACCOUNT_KEY_CLASSPATH);

                            if (serviceAccountStream == null) {
                                throw new IOException("Firebase service account key NOT FOUND at: " + SERVICE_ACCOUNT_KEY_CLASSPATH +
                                        "\nEnsure the JSON file is in your Maven project's src/main/resources/assets/ directory" +
                                        " and the path in FirebaseService.java matches its filename exactly (with a leading '/').");
                            }

                            FirebaseOptions options = FirebaseOptions.builder()
                                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                                    .build();

                            FirebaseApp.initializeApp(options);
                            // Get the default Firestore instance from the newly initialized app
                            db = FirestoreClient.getFirestore();
                            System.out.println("Firebase initialized successfully. Firestore instance obtained (default database).");

                        } catch (IOException e) {
                            System.err.println("CRITICAL ERROR: Firebase initialization failed due to FILE/RESOURCE issue: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Failed to initialize FirebaseService. Check 'firebase-config.json' path and existence.", e);
                        } catch (IllegalStateException e) {
                            // This can happen if FirebaseApp was initialized elsewhere or twice.
                            // If it was already initialized, we just need to get the Firestore instance.
                            System.err.println("WARNING: FirebaseApp was likely already initialized. Attempting to get existing Firestore instance. Error: " + e.getMessage());
                            e.printStackTrace();
                            try {
                                db = FirestoreClient.getFirestore();
                                System.out.println("Successfully obtained Firestore instance from existing FirebaseApp.");
                            } catch (Exception ex) {
                                System.err.println("CRITICAL ERROR: Could not get Firestore instance even with existing FirebaseApp: " + ex.getMessage());
                                ex.printStackTrace();
                                throw new RuntimeException("Failed to obtain Firestore instance after FirebaseApp seemed initialized.", ex);
                            }
                        } catch (Exception e) {
                            System.err.println("CRITICAL ERROR: Unexpected exception during Firebase initialization: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Unexpected error during FirebaseService initialization.", e);
                        }
                    } else {
                        // If FirebaseApp is already initialized (e.g., by previous call to this method)
                        db = FirestoreClient.getFirestore();
                        System.out.println("FirebaseApp already existed. Obtained Firestore instance (default database).");
                    }
                }
            }
        } else {
            System.out.println("FirebaseService already initialized. Skipping re-initialization.");
        }
    }

    /**
     * Provides the singleton Firestore database instance.
     * This method should ONLY be called AFTER FirebaseService.initialize() has successfully completed.
     * It also acts as a fallback to call initialize() if 'db' is null, though primary init should be at app startup.
     *
     * @return The Firestore database instance.
     * @throws IllegalStateException if FirebaseService has not been initialized successfully.
     */
    public static Firestore getFirestoreInstance() {
        if (db == null) {
            // This indicates a critical error: initialize() was not called at startup, or it failed.
            // Attempt to initialize now as a fallback.
            System.err.println("WARNING: getFirestoreInstance() called before initialization. Attempting fallback initialization.");
            initialize(); // Call initialize as fallback
        }
        if (db == null) {
            System.err.println("ERROR: Firestore instance is NULL even after fallback initialization. FirebaseService is not truly initialized.");
            throw new IllegalStateException("Firestore not initialized. Ensure FirebaseService.initialize() is called successfully at application startup.");
        }
        return db;
    }
}