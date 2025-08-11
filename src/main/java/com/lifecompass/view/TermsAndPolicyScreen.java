package com.lifecompass.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.Text; // Import javafx.scene.text.Text
import javafx.scene.text.TextFlow; // Import javafx.scene.text.TextFlow

public class TermsAndPolicyScreen extends VBox {

    private Stage ownerStage;

    // Static content directly related to the LifeCompass project description - REMOVED ** MARKERS
    private static final String TERMS_CONTENT =
            "LifeCompass: Terms and Conditions of Service\n\n" + // First line
            "Welcome to LifeCompass, your holistic mental wellness platform. These Terms and Conditions (\"Terms\") govern your access to and use of the LifeCompass desktop application, provided by [Your Company Name/Team Name]. By accessing or using the Service, you agree to be bound by these Terms.\n\n" +
            "1. Acceptance of Terms\n\n" + // Heading
            "Your use of LifeCompass signifies your unequivocal acceptance of these Terms. This includes all users (individuals managing mental health), psychologists (licensed professionals), and administrators. If you do not agree, you may not use the Service.\n\n" +
            "2. The LifeCompass Service (Overview)\n\n" + // Heading
            "LifeCompass offers distinct modules tailored to different roles:\n" +
            "   - User Module: Provides tools for mood tracking (emojis, sliders), journaling (text, voice, media), visual mood analysis, CBT-based check-ins, an 'Explore' section (music, articles, games, exercises), personal library, profile management, expressive art board, emotion-aware planner, Crisis Mode, gamification, anonymized group chat, and privacy features (AES encryption, Firebase sync).\n" +
            "   - Therapist/Psychologist Module: Includes features for profile verification, dashboard with pending requests, patient chat, graph-based monitoring, real-time crisis alerts, session feedback, AI suggestion tools, and performance analytics.\n" +
            "   - Admin Module: Manages account verification, request monitoring, activity logging, a Crisis Response Interface (geo-location, emergency contacts), AI moderation, and platform analytics.\n\n" +
            "IMPORTANT: LifeCompass is a supportive tool and does NOT provide emergency medical services or replace professional therapy. In a crisis, always contact emergency services or a licensed mental health professional directly.\n\n" + // Critical Warning
            "3. User Accounts and Security\n\n" + // Heading
            "To access certain features, you must create an account. You are responsible for maintaining the confidentiality of your account password and for all activities that occur under your account. LifeCompass utilizes Firebase Authentication and implements AES encryption for local journal storage to enhance your data security.\n\n" +
            "4. Data Privacy\n\n" + // Heading
            "Your privacy is paramount. Our data handling practices, including how mood logs, journal entries, and personal information are collected, stored (via Firebase Firestore and Storage), and used, are detailed in our Privacy Policy. By using LifeCompass, you consent to these practices.\n\n" +
            "5. Content and Intellectual Property\n\n" + // Heading
            "All content provided by LifeCompass (e.g., articles, music, exercise guides) is for informational purposes only. User-generated content (journals, mood logs, chat messages) is owned by the respective users. The application's design, code, and core functionalities are the exclusive property of [Your Company Name/Team Name].\n\n" +
            "6. Third-Party Services\n\n" + // Heading
            "LifeCompass may integrate with third-party services (e.g., Google Speech-to-Text API, NewsAPI.org). We are not responsible for the privacy practices or content of these third-party services. Your interaction with them is subject to their respective terms and policies.\n\n" +
            "7. Limitation of Liability\n\n" + // Heading
            "In no event shall LifeCompass, nor its directors, employees, partners, agents, suppliers, or affiliates, be liable for any indirect, incidental, special, consequential or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses, resulting from (i) your access to or use of or inability to access or use the Service; (ii) any conduct or content of any third party on the Service; (iii) any content obtained from the Service; and (iv) unauthorized access, use or alteration of your transmissions or content, whether based on warranty, contract, tort (including negligence) or any other legal theory, whether or not we have been informed of the possibility of such damage, and even if a remedy set forth herein is found to have failed of its essential purpose.\n\n" +
            "8. Changes to Terms\n\n" + // Heading
            "We reserve the right, at our sole discretion, to modify or replace these Terms at any time. If a revision is material we will provide at least 30 days notice prior to any new terms taking effect. What constitutes a material change will be determined at our sole discretion.\n\n" +
            "9. Governing Law\n\n" + // Heading
            "These Terms shall be governed and construed in accordance with the laws of India, without regard to its conflict of law provisions.\n\n" +
            "10. Contact Us\n\n" + // Heading
            "If you have any questions about these Terms, please contact us at support@lifecompass.com.\n\n" +
            "Last updated: July 27, 2025.";

