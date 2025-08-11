package com.lifecompass.model.psychologist;

import java.util.Map;

public class PatientAnalytics {
    private String averageImprovement;
    private String sessionCompletionRate;
    private String patientSatisfaction;
    private Map<String, Map<String, Integer>> treatmentEffectiveness;

    public PatientAnalytics() {}

    // Getters and Setters
    public String getAverageImprovement() { return averageImprovement; }
    public void setAverageImprovement(String averageImprovement) { this.averageImprovement = averageImprovement; }
    public String getSessionCompletionRate() { return sessionCompletionRate; }
    public void setSessionCompletionRate(String sessionCompletionRate) { this.sessionCompletionRate = sessionCompletionRate; }
    public String getPatientSatisfaction() { return patientSatisfaction; }
    public void setPatientSatisfaction(String patientSatisfaction) { this.patientSatisfaction = patientSatisfaction; }
    public Map<String, Map<String, Integer>> getTreatmentEffectiveness() { return treatmentEffectiveness; }
    public void setTreatmentEffectiveness(Map<String, Map<String, Integer>> treatmentEffectiveness) { this.treatmentEffectiveness = treatmentEffectiveness; }
}