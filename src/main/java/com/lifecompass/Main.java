package com.lifecompass;

import com.lifecompass.config.FirebaseConfig;
import com.lifecompass.util.SceneManager;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class Main extends Application {
    
  
@Override
public void init() throws Exception {
    super.init();
    System.out.println("JavaFX Application init() method called. Initializing FirebaseService...");
    com.lifecompass.model.FirebaseService.initialize();
    com.lifecompass.services.FirebaseService.initialize();
    System.out.println("FirebaseService initialization attempted from JavaFX init() completed.");
}
    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        FirebaseConfig.initialize();
     
        try {
            new LoginScreen().start(primaryStage);
        } catch (Exception e) {
            System.err.println("Failed to start initial screen: " + e.getMessage());
            e.printStackTrace();
            SceneManager.showAlert(Alert.AlertType.ERROR, "Application Launch Error", "Failed to start the application. Please check logs for details.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}