

package com.lifecompass.services; // CONFIRMED: Using 'services' as per your last provided code

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.FirestoreException;

import com.lifecompass.model.JournalEntry;
import com.lifecompass.config.FirebaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class FirebaseJournalService {

    private final Firestore db;
    public static final String DEFAULT_USER_ID = "example_user_uid"; // CONFIRMED: This matches your Firestore screenshot

    public FirebaseJournalService() {
        this.db = FirebaseConfig.getFirestore();
        System.out.println("FirebaseJournalService: Initialized with Firestore instance. DB is null: " + (db == null));
    }

    private CollectionReference getJournalEntriesCollection() {
        return db.collection("journalEntries");
    }

    public String addJournalEntry(JournalEntry entry) throws ExecutionException, InterruptedException {
        if (entry.getUserId() == null || entry.getUserId().isEmpty()) {
            entry.setUserId(DEFAULT_USER_ID);
        }
        ApiFuture<DocumentReference> future = getJournalEntriesCollection().add(entry);
        DocumentReference docRef = future.get();
        System.out.println("FirebaseJournalService: Added journal entry with ID: " + docRef.getId());
        return docRef.getId();
    }

    public ListenerRegistration listenForJournalEntries(Consumer<List<JournalEntry>> callback) {
        System.out.println("FirebaseJournalService: Setting up listener for userId: " + DEFAULT_USER_ID);
        Query query = getJournalEntriesCollection()
                .whereEqualTo("userId", DEFAULT_USER_ID)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        return query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirestoreException e) {
                if (e != null) {
                    System.err.println("Firebase Listener (Journal Entries): Listen failed with error: " + e.getMessage());
                    e.printStackTrace();
                    callback.accept(new ArrayList<>());
                    return;
                }

                System.out.println("Firebase Listener (Journal Entries): Snapshot event received. Documents in snapshot: " + snapshots.size());
                List<JournalEntry> entries = new ArrayList<>();

                if (snapshots.isEmpty()) {
                    System.out.println("Firebase Listener (Journal Entries): Snapshot is empty for userId: " + DEFAULT_USER_ID + ". No entries to display.");
                }

                for (QueryDocumentSnapshot doc : snapshots.getDocuments()) {
                    try {
                        JournalEntry entry = doc.toObject(JournalEntry.class);
                        // Validate mapped object immediately
                        if (entry == null) {
                            System.err.println("Firebase Listener (Journal Entries): ERROR! doc.toObject() returned null for document ID: " + doc.getId());
                            continue;
                        }
                        if (entry.getId() == null) { // Ensure ID is mapped by @DocumentId
                            entry.setId(doc.getId()); // Manually set ID if @DocumentId fails for some reason
                            System.out.println("Firebase Listener (Journal Entries): Manually set ID for entry: " + entry.getId());
                        }

                        // Check crucial fields after mapping
                        if (entry.getCreatedAt() == null) {
                            System.err.println("Firebase Listener (Journal Entries): WARNING! Mapped 'createdAt' is NULL for document ID: " + doc.getId() + ". This is likely the cause of 'N/A' date.");
                            // If createdAt is null, its formatted date will be "N/A - Date Error".
                            // This indicates a problem with the Date mapping.
                        }

                        entries.add(entry);
                        System.out.println("Firebase Listener (Journal Entries): Successfully mapped document ID: " + doc.getId() + " - Title: '" + entry.getTitle() + "' - CreatedAt(raw): " + entry.getCreatedAt() + " - CreatedAt(Local): " + entry.getTimestampAsLocalDateTime());
                    } catch (Exception ex) {
                        System.err.println("Firebase Listener (Journal Entries): ERROR! Failed to convert document " + doc.getId() + " to JournalEntry. " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                System.out.println("Firebase Listener (Journal Entries): Finished processing snapshot. Calling callback with " + entries.size() + " entries.");
                callback.accept(entries);
            }
        });
    }

    public ListenerRegistration listenForJournalEntryCount(Consumer<Integer> callback) {
        Query query = getJournalEntriesCollection()
                .whereEqualTo("userId", DEFAULT_USER_ID);

        return query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirestoreException e) {
                if (e != null) {
                    System.err.println("Firebase Listener (Count): Listen failed: " + e.getMessage());
                    callback.accept(0);
                    return;
                }
                System.out.println("Firebase Listener (Count): Received " + snapshots.size() + " documents. Updating count.");
                callback.accept(snapshots.size());
            }
        });
    }

    public void deleteJournalEntry(String entryId) throws ExecutionException, InterruptedException, IllegalArgumentException {
        DocumentReference docRef = getJournalEntriesCollection().document(entryId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            JournalEntry entryToDelete = document.toObject(JournalEntry.class);
            if (entryToDelete != null && entryToDelete.getUserId() != null && entryToDelete.getUserId().equals(DEFAULT_USER_ID)) {
                ApiFuture<com.google.cloud.firestore.WriteResult> deleteFuture = docRef.delete();
                deleteFuture.get();
                System.out.println("FirebaseJournalService: Journal entry " + entryId + " deleted successfully!");
            } else {
                System.err.println("FirebaseJournalService: Attempted to delete journal entry " + entryId + ": unauthorized or userId mismatch.");
                throw new IllegalArgumentException("Unauthorized to delete this entry or entry not found for your user.");
            }
        } else {
            System.err.println("FirebaseJournalService: Attempted to delete journal entry " + entryId + " but it does not exist.");
            throw new IllegalArgumentException("Journal entry not found.");
        }
    }
}