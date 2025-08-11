// package com.lifecompass.util;

// import com.lifecompass.view.PsychologistLoginScreen;
// import com.lifecompass.view.Psycologiestview.PsychologistDashboard;

// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.stage.Stage;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;

// public class SceneManager {

//     private static Stage primaryStage;

//     public static void setPrimaryStage(Stage stage) {
//         primaryStage = stage;
//     }

//     public static void switchScreen(Application app, String title) {
//         Platform.runLater(() -> {
//             try {
//                 Stage newStage = new Stage();
//                 app.start(newStage);
//                 newStage.setTitle(title);

//                 if (primaryStage != null && primaryStage.isShowing() && primaryStage != newStage) {
//                     primaryStage.close();
//                 }
//                 primaryStage = newStage;
//             } catch (Exception e) {
//                 System.err.println("Failed to load screen: " + app.getClass().getSimpleName());
//                 e.printStackTrace();
//                 showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested screen.");
//             }
//         });
//     }

//     public static void showAlert(Alert.AlertType type, String title, String message) {
//         Platform.runLater(() -> {
//             Alert alert = new Alert(type);
//             alert.setTitle(title);
//             alert.setHeaderText(null);
//             alert.setContentText(message);
//             alert.showAndWait();
//         });
//     }

//     public static Stage getPrimaryStage() {
//         return primaryStage;
//     }

//     // Fixed: Implemented the method properly
//     // public static void switchSceneToContent(Stage stage, Object content, String title) {
//     //     Platform.runLater(() -> {
//     //         try {
//     //             if (content instanceof Scene) {
//     //                 stage.setScene((Scene) content);
//     //                 stage.setTitle(title);
//     //                 stage.show();
//     //             } else {
//     //                 System.err.println("Content must be a Scene object");
//     //                 showAlert(Alert.AlertType.ERROR, "Scene Error", "Invalid content type for scene switching.");
//     //             }
//     //         } catch (Exception e) {
//     //             System.err.println("Failed to switch scene: " + e.getMessage());
//     //             e.printStackTrace();
//     //             showAlert(Alert.AlertType.ERROR, "Scene Error", "Could not switch to the requested scene.");
//     //         }
//     //     });
//     // }
//     public static void switchSceneToContent(Stage primaryStage2, Object registrationContent, String string) {
//         Platform.runLater(() -> {
//             try {
//                 if (registrationContent instanceof Scene) {
//                     primaryStage2.setScene((Scene) registrationContent);
//                     primaryStage2.setTitle(string);
//                     primaryStage2.show();
//                 } else {
//                     System.err.println("Content must be a Scene object");
//                     showAlert(Alert.AlertType.ERROR, "Scene Error", "Invalid content type for scene switching.");
//                 }
//             } catch (Exception e) {
//                 System.err.println("Failed to switch scene: " + e.getMessage());
//                 e.printStackTrace();
//                 showAlert(Alert.AlertType.ERROR, "Scene Error", "Could not switch to the requested scene.");
//             }
//         });
//     }

    
// }


package com.lifecompass.util;

import com.lifecompass.view.PsychologistLoginScreen;
import com.lifecompass.view.Psycologiestview.PsychologistDashboard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent; // NEW: Import Parent
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * ORIGINAL METHOD FROM YOUR PROVIDED SNIPPET.
     * WARNING: Calling app.start(newStage) multiple times is generally
     * problematic for JavaFX application lifecycle. Consider refactoring
     * to use switchSceneContent if possible for major screen transitions
     * on the same primary stage.
     */
    public static void switchScreen(Application app, String title) {
        Platform.runLater(() -> {
            try {
                Stage newStage = new Stage();
                app.start(newStage);
                newStage.setTitle(title);

                if (primaryStage != null && primaryStage.isShowing() && primaryStage != newStage) {
                    primaryStage.close();
                }
                primaryStage = newStage;
            } catch (Exception e) {
                System.err.println("Failed to load screen: " + app.getClass().getSimpleName());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested screen.");
            }
        });
    }

    /**
     * Switches the content (root node) of the given stage's existing scene.
     * Use this for navigating between main application screens (e.g., login, dashboard).
     * This replaces the problematic `switchScreen(Application, String)` and clarifies `switchSceneToContent`.
     * @param targetStage The stage whose scene content will be changed.
     * @param newContent The new Parent (e.g., BorderPane, VBox, Pane) to set as the root of the scene.
     * @param title The new title for the stage.
     */
    public static void switchSceneToContent(Stage targetStage, Parent newContent, String title) { // FIX: Changed Node to Parent
        if (targetStage == null) {
            showAlert(Alert.AlertType.ERROR, "Scene Error", "Target Stage is null. Cannot switch scene content.");
            return;
        }

        Platform.runLater(() -> {
            try {
                // If a scene doesn't exist yet for the targetStage, create one. Otherwise, update existing.
                if (targetStage.getScene() == null) {
                    targetStage.setScene(new Scene(newContent)); // FIX: Scene constructor takes Parent
                } else {
                    targetStage.getScene().setRoot(newContent); // FIX: setRoot takes Parent
                }
                targetStage.setTitle(title);
                // targetStage.sizeToScene(); // Optional: uncomment if you want the stage to resize to content
                targetStage.show();
            } catch (Exception e) {
                System.err.println("Failed to switch scene content: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Scene Error", "Could not switch to the requested scene content.");
            }
        });
    }

    /**
     * Displays a standard JavaFX Alert dialog.
     * @param alertType The type of alert (e.g., Alert.AlertType.INFORMATION, Alert.AlertType.ERROR).
     * @param title The title of the alert window.
     * @param message The main message content of the alert.
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Displays a confirmation dialog and returns true if OK is pressed, false otherwise.
     * @param title The title of the confirmation window.
     * @param message The confirmation message.
     * @return true if OK is pressed, false otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}