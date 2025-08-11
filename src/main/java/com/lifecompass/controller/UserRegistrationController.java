package com.lifecompass.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.lifecompass.config.FirebaseConfig;
import com.lifecompass.dao.UserDao;
import com.lifecompass.dao.impl.UserDaoFirestoreImpl;
import com.lifecompass.model.User;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public class UserRegistrationController {

    private final FirebaseAuth firebaseAuth;
    private final UserDao userDao;

    public UserRegistrationController() {
        FirebaseConfig.initialize();
        this.firebaseAuth = FirebaseConfig.getFirebaseAuth();
        this.userDao = new UserDaoFirestoreImpl();
    }

    public boolean registerUser(String fullName, String gender, LocalDate dateOfBirth, String phoneNumber,
                                 String familyPhoneNumber, String friendsPhoneNumber, String email, String username,
                                 String password, String city, String state, String country, String pinZipCode,
                                 boolean termsAgreed, boolean privacyAgreed) {
        if (!termsAgreed || !privacyAgreed) {
            System.err.println("User must agree to terms and privacy policy.");
            return false;
        }
        if (phoneNumber.isEmpty() || familyPhoneNumber.isEmpty() || friendsPhoneNumber.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.err.println("Required fields for registration are missing.");
            return false;
        }

        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(fullName)
                    .setEmailVerified(false)
                    .setDisabled(false);

            UserRecord userRecord = firebaseAuth.createUser(request);
            System.out.println("Successfully created new user in Firebase Auth: " + userRecord.getUid());

            User user = new User(userRecord.getUid(), fullName, gender, dateOfBirth, phoneNumber,
                                 familyPhoneNumber, friendsPhoneNumber, email, username,
                                 null,
                                 city, state, country, pinZipCode, termsAgreed, privacyAgreed);

            userDao.addUser(user);
            System.out.println("User details saved to Firestore for UID: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException e) {
            System.err.println("Firebase Auth Error during registration: " + e.getMessage());
            return false;
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Firestore Error during user data storage: " + e.getMessage());
            try {
                System.err.println("Attempted to roll back Firebase Auth user due to Firestore error.");
            } catch (Exception ex) {
                System.err.println("Error rolling back Firebase Auth user: " + ex.getMessage());
            }
            return false;
        }
    }
}