    private static final String PRIVACY_CONTENT =
            "LifeCompass: Privacy Policy\n\n" + // First line
            "Your privacy is a cornerstone of the LifeCompass platform. This Privacy Policy outlines how [Your Company Name/Team Name] (\"we\", \"our\", or \"us\") collects, uses, maintains, and discloses information collected from users (each, a \"User\") of the LifeCompass desktop application.\n\n" +
            "1. Information We Collect\n\n" + // Heading
            "We collect several different types of information for various purposes to provide and improve our Service to you. This includes:\n" +
            "   - Account Information: Full Name, Username, Email address, Password (stored as hash), Gender, Date of Birth, Phone Number, Profile Picture URL. These are collected during registration and profile management.\n" +
            "   - Wellness Data: Mood entries (emojis, intensity, tags), journal entries (text, audio, video, images), expressive art creations, and responses to CBT check-ins. This data is collected directly from your input.\n" +
            "   - Usage Data: Information on how you interact with the app, such as features used, time spent, login frequency, and interaction with various modules (e.g., 'Explore' content, community chats). This helps us improve the service.\n" +
            "   - Technical Data: Device information, operating system, IP address (for security and basic analytics). This information is collected automatically.\n\n" +
            "2. Use of Data\n\n" + // Heading
            "LifeCompass uses the collected data for various purposes:\n" +
            "   - To provide and maintain our Service (e.g., displaying your mood trends, saving journal entries).\n" +
            "   - To personalize your experience, including applying chosen themes and suggesting relevant content or activities.\n" +
            "   - To facilitate communication between users and psychologists (only upon user-initiated request via the 'Explore' module).\n" +
            "   - For internal analytics and performance monitoring (e.g., tracking module engagement, therapist performance metrics).\n" +
            "   - To enhance security, detect fraud, and troubleshoot technical issues.\n" +
            "   - To comply with legal obligations and enforce our Terms of Service.\n" +
            "   - To manage Crisis Mode alerts and emergency response protocols (with strict adherence to defined protocols and user consent where applicable).\n\n" +
            "3. Disclosure of Data\n\n" + // Heading
            "We will not sell, trade, or rent your Personal Data to third parties except in the following situations:\n" +
            "   - With your explicit consent: We may disclose your personal information if you give us your explicit permission.\n" +
            "   - With service providers: We may employ third-party companies and individuals to facilitate our Service, such as cloud hosting or API providers (e.g., Google Speech-to-Text). These third parties have access to your Personal Data only to perform these tasks on our behalf and are obligated not to disclose or use it for any other purpose.\n" +
            "   - For legal reasons: If required to do so by law or in response to valid requests by public authorities (e.g., a court or a government agency).\n" +
            "   - To protect rights: To protect the rights, property, or personal safety of LifeCompass, our users, or the public.\n" +
            "   - Business Transfer: If LifeCompass is involved in a merger, acquisition or asset sale, your Personal Data may be transferred.\n" +
            "   - Psychologists: When you initiate a request to connect with a psychologist through our platform, we will share your user ID, name, email, and any message you provide to facilitate the connection. Psychologists on our platform are bound by their own ethical and professional standards regarding client confidentiality.\n\n" +
            "4. Data Storage and Security\n\n" + // Heading
            "Your data is securely stored using Firebase Firestore and Firebase Storage. Sensitive data like journal entries are encrypted using AES encryption (for local storage synchronization) before being sent to Firebase. While we implement robust security measures, no electronic storage or transmission method is 100% secure. We strive to protect your Personal Data, but we cannot guarantee its absolute security.\n\n" +
            "5. Your Choices and Rights\n\n" + // Heading
            "You have control over your data:\n" +
            "   - You can review and update your profile information through the Profile Management section.\n" +
            "   - You can request correction or deletion of any incorrect Personal Data.\n" +
            "   - You can request deletion of your Personal Data (subject to legal and operational requirements).\n" +
            "   - You can withdraw consent where we rely on your consent to process your Personal Data.\n" +
            "You may exercise these rights by contacting us through your account settings or directly via email.\n\n" +
            "6. Children's Privacy\n\n" + // Heading
            "LifeCompass is intended for users aged 13 and above. We do not knowingly collect personally identifiable information from anyone under the age of 13. If you are a parent or guardian and you are aware that your child has provided us with Personal Data, please contact us immediately.\n\n" +
            "7. Changes to This Privacy Policy\n\n" + // Heading
            "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page. You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.\n\n" +
            "8. Contact Us\n\n" + // Heading
            "If you have any questions about this Privacy Policy, please contact us:\n" +
            "   - By email: support@lifecompass.com\n\n" +
            "Last updated: July 27, 2025.";

