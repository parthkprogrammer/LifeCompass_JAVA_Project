package com.lifecompass.dao;

import com.lifecompass.model.psychologist.*;
import com.lifecompass.services.FirebaseService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.Timestamp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistDao {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistDao.class);

    private final Firestore db;

    private int limit;
    private static final String PSYCHOLOGISTS_COLLECTION = "psychologists";
    private static final String USERS_COLLECTION = "users";
    private static final String APPOINTMENTS_COLLECTION = "appointments";
    private static final String NOTIFICATIONS_COLLECTION = "notifications";
    private static final String ALERTS_COLLECTION = "crisisAlerts";
    private static final String CHATS_COLLECTION = "chats";

    public PsychologistDao() {
        this.db = FirebaseService.getFirestoreInstance();
        logger.info("PsychologistDao initialized. Firestore instance obtained successfully.");
    }

    private <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        apiFuture.addListener(() -> {
            try {
                completableFuture.complete(apiFuture.get());
            } catch (Exception e) {
                logger.error("Error in toCompletableFuture conversion: {}", e.getMessage(), e);
                completableFuture.completeExceptionally(e);
            }
        }, Runnable::run);
        return completableFuture;
    }

    public CompletableFuture<PsychologistProfile> getPsychologistProfile(String psychologistId) {
        logger.info("DAO: Fetching profile for psychologist ID: {}", psychologistId);
        ApiFuture<DocumentSnapshot> future = db.collection(PSYCHOLOGISTS_COLLECTION).document(psychologistId).get();
        return toCompletableFuture(future).thenApply(doc -> {
            if (doc.exists()) {
                PsychologistProfile profile = doc.toObject(PsychologistProfile.class);
                logger.info("DAO: Profile found for {}: {}", psychologistId, profile.getFullName());
                return profile;
            } else {
                logger.warn("DAO: Profile NOT found for psychologist ID: {}. Returning null.", psychologistId);
                return null;
            }
        }).exceptionally(e -> {
            logger.error("DAO Error fetching profile for ID {}: {}", psychologistId, e.getMessage(), e);
            return null;
        });
    }
    
    public CompletableFuture<String> getPsychologistName(String psychologistId) {
        logger.info("DAO: Fetching name for psychologist ID: {}", psychologistId);
        ApiFuture<DocumentSnapshot> future = db.collection(PSYCHOLOGISTS_COLLECTION).document(psychologistId).get();
        return toCompletableFuture(future).thenApply(doc -> {
            if (doc.exists()) {
                String name = doc.getString("fullName");
                logger.info("DAO: Name found for {}: {}", psychologistId, name);
                return name;
            } else {
                logger.warn("DAO: Name NOT found for psychologist ID: {}. Returning 'Psychologist'.", psychologistId);
                return "Psychologist";
            }
        }).exceptionally(e -> {
            logger.error("DAO Error fetching name for ID {}: {}", psychologistId, e.getMessage(), e);
            return "Psychologist";
        });
    }

    public CompletableFuture<Void> updatePsychologistProfile(PsychologistProfile profile) {
        logger.info("DAO: Updating profile for psychologist ID: {}", profile.getId());
        ApiFuture<WriteResult> future = db.collection(PSYCHOLOGISTS_COLLECTION).document(profile.getId()).set(profile);
        return toCompletableFuture(future).thenApply(res -> {
            logger.info("DAO: Profile for {} updated successfully. Write time: {}", profile.getId(), res.getUpdateTime());
            return (Void) null;
        }).exceptionally(e -> {
            logger.error("DAO Error updating profile for ID {}: {}", profile.getId(), e.getMessage(), e);
            return null;
        });
    }

    public CompletableFuture<List<Patient>> getPatientsForPsychologist(String psychologistId) {
        logger.info("DAO: Attempting to fetch patients for psychologist ID: {}. Querying collection: {}", psychologistId, USERS_COLLECTION);
        ApiFuture<QuerySnapshot> future = db.collection(USERS_COLLECTION)
                .whereEqualTo("assignedPsychologistId", psychologistId)
                .get();
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<Patient> patients = snapshot.toObjects(Patient.class);
            if (patients.isEmpty()) {
                logger.warn("DAO: Found 0 patients for psychologist ID: {}. Check 'users' collection and 'assignedPsychologistId' field value, or if Patient.class mapping is incorrect.", psychologistId);
            } else {
                logger.info("DAO: Successfully found {} patients for psychologist ID: {}. First patient ID: {}", patients.size(), psychologistId, patients.get(0).getId());
            }
            return patients;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching patients for ID {}: {}", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }

    public CompletableFuture<List<Patient>> getPatientsWithRiskLevels(String psychologistId) {
        logger.info("DAO: Fetching patients with risk levels for psychologist ID: {}. Querying collection: {}.", psychologistId, USERS_COLLECTION);
        ApiFuture<QuerySnapshot> future = db.collection(USERS_COLLECTION)
                .whereEqualTo("assignedPsychologistId", psychologistId)
                .orderBy("name", Query.Direction.ASCENDING)
                .get();

        return toCompletableFuture(future).thenApply(snapshot -> {
            List<Patient> patients = snapshot.toObjects(Patient.class);
            if (patients.isEmpty()) {
                logger.warn("DAO: Found 0 patients with risk for psychologist ID: {}.", psychologistId);
            } else {
                logger.info("DAO: Successfully found {} patients with risk levels for ID: {}.", patients.size(), psychologistId);
            }
            return patients;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching patients with risk levels for ID {}: {}. Ensure Patient.java has 'risk' and 'initials' fields and they exist in Firestore 'users' docs.", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }


    public CompletableFuture<Integer> getActiveChatsCount(String psychologistId) {
        logger.info("DAO: Fetching active chats count for psychologist ID: {}. Querying collection: {}", psychologistId, CHATS_COLLECTION);
        ApiFuture<QuerySnapshot> future = db.collection(CHATS_COLLECTION)
                .whereEqualTo("psychologistId", psychologistId)
                .whereEqualTo("status", "active")
                .get();
        
        return toCompletableFuture(future)
                .thenApply(snapshot -> {
                    int count = snapshot.size();
                    logger.info("DAO: Found {} active chats for psychologist ID: {}", count, psychologistId);
                    return count;
                })
                .exceptionally(e -> {
                    logger.error("DAO Error fetching active chats count for ID {}: {}", psychologistId, e.getMessage(), e);
                    return 0;
                });
    }

    public CompletableFuture<List<Appointment>> getAppointmentsForPsychologist(String psychologistId) {
        logger.info("DAO: Fetching ALL appointments for psychologist ID: {}. Querying collection: {}", psychologistId, APPOINTMENTS_COLLECTION);
        
        ApiFuture<QuerySnapshot> future = db.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("psychologistId", psychologistId)
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING)
                .get();
        
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<Appointment> appointments = snapshot.toObjects(Appointment.class);
            logger.info("DAO: Found {} ALL appointments for psychologist ID: {}.", appointments.size(), psychologistId);
            if (!appointments.isEmpty()) {
                logger.debug("DAO Debug: First ALL appointment mapped: PatientName={}, Date={}, Time={}", 
                             appointments.get(0).getPatientName(), appointments.get(0).getLocalDate(), appointments.get(0).getTime());
            }
            return appointments;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching ALL appointments for ID {}: {}. Check Firestore rules, field names ('psychologistId', 'date' as Timestamp, 'time').", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }


    public CompletableFuture<List<Appointment>> getAppointmentsForToday(String psychologistId, LocalDate date) {
        logger.info("DAO: Fetching appointments for psychologist ID: {} for date: {}. Querying collection: {}", psychologistId, date, APPOINTMENTS_COLLECTION);
        
        Timestamp startOfDay = Timestamp.of(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Timestamp endOfDay = Timestamp.of(Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant()));

        ApiFuture<QuerySnapshot> future = db.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("psychologistId", psychologistId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThanOrEqualTo("date", endOfDay)
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING)
                .get();
        
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<Appointment> appointments = snapshot.toObjects(Appointment.class);
            logger.info("DAO: Found {} appointments for psychologist ID: {} today.", appointments.size(), psychologistId);
            if (!appointments.isEmpty()) {
                logger.debug("DAO Debug: First TODAY appointment mapped: PatientName={}, Date={}, Time={}", 
                             appointments.get(0).getPatientName(), appointments.get(0).getLocalDate(), appointments.get(0).getTime());
            }
            return appointments;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching TODAY appointments for ID {} for date {}: {}. Check Firestore rules, field names ('psychologistId', 'date' as Timestamp, 'time').", psychologistId, date, e.getMessage(), e);
            return Collections.emptyList();
        });
    }

    public CompletableFuture<Void> updateAppointment(Appointment appointment) {
        logger.info("DAO: Updating appointment ID: {} for patient {}.", appointment.getId(), appointment.getPatientName());
        ApiFuture<WriteResult> future = db.collection(APPOINTMENTS_COLLECTION).document(appointment.getId()).set(appointment);
        return toCompletableFuture(future).thenApply(res -> {
            logger.info("DAO: Appointment {} updated successfully. Write time: {}.", appointment.getId(), res.getUpdateTime());
            return (Void) null;
        }).exceptionally(e -> {
            logger.error("DAO Error updating appointment ID {}: {}", appointment.getId(), e.getMessage(), e);
            return null;
        });
    }

    public CompletableFuture<List<CrisisAlert>> getActiveCrisisAlerts(String psychologistId) {
        logger.info("DAO: Fetching active crisis alerts for psychologist ID: {}. Querying collection: {}.", psychologistId, ALERTS_COLLECTION);
        ApiFuture<QuerySnapshot> future = db.collection(ALERTS_COLLECTION)
                .whereEqualTo("psychologistId", psychologistId)
                .whereEqualTo("status", "Active")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<CrisisAlert> alerts = snapshot.toObjects(CrisisAlert.class);
            logger.info("DAO: Found {} active crisis alerts for psychologist ID: {}.", alerts.size(), psychologistId);
            return alerts;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching active crisis alerts for ID {}: {}. Check Firestore rules, field names ('psychologistId', 'status', 'timestamp').", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }

    public CompletableFuture<Void> updateCrisisAlertStatus(String alertId, String newStatus) {
        logger.info("DAO: Updating crisis alert ID: {} to status: {}.", alertId, newStatus);
        ApiFuture<WriteResult> future = db.collection(ALERTS_COLLECTION).document(alertId).update("status", newStatus);
        return toCompletableFuture(future).thenApply(res -> {
            logger.info("DAO: Crisis alert {} status updated to {} successfully. Write time: {}", alertId, newStatus, res.getUpdateTime());
            return (Void) null;
        }).exceptionally(e -> {
            logger.error("DAO Error updating crisis alert status for ID {}: {}", alertId, e.getMessage(), e);
            return null;
        });
    }

    public CompletableFuture<PatientAnalytics> getPatientAnalytics(String psychologistId) {
        logger.info("DAO: Generating dummy patient analytics for psychologist ID: {}.", psychologistId);
        CompletableFuture<PatientAnalytics> future = new CompletableFuture<>();
        PatientAnalytics data = new PatientAnalytics();
        data.setAverageImprovement("+23%");
        data.setSessionCompletionRate("94%");
        data.setPatientSatisfaction("4.8/5");
        data.setTreatmentEffectiveness(Map.of(
            "Cognitive Behavioral Therapy", Map.of("patients", 15, "improvement", 78),
            "Mindfulness-Based Therapy", Map.of("patients", 8, "improvement", 65),
            "Dialectical Behavior Therapy", Map.of("patients", 6, "improvement", 82)
        ));
        future.complete(data);
        logger.info("DAO: Dummy patient analytics data generated successfully.");
        return future;
    }

    public CompletableFuture<List<Patient>> getRecentPatientActivity(String psychologistId) {
        logger.info("DAO: Fetching recent patient activity for psychologist ID: {}. Querying collection: {}", psychologistId, USERS_COLLECTION);
        ApiFuture<QuerySnapshot> future = db.collection(USERS_COLLECTION)
                .whereEqualTo("assignedPsychologistId", psychologistId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();

        return toCompletableFuture(future).thenApply(snapshot -> {
            List<Patient> patients = snapshot.toObjects(Patient.class);
            logger.info("DAO: Found {} recent patient activities for psychologist ID: {}", patients.size(), psychologistId);
            return patients;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching recent patient activity for ID {}: {}", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }

    public CompletableFuture<List<PsychologistNotification>> getNotifications(String psychologistId) {
        logger.info("DAO: Fetching notifications for psychologist ID: {}. Querying subcollection: {}/{}/{}", psychologistId, PSYCHOLOGISTS_COLLECTION, psychologistId, NOTIFICATIONS_COLLECTION);
        CollectionReference notifsRef = db.collection(PSYCHOLOGISTS_COLLECTION).document(psychologistId).collection(NOTIFICATIONS_COLLECTION);
        ApiFuture<QuerySnapshot> future = notifsRef.orderBy("timestamp", Query.Direction.DESCENDING).get();
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<PsychologistNotification> notifications = snapshot.toObjects(PsychologistNotification.class);
            logger.info("DAO: Found {} notifications for psychologist ID: {}.", notifications.size(), psychologistId);
            return notifications;
        }).exceptionally(e -> {
            logger.error("DAO Error fetching notifications for ID {}: {}. Check 'notifications' subcollection and 'timestamp' field.", psychologistId, e.getMessage(), e);
            return Collections.emptyList();
        });
    }

    public CompletableFuture<Void> markAllNotificationsAsRead(String psychologistId) {
        logger.info("DAO: Marking all notifications as read for psychologist ID: {}.", psychologistId);
        return CompletableFuture.completedFuture((Void) null);
    }

    public CompletableFuture<Void> deleteNotification(String notificationId) {
        logger.info("DAO: Deleting notification ID: {}.", notificationId);
        return CompletableFuture.completedFuture((Void) null);
    }

    public CompletableFuture<PsychologistSettings> getPsychologistSettings(String psychologistId) {
        logger.info("DAO: Fetching settings for psychologist ID: {}. Querying collection: 'psychologistSettings'.", psychologistId);
        ApiFuture<DocumentSnapshot> future = db.collection("psychologistSettings").document(psychologistId).get();
        return toCompletableFuture(future).thenApply(doc -> {
            if (doc.exists()) {
                PsychologistSettings settings = doc.toObject(PsychologistSettings.class);
                logger.info("DAO: Settings found for {}.", psychologistId);
                return settings;
            } else {
                logger.warn("DAO: Settings NOT found for psychologist ID: {}. Returning default settings.", psychologistId);
                return new PsychologistSettings(); 
            }
        }).exceptionally(e -> {
            logger.error("DAO Error fetching settings for ID {}: {}. Check 'psychologistSettings' collection and document ID.", psychologistId, e.getMessage(), e);
            return new PsychologistSettings(); 
        });
    }

    public CompletableFuture<Void> updatePsychologistSettings(PsychologistSettings settings) {
        logger.info("DAO: Updating settings for psychologist ID: {}.", settings.getPsychologistId());
        ApiFuture<WriteResult> future = db.collection("psychologistSettings").document(settings.getPsychologistId()).set(settings);
        return toCompletableFuture(future).thenApply(res -> {
            logger.info("DAO: Settings for {} updated successfully. Write time: {}.", settings.getPsychologistId(), res.getUpdateTime());
            return (Void) null;
        }).exceptionally(e -> {
            logger.error("DAO Error updating settings for ID {}: {}", settings.getPsychologistId(), e.getMessage(), e);
            return null;
        });
    }

    public CompletableFuture<Boolean> changePassword(String psychologistId, String currentPass, String newPass) {
        logger.info("DAO: Password change requested for psychologist ID: {}.", psychologistId);
        if ("password123".equals(currentPass)) {
            logger.info("DAO: Dummy password check succeeded for {}.", psychologistId);
            return CompletableFuture.completedFuture(true);
        } else {
            logger.warn("DAO: Dummy password check failed for {}.", psychologistId);
            return CompletableFuture.completedFuture(false);
        } // Corrected: closing brace for else block
    } // Corrected: closing brace for method

    public CompletableFuture<String> getTodaysAppointments(String psychologistId) {
        logger.info("DAO: Fetching today's appointments for psychologist ID: {}. Querying collection: {}", psychologistId, APPOINTMENTS_COLLECTION);
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.of(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Timestamp endOfDay = Timestamp.of(Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant()));

        ApiFuture<QuerySnapshot> future = db.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("psychologistId", psychologistId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThanOrEqualTo("date", endOfDay)
                .get();

        return toCompletableFuture(future).thenApply(snapshot -> {
            int count = snapshot.size();
            logger.info("DAO: Found {} appointments for psychologist ID: {} today.", count, psychologistId);
            return "You have " + count + " appointments today.";
        }).exceptionally(e -> {
            logger.error("DAO Error fetching today's appointments for ID {}: {}", psychologistId, e.getMessage(), e);
            return "Error fetching today's appointments.";
        });
    }
}