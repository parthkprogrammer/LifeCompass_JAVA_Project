# LifeCompass – Holistic Mental Wellness Platform

## 📌 Overview

**LifeCompass** is a JavaFX-based desktop application designed to promote **mental wellness** and provide access to emotional and psychological support.
It connects users, psychologists, and administrators through a **centralized platform** offering features like activity tracking, therapy scheduling, music recommendations, community building, and crisis management.

## 🎯 Key Features

### 👤 User
- Register/Login securely
- Mood tracking and analytics
- Access personalized activities, articles, and music
- Join community discussions
- Book therapy sessions with psychologists

### 🧠 Psychologist
- Manage patient profiles
- View appointments and provide online consultations
- Crisis monitoring and emergency alerts
- Profile and settings management

### 🛡 Admin
- Manage users and psychologists
- Verify psychologist credentials
- Crisis response coordination
- Platform data insights and analytics

## 🛠 Tech Stack

- **JavaFX** – UI Framework
- **Firebase** – Authentication & Data Storage
- **REST APIs** – External content integration (Music, Articles, Activities)
- **Maven** – Build automation
- **FXML** – UI Design
- **JDBC** – Data connectivity

## 📂 Project Structure
lifecompasfinal11/
├── pom.xml                          # Maven dependencies
└── src
├── main
│   ├── java
│   │   └── com
│   │       └── lifecompass
│   │           ├── api          # API client classes
│   │           ├── config       # Firebase config
│   │           ├── controller   # Controllers for various modules
│   │           ├── dao          # Data access objects
│   │           ├── model        # Data models
│   │           └── util         # Utility classes
│   └── resources                # FXML layouts, CSS, images
└── test
## ⚙️ Setup Instructions

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/parthkprogrammer/lifecompass_project_java.git](https://github.com/yourusername/lifecompass_project_java.git)
    cd lifecompass_project_java/lifecompasfinal11
    ```

2.  **Configure Firebase**
    - Add your Firebase configuration in `src/main/java/com/lifecompass/config/FirebaseConfig.java`
    - Enable Authentication & Firestore Database in your Firebase project.

3.  **Run the Application**
    - **Using Maven:**
      ```bash
      mvn clean install
      mvn javafx:run
      ```
    - Or run the `Main.java` class from your IDE.

4.  **API Configuration**
    - Update API keys in their respective `ApiClient` classes (e.g., Music, Articles, Activities).

## 📸 Screenshots

*(Add UI screenshots here for better presentation)*

## 📜 License

This project is for educational purposes and can be modified for personal or research use.