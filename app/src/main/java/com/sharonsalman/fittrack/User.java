package com.sharonsalman.fittrack;

public class User {
    private String email;
    private String name;
    private int age;
    private String workoutFrequency;
    private String fitnessLevel;
    private String workoutLocation;
    private String goal;
    private float currentWeight;
    private float targetWeight;

    // Constructor
    public User(String email, String name, int age, String workoutFrequency, String fitnessLevel,
                String workoutLocation, String goal, float currentWeight, float targetWeight) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.workoutFrequency = workoutFrequency;
        this.fitnessLevel = fitnessLevel;
        this.workoutLocation = workoutLocation;
        this.goal = goal;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWorkoutFrequency() {
        return workoutFrequency;
    }

    public void setWorkoutFrequency(String workoutFrequency) {
        this.workoutFrequency = workoutFrequency;
    }

    public String getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public String getWorkoutLocation() {
        return workoutLocation;
    }

    public void setWorkoutLocation(String workoutLocation) {
        this.workoutLocation = workoutLocation;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(float currentWeight) {
        this.currentWeight = currentWeight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }
}
