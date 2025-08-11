

// package com.lifecompass.dao.impl; // Corrected package name

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.CollectionReference;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.QueryDocumentSnapshot;
// import com.google.cloud.firestore.QuerySnapshot;
// import com.google.cloud.firestore.WriteResult;
// import com.lifecompass.config.FirebaseConfig; // Assuming this class exists and provides Firestore
// import com.lifecompass.dao.MoodDao;         // Assuming this interface exists
// import com.lifecompass.model.MoodEntry;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.Future;

// public class MoodDaoFirestoreImpl implements MoodDao {

//     private final Firestore db;
//     private static final String COLLECTION_NAME = "moodEntries"; // Consistent collection name

//     public MoodDaoFirestoreImpl() {
//         this.db = FirebaseConfig.getFirestore();
//         if (this.db == null) {
//             // Handle case where Firestore is not initialized/returned by FirebaseConfig
//             System.err.println("ERROR: Firestore instance is null. Ensure FirebaseConfig.getFirestore() initializes Firebase correctly.");
//             throw new IllegalStateException("Firestore not initialized. Cannot proceed with MoodDaoFirestoreImpl.");
//         }
//     }

//     @Override
//     public void addMoodEntry(MoodEntry entry) {
//         try {
//             CollectionReference colRef = db.collection(COLLECTION_NAME);
//             DocumentReference docRef = colRef.document();
//             entry.setId(docRef.getId()); // Set the Firestore generated ID back to the object
//             ApiFuture<WriteResult> result = docRef.set(entry.toMap()); // Using entry.toMap()
//             result.get(); // Blocks until write is complete
//             System.out.println("MoodEntry " + entry.getId() + " added to Firestore.");
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error adding mood entry to Firestore: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     @Override
//     public Optional<MoodEntry> getMoodEntryById(String id) {
//         try {
//             DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
//             ApiFuture<DocumentSnapshot> future = docRef.get();
//             DocumentSnapshot document = future.get();
//             if (document.exists()) {
//                 return Optional.of(MoodEntry.fromMap(document.getId(), document.getData()));
//             } else {
//                 System.out.println("No such mood entry document with ID: " + id);
//                 return Optional.empty();
//             }
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error getting mood entry by ID from Firestore: " + e.getMessage());
//             e.printStackTrace();
//             return Optional.empty();
//         }
//     }

//     @Override
//     public List<MoodEntry> getMoodEntriesByUserId(String userId) {
//         List<MoodEntry> moodEntries = new ArrayList<>();
//         try {
//             ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME) // Use COLLECTION_NAME
//                                                 .whereEqualTo("userId", userId)
//                                                 .orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING) // Order by latest first
//                                                 .get(); // Get all for user, or add .limit(X) for recent

//             List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//             for (QueryDocumentSnapshot document : documents) {
//                 moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
//             }
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error getting mood entries by user ID from Firestore: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return moodEntries;
//     }

//     // This method is likely intended for the "Recent Mood Entries" display on dashboard.
//     // Let's name it clearly and make sure it's consistent with how MoodEntry is mapped.
//     // If MoodDao interface defines 'getRecentMoodEntriesForUser', keep this name.
//     // If not, and you want this to be the primary 'get for user', use 'getMoodEntriesByUserId' for dashboard.
//     // Assuming you want the 'recent' one from before:
//     public List<MoodEntry> getRecentMoodEntriesForUser(String userId) // NOT @Override unless MoodDao defines it
//             throws ExecutionException, InterruptedException {
//         List<MoodEntry> moodEntries = new ArrayList<>();
//         if (db == null) {
//             System.err.println("Firestore not initialized, cannot fetch recent mood entries.");
//             return moodEntries;
//         }

//         ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME) // Use COLLECTION_NAME
//                                             .whereEqualTo("userId", userId)
//                                             .orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING)
//                                             .limit(5) // Limit to, say, the 5 most recent entries
//                                             .get();

//         List<QueryDocumentSnapshot> documents = query.get().getDocuments();
//         for (QueryDocumentSnapshot document : documents) {
//             try {
//                 // Use fromMap, assuming it's available and correctly handles mapping
//                 moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
//             } catch (Exception e) {
//                 System.err.println("Error mapping document to MoodEntry: " + document.getId() + " - " + e.getMessage());
//                 e.printStackTrace();
//             }
//         }
//         return moodEntries;
//     }

//     // If MoodDao interface has getAllMoodEntries() without args, then uncomment and use this:
//     // @Override
//     // public List<MoodEntry> getAllMoodEntries() {
//     //     List<MoodEntry> moodEntries = new ArrayList<>();
//     //     try {
//     //         ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
//     //         List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//     //         for (QueryDocumentSnapshot document : documents) {
//     //             moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
//     //         }
//     //     } catch (InterruptedException | ExecutionException e) {
//     //         System.err.println("Error getting all mood entries from Firestore: " + e.getMessage());
//     //         e.printStackTrace();
//     //     }
//     //     return moodEntries;
//     // }


