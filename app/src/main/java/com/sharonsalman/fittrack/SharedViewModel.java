package com.sharonsalman.fittrack;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<Integer> age = new MutableLiveData<>();
    private final MutableLiveData<Integer> workoutFrequency = new MutableLiveData<>();
    private final MutableLiveData<String> fitnessLevel = new MutableLiveData<>();
    private final MutableLiveData<String> workoutLocation = new MutableLiveData<>();
    private final MutableLiveData<String> goals = new MutableLiveData<>();
    private final MutableLiveData<Float> currentWeight = new MutableLiveData<>();
    private final MutableLiveData<Float> targetWeight = new MutableLiveData<>();

    // Getters and setters for each field
    public MutableLiveData<String> getEmail() { return email; }
    public void setEmail(String email) { this.email.setValue(email); }

    public MutableLiveData<String> getName() { return name; }
    public void setName(String name) { this.name.setValue(name); }

    public MutableLiveData<Integer> getAge() { return age; }
    public void setAge(Integer age) { this.age.setValue(age); }

    public MutableLiveData<Integer> getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(Integer workoutFrequency) { this.workoutFrequency.setValue(workoutFrequency); }

    public MutableLiveData<String> getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel.setValue(fitnessLevel); }

    public MutableLiveData<String> getWorkoutLocation() { return workoutLocation; }
    public void setWorkoutLocation(String workoutLocation) { this.workoutLocation.setValue(workoutLocation); }

    public MutableLiveData<String> getGoals() { return goals; }
    public void setGoals(String goals) { this.goals.setValue(goals); }

    public MutableLiveData<Float> getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Float currentWeight) { this.currentWeight.setValue(currentWeight); }

    public MutableLiveData<Float> getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Float targetWeight) { this.targetWeight.setValue(targetWeight); }
}
