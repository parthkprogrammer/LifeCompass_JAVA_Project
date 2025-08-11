package com.lifecompass.controller; // <-- CORRECTED PACKAGE NAME HERE

// CORRECTED IMPORTS
import com.lifecompass.model.FirebaseService; // Corrected model package
import com.lifecompass.model.User; // Corrected model package
import com.lifecompass.view.Adminview.UserManagementView; // Corrected view package
import javafx.application.Platform;
// Do NOT import com.google.firebase.auth.UserRecord here, you're using UserManagementView.UserRecord.
// import com.google.firebase.auth.UserRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UserManagementController {

    private UserManagementView view;
    // Remove the FirebaseService instance variable. It's a static utility class.
    // private FirebaseService firebaseService; // <-- REMOVE THIS LINE

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public UserManagementController(UserManagementView view) {
        this.view = view;
        // Do NOT instantiate FirebaseService here. It's initialized once at app startup (e.g., in AdminDashboardView.init()).
        // this.firebaseService = null; // <-- This line is also not needed if you remove the field.
    }

    public void loadUsers(String searchQuery) {
        Supplier<List<User>> userSupplier = () -> {
            try {
                System.out.println("FirebaseService.getUsers() called with search: " + searchQuery);
                // Call static method on FirebaseService directly
                List<User> fetchedUsers = FirebaseService.getUsers(searchQuery); // <-- Use static call
                System.out.println("Fetched " + (fetchedUsers != null ? fetchedUsers.size() : 0) + " raw users from Firestore.");
                if (fetchedUsers != null) {
                    fetchedUsers.forEach(user -> System.out.println("   Fetched User: ID=" + user.getId() + ", Email=" + user.getEmail() + ", Role=" + user.getRole()));
                }
                return fetchedUsers;
            } catch (Exception e) {
                System.err.println("Error fetching users: " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to load user data: " + e.getMessage()));
                return null;
            }
        };

        CompletableFuture.supplyAsync(userSupplier)
            .thenAccept(users -> Platform.runLater(() -> {
                if (users != null) {
                    // This explicit reference to the nested class in UserManagementView is correct.
                    List<UserManagementView.UserRecord> userRecords = users.stream()
                        .filter(user -> {
                            boolean isUserRole = "user".equals(user.getRole());
                            if (!isUserRole) {
                                System.out.println("   Filtering out user with non-'user' role: ID=" + user.getId() + ", Role=" + user.getRole());
                            }
                            return isUserRole;
                        })
                        .map(user -> {
                            String formattedDate = user.getCreatedAt() != null ? DATE_FORMATTER.format(user.getCreatedAt()) : "N/A";
                            String status = user.getStatus() != null ? user.getStatus() : "Unknown";
                            String risk = user.getRiskLevel() != null ? user.getRiskLevel() : "Low Risk";
                            System.out.println("   Mapping User: ID=" + user.getId() + ", Email=" + user.getEmail() + ", Formatted Date=" + formattedDate + ", Status=" + status + ", Risk=" + risk);
                            return new UserManagementView.UserRecord( // Correct way to instantiate your nested record
                                user.getId(),
                                user.getEmail(),
                                formattedDate,
                                status,
                                risk
                            );
                        })
                        .collect(Collectors.toList());

                    System.out.println("Prepared " + userRecords.size() + " user records for display.");
                    view.updateUserRecords(userRecords);

                    if (userRecords.isEmpty() && !users.isEmpty()) {
                        System.out.println("WARNING: Fetched users but none passed the role filter or mapping. Check roles in Firestore.");
                    } else if (userRecords.isEmpty() && users.isEmpty()) {
                        System.out.println("INFO: No users fetched from Firestore or query returned empty.");
                    }

                } else {
                    System.out.println("Users list is null after fetching, indicating an error occurred.");
                }
            }))
            .exceptionally(ex -> {
                System.err.println("Unexpected error in user data loading: " + ex.getMessage());
                return null;
            });
    }

    public void handleViewDetails(String userId) {
        view.showAlert("User Details", "Details for User ID: " + userId + " (Not yet implemented fully)");
        System.out.println("Viewing details for user: " + userId);
    }

    public void handleUserActions(String userId, String currentStatus, String currentRiskLevel) {
        String newStatus = "active".equalsIgnoreCase(currentStatus) ? "suspended" : "active";

        CompletableFuture.runAsync(() -> {
            try {
                // Call static method directly
                FirebaseService.updateUserField(userId, "status", newStatus); // <-- Use static call
                Platform.runLater(() -> {
                    view.showAlert("Success", "User " + userId + " status updated to " + newStatus);
                    loadUsers(null); // Refresh the list to show the change
                });
            } catch (Exception e) {
                System.err.println("Error updating user status: " + e.getMessage());
                Platform.runLater(() -> view.showErrorAlert("Error", "Failed to update status for " + userId + ": " + e.getMessage()));
            }
        });
    }

    public void handleExport() {
        view.showAlert("Export Data", "Export functionality is not yet implemented.");
    }

    public void handleFilter() {
        view.showAlert("Filter Users", "Filter functionality is not yet implemented fully.");
    }
}