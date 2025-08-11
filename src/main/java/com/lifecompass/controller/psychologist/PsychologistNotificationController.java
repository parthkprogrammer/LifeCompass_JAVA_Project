package com.lifecompass.controller.psychologist;

import com.lifecompass.dao.PsychologistDao;
import com.lifecompass.model.psychologist.PsychologistNotification;
import com.lifecompass.view.Psycologiestview.PsychologistNotificationScreen;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychologistNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(PsychologistNotificationController.class);

    private final PsychologistNotificationScreen view;
    private final PsychologistDao psychologistDao;
    private final String psychologistId;

    public PsychologistNotificationController(PsychologistNotificationScreen view, PsychologistDao psychologistDao, String psychologistId) {
        this.view = view;
        this.psychologistDao = psychologistDao;
        this.psychologistId = psychologistId;
        logger.info("PsychologistNotificationController initialized for ID: {}", psychologistId);
    }

    public void initialize() {
        view.setMarkAllReadHandler(this::handleMarkAllRead);
        view.setDismissHandler(this::handleDismiss);
        logger.debug("Notification handlers set.");
        loadNotifications();
    }

    private void loadNotifications() {
        view.showLoading(true);
        logger.info("Loading notifications for ID: {}", psychologistId);
        psychologistDao.getNotifications(psychologistId)
            .thenAccept(notifications -> {
                Platform.runLater(() -> {
                    view.populateNotifications(notifications);
                    view.showLoading(false);
                    logger.info("Notifications list populated with {} items.", notifications.size());
                });
            })
            .exceptionally(e -> {
                logger.error("Error loading notifications for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to load notifications: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    private void handleMarkAllRead() {
        view.showLoading(true);
        logger.info("Marking all notifications as read for psychologist ID: {}", psychologistId);
        psychologistDao.markAllNotificationsAsRead(psychologistId)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    view.showSuccess("All notifications marked as read.");
                    logger.info("All notifications marked as read for ID: {}.", psychologistId);
                    loadNotifications();
                });
            })
            .exceptionally(e -> {
                logger.error("Error marking all notifications as read for ID {}: {}", psychologistId, e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to mark all as read: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }

    private void handleDismiss(PsychologistNotification notification) {
        view.showLoading(true);
        logger.info("Dismissing notification ID: {}", notification.getId());
        psychologistDao.deleteNotification(notification.getId())
            .thenRun(() -> {
                Platform.runLater(() -> {
                    view.showSuccess("Notification dismissed.");
                    logger.info("Notification ID {} dismissed successfully.", notification.getId());
                    loadNotifications();
                });
            })
            .exceptionally(e -> {
                logger.error("Error dismissing notification ID {}: {}", notification.getId(), e.getMessage(), e);
                Platform.runLater(() -> {
                    view.showError("Failed to dismiss notification: " + e.getMessage());
                    view.showLoading(false);
                });
                return null;
            });
    }
}