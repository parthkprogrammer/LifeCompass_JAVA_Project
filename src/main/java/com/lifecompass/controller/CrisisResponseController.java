package com.lifecompass.controller;

import com.lifecompass.model.FirebaseService;
import com.lifecompass.model.CrisisSituation; // Import the model
import com.lifecompass.view.Adminview.CrisisResponseView; // Import the view
import javafx.application.Platform;
import javafx.scene.control.Alert; // Import Alert to use Alert.AlertType

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CrisisResponseController {

    private CrisisResponseView view;
    private FirebaseService firebaseService;

    // Date formatter for displaying reported time
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // Example format

    public CrisisResponseController(CrisisResponseView view) {
        this.view = view;
        
    }

    /**
     * Loads all active/pending crisis situations from Firebase and updates the view.
     */
    public void loadCrisisEvents() {
        Supplier<List<CrisisSituation>> crisisSupplier = () -> {
            try {
                System.out.println("CrisisResponseController: Calling FirebaseService.getAllCrisisSituations()");
                List<CrisisSituation> fetchedCrises = firebaseService.getAllCrisisSituations();
                System.out.println("CrisisResponseController: Fetched " + (fetchedCrises != null ? fetchedCrises.size() : 0) + " raw CrisisSituations from Firestore.");
                if (fetchedCrises != null) {
                    fetchedCrises.forEach(crisis -> System.out.println("CrisisResponseController:   Raw Fetched Crisis -> ID: " + crisis.getId() +
                                       ", User: " + crisis.getUserId() +
                                       ", Status: " + crisis.getStatus() +
                                       ", Severity: " + crisis.getSeverity() +
                                       ", Protocol Active: " + crisis.getProtocolActivated() +
                                       ", Authorities Contacted: " + crisis.getAuthoritiesContacted()));
                }
                return fetchedCrises;
            } catch (Exception e) {
                System.err.println("CrisisResponseController: Error fetching crisis events: " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to load crisis events: " + e.getMessage()));
                e.printStackTrace(); // Print stack trace for debugging
                return null;
            }
        };

        CompletableFuture.supplyAsync(crisisSupplier)
            .thenAccept(crises -> Platform.runLater(() -> {
                if (crises != null) {
                    // Convert CrisisSituation model to the view's CrisisEvent format
                    List<CrisisResponseView.CrisisEvent> viewEvents = crises.stream()
                        // No explicit filter here, as FirebaseService.getAllCrisisSituations() already filters "Resolved"
                        .map(crisis -> new CrisisResponseView.CrisisEvent(
                            crisis.getId(), // Pass the document ID
                            crisis.getUserId(),
                            crisis.getSeverity(),
                            crisis.getStatus(),
                            crisis.getDescription(),
                            crisis.getTimestamp() != null ? DATE_FORMATTER.format(crisis.getTimestamp()) : "N/A",
                            crisis.getAssignedToPsychologistId() != null ? crisis.getAssignedToPsychologistId() : "Unassigned",
                            crisis.getProtocolActivated() != null ? crisis.getProtocolActivated() : false, // Default to false if null
                            crisis.getAuthoritiesContacted() != null ? crisis.getAuthoritiesContacted() : false // Default to false if null
                        ))
                        .collect(Collectors.toList());
                    view.updateCrisisEvents(viewEvents);
                    System.out.println("CrisisResponseController: Prepared " + viewEvents.size() + " crisis events for display.");
                } else {
                    System.out.println("CrisisResponseController: Crisis list is null after fetching, indicating an error occurred in supplier.");
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("CrisisResponseController: Unexpected error in crisis events loading (CompletableFuture.exceptionally): " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });
    }

    /**
     * Handles the "Emergency Protocol" action for a crisis event.
     * Updates the status and protocolActivated flag in Firestore.
     * @param event The crisis event to update.
     */
    public void handleEmergencyProtocol(CrisisResponseView.CrisisEvent event) {
        String newStatus = "Emergency Protocol Activated"; // Specific status for this action
        Boolean newProtocolActivated = true;
        Boolean currentAuthoritiesContacted = event.authoritiesContacted(); // Keep current Authorities Contacted status

        System.out.println("CrisisResponseController: Activating Emergency Protocol for Crisis ID: " + event.id());
        CompletableFuture.runAsync(() -> {
            try {
                firebaseService.updateCrisisStatus(event.id(), newStatus, newProtocolActivated, currentAuthoritiesContacted);
                Platform.runLater(() -> {
                    // FIX: Added Alert.AlertType.INFORMATION
                    view.showAlert(Alert.AlertType.INFORMATION, "Protocol Activated", "Emergency protocol activated for " + event.userId());
                    loadCrisisEvents(); // Refresh the list
                });
            } catch (Exception e) {
                System.err.println("CrisisResponseController: Error activating protocol for " + event.userId() + ": " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to activate protocol: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the "Contact Authorities" action for a crisis event.
     * Updates the authoritiesContacted flag in Firestore.
     * @param event The crisis event to update.
     */
    public void handleContactAuthorities(CrisisResponseView.CrisisEvent event) {
        Boolean newAuthoritiesContacted = true;
        String currentStatus = event.status(); // Keep current status
        Boolean currentProtocolActivated = event.protocolActivated(); // Keep current protocol activated status

        System.out.println("CrisisResponseController: Contacting Authorities for Crisis ID: " + event.id());
        CompletableFuture.runAsync(() -> {
            try {
                firebaseService.updateCrisisStatus(event.id(), currentStatus, currentProtocolActivated, newAuthoritiesContacted);
                Platform.runLater(() -> {
                    // FIX: Added Alert.AlertType.INFORMATION
                    view.showAlert(Alert.AlertType.INFORMATION, "Authorities Contacted", "Authorities have been contacted for " + event.userId());
                    loadCrisisEvents(); // Refresh the list
                });
            } catch (Exception e) {
                System.err.println("CrisisResponseController: Error contacting authorities for " + event.userId() + ": " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to contact authorities: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the "Update Status" action for a crisis event (e.g., to mark as Resolved, Monitoring, etc.).
     * For simplicity, this example resolves the crisis directly. A real app might open a dialog.
     * @param event The crisis event to update.
     */
    public void handleUpdateStatus(CrisisResponseView.CrisisEvent event) {
        // For simplicity, let's assume "Resolved" for now.
        // In a real application, you'd likely open a dialog here to select a new status.
        String newStatus = "Resolved";
        Boolean newProtocolActivated = false; // Deactivate protocol if resolved
        Boolean newAuthoritiesContacted = false; // Reset if resolved

        System.out.println("CrisisResponseController: Updating Status to '" + newStatus + "' for Crisis ID: " + event.id());
        CompletableFuture.runAsync(() -> {
            try {
                firebaseService.updateCrisisStatus(event.id(), newStatus, newProtocolActivated, newAuthoritiesContacted);
                // Alternatively, use resolveCrisis if that's a more direct action:
                // firebaseService.resolveCrisis(event.id());
                Platform.runLater(() -> {
                    // FIX: Added Alert.AlertType.INFORMATION
                    view.showAlert(Alert.AlertType.INFORMATION, "Status Updated", "Crisis for " + event.userId() + " updated to " + newStatus + ".");
                    loadCrisisEvents(); // Refresh the list
                });
            } catch (Exception e) {
                System.err.println("CrisisResponseController: Error updating status for " + event.userId() + ": " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to update status: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}