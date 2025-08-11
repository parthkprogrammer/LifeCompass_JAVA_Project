package com.lifecompass.dao;

import com.lifecompass.model.User;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface UserDao {

    void addUser(User user) throws ExecutionException, InterruptedException;
    Optional<User> getUserById(String id) throws ExecutionException, InterruptedException;
    Optional<User> getUserByEmail(String email) throws ExecutionException, InterruptedException;
    void updateUser(User user) throws ExecutionException, InterruptedException;
    void deleteUser(String id) throws ExecutionException, InterruptedException;
}