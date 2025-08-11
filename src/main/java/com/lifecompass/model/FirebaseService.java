package com.lifecompass.model; // <-- THIS MUST BE CORRECTED IN YOUR FILE

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream; // Used for absolute path loading
import java.io.IOException;
import java.io.InputStream;    // Used for ClassLoader.getResourceAsStream
import java.time.LocalDate;     // Keep if needed for other models (e.g., User/Psychologist dates)
import java.time.ZoneId;       // Keep if needed for other models (e.g., User/Psychologist dates)
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseService {

    private static Firestore db;

    // --- IMPORTANT: CHOOSE ONE PATH STRATEGY FOR YOUR SERVICE ACCOUNT KEY ---

    // OPTION A: ABSOLUTE PATH (Best for local development/debugging where path is fixed)
    // YOU MUST REPLACE "U:/New_Final/lifecompasfinal11/" WITH THE ACTUAL ABSOLUTE PATH TO YOUR PROJECT ROOT.
    // Ensure the filename 'firebase-config.json' is correct.
    private static final String SERVICE_ACCOUNT_KEY_FILE_PATH = "src/main/resources/assets/firebase-config.json";

    // // OPTION B: CLASSPATH RESOURCE (Recommended for deployment, e.g., in a JAR)
    // // This assumes 'firebase-config.json' is directly inside 'src/main/resources/assets/'
    // private static final String SERVICE_ACCOUNT_KEY_CLASSPATH = "assets/firebase-config.json";


    // Private constructor: Prevents direct instantiation of this utility class.
    // All interaction should be via static methods.
    private FirebaseService() {
        // Initialization logic is in the static initialize() method.
    }

    /**
     * Initializes the Firebase application and Firestore client.
     * This method MUST be called exactly ONCE at the very beginning of your application's lifecycle,
     * typically in the JavaFX Application's init() method (e.g., AdminDashboardView.init()).
     */
    public static void initialize() {
        if (db == null) { // Only attempt initialization if 'db' is currently null
            if (FirebaseApp.getApps().isEmpty()) { // Check if FirebaseApp is already initialized
                System.out.println("FirebaseService.initialize() called. FirebaseApp not initialized. Attempting full initialization...");
                try {
                    InputStream serviceAccountStream = null;

                    // --- UNCOMMENT ONE OF THE FOLLOWING BLOCKS TO CHOOSE YOUR LOADING METHOD ---

                    // OPTION A: Loading using FileInputStream with the ABSOLUTE_PATH (Uncomment this for local testing)
                    System.out.println("Attempting to load service account key from absolute path: " + SERVICE_ACCOUNT_KEY_FILE_PATH);
                    serviceAccountStream = new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH);


                    // // OPTION B: Loading using ClassLoader.getResourceAsStream (Uncomment this for bundled JARs)
                    // // System.out.println("Attempting to load service account key from classpath: " + SERVICE_ACCOUNT_KEY_CLASSPATH);
                    // // serviceAccountStream = FirebaseService.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_KEY_CLASSPATH);
                    // // if (serviceAccountStream == null) {
                    // //     System.err.println("CRITICAL ERROR: Firebase service account key NOT FOUND on classpath: " + SERVICE_ACCOUNT_KEY_CLASSPATH);
                    // //     throw new IOException("Firebase service account key not found in resources: " + SERVICE_ACCOUNT_KEY_CLASSPATH);
                    // // }


                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                            .build();

                    FirebaseApp.initializeApp(options);
                    db = FirestoreClient.getFirestore(); // Get the Firestore instance from the newly initialized app
                    System.out.println("Firebase initialized successfully. Firestore instance obtained.");

                } catch (IOException e) {
                    System.err.println("CRITICAL ERROR: Firebase initialization failed due to FILE/RESOURCE issue: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to initialize FirebaseService. Check 'firebase-config.json' path and existence.", e);
                } catch (IllegalStateException e) {
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
                System.out.println("FirebaseApp already existed. Obtained Firestore instance.");
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
     * @throws IllegalStateException if FirebaseService has not been initialized.
     */
    public static Firestore getFirestore() {
        if (db == null) {
            // This indicates a critical error: initialize() was not called at startup, or it failed.
            // Attempt to initialize now as a fallback.
            System.err.println("WARNING: getFirestore() called before initialization. Attempting fallback initialization.");
            initialize(); // Call initialize as fallback
        }
        if (db == null) {
            System.err.println("ERROR: Firestore instance is NULL even after fallback initialization. FirebaseService is not truly initialized.");
            throw new IllegalStateException("Firestore not initialized. Ensure FirebaseService.initialize() is called successfully at application startup.");
        }
        return db;
    }

    // --- ALL THE FOLLOWING METHODS MUST BE STATIC ---
    
    public static void addVerificationRequest(VerificationRequestModel request) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Adding new verification request for entity ID: " + request.getEntityId());
        // Firestore automatically generates an ID if document() is called without an argument
        DocumentReference docRef = getFirestore().collection("verification_requests").document();
        request.setId(docRef.getId()); // Set the generated ID back to the model for reference

        ApiFuture<WriteResult> result = docRef.set(request);
        result.get(); // Blocks until the write is complete, or throws an exception
        System.out.println("FirebaseService: Verification request for " + request.getEntityName() +
                           " added to Firestore. Document ID: " + request.getId());
    }

    public static int getTotalUserCount() throws ExecutionException, InterruptedException {
        ApiFuture<AggregateQuerySnapshot> countFuture = getFirestore().collection("users")
                .whereEqualTo("role", "user")
                .count()
                .get();
        return (int) countFuture.get().getCount();
    }

    public static int getVerifiedTherapistCount() throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Querying 'psychologists' for 'verified' status...");
        CollectionReference psychologistsCollection = getFirestore().collection("psychologists");
        System.out.println("FirebaseService: Querying collection: " + psychologistsCollection.getPath());

        Query verifiedPsychologistsQuery = psychologistsCollection.whereEqualTo("verificationStatus", "verified");
        System.out.println("FirebaseService: Query condition: verificationStatus == \"verified\"");

        ApiFuture<AggregateQuerySnapshot> countFuture = verifiedPsychologistsQuery.count().get();

        long count = countFuture.get().getCount();
        System.out.println("FirebaseService: Raw count for verified psychologists fetched: " + count);

        ApiFuture<QuerySnapshot> documentsFuture = verifiedPsychologistsQuery.get();
        List<QueryDocumentSnapshot> matchingDocs = documentsFuture.get().getDocuments();
        System.out.println("FirebaseService: Found " + matchingDocs.size() + " matching documents for 'verified' psychologists.");
        if (!matchingDocs.isEmpty()) {
            matchingDocs.forEach(doc -> System.out.println("FirebaseService:   Matching Doc ID: " + doc.getId() + " - Data: " + doc.getData()));
        } else {
            System.out.println("FirebaseService:   No documents found matching 'verificationStatus == \"verified\"'.");
        }

        return (int) count;
    }

    // public static int getPendingReviewsCount() throws ExecutionException, InterruptedException {
    //     ApiFuture<AggregateQuerySnapshot> countFuture = getFirestore().collection("psychologists")
    //             .whereEqualTo("verificationStatus", "pending")
    //             .count()
    //             .get();
    //     return (int) countFuture.get().getCount();
    // }
    public static int getPendingReviewsCount() throws ExecutionException, InterruptedException {
    // Corrected query: Counts documents in the verification_requests collection
    ApiFuture<AggregateQuerySnapshot> countFuture = getFirestore().collection("verification_requests")
            .whereEqualTo("status", "Under Review") // Or whatever the initial status is in your DB
            .count()
            .get();
    return (int) countFuture.get().getCount();
}

    // public static int getActiveCrisisCount() throws ExecutionException, InterruptedException {
    //     ApiFuture<AggregateQuerySnapshot> countFuture = getFirestore().collection("crisis_situations")
    //             .whereIn("status", Arrays.asList("pending", "Active Response", "Monitoring", "Emergency Protocol Activated"))
    //             .count()
    //             .get();
    //     return (int) countFuture.get().getCount();
    // }
    public static int getActiveCrisisCount() throws ExecutionException, InterruptedException {
    // Corrected query: Counts documents where status is NOT "Resolved"
    ApiFuture<AggregateQuerySnapshot> countFuture = getFirestore().collection("crisis_situations")
            .whereNotEqualTo("status", "Resolved")
            .count()
            .get();
    return (int) countFuture.get().getCount();
}

    public static List<CrisisSituation> getActiveCrisisSituations() throws ExecutionException, InterruptedException {
        Query query = getFirestore().collection("crisis_situations")
                .whereEqualTo("status", "active")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(CrisisSituation.class))
                .collect(Collectors.toList());
    }

    public static List<CrisisSituation> getAllCrisisSituations() throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Fetching all non-resolved crisis situations...");
        Query query = getFirestore().collection("crisis_situations")
                .whereNotEqualTo("status", "Resolved")
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        System.out.println("FirebaseService: Fetched " + documents.size() + " crisis documents.");
        return documents.stream()
                .map(doc -> doc.toObject(CrisisSituation.class))
                .collect(Collectors.toList());
    }

    public static void updateCrisisStatus(String crisisId, String newStatus, Boolean protocolActivated, Boolean authoritiesContacted) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Updating Crisis ID " + crisisId + " to Status: " + newStatus);
        Map<String, Object> updates = Map.of(
                "status", newStatus,
                "protocolActivated", protocolActivated,
                "authoritiesContacted", authoritiesContacted,
                "lastUpdatedAt", new Date()
        );

        ApiFuture<WriteResult> updateFuture = getFirestore().collection("crisis_situations").document(crisisId)
                .update(updates);
        updateFuture.get();
        System.out.println("FirebaseService: Crisis " + crisisId + " status updated to " + newStatus);
    }

    public static void resolveCrisis(String crisisId) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Resolving Crisis ID " + crisisId);
        updateCrisisStatus(crisisId, "Resolved", false, false);
        System.out.println("FirebaseService: Crisis " + crisisId + " marked as Resolved.");
    }

    // Corrected type arguments for User and Psychologist
    // public static List<com.lifecompass.model.User> getUsers(String searchQuery) throws ExecutionException, InterruptedException {
    //     Query query = getFirestore().collection("users")
    //             .whereEqualTo("role", "user")
    //             .orderBy("createdAt", Query.Direction.DESCENDING);

    //     ApiFuture<QuerySnapshot> future = query.get();
    //     List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    //     if (searchQuery != null && !searchQuery.trim().isEmpty()) {
    //         String lowerCaseQuery = searchQuery.toLowerCase();
    //         return documents.stream()
    //                 .map(doc -> doc.toObject(com.lifecompass.model.User.class)) // Full qualification
    //                 .filter(user -> (user.getId() != null && user.getId().toLowerCase().contains(lowerCaseQuery)) ||
    //                         (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseQuery)) ||
    //                         (user.getName() != null && user.getName().toLowerCase().contains(lowerCaseQuery)))
    //                 .collect(Collectors.toList());
    //     } else {
    //         return documents.stream()
    //                 .map(doc -> doc.toObject(com.lifecompass.model.User.class)) // Full qualification
    //                 .collect(Collectors.toList());
    //     }
    // }

    public static List<com.lifecompass.model.User> getUsers(String searchQuery) throws ExecutionException, InterruptedException {
    Query query = getFirestore().collection("users")
            .whereEqualTo("role", "user")
            .orderBy("createdAt", Query.Direction.DESCENDING);

    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    
    // Use a custom fromMap() method to correctly map the data
    List<com.lifecompass.model.User> users = documents.stream()
        .map(doc -> com.lifecompass.model.User.fromMap(doc.getId(), doc.getData()))
        .collect(Collectors.toList());

    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        String lowerCaseQuery = searchQuery.toLowerCase();
        return users.stream()
                .filter(user -> (user.getId() != null && user.getId().toLowerCase().contains(lowerCaseQuery)) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseQuery)) ||
                                (user.getName() != null && user.getName().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    } else {
        return users;
    }
}

    public static List<com.lifecompass.model.Psychologist> getPsychologists(String searchQuery) throws ExecutionException, InterruptedException {
        Query query = getFirestore().collection("psychologists")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String lowerCaseQuery = searchQuery.toLowerCase();
            return documents.stream()
                    .map(doc -> doc.toObject(com.lifecompass.model.Psychologist.class)) // Full qualification
                    .filter(psy -> (psy.getId() != null && psy.getId().toLowerCase().contains(lowerCaseQuery)) ||
                            (psy.getEmail() != null && psy.getEmail().toLowerCase().contains(lowerCaseQuery)) ||
                            (psy.getName() != null && psy.getName().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList());
        } else {
            return documents.stream()
                    .map(doc -> doc.toObject(com.lifecompass.model.Psychologist.class)) // Full qualification
                    .collect(Collectors.toList());
        }
    }

    public static List<com.lifecompass.model.VerificationRequestModel> getVerificationRequests() throws ExecutionException, InterruptedException {
        Query query = getFirestore().collection("verification_requests")
                .orderBy("submittedAt", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(com.lifecompass.model.VerificationRequestModel.class)) // Full qualification
                .collect(Collectors.toList());
    }

    public static void updateVerificationRequestStatus(String requestId, String newStatus) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> updateFuture = getFirestore().collection("verification_requests").document(requestId)
                .update("status", newStatus);
        updateFuture.get();
        System.out.println("Verification request " + requestId + " status updated to " + newStatus);
    }

    public static void approvePsychologist(String psychologistId) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Entering approvePsychologist for ID: " + psychologistId);
        ApiFuture<WriteResult> updateFuture = getFirestore().collection("psychologists").document(psychologistId)
                .update("status", "active", "verificationStatus", "verified");
        updateFuture.get();
        System.out.println("FirebaseService: Psychologist " + psychologistId + " approved and status updated.");
    }

    public static void rejectPsychologist(String psychologistId) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Entering rejectPsychologist for ID: " + psychologistId);
        ApiFuture<WriteResult> updateFuture = getFirestore().collection("psychologists").document(psychologistId)
                .update("verificationStatus", "rejected");
        updateFuture.get();
        System.out.println("FirebaseService: Psychologist " + psychologistId + " rejected.");
    }

    public static void updateUserField(String userId, String field, String value) throws ExecutionException, InterruptedException {
        System.out.println("FirebaseService: Updating user field " + field + " for ID: " + userId);
        ApiFuture<WriteResult> updateFuture = getFirestore().collection("users").document(userId)
                .update(field, value);
        updateFuture.get();
        System.out.println("User " + userId + " " + field + " updated to " + value);
    }
    
}