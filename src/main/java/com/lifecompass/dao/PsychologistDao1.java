  package com.lifecompass.dao;

import com.lifecompass.model.Psychologist;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
public interface PsychologistDao1 {
  


    void addPsychologist(Psychologist psychologist) throws ExecutionException, InterruptedException;
    Optional<Psychologist> getPsychologistById(String id) throws ExecutionException, InterruptedException;
    Optional<Psychologist> getPsychologistByEmail(String email) throws ExecutionException, InterruptedException;
    void updatePsychologist(Psychologist psychologist) throws ExecutionException, InterruptedException;
    void deletePsychologist(String id) throws ExecutionException, InterruptedException;
    List<Psychologist> getAllPsychologists() throws ExecutionException, InterruptedException;
    List<Psychologist> searchPsychologists(String query) throws ExecutionException, InterruptedException;
    List<Psychologist> getPsychologistsBySpecialization(String specialization) throws ExecutionException, InterruptedException;
    List<Psychologist> getPsychologistsByLocation(String location) throws ExecutionException, InterruptedException;
    List<Psychologist> getPsychologistsByAvailability(String availability) throws ExecutionException, InterruptedException;
    List<Psychologist> getPsychologistsByRating(double minRating) throws ExecutionException,    InterruptedException;   
    List<Psychologist> getPsychologistsByLanguage(String language) throws ExecutionException, InterruptedException;
}