//     @Override
//     public void updateMoodEntry(MoodEntry entry) {
//         try {
//             DocumentReference docRef = db.collection(COLLECTION_NAME).document(entry.getId());
//             ApiFuture<WriteResult> result = docRef.set(entry.toMap()); // Using entry.toMap()
//             result.get();
//             System.out.println("MoodEntry " + entry.getId() + " updated in Firestore.");
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error updating mood entry in Firestore: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     @Override
//     public void deleteMoodEntry(String id) {
//         try {
//             ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
//             writeResult.get();
//             System.out.println("MoodEntry " + id + " deleted from Firestore.");
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error deleting mood entry from Firestore: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     @Override
//     public List<MoodEntry> getAllMoodEntries() {
//         List<MoodEntry> moodEntries = new ArrayList<>();
//         try {
//             ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
//             List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//             for (QueryDocumentSnapshot document : documents) {
//                 moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
//             }
//         } catch (InterruptedException | ExecutionException e) {
//             System.err.println("Error getting all mood entries from Firestore: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return moodEntries;
//     }
// }


package com.lifecompass.dao.impl; // Corrected package name

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.lifecompass.config.FirebaseConfig; // Assuming this class exists and provides Firestore
import com.lifecompass.dao.MoodDao;         // Assuming this interface exists
import com.lifecompass.model.MoodEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MoodDaoFirestoreImpl implements MoodDao {

    private final Firestore db;
    private static final String COLLECTION_NAME = "moodEntries"; // Consistent collection name

    public MoodDaoFirestoreImpl() {
        this.db = FirebaseConfig.getFirestore();
        if (this.db == null) {
            // Handle case where Firestore is not initialized/returned by FirebaseConfig
            System.err.println("ERROR: Firestore instance is null. Ensure FirebaseConfig.getFirestore() initializes Firebase correctly.");
            throw new IllegalStateException("Firestore not initialized. Cannot proceed with MoodDaoFirestoreImpl.");
        }
    }

    @Override
    public void addMoodEntry(MoodEntry entry) {
        try {
            CollectionReference colRef = db.collection(COLLECTION_NAME);
            DocumentReference docRef = colRef.document();
            entry.setId(docRef.getId()); // Set the Firestore generated ID back to the object
            ApiFuture<WriteResult> result = docRef.set(entry.toMap()); // Using entry.toMap()
            result.get(); // Blocks until write is complete
            System.out.println("MoodEntry " + entry.getId() + " added to Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error adding mood entry to Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<MoodEntry> getMoodEntryById(String id) {
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return Optional.of(MoodEntry.fromMap(document.getId(), document.getData()));
            } else {
                System.out.println("No such mood entry document with ID: " + id);
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting mood entry by ID from Firestore: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<MoodEntry> getMoodEntriesByUserId(String userId) {
        List<MoodEntry> moodEntries = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME) // Use COLLECTION_NAME
                                                .whereEqualTo("userId", userId)
                                                .orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING) // Order by latest first
                                                .get(); // Get all for user, or add .limit(X) for recent

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting mood entries by user ID from Firestore: " + e.getMessage());
            e.printStackTrace();
        }
        return moodEntries;
    }

    // This method is likely intended for the "Recent Mood Entries" display on dashboard.
    // Let's name it clearly and make sure it's consistent with how MoodEntry is mapped.
    // If MoodDao interface defines 'getRecentMoodEntriesForUser', keep this name.
    // If not, and you want this to be the primary 'get for user', use 'getMoodEntriesByUserId' for dashboard.
    // Assuming you want the 'recent' one from before:
    public List<MoodEntry> getRecentMoodEntriesForUser(String userId) // NOT @Override unless MoodDao defines it
            throws ExecutionException, InterruptedException {
        List<MoodEntry> moodEntries = new ArrayList<>();
        if (db == null) {
            System.err.println("Firestore not initialized, cannot fetch recent mood entries.");
            return moodEntries;
        }

        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME) // Use COLLECTION_NAME
                                            .whereEqualTo("userId", userId)
                                            .orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING)
                                            .limit(5) // Limit to, say, the 5 most recent entries
                                            .get();

        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            try {
                // Use fromMap, assuming it's available and correctly handles mapping
                moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
            } catch (Exception e) {
                System.err.println("Error mapping document to MoodEntry: " + document.getId() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        return moodEntries;
    }

    // If MoodDao interface has getAllMoodEntries() without args, then uncomment and use this:
    // @Override
    // public List<MoodEntry> getAllMoodEntries() {
    //     List<MoodEntry> moodEntries = new ArrayList<>();
    //     try {
    //         ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
    //         List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    //         for (QueryDocumentSnapshot document : documents) {
    //             moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
    //         }
    //     } catch (InterruptedException | ExecutionException e) {
    //         System.err.println("Error getting all mood entries from Firestore: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    //     return moodEntries;
    // }


    @Override
    public void updateMoodEntry(MoodEntry entry) {
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(entry.getId());
            ApiFuture<WriteResult> result = docRef.set(entry.toMap()); // Using entry.toMap()
            result.get();
            System.out.println("MoodEntry " + entry.getId() + " updated in Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error updating mood entry in Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMoodEntry(String id) {
        try {
            ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
            writeResult.get();
            System.out.println("MoodEntry " + id + " deleted from Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error deleting mood entry from Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<MoodEntry> getAllMoodEntries() {
        List<MoodEntry> moodEntries = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                moodEntries.add(MoodEntry.fromMap(document.getId(), document.getData()));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting all mood entries from Firestore: " + e.getMessage());
            e.printStackTrace();
        }
        return moodEntries;
    }
}
