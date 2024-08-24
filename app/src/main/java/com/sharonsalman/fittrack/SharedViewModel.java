package com.sharonsalman.fittrack;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModel extends AndroidViewModel {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    private static final String PREFERENCES_FILE = "com.sharonsalman.fittrack.preferences";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String NAME_KEY = "name";
    private static final String AGE_KEY = "age";
    private static final String WORKOUT_FREQUENCY_KEY = "workout_frequency";
    private static final String FITNESS_LEVEL_KEY = "fitness_level";
    private static final String WORKOUT_LOCATION_KEY = "workout_location";
    private static final String GOALS_KEY = "goals";
    private static final String CURRENT_WEIGHT_KEY = "current_weight";
    private static final String TARGET_WEIGHT_KEY = "target_weight";

    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<Integer> age = new MutableLiveData<>();
    private final MutableLiveData<String> workoutFrequency = new MutableLiveData<>();
    private final MutableLiveData<String> fitnessLevel = new MutableLiveData<>();
    private final MutableLiveData<String> workoutLocation = new MutableLiveData<>();
    private final MutableLiveData<String> goals = new MutableLiveData<>();
    private final MutableLiveData<Float> currentWeight = new MutableLiveData<>();
    private final MutableLiveData<Float> targetWeight = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
        database = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();
        sharedPreferences = application.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        loadPreferences();
    }

    private void loadPreferences() {
        email.setValue(sharedPreferences.getString(EMAIL_KEY, ""));
        name.setValue(sharedPreferences.getString(NAME_KEY, ""));
        password.setValue(sharedPreferences.getString(PASSWORD_KEY, ""));
        age.setValue(sharedPreferences.getInt(AGE_KEY, 0));
        workoutFrequency.setValue(sharedPreferences.getString(WORKOUT_FREQUENCY_KEY, ""));
        fitnessLevel.setValue(sharedPreferences.getString(FITNESS_LEVEL_KEY, ""));
        workoutLocation.setValue(sharedPreferences.getString(WORKOUT_LOCATION_KEY, "Home"));
        goals.setValue(sharedPreferences.getString(GOALS_KEY, ""));
        currentWeight.setValue(sharedPreferences.getFloat(CURRENT_WEIGHT_KEY, 0f));
        targetWeight.setValue(sharedPreferences.getFloat(TARGET_WEIGHT_KEY, 0f));
    }

    private void savePreference(String key, String value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, value).apply();
        } else {
            Log.e("SharedViewModel", "SharedPreferences is null when saving " + key);
        }
    }

    private void savePreference(String key, int value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(key, value).apply();
        } else {
            Log.e("SharedViewModel", "SharedPreferences is null when saving " + key);
        }
    }

    private void savePreference(String key, float value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putFloat(key, value).apply();
        } else {
            Log.e("SharedViewModel", "SharedPreferences is null when saving " + key);
        }
    }

    public MutableLiveData<String> getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null) {
            Log.e("SharedViewModel", "Attempt to set null email");
            return;
        }

        // Replace accidental commas with periods
        String cleanedEmail = email.replace(",", ".");

        String trimmedEmail = cleanedEmail.trim();
        Log.d("SharedViewModel", "Setting email: " + trimmedEmail);

        // Validate email format
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            this.email.setValue(trimmedEmail);
            try {
                savePreference(EMAIL_KEY, trimmedEmail);
                Log.d("SharedViewModel", "Email set and saved: " + trimmedEmail);
            } catch (Exception e) {
                Log.e("SharedViewModel", "Error saving email to SharedPreferences", e);
            }
        } else {
            Log.e("SharedViewModel", "Invalid email format: " + trimmedEmail);
        }
    }

    public MutableLiveData<String> getName() { return name; }
    public void setName(String name) {
        this.name.setValue(name);
        savePreference(NAME_KEY, name);
    }

    public MutableLiveData<String> getPassword() { return password; }
    public void setPassword(String password) {
        this.password.setValue(password);
        savePreference(PASSWORD_KEY, password);
    }

    public MutableLiveData<Integer> getAge() { return age; }
    public void setAge(Integer age) {
        this.age.setValue(age);
        savePreference(AGE_KEY, age);
    }


    public MutableLiveData<String> getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(String workoutFrequency) {
        this.workoutFrequency.setValue(workoutFrequency);
        savePreference(WORKOUT_FREQUENCY_KEY, workoutFrequency);
    }


    public MutableLiveData<String> getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel.setValue(fitnessLevel);
        savePreference(FITNESS_LEVEL_KEY, fitnessLevel);
    }

    public MutableLiveData<String> getWorkoutLocation() { return workoutLocation; }
    public void setWorkoutLocation(String workoutLocation) {
        this.workoutLocation.setValue(workoutLocation);
        savePreference(WORKOUT_LOCATION_KEY, workoutLocation);
    }

    public MutableLiveData<String> getGoals() { return goals; }
    public void setGoals(String goals) {
        this.goals.setValue(goals);
        savePreference(GOALS_KEY, goals);
    }

    public MutableLiveData<Float> getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Float currentWeight) {
        this.currentWeight.setValue(currentWeight);
        savePreference(CURRENT_WEIGHT_KEY, currentWeight);
    }

    public MutableLiveData<Float> getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Float targetWeight) {
        this.targetWeight.setValue(targetWeight);
        savePreference(TARGET_WEIGHT_KEY, targetWeight);
    }

    public void registerUser(OnRegisterCompleteListener listener) {
        String storedEmail = email.getValue();
        String storedPassword = password.getValue();

        if (storedEmail == null || storedEmail.isEmpty() || storedPassword == null || storedPassword.isEmpty()) {
            listener.onRegisterComplete(false, "Email or password is missing");
            return;
        }

        auth.createUserWithEmailAndPassword(storedEmail, storedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onRegisterComplete(true, "User registered successfully");
                    } else {
                        listener.onRegisterComplete(false, "Registration failed: " + task.getException().getMessage());
                    }
                });
    }

    public interface OnRegisterCompleteListener {
        void onRegisterComplete(boolean success, String message);
    }

    public void saveDataToFirebase(OnSaveCompleteListener listener) {
        String nameValue = name.getValue();
        Integer ageValue = age.getValue();
        String workoutFrequencyValue = workoutFrequency.getValue();
        String fitnessLevelValue = fitnessLevel.getValue();
        String workoutLocationValue = workoutLocation.getValue();
        String goalsValue = goals.getValue();
        Float currentWeightValue = currentWeight.getValue();
        Float targetWeightValue = targetWeight.getValue();

        if (nameValue == null || ageValue == null || workoutFrequencyValue == null ||
                fitnessLevelValue == null || workoutLocationValue == null || goalsValue == null ||
                currentWeightValue == null || targetWeightValue == null) {
            listener.onSaveComplete(false, "Data is incomplete");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            listener.onSaveComplete(false, "No authenticated user found");
            return;
        }

        String uid = firebaseUser.getUid(); // Get the Firebase UID
        DatabaseReference userRef = database.getReference("users").child(uid);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", nameValue);
        userMap.put("age", ageValue);
        userMap.put("workoutFrequency", workoutFrequencyValue);
        userMap.put("fitnessLevel", fitnessLevelValue);
        userMap.put("workoutLocation", workoutLocationValue);
        userMap.put("goals", goalsValue);
        userMap.put("currentWeight", currentWeightValue);
        userMap.put("targetWeight", targetWeightValue);

        userRef.updateChildren(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSaveComplete(true, "Data saved successfully");
                    } else {
                        listener.onSaveComplete(false, "Save failed: " + task.getException().getMessage());
                    }
                });
    }


    public interface OnSaveCompleteListener {
        void onSaveComplete(boolean success, String message);
    }
}
