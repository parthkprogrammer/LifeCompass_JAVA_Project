package com.lifecompass.controller;

import com.lifecompass.model.FirebaseService;
import com.lifecompass.model.VerificationRequestModel; // Import the new model
import com.lifecompass.view.Adminview.AdminVerification; // Import the view
import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VerificationController {

    private AdminVerification view;
    private FirebaseService firebaseService;

    // Date formatter for displaying submission date
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public VerificationController(AdminVerification view) {
        this.view = view;
        // 4. REMOVE THIS LINE: Do NOT create a new instance of FirebaseService.
        // this.firebaseService = new FirebaseService();

        // FirebaseService is initialized once at application startup (e.g., in AdminDashboardView.init()).
        // All calls to FirebaseService should be directly on the class, like:
        // FirebaseService.getVerificationRequests();
        // FirebaseService.approvePsychologist(...);
    }

    /**
     * Loads all pending verification requests from Firebase and updates the view.
     */
//     public void loadVerificationRequests() {
//        // view.showLoading(true); // Show loading state in the view
//        // System.out.println("Controller: Loading verification requests...");
//         Supplier<List<VerificationRequestModel>> requestsSupplier = () -> {
//             try {
//                 // Fetch all requests
//                 List<VerificationRequestModel> fetchedRequests = firebaseService.getVerificationRequests();
//                 System.out.println("Fetched " + (fetchedRequests != null ? fetchedRequests.size() : 0) + " raw verification requests from Firestore.");
//                 // Optional: Print fetched requests for debugging
//                 // if (fetchedRequests != null) {
//                 //     fetchedRequests.forEach(req -> System.out.println("  Fetched Request: ID=" + req.getId() + ", Name=" + req.getEntityName() + ", Status=" + req.getStatus()));
//                 // }
//                 return fetchedRequests;
//             } catch (Exception e) {
//                 System.err.println("Error fetching verification requests: " + e.getMessage());
//                 Platform.runLater(() -> view.showErrorAlert("Error", "Failed to load verification requests: " + e.getMessage()));
//                 return null;
//             }
//         };

//         CompletableFuture.supplyAsync(requestsSupplier)
//             .thenAccept(requests -> Platform.runLater(() -> {
//                 if (requests != null) {
//                     // Convert VerificationRequestModel to the view's inner VerificationRequest format
//                     List<AdminVerification.VerificationRequest> viewRequests = requests.stream()
//                         .filter(req -> !"Approved".equals(req.getStatus()) && !"Rejected".equals(req.getStatus()))
//                         .map(req -> new AdminVerification.VerificationRequest(
//                             // All 7 arguments must be passed here to match the constructor
//                             req.getId(),               // 1st argument: String id
//                             req.getEntityId(),         // 2nd argument: String entityId
//                             req.getEntityName(),       // 3rd argument: String name
//                             req.getRole(),             // 4th argument: String role
//                             req.getRequestType(),      // 5th argument: String requestType
//                             req.getSubmittedAt() != null ? DATE_FORMATTER.format(req.getSubmittedAt()) : "N/A", // 6th argument: String submissionDate
//                             req.getStatus()            // 7th argument: String status
// , null
//                         ))
//                         .collect(Collectors.toList());
//                     view.updateVerificationRequests(viewRequests);
//                     System.out.println("Prepared " + viewRequests.size() + " verification requests for display.");
//                 }
//             }))
//             .exceptionally(ex -> {
//                 System.err.println("Unexpected error in verification requests loading: " + ex.getMessage());
//                 return null;
//             });
//     }
public void loadVerificationRequests() {
        view.showLoading(true); // Show loading state in the view
        System.out.println("Controller: Loading verification requests...");

        CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch all requests using static method
                List<VerificationRequestModel> fetchedRequests = FirebaseService.getVerificationRequests();
                System.out.println("Controller: Fetched " + (fetchedRequests != null ? fetchedRequests.size() : 0) + " raw verification requests from Firestore.");

                // Filter for "Under Review" status before mapping to view-specific format
                // CRITICAL CHANGE: Using equalsIgnoreCase for case-insensitive comparison
                return fetchedRequests.stream()
                        .filter(req -> req.getStatus() != null && "Under Review".equalsIgnoreCase(req.getStatus())) // <--- CHANGED THIS LINE (added null check for status)
                        .map(req -> new AdminVerification.VerificationRequest(
                                req.getId(),
                                req.getEntityId(),
                                req.getEntityName(),
                                req.getRole(),
                                req.getRequestType(),
                                req.getSubmittedAt() != null ? DATE_FORMATTER.format(req.getSubmittedAt()) : "N/A",
                                req.getStatus(),
                                req.getDocumentUrls() // <--- CORRECTLY PASSING documentUrls
                        ))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Controller Error fetching verification requests: " + e.getMessage());
                Platform.runLater(() -> view.showMessage("Error", "Failed to load verification requests: " + e.getMessage(), true));
                return null;
            }
        }).thenAccept(viewRequests -> Platform.runLater(() -> {
            if (viewRequests != null) {
                view.updateVerificationRequests(viewRequests);
                System.out.println("Controller: Prepared " + viewRequests.size() + " verification requests for display.");
            }
            view.showLoading(false); // Hide loading state
        })).exceptionally(ex -> {
            System.err.println("Controller: Unexpected error in verification requests loading: " + ex.getMessage());
            Platform.runLater(() -> view.showMessage("Error", "An unexpected error occurred while loading requests.", true));
            view.showLoading(false); // Hide loading state
            return null;
        });
    }
    /**
     * Handles the approval logic for a verification request.
     * Updates Firestore status for the request and the associated user/psychologist.
     * @param request The verification request to approve.
     */
    public void handleApprove(AdminVerification.VerificationRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                // 1. Update the status of the verification request itself
                firebaseService.updateVerificationRequestStatus(request.id(), "Approved");

                // 2. Based on role, update the actual User or Psychologist's verification status
                if ("Psychologist".equalsIgnoreCase(request.role())) {
                    // Assuming entityId in VerificationRequestModel is the actual Psychologist ID
                    firebaseService.approvePsychologist(request.entityId());
                } else if ("User".equalsIgnoreCase(request.role())) {
                    // Assuming entityId in VerificationRequestModel is the actual User ID
                    // For user verification, you might set their 'status' to 'active' or 'verified'
                    firebaseService.updateUserField(request.entityId(), "status", "active"); // Example: activate user
                    // Or set a verificationStatus field on the User model if it exists
                    // firebaseService.updateUserField(request.entityId(), "verificationStatus", "verified");
                }

                Platform.runLater(() -> {
                    view.showAlert("Success", "Verification request for " + request.name() + " has been approved and status updated.");
                    loadVerificationRequests(); // Refresh the list
                });

            } catch (Exception e) {
                System.err.println("Error approving request for " + request.name() + ": " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to approve request: " + e.getMessage()));
            }
        });
    }

    /**
     * Handles the rejection logic for a verification request.
     * Updates Firestore status for the request and the associated user/psychologist.
     * @param request The verification request to reject.
     */
    public void handleReject(AdminVerification.VerificationRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                // 1. Update the status of the verification request itself
                firebaseService.updateVerificationRequestStatus(request.id(), "Rejected");

                // 2. Optionally, update the associated user/psychologist's verification status
                if ("Psychologist".equalsIgnoreCase(request.role())) {
                    firebaseService.rejectPsychologist(request.entityId());
                } else if ("User".equalsIgnoreCase(request.role())) {
                    // For user rejection, you might set their 'status' to 'rejected' or leave them 'inactive'
                    // Or set a verificationStatus field on the User model if it exists
                    // firebaseService.updateUserField(request.entityId(), "verificationStatus", "rejected");
                }

                Platform.runLater(() -> {
                    view.showAlert("Success", "Verification request for " + request.name() + " has been rejected.");
                    loadVerificationRequests(); // Refresh the list
                });

            } catch (Exception e) {
                System.err.println("Error rejecting request for " + request.name() + ": " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to reject request: " + e.getMessage()));
            }
        });
    }

    /**
     * Handles the "Review Documents" action.
     * @param request The verification request.
     */
    public void handleReviewDocuments(AdminVerification.VerificationRequest request) {
        // In a real application, this would involve opening a new window or
        // external browser to view documents stored in Firebase Storage.
        // For now, it shows an alert as a placeholder.
        view.showAlert("Review Documents", "Simulating document review for " + request.name() + " (" + request.requestType() + ").\n" +
                       "In a real app, this would fetch/open documents from storage.");
    }
}
