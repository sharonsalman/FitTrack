package com.sharonsalman.fittrack;

public class UserData {
    private String fitnessLevel;
    private int workoutFrequency;
    private String workoutLocation;

    // Default constructor
    public UserData() {}

    // Getters and setters
    public String getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel = fitnessLevel; }

    public int getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(int workoutFrequency) { this.workoutFrequency = workoutFrequency; }

    public String getWorkoutLocation() { return workoutLocation; }
    public void setWorkoutLocation(String workoutLocation) { this.workoutLocation = workoutLocation; }
}