    private ScrollPane contentScrollPane;
    private javafx.scene.text.TextFlow contentTextFlow;

    public TermsAndPolicyScreen(Stage ownerStage) {
        this.ownerStage = ownerStage;
        initializeUI();
    }

    private void initializeUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #f9fafb;");

        Label headerLabel = new Label("Terms & Privacy Policy");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-text-fill: #333333;");

        HBox navButtons = new HBox(15);
        navButtons.setAlignment(Pos.CENTER);

        final Button termsButton = new Button("Terms & Conditions");
        final Button privacyButton = new Button("Privacy Policy");

        termsButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5;");
        termsButton.setOnAction(e -> displayContent(TERMS_CONTENT, termsButton, privacyButton));

        privacyButton.setStyle("-fx-background-color: #f0f2f5; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-width: 1;");
        privacyButton.setOnAction(e -> displayContent(PRIVACY_CONTENT, privacyButton, termsButton));

        navButtons.getChildren().addAll(termsButton, privacyButton);

        contentTextFlow = new javafx.scene.text.TextFlow();
        contentTextFlow.setPadding(new Insets(0));
        contentTextFlow.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        contentScrollPane = new ScrollPane(contentTextFlow);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScrollPane.setPrefHeight(400);
        contentScrollPane.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 15;");
        VBox.setVgrow(contentScrollPane, Priority.ALWAYS);

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #5a5a5a; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        closeButton.setOnAction(e -> ((Stage) this.getScene().getWindow()).close());

        this.getChildren().addAll(headerLabel, navButtons, contentScrollPane, closeButton);

        displayContent(TERMS_CONTENT, termsButton, privacyButton);
    }

    /**
     * Displays the given content, applying bold formatting to designated lines.
     * Logic: First line is bold. Lines starting with a number and period (e.g., "1. ") are bold.
     * @param content The text content to display.
     * @param activeButton The button that should look "active".
     * @param otherButton The button that should look "inactive".
     */
    private void displayContent(String content, Button activeButton, Button otherButton) {
        contentTextFlow.getChildren().clear();

        String[] lines = content.split("\n", -1); // Split by newline, keep empty lines

        boolean firstLine = true;
        for (String line : lines) {
            Text textNode;
            if (firstLine || line.matches("^\\d+\\..*")) { // Check if it's the first line OR starts with "Number. "
                textNode = new Text(line);
                textNode.setFont(Font.font("Arial", FontWeight.BOLD, 13)); // Apply bold font
                firstLine = false; // After first line, reset flag
            } else {
                textNode = new Text(line);
                textNode.setFont(Font.font("Arial", FontWeight.NORMAL, 13)); // Apply normal font
            }
            textNode.setFill(javafx.scene.paint.Color.web("#333333")); // Consistent dark text color
            
            contentTextFlow.getChildren().add(textNode);
            contentTextFlow.getChildren().add(new Text("\n")); // Add newline after each line
        }
        
        // Remove the very last newline if content doesn't end with one naturally
        if (!content.endsWith("\n") && contentTextFlow.getChildren().size() > 0) {
            contentTextFlow.getChildren().remove(contentTextFlow.getChildren().size() - 1);
        }

        // Button styling logic
        activeButton.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5;");
        otherButton.setStyle("-fx-background-color: #f0f2f5; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        contentScrollPane.setVvalue(0.0); // Reset scroll to top
    }

    public void show() {
        Stage termsStage = new Stage();
        termsStage.initModality(Modality.WINDOW_MODAL);
        termsStage.initOwner(ownerStage);
        termsStage.setTitle("LifeCompass: Terms & Privacy Policy");

        Scene scene = new Scene(this, 700, 600);
        termsStage.setScene(scene);
        termsStage.showAndWait();
    }
}