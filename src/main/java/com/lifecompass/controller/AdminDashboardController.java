package com.lifecompass.controller;

import com.lifecompass.model.FirebaseService;
import com.lifecompass.model.User;
import com.lifecompass.model.CrisisSituation;
import com.lifecompass.model.Psychologist; // Import Psychologist model
import com.lifecompass.view.Adminview.AdminDashboardView;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AdminDashboardController {

    private AdminDashboardView view;
    private FirebaseService firebaseService;

    // References to UI elements that will display data, allowing the controller to update them
    private AdminDashboardView.MetricCard totalUsersCard;
    private AdminDashboardView.MetricCard therapistsCard; // Represents the "Psychologist" count card
    private AdminDashboardView.MetricCard pendingReviewsCard;
    private AdminDashboardView.MetricCard activeCrisesCard;

    public AdminDashboardController(AdminDashboardView view) {
        this.view = view;
       // FirebaseService handles its own initialization
    }

    /**
     * Sets the references to the metric card UI components from the view.
     * This allows the controller to update their displayed values.
     * @param totalUsersCard The UI card for total users.
     * @param therapistsCard The UI card for psychologists.
     * @param pendingReviewsCard The UI card for pending reviews.
     * @param activeCrisesCard The UI card for active crises.
     */
    public void setMetricCards(AdminDashboardView.MetricCard totalUsersCard,
                               AdminDashboardView.MetricCard therapistsCard,
                               AdminDashboardView.MetricCard pendingReviewsCard,
                               AdminDashboardView.MetricCard activeCrisesCard) {
        this.totalUsersCard = totalUsersCard;
        this.therapistsCard = therapistsCard;
        this.pendingReviewsCard = pendingReviewsCard;
        this.activeCrisesCard = activeCrisesCard;
    }

    /**
     * Loads and updates all dashboard metrics by fetching data from FirebaseService
     * asynchronously and updating the UI on the JavaFX Application Thread.
     * This method is designed to be called whenever the Overview tab is activated
     * to ensure fresh data is displayed.
     */
    public void loadDashboardMetrics() {
        System.out.println("AdminDashboardController: Loading all dashboard metrics...");

        // Fetch Total Users (only from 'users' collection where role is 'user')
        Supplier<Integer> totalUserSupplier = () -> {
            try {
                int count = firebaseService.getTotalUserCount();
                System.out.println("AdminDashboardController: Fetched Total User Count: " + count);
                return count;
            } catch (Exception e) {
                System.err.println("AdminDashboardController: Error fetching total user count: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        };
        CompletableFuture.supplyAsync(totalUserSupplier)
            .thenAccept(count -> Platform.runLater(() -> {
                if (totalUsersCard != null) {
                    totalUsersCard.updateValue(String.valueOf(count));
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("AdminDashboardController: Unexpected error in total user count processing: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });


        // Fetch Verified Therapists (from 'psychologists' collection)
        Supplier<Integer> verifiedTherapistSupplier = () -> {
            try {
                int count = firebaseService.getVerifiedTherapistCount();
                System.out.println("AdminDashboardController: Fetched Verified Psychologist Count: " + count);
                return count;
            } catch (Exception e) {
                System.err.println("AdminDashboardController: Error fetching verified therapist count: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        };
        CompletableFuture.supplyAsync(verifiedTherapistSupplier)
            .thenAccept(count -> Platform.runLater(() -> {
                if (therapistsCard != null) {
                    therapistsCard.updateValue(String.valueOf(count));
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("AdminDashboardController: Unexpected error in verified therapist count processing: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        // Fetch Pending Reviews (from 'psychologists' collection)
        Supplier<Integer> pendingReviewsSupplier = () -> {
            try {
                int count = firebaseService.getPendingReviewsCount();
                System.out.println("AdminDashboardController: Fetched Pending Reviews Count: " + count);
                return count;
            } catch (Exception e) {
                System.err.println("AdminDashboardController: Error fetching pending reviews count: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        };
        CompletableFuture.supplyAsync(pendingReviewsSupplier)
            .thenAccept(count -> Platform.runLater(() -> {
                if (pendingReviewsCard != null) {
                    pendingReviewsCard.updateValue(String.valueOf(count));
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("AdminDashboardController: Unexpected error in pending reviews count processing: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        // Fetch Active Crises Count (from 'crisis_situations' collection)
        Supplier<Integer> activeCrisesSupplier = () -> {
            try {
                int count = firebaseService.getActiveCrisisCount();
                System.out.println("AdminDashboardController: Fetched Active Crises Count: " + count);
                return count;
            }
            catch (Exception e) {
                System.err.println("AdminDashboardController: Error fetching active crisis count: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        };
        CompletableFuture.supplyAsync(activeCrisesSupplier)
            .thenAccept(count -> Platform.runLater(() -> {
                if (activeCrisesCard != null) {
                    activeCrisesCard.updateValue(String.valueOf(count));
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("AdminDashboardController: Unexpected error in active crisis count processing: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        // Fetch Active Crisis Situations for the list
        Supplier<List<CrisisSituation>> crisisListSupplier = () -> {
            try {
                List<CrisisSituation> crises = firebaseService.getActiveCrisisSituations();
                System.out.println("AdminDashboardController: Fetched " + (crises != null ? crises.size() : 0) + " Active Crisis Situations.");
                return crises;
            } catch (Exception e) {
                System.err.println("AdminDashboardController: Error fetching active crisis situations: " + e.getMessage());
                e.printStackTrace();
                return null; // Return null on error
            }
        };
        CompletableFuture.supplyAsync(crisisListSupplier)
            .thenAccept(crisisList -> Platform.runLater(() -> {
                if (crisisList != null) {
                    view.updateActiveCrisisSituations(crisisList);
                }
            }))
            .exceptionally((Throwable ex) -> {
                System.err.println("AdminDashboardController: Unexpected error in active crisis situations processing: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        // Add similar calls for System Performance and Recent Activity data
        // (These would follow the same CompletableFuture.supplyAsync pattern if they fetch from Firebase)
    }
}