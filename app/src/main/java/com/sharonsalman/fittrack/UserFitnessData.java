package com.sharonsalman.fittrack;

public class UserFitnessData {
    private String workoutFrequency;
    private String fitnessLevel;
    private String workoutLocation;
    private String goal;
    private float currentWeight;
    private float targetWeight;

    // Empty constructor needed for Firebase
    public UserFitnessData() {}

    // Constructor with parameters
    public UserFitnessData(String workoutFrequency, String fitnessLevel, String workoutLocation, String goal, float currentWeight, float targetWeight) {
        this.workoutFrequency = workoutFrequency;
        this.fitnessLevel = fitnessLevel;
        this.workoutLocation = workoutLocation;
        this.goal = goal;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
    }

    public UserFitnessData(String workoutFrequency, String fitnessLevel, String workoutLocation, String goal, float targetWeight) {
        this.workoutFrequency = workoutFrequency;
        this.fitnessLevel = fitnessLevel;
        this.workoutLocation = workoutLocation;
        this.goal = goal;
        this.targetWeight = targetWeight;
    }

    // Getters and setters
    public String getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(String workoutFrequency) { this.workoutFrequency = workoutFrequency; }

    public String getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel = fitnessLevel; }

    public String getWorkoutLocation() { return workoutLocation; }
    public void setWorkoutLocation(String workoutLocation) { this.workoutLocation = workoutLocation; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public float getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(float currentWeight) { this.currentWeight = currentWeight; }

    public float getTargetWeight() { return targetWeight; }
    public void setTargetWeight(float targetWeight) { this.targetWeight = targetWeight; }
}