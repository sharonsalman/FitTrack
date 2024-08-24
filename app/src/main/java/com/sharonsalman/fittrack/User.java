package com.sharonsalman.fittrack;

import java.util.List;
import java.util.Map;

public class User {
    private String email;
    private String password;
    private String name;
    private int age;
    private String workoutFrequency;
    private String fitnessLevel;
    private String workoutLocation;
    private String goal;
    private float currentWeight;
    private float targetWeight;
    private List<String> program_dates;
    private String selectedProgram;

    // Empty constructor needed for Firebase
    public User() {}

    // Constructor with parameters
    public User(String email, String name, String password, int age, String workoutFrequency, String fitnessLevel,
                String workoutLocation, String goal, float currentWeight, float targetWeight, List<String> program_dates, String selectedProgram) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.age = age;
        this.workoutFrequency = workoutFrequency;
        this.fitnessLevel = fitnessLevel;
        this.workoutLocation = workoutLocation;
        this.goal = goal;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
        this.program_dates = program_dates;
        this.selectedProgram = selectedProgram;
    }

    public User(String fitnessLevel, String goal, String workoutFrequency, String workoutLocation, float targetWeight) {
        this.fitnessLevel = fitnessLevel;
        this.goal = goal;
        this.workoutFrequency = workoutFrequency;
        this.workoutLocation = workoutLocation;
        this.targetWeight = targetWeight;
    }
    public User(String fitnessLevel,String workoutFrequency,String workoutLocation){
        this.fitnessLevel = fitnessLevel;
        this.workoutFrequency = workoutFrequency;
        this.workoutLocation = workoutLocation;
    }
    public User(String workoutFrequency, String fitnessLevel, String workoutLocation, String goal, float currentWeight, float targetWeight){
        this.workoutFrequency = workoutFrequency;
        this.fitnessLevel = fitnessLevel;
        this.workoutLocation = workoutLocation;
        this.goal = goal;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

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

    public List<String> getProgram_dates() { return program_dates; }
    public void setProgram_dates(List<String> program_dates) { this.program_dates = program_dates; }

    public String getSelectedProgram() { return selectedProgram; }
    public void setSelectedProgram(String selectedProgram) { this.selectedProgram = selectedProgram; }
}