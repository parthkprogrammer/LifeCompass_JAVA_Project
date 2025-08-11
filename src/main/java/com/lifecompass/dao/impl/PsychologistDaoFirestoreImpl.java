package com.lifecompass.dao.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.lifecompass.config.FirebaseConfig;
import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.dao.PsychologistDao1;
import com.lifecompass.model.Psychologist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PsychologistDaoFirestoreImpl implements PsychologistDao1 {

    private final Firestore db;
    private static final String COLLECTION_NAME = "psychologists";

    public PsychologistDaoFirestoreImpl() {
        this.db = FirebaseConfig.getFirestore();
    }

    @Override
    public void addPsychologist(Psychologist psychologist) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(psychologist.getId());
        ApiFuture<WriteResult> result = docRef.set(psychologist.toMap());
        result.get();
        System.out.println("Psychologist " + psychologist.getId() + " added to Firestore.");
    }

    public List<Psychologist> getAllPsychologists() throws InterruptedException, ExecutionException{
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection("psychologists").get();
        for(int i = 0;i<querySnap.get().getDocuments().size();i++){
            Map<String,Object> map = querySnap.get().getDocuments().get(i).getData();
            list.add(Psychologist.fromMap(querySnap.get().getDocuments().get(i).getId(), map));
            
        }
        return list;
    }

    public List<Psychologist> getVerifiedPsychologists() throws ExecutionException, InterruptedException {
        List<Psychologist> verifiedPsychologists = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereEqualTo("verificationStatus", "verified") // <--- CRITICAL FILTER ADDED
                .get();

        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            verifiedPsychologists.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        System.out.println("Fetched " + verifiedPsychologists.size() + " verified psychologists.");
        return verifiedPsychologists;
    }

    @Override
    public Optional<Psychologist> getPsychologistById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.of(Psychologist.fromMap(document.getId(), document.getData()));
        } else {
            System.out.println("No such psychologist document!");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Psychologist> getPsychologistByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            return Optional.of(Psychologist.fromMap(documents.get(0).getId(), documents.get(0).getData()));
        } else {
            System.out.println("No psychologist found with email: " + email);
            return Optional.empty();
        }
    }

    @Override
    public void updatePsychologist(Psychologist psychologist) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(psychologist.getId());
        ApiFuture<WriteResult> result = docRef.set(psychologist.toMap());
        result.get();
        System.out.println("Psychologist " + psychologist.getId() + " updated in Firestore.");
    }

    @Override
    public void deletePsychologist(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
        writeResult.get();
        System.out.println("Psychologist " + id + " deleted from Firestore.");
    }

    @Override
    public List<Psychologist> searchPsychologists(String query) throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get();
        
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
    }

    @Override
    public List<Psychologist> getPsychologistsBySpecialization(String specialization)
            throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereEqualTo("specialization", specialization)
                .get();
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
    }

    @Override
    public List<Psychologist> getPsychologistsByLocation(String location)
            throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereEqualTo("location", location)
                .get();
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
    }

    @Override
    public List<Psychologist> getPsychologistsByAvailability(String availability)
            throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereEqualTo("availability", availability)
                .get();
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
    }

    @Override
    public List<Psychologist> getPsychologistsByRating(double minRating)
            throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("rating", minRating)
                .get();
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
    }

    @Override
    public List<Psychologist> getPsychologistsByLanguage(String language)
            throws ExecutionException, InterruptedException {
        List<Psychologist> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> querySnap = db.collection(COLLECTION_NAME)
                .whereEqualTo("language", language)
                .get();
        for (QueryDocumentSnapshot document : querySnap.get().getDocuments()) {
            list.add(Psychologist.fromMap(document.getId(), document.getData()));
        }
        return list;
        
    }